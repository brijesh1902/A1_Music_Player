package com.brizzs.a1musicplayer.ui.main

sealed interface MainUiIntent {
    object LoadData : MainUiIntent
    data class SearchQueryChanged(val query: String) : MainUiIntent
    data class SelectSong(val songPath: String) : MainUiIntent
}
