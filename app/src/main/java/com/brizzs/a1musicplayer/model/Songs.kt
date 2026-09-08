package com.brizzs.a1musicplayer.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "songs")
data class Songs(
    @NonNull
    @PrimaryKey
    var name: String = "",
    var artist: String? = null,
    var image: String? = null,
    var duration: String? = null,
    var data: String? = null,
    var date: String? = null,
    var album: String? = null,
    var albumKey: String? = null
) : Serializable {
    @Ignore
    constructor() : this("")
}
