package com.nullinnix.orange.ui_utilities

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullinnix.orange.ADDED_TO_PLAYLIST
import com.nullinnix.orange.CREATED_NEW_PLAYLIST
import com.nullinnix.orange.Playlist
import com.nullinnix.orange.misc.corners
import com.nullinnix.orange.misc.getDate
import com.nullinnix.orange.misc.noGleamTaps
import com.nullinnix.orange.misc.screenWidth
import com.nullinnix.orange.song_managing.ALL_SONGS
import com.nullinnix.orange.song_managing.GET
import com.nullinnix.orange.song_managing.LIKED_SONGS
import com.nullinnix.orange.song_managing.PlaylistManager
import com.nullinnix.orange.ui.theme.LighterGray
import com.nullinnix.orange.ui.theme.MildBlack
import com.nullinnix.orange.ui.theme.MildGray
import com.nullinnix.orange.ui.theme.MilderGray
import com.nullinnix.orange.ui.theme.Opaque
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.TranslucentBlack
import com.nullinnix.orange.ui.theme.TranslucentOrange
import com.nullinnix.orange.ui.theme.TranslucentOrangeSemi
import com.nullinnix.orange.ui.theme.Transparent
import com.nullinnix.orange.ui.theme.White

@Composable
fun PlaylistsEditor(
    playlistManager: PlaylistManager,
    isMultiSelecting: Boolean,
    placeHolderName: String,
    onAction: (Int?, String?) -> Unit
) {
    val allPlaylists by remember {
        mutableStateOf(playlistManager.playlists.value!!.toMap())
    }

    var playlistName by remember {
        mutableStateOf(placeHolderName)
    }

    BackHandler {
        onAction(null, null)
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(TranslucentBlack)
            .noGleamTaps {

            }
            .padding(10.dp), contentAlignment = Alignment.Center) {
        Column {
            Text(
                text = "Create a new playlist",
                color = White,
                fontWeight = FontWeight.ExtraBold
            )

            LazyColumn(
                modifier = Modifier
                    .clip(corners(5.dp))
                    .background(MildBlack)
                    .padding(5.dp)

            ) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = playlistName,
                            onValueChange = {
                                if(it.length <= 100){
                                    playlistName = it
                                }
                            },
                            maxLines = 1,
                            placeholder = {
                                Text(text = "playlist name...", color = MildGray)
                            },
                            singleLine = true,
                            shape = corners(10.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = TranslucentOrange,
                                unfocusedIndicatorColor = Transparent,
                                focusedIndicatorColor = Transparent,
                                unfocusedContainerColor = TranslucentOrange
                            ),
                            modifier = Modifier.width(screenWidth().dp - 90.dp),
                            textStyle = TextStyle(color = White, fontSize = 17.sp)
                        )

                        Box(
                            modifier = Modifier
                                .size(55.dp)
                                .clip(corners(10.dp))
                                .background(Orange)
                                .clickable {
                                    onAction(CREATED_NEW_PLAYLIST, playlistName)
                                }, contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Create",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    if (isMultiSelecting) {
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(text = "OR", color = White, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

                        Text(
                            text = "Add to an existing playlist",
                            color = White,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        PlaylistView(
                            playlist = allPlaylists[LIKED_SONGS]!!,
                            isDefaultPlaylist = true,
                            isCurrentPlaylist = playlistManager.lastPlaylist(
                                GET,
                                null
                            ) == LIKED_SONGS
                        ) {
                            onAction(ADDED_TO_PLAYLIST, LIKED_SONGS)
                        }

                        if (allPlaylists.size > 2) {
                            Spacer(modifier = Modifier.height(15.dp))
                        }

                        for (playlist in allPlaylists.values) {
                            if (!listOf(ALL_SONGS, LIKED_SONGS).contains(playlist.id)) {
                                PlaylistView(
                                    playlist = playlist,
                                    isDefaultPlaylist = false,
                                    isCurrentPlaylist = playlistManager.lastPlaylist(
                                        GET,
                                        null
                                    ) == playlist.id
                                ) {
                                    onAction(ADDED_TO_PLAYLIST, it)
                                }

                                Spacer(modifier = Modifier.height(5.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistView(
    playlist: Playlist,
    isDefaultPlaylist: Boolean,
    isCurrentPlaylist: Boolean,
    onAddSongs: (String) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(corners(10.dp))
            .background(if (isDefaultPlaylist) TranslucentOrangeSemi else if (isCurrentPlaylist) Opaque else TranslucentOrange)
            .clickable(!isCurrentPlaylist) {
                onAddSongs(playlist.id)
            }
            .padding(5.dp)
    ) {
        Text(
            text = if (playlist.name.length > 1) playlist.name[0].uppercase() + playlist.name.substring(
                1
            ) else playlist.name.uppercase() + ":",
            color = if (!isCurrentPlaylist) White else MilderGray,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
        )

        Row(Modifier.padding(start = 15.dp)) {
            Column {
                Text(
                    text = "Number of songs: ${playlist.songs.size}",
                    color = if (!isCurrentPlaylist) LighterGray else MilderGray,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Date Created: ${if (playlist.dateCreated == getDate()) "Today" else playlist.dateCreated}",
                    fontWeight = FontWeight.Medium,
                    color = if (!isCurrentPlaylist) LighterGray else MilderGray
                )
            }
        }
    }
}