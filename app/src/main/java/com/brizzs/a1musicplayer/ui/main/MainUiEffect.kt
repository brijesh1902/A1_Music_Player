package com.brizzs.a1musicplayer.ui.main

sealed interface MainUiEffect {
    data class ShowToast(val message: String) : MainUiEffect
    data class NavigateToPlayer(val songPath: String) : MainUiEffect
}
