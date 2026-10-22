package de.example.minecraftfunfacts

import android.app.Activity
import android.content.ComponentName
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors

class MainActivity : Activity() {
    private var controller: MediaController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val facts = Facts(this)
        val wrapper = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(28, 32, 28, 20) }
        fun label(text: String) = TextView(this).apply { this.text = text; textSize = 20f; setPadding(0, 12, 0, 12) }
        wrapper.addView(label("Minecraft Fun Facts"))
        wrapper.addView(label("${facts.all.size} Fakten · komplett offline"))
        val mode = Switch(this).apply { text = "Automatische Endloswiedergabe"; isChecked = prefs.getBoolean("auto", true) }
        mode.setOnCheckedChangeListener { _, checked -> prefs.edit().putBoolean("auto", checked).apply() }
        wrapper.addView(mode)
        wrapper.addView(label("Kategorie"))
        val names = listOf("Alle Fakten") + facts.all.map { it.category }.distinct().sorted()
        val ids = listOf("all") + names.drop(1)
        val spinner = Spinner(this)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, names)
        spinner.setSelection(ids.indexOf(prefs.getString("category", "all")).coerceAtLeast(0))
        wrapper.addView(spinner)
        val description = label("Wähle eine Kategorie und starte die Wiedergabe.")
        wrapper.addView(description)
        val play = Button(this).apply { text = "Start / Pause"; isEnabled = false }
        val next = Button(this).apply { text = "Nächster Fakt"; isEnabled = false }
        val previous = Button(this).apply { text = "Vorheriger Fakt"; isEnabled = false }
        wrapper.addView(play); wrapper.addView(next); wrapper.addView(previous)
        setContentView(ScrollView(this).apply { addView(wrapper) })
        val future = MediaController.Builder(this, SessionToken(this, ComponentName(this, FunFactService::class.java))).buildAsync()
        future.addListener({
            try {
                controller = future.get()
                play.isEnabled = true; next.isEnabled = true; previous.isEnabled = true
                fun display() { description.text = controller?.currentMediaItem?.mediaMetadata?.description ?: "Bereit" }
                controller?.addListener(object : androidx.media3.common.Player.Listener {
                    override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) { display() }
                })
                display()
                play.setOnClickListener {
                    val c = controller ?: return@setOnClickListener
                    if (c.isPlaying) c.pause() else {
                        val category = ids[spinner.selectedItemPosition]
                        if (prefs.getString("category", "all") != category) {
                            prefs.edit().putString("category", category).apply()
                            c.setMediaItem(androidx.media3.common.MediaItem.Builder().setMediaId("category:$category").build())
                            c.prepare()
                        }
                        c.play()
                    }
                }
                next.setOnClickListener { controller?.seekToNextMediaItem() }
                previous.setOnClickListener { controller?.seekToPreviousMediaItem() }
            } catch (e: Exception) { description.text = "Medienverbindung fehlgeschlagen: ${e.message}" }
        }, MoreExecutors.directExecutor())
    }
    override fun onDestroy() { controller?.release(); super.onDestroy() }
}
