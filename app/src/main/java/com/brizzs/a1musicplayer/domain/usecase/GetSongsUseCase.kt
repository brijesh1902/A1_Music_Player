package com.brizzs.a1musicplayer.domain.usecase

import com.brizzs.a1musicplayer.model.Songs
import com.brizzs.a1musicplayer.ui.main.MainRepo

class GetSongsUseCase(private val repo: MainRepo) {
    fun invoke(): List<Songs> {
        val rawSongs = repo.getSongs().value ?: emptyList()
        return rawSongs.filter { song ->
            val dataPath = song.data
            dataPath != null && dataPath.endsWith(".mp3")
        }
    }
}
