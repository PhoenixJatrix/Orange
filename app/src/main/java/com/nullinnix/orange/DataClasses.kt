package com.nullinnix.orange

import android.graphics.Bitmap

data class SongData(
    val title: String,
    val path: String,
    val id: String,
    val displayName: String,
    val duration: Long,
    val album: String,
    val genre: String?,
    val artist: String,
    val thumbnail: Bitmap?
)

data class Playlist(
    val name: String,
    val songs: List<String>,
    val id: String,
    val dateCreated: String,
)