package com.brizzs.a1musicplayer.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "artist_table", indices = [Index(value = ["id"], unique = true)])
data class Artist(
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = "",
    var songName: String? = null,
    var artist: String? = null,
    var image: String? = null
) {
    @Ignore
    constructor() : this("")
}
