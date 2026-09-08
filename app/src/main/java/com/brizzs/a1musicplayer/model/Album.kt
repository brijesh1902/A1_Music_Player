package com.brizzs.a1musicplayer.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "album_table", indices = [Index(value = ["id"], unique = true)])
data class Album(
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = "",
    var album: String? = null,
    var artist: String? = null,
    var image: String? = null
) : Serializable {
    @Ignore
    constructor() : this("")
}
