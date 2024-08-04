package com.nullinnix.orange.ui_utilities

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullinnix.orange.ADDED_TO_PLAYLIST
import com.nullinnix.orange.CANCEL_SELECTION
import com.nullinnix.orange.CREATED_NEW_PLAYLIST
import com.nullinnix.orange.DELETE_SELECTED
import com.nullinnix.orange.Playlist
import com.nullinnix.orange.REMOVE_FROM_PLAYLIST
import com.nullinnix.orange.SELECT_ALL
import com.nullinnix.orange.SELECT_INTERVAL
import com.nullinnix.orange.SongData
import com.nullinnix.orange.misc.corners
import com.nullinnix.orange.misc.getDate
import com.nullinnix.orange.misc.noGleamTaps
import com.nullinnix.orange.misc.screenWidth
import com.nullinnix.orange.song_managing.ALL_SONGS
import com.nullinnix.orange.song_managing.GET
import com.nullinnix.orange.song_managing.LIKED_SONGS
import com.nullinnix.orange.song_managing.PlaylistManager
import com.nullinnix.orange.ui.theme.DarkerGray
import com.nullinnix.orange.ui.theme.GrayLight
import com.nullinnix.orange.ui.theme.LighterGray
import com.nullinnix.orange.ui.theme.MildBlack
import com.nullinnix.orange.ui.theme.MildGray
import com.nullinnix.orange.ui.theme.MilderGray
import com.nullinnix.orange.ui.theme.Opaque
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.Translucent
import com.nullinnix.orange.ui.theme.TranslucentBlack
import com.nullinnix.orange.ui.theme.TranslucentOrange
import com.nullinnix.orange.ui.theme.TranslucentOrangeSemi
import com.nullinnix.orange.ui.theme.Transparent
import com.nullinnix.orange.ui.theme.White

