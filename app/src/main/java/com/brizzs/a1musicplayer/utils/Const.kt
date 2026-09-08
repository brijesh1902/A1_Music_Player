package com.brizzs.a1musicplayer.utils

import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.brizzs.a1musicplayer.R
import com.brizzs.a1musicplayer.adapters.SongsAdapter

object Const {
    const val SONG_NAME = "songname"
    const val SONG_IMAGE = "image"
    const val SONG_ARTIST = "singer"

    fun CLICKANIMATION(view: View) {
        view.startAnimation(AnimationUtils.loadAnimation(view.context, R.anim.anim_item))
    }

    fun UPDATEVIEW(recyclerView: RecyclerView, adapter: SongsAdapter?) {
        if (adapter != null) {
            recyclerView.post { adapter.notifyDataSetChanged() }
        }
    }
}
