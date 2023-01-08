package com.brizzs.a1musicplayer.utils;

import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.adapters.SongsAdapter;

public class Const {

    public static final String SONG_NAME = "songname";
    public static final String SONG_IMAGE = "image";
    public static final String SONG_ARTIST = "singer";

    public static void CLICKANIMATION(View view){
        view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.anim_item));
    }




    public static void UPDATEVIEW(RecyclerView recyclerView, SongsAdapter adapter){
        recyclerView.post(adapter::notifyDataSetChanged);
    }



}
