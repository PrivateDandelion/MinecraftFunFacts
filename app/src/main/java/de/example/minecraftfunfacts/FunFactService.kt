package de.example.minecraftfunfacts

import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

class FunFactService : MediaLibraryService() {
    private lateinit var player: ExoPlayer
    private lateinit var session: MediaLibrarySession
    private lateinit var facts: Facts
    private var lastId: String? = null
    private val prefs by lazy { getSharedPreferences("settings", MODE_PRIVATE) }

    private fun categories(): List<String> = facts.all.map { it.category }.distinct().sorted()
    private fun categoryItem(name: String): MediaItem = MediaItem.Builder().setMediaId("category:$name")
        .setMediaMetadata(androidx.media3.common.MediaMetadata.Builder().setTitle(if (name == "all") "Alle Fakten" else name).setIsBrowsable(true).setIsPlayable(false).build()).build()

    private fun shuffled(category: String): List<MediaItem> {
        val pool = facts.category(category).shuffled().toMutableList()
        if (pool.size > 1 && pool.first().id == lastId) { val first = pool.removeAt(0); pool.add(first) }
        return pool.map { it.mediaItem() }
    }

    override fun onCreate() {
        super.onCreate()
        facts = Facts(this)
        player = ExoPlayer.Builder(this).build()
        player.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                lastId = mediaItem?.mediaId
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO && !prefs.getBoolean("auto", true)) player.pause()
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED && prefs.getBoolean("auto", true)) {
                    val category = prefs.getString("category", "all") ?: "all"
                    player.setMediaItems(shuffled(category))
                    player.prepare()
                    player.play()
                }
            }
        })
        session = MediaLibrarySession.Builder(this, player, object : MediaLibrarySession.Callback {
            override fun onGetLibraryRoot(session: MediaLibrarySession, browser: MediaSession.ControllerInfo, params: MediaLibraryService.LibraryParams?): ListenableFuture<LibraryResult<MediaItem>> =
                Futures.immediateFuture(LibraryResult.ofItem(categoryItem("root"), params))
            override fun onGetChildren(session: MediaLibrarySession, browser: MediaSession.ControllerInfo, parentId: String, page: Int, pageSize: Int, params: MediaLibraryService.LibraryParams?): ListenableFuture<LibraryResult<com.google.common.collect.ImmutableList<MediaItem>>> {
                val result = when(parentId) {
                    "category:root" -> listOf(categoryItem("all")) + categories().map(::categoryItem)
                    else -> if (parentId.startsWith("category:")) facts.category(parentId.removePrefix("category:")).map { it.mediaItem() } else emptyList()
                }
                return Futures.immediateFuture(LibraryResult.ofItemList(result.drop(page * pageSize).take(pageSize.coerceAtLeast(1)), params))
            }
            override fun onAddMediaItems(session: MediaSession, controller: MediaSession.ControllerInfo, mediaItems: List<MediaItem>): ListenableFuture<List<MediaItem>> {
                val actual = mediaItems.flatMap { requested ->
                    if (requested.mediaId.startsWith("category:")) {
                        val category = requested.mediaId.removePrefix("category:")
                        prefs.edit().putString("category", category).apply()
                        shuffled(category)
                    } else facts.all.firstOrNull { it.id == requested.mediaId }?.let { listOf(it.mediaItem()) } ?: emptyList()
                }
                return Futures.immediateFuture(actual)
            }
        }).build()
        // First-start playlist; Android Auto controls the player via this session.
        player.setMediaItems(shuffled(prefs.getString("category", "all") ?: "all"))
        player.prepare()
    }
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession = session
    override fun onDestroy() { session.release(); player.release(); super.onDestroy() }
}
