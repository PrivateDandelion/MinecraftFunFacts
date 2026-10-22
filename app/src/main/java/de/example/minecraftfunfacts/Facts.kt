package de.example.minecraftfunfacts

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import org.json.JSONArray

data class Fact(val id: String, val category: String, val title: String, val text: String, val audio: String) {
    fun mediaItem(): MediaItem = MediaItem.Builder()
        .setMediaId(id)
        .setUri("asset:///" + audio)
        .setMediaMetadata(MediaMetadata.Builder()
            .setTitle(title).setArtist("Minecraft Fun Facts · " + category)
            .setDescription(text).setIsPlayable(true).build())
        .build()
}

class Facts(context: Context) {
    val all: List<Fact> = JSONArray(context.assets.open("facts.json").bufferedReader().use { it.readText() }).let { data ->
        (0 until data.length()).map { i ->
            val f = data.getJSONObject(i)
            Fact(f.getString("id"), f.getString("category"), f.getString("title"), f.getString("text"), f.getString("audio"))
        }
    }
    fun category(name: String): List<Fact> = if (name == "all") all else all.filter { it.category == name }
}