@Composable
fun Dialog(
    text: AnnotatedString,
    negativeHint: String,
    positiveHint: String,
    onAction: (Boolean) -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(TranslucentBlack)
        .padding(15.dp),
        contentAlignment = Alignment.Center
    ){
        Column(modifier = Modifier
            .clip(corners(10.dp))
            .fillMaxWidth()
            .background(White)
        ) {
            Spacer(modifier = Modifier.height(5.dp))

            Text(text = text, modifier = Modifier.padding(horizontal = 5.dp))

            Spacer(modifier = Modifier.height(15.dp))

            Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(
                    modifier = Modifier
                        .width(((screenWidth() - 30) / 2).dp)
                        .height(40.dp)
                        .background(DarkerGray)
                        .clickable {
                            onAction(false)
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = negativeHint,
                        color = LighterGray,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .width(((screenWidth() - 30) / 2).dp)
                        .height(40.dp)
                        .background(Orange)
                        .clickable {
                            onAction(true)
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = positiveHint,
                        color = White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun Dialog(
    text: String,
    negativeHint: String,
    positiveHint: String,
    onAction: (Boolean) -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(TranslucentBlack)
        .padding(15.dp),
        contentAlignment = Alignment.Center
    ){
        Column(modifier = Modifier
            .clip(corners(10.dp))
            .fillMaxWidth()
            .background(White)
        ) {
            Spacer(modifier = Modifier.height(5.dp))

            Text(text = text, modifier = Modifier.padding(horizontal = 5.dp))

            Spacer(modifier = Modifier.height(15.dp))

            Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(
                    modifier = Modifier
                        .width(((screenWidth() - 30) / 2).dp)
                        .height(40.dp)
                        .background(DarkerGray)
                        .clickable {
                            onAction(false)
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = negativeHint,
                        color = LighterGray,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .width(((screenWidth() - 30) / 2).dp)
                        .height(40.dp)
                        .background(Orange)
                        .clickable {
                            onAction(true)
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = positiveHint,
                        color = White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MultiSelectOptions(modifier: Modifier = Modifier, selectedSongs: List<String>, isDefaultPlaylist: Boolean, allSelected: Boolean, onClick: (Int) -> Unit) {
    Column(
        modifier = modifier
            .height(90.dp)
            .background(TranslucentBlack)
            .padding(5.dp)
            .noGleamTaps {

            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            MultiSelectItem(
                label = "Select ALl",
                enabled = !allSelected
            ) {
                onClick(SELECT_ALL)
            }

            MultiSelectItem(
                label = "Select Interval",
                enabled = selectedSongs.size >= 2 && !allSelected
            ) {
                onClick(SELECT_INTERVAL)
            }

            MultiSelectItem(label = "Cancel") {
                onClick(CANCEL_SELECTION)
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            MultiSelectItem(
                label = "Delete selected"
            ) {
                onClick(DELETE_SELECTED)
            }

            if (!isDefaultPlaylist) {
                MultiSelectItem(label = "Remove From Playlist") {
                    onClick(REMOVE_FROM_PLAYLIST)
                }
            }
        }
    }
}

@Composable
fun MultiSelectItem(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    val width = (screenWidth() - 20) / 3

    Box(modifier = Modifier
        .width(width.dp)
        .clip(corners(5.dp))
        .background(if (enabled) TranslucentOrange else Translucent)
        .clickable(enabled) {
            onClick()
        }
        .padding(3.dp),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = label.uppercase(),
            color = if(enabled) White else MildGray,
            fontSize = 12.sp,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
    }
}

@Composable
fun MiniPlayer(songData: SongData, onAction: (Int) -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(White)
        .height(90.dp)
    ){
        if(songData.thumbnail != null){
            Box (
                Modifier
                    .fillMaxSize()
                    .blur(1.dp), contentAlignment = Alignment.Center){
                Image(bitmap = songData.thumbnail.asImageBitmap(), contentDescription = "", contentScale = ContentScale.FillWidth)
            }
        }

        Row (Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround){
            Image(imageVector = Icons.Rounded.Send, contentDescription = "")
            Image(imageVector = Icons.Rounded.Send, contentDescription = "")
            Image(imageVector = Icons.Rounded.Send, contentDescription = "")
        }
    }
}

@Composable
fun PlaylistsEditor(playlistManager: PlaylistManager, isMultiSelecting: Boolean, selectedSongs: List<String>, onAction: (Int?, String?) -> Unit) {
    val allPlaylists by remember {
        mutableStateOf(playlistManager.playlists.value!!.toMap())
    }

    var playlistName by remember {
        mutableStateOf("")
    }

    BackHandler {
        onAction(null, null)
    }

    Box (
        Modifier
            .fillMaxSize()
            .background(TranslucentBlack)
            .noGleamTaps {

            }
            .padding(10.dp), contentAlignment = Alignment.Center){
        Column {
            Text(text = "Create or add to playlist", color = White, fontWeight = FontWeight.ExtraBold)

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
                                playlistName = it
                            },
                            maxLines = 1,
                            placeholder = {
                                Text(text = "Create a new playlist...", color = MildGray)
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
                        PlaylistView(playlist = allPlaylists[LIKED_SONGS]!!, isDefaultPlaylist = true, isCurrentPlaylist = playlistManager.lastPlaylist(GET, null) == LIKED_SONGS){
                            onAction(ADDED_TO_PLAYLIST, LIKED_SONGS)
                        }

                        if(allPlaylists.size > 2){
                            Spacer(modifier = Modifier.height(15.dp))
                        }

                        for (playlist in allPlaylists.values) {
                            if (!listOf(ALL_SONGS, LIKED_SONGS).contains(playlist.id)) {
                                PlaylistView(playlist = playlist, isDefaultPlaylist = false, isCurrentPlaylist = playlistManager.lastPlaylist(GET, null) == playlist.id){
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
fun PlaylistView(playlist: Playlist, isDefaultPlaylist: Boolean, isCurrentPlaylist: Boolean, onAddSongs: (String) -> Unit) {
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
        Text(text = if(playlist.name.length > 1) playlist.name[0].uppercase() + playlist.name.substring(1) else playlist.name.uppercase() + ":", color = if(!isCurrentPlaylist) White else MilderGray, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)

        Row(Modifier.padding(start = 15.dp)) {
            Column {
                Text(
                    text = "Number of songs: ${playlist.songs.size}",
                    color = if(!isCurrentPlaylist)LighterGray else MilderGray,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Date Created: ${if(playlist.dateCreated == getDate()) "Today" else playlist.dateCreated}",
                    fontWeight = FontWeight.Medium,
                    color = if(!isCurrentPlaylist)LighterGray else MilderGray
                )
            }
        }
    }
}