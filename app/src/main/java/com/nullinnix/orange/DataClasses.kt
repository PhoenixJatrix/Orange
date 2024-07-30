package com.nullinnix.orange

import android.graphics.Bitmap

data class SongData(
    val title: String,
    val path: String,
    val id: String,
    val duration: Long,
    val album: String,
    val genre: String?,
    val artist: String,
    val thumbnail: Bitmap?
)