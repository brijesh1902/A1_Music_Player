package com.brizzs.a1musicplayer.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "playlist", indices = [Index(value = ["id"], unique = true)])
data class PlayList(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    var id: String = "",
    var name: String? = null,
    var image1: String? = null,
    var image2: String? = null,
    var image3: String? = null,
    var image4: String? = null
) : Serializable {
    @Ignore
    constructor() : this("")
}
