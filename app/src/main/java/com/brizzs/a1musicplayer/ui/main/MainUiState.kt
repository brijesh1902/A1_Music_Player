package com.brizzs.a1musicplayer.ui.main

import com.brizzs.a1musicplayer.model.Album
import com.brizzs.a1musicplayer.model.Artist
import com.brizzs.a1musicplayer.model.Songs

data class MainUiState(
    val isLoading: Boolean = false,
    val songs: List<Songs> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null
)
