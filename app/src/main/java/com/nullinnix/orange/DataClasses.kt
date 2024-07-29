package com.nullinnix.orange

data class SongData(
    val title: String,
    val path: String,
    val id: String,
    val displayName: String,
    val duration: Long,
    val album: String,
    val genre: String?,
    val artist: String
)