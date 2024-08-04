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
    val deletable: Boolean
)

const val PLAY_SONG = 0
const val MULTI_SELECT_SONGS = 1

const val DELETE_SELECTED = -1
const val REMOVE_FROM_PLAYLIST = 0
const val SELECT_INTERVAL = 1
const val CANCEL_SELECTION = 2
const val SELECT_ALL = 3

const val ADDED_TO_PLAYLIST = 0
const val CREATED_NEW_PLAYLIST = 1