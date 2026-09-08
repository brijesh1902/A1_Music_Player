package com.brizzs.a1musicplayer.model

import java.io.Serializable

data class DataModel(
    var title: String? = null,
    var description: String? = null,
    var image: Int = 0
) : Serializable
