package com.nullinnix.orange.ui_utilities

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullinnix.orange.Playlist
import com.nullinnix.orange.R
import com.nullinnix.orange.SongData
import com.nullinnix.orange.misc.corners
import com.nullinnix.orange.misc.durationSecondsToDays
import com.nullinnix.orange.misc.getAnyAvailableAlbumCover
import com.nullinnix.orange.misc.getPercentage
import com.nullinnix.orange.misc.noGleamTaps
import com.nullinnix.orange.misc.screenWidth
import com.nullinnix.orange.song_managing.PlaylistManager
import com.nullinnix.orange.song_managing.getSongDataFromIDs
import com.nullinnix.orange.ui.theme.LighterGray
import com.nullinnix.orange.ui.theme.MildGray
import com.nullinnix.orange.ui.theme.MildTranslucentBlack
import com.nullinnix.orange.ui.theme.NinetyTranslucentBlack
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.Red
import com.nullinnix.orange.ui.theme.Translucent
import com.nullinnix.orange.ui.theme.TranslucentBlack
import com.nullinnix.orange.ui.theme.TranslucentOrange
import com.nullinnix.orange.ui.theme.TranslucentOrangeSemi
import com.nullinnix.orange.ui.theme.Transparent
import com.nullinnix.orange.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TopAppBar(
    currentPlaylist: String,
    playlists: Map<String, Playlist>,
    modifier: Modifier,
    isMultiSelecting: Boolean,
    selectedSongs: List<String>,
    numberOfSongsInPlaylist: Int,
    isSearching: Boolean,
    allDeviceSongs: Map<String, SongData>,
    onPlaylistChanged: (String) -> Unit,
    playlistManager: PlaylistManager,
    onCreatePlaylist: () -> Unit,
    onDeletePlaylist: (String) -> Unit,
    onSearchParametersUpdate: (String?) -> Unit,
    isSearchingUpdated: (Boolean) -> Unit,
    onSettings: () -> Unit,
    onShowAnalytics: () -> Unit
) {
    var showingPlaylists by remember {
        mutableStateOf(false)
    }

    var toDelete by remember {
        mutableStateOf("")
    }

    var searchParameter by remember {
        mutableStateOf("")
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    val playlistButtonRotationAnim by animateFloatAsState(
        targetValue = if (isSearching) 45f else 0f,
        label = "",
        animationSpec = tween(1500)
    )

    val focusRequester by remember {
        mutableStateOf(FocusRequester())
    }

    LaunchedEffect(key1 = showingPlaylists) {
        if (!showingPlaylists) {
            toDelete = ""
        }
    }

    BackHandler(showingPlaylists || isSearching) {
        if (toDelete != "") {
            toDelete = ""
        } else {
            showingPlaylists = false
        }

        if (isSearching) {
            onSearchParametersUpdate(null)
            isSearchingUpdated(false)
            searchParameter = ""
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (showingPlaylists) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(if (showingPlaylists) TranslucentBlack else Transparent)
                    .noGleamTaps(showingPlaylists) {
                        showingPlaylists = false
                    }
            )
        }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(if (isMultiSelecting && isSearching) 140.dp else if (isSearching) 110.dp else 85.dp)
                .noGleamTaps {

                }
                .background(
                    Color(
                        TranslucentBlack.red,
                        TranslucentBlack.green,
                        TranslucentBlack.blue,
                        0.8f
                    )
                )
                .blur(5.dp),
            contentAlignment = Alignment.Center
        ) {

        }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 45.dp)
                .noGleamTaps {

                },
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clip(corners(10.dp))
                    .background(if (showingPlaylists) NinetyTranslucentBlack else Transparent)
                    .noGleamTaps {

                    }
                    .padding(
                        start = 5.dp,
                        top = if (showingPlaylists) 5.dp else 0.dp,
                        end = 5.dp,
                        bottom = 5.dp
                    )
                    .animateContentSize()
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!isMultiSelecting && !isSearching) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                                    .clip(corners())
                                    .background(Translucent)
                                    .clickable {
                                        showingPlaylists = !showingPlaylists
                                    }
                                    .padding(vertical = 5.dp, horizontal = 10.dp)
                            ) {
                                Text(
                                    text = "Playlists",
                                    color = White,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Spacer(modifier = Modifier.width(5.dp))

                                Image(
                                    painter = painterResource(id = R.drawable.playlist),
                                    contentDescription = "",
                                    colorFilter = ColorFilter.tint(Orange),
                                    modifier = Modifier
                                        .size(20.dp)
                                )
                            }
                        }
                    }

                    if (showingPlaylists) {
                        for (playlist in playlists.values) {
                            Box(modifier = Modifier
                                .width(getPercentage(screenWidth(), 80).dp)
                                .height(110.dp)
                                .clip(corners(15.dp))
                                .border(
                                    color = if (currentPlaylist == playlist.id) Orange else Transparent,
                                    width = if (currentPlaylist == playlist.id) 3.dp else 0.dp,
                                    shape = corners(15.dp)
                                )
                                .clickable {
                                    if (currentPlaylist != playlist.id) {
                                        onPlaylistChanged(playlist.id)
                                        showingPlaylists = false
                                    }
                                }
                            ) {
                                if (getAnyAvailableAlbumCover(
                                        getSongDataFromIDs(
                                            playlist.songs,
                                            allDeviceSongs = allDeviceSongs
                                        ).values.toList(), context = LocalContext.current
                                    ) != null
                                ) {
                                    Image(
                                        bitmap = getAnyAvailableAlbumCover(
                                            getSongDataFromIDs(
                                                playlist.songs,
                                                allDeviceSongs = allDeviceSongs
                                            ).values.toList(), context = LocalContext.current
                                        )!!.asImageBitmap(),
                                        contentDescription = "",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .blur(100.dp)
                                            .background(MildTranslucentBlack)
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(TranslucentOrangeSemi)
                                            .padding(5.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (playlist.name.length > 1) playlist.name[0].uppercase() + playlist.name.substring(
                                                1
                                            ) else playlist.name.uppercase(),
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if(playlist.id == currentPlaylist) White else LighterGray,
                                            fontSize = 18.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier
                                                .fillMaxWidth(0.9f)
                                        )

                                        if (playlist.deletable && toDelete != playlist.id) {
                                            Image(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "",
                                                colorFilter = ColorFilter.tint(LighterGray),
                                                modifier = Modifier
                                                    .clip(corners(5.dp))
                                                    .background(Translucent)
                                                    .clickable {
                                                        toDelete = playlist.id
                                                        showDeleteDialog = true
                                                    }
                                            )
                                        }
                                    }

                                    if (toDelete != playlist.id) {
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "Number of songs: ${playlist.songs.size} ${if (playlist.songs.size == 1) "song" else "songs"}",
                                            color = if(playlist.id == currentPlaylist) White else LighterGray,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            modifier = Modifier.padding(horizontal = 3.dp)
                                        )
                                    }

                                    Text(
                                        text = "Duration: ${
                                            durationSecondsToDays(
                                                playlistManager.getPlaylistDuration(
                                                    getSongDataFromIDs(
                                                        playlist.songs,
                                                        allDeviceSongs = allDeviceSongs
                                                    ).values.toList()
                                                ).toInt() / 1000
                                            )
                                        }",
                                        color = if(playlist.id == currentPlaylist) White else LighterGray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        modifier = Modifier.padding(horizontal = 3.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopEnd)
                    .clip(corners())
                    .padding(3.dp)
                    .animateContentSize(animationSpec = tween(150))
            ) {
                if (isSearching) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = searchParameter,
                            onValueChange = {
                                searchParameter = it
                                onSearchParametersUpdate(searchParameter)
                            },
                            maxLines = 1,
                            placeholder = {
                                Text(
                                    text = "Search for a song, artist or album...",
                                    color = MildGray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            singleLine = true,
                            shape = corners(15.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Translucent,
                                unfocusedIndicatorColor = Transparent,
                                focusedIndicatorColor = Transparent,
                                unfocusedContainerColor = Translucent
                            ),
                            modifier = Modifier
                                .width(screenWidth().dp - 50.dp)
                                .focusRequester(focusRequester),
                            textStyle = TextStyle(color = White, fontSize = 17.sp)
                        )

                        Image(
                            imageVector = Icons.Default.Add,
                            contentDescription = "",
                            modifier = Modifier
                                .size(28.dp)
                                .clip(corners(90.dp))
                                .background(Translucent)
                                .rotate(playlistButtonRotationAnim)
                                .clickable {
                                    onSearchParametersUpdate(null)
                                    isSearchingUpdated(false)
                                    searchParameter = ""
                                },
                            colorFilter = ColorFilter.tint(Red)
                        )
                    }
                }

                if (isMultiSelecting) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.End)
                            .clip(corners(90.dp))
                            .padding(3.dp), horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = White, fontSize = 16.sp)) {
                                    append(selectedSongs.size.toString())
                                }

                                withStyle(SpanStyle(color = Orange, fontSize = 18.sp)) {
                                    append(" / ")
                                }

                                append(numberOfSongsInPlaylist.toString())
                            },
                            fontWeight = FontWeight.ExtraBold,
                            color = White,
                            textAlign = TextAlign.Center
                        )

                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(corners())
                                .background(Translucent)
                                .clickable {
                                    onCreatePlaylist()
                                }
                        ) {
                            Text(
                                text = "Create or add to Playlist",
                                color = White,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(start = 3.dp)
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Image(
                                imageVector = Icons.Default.Add,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(25.dp)
                                    .clip(corners(90.dp))
                                    .background(Translucent),
                                colorFilter = ColorFilter.tint(White)
                            )
                        }
                    }
                }

                if (!isSearching && !isMultiSelecting) {
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            Modifier
                                .clip(corners(90.dp))
                                .background(Translucent)
                                .padding(3.dp), horizontalArrangement = Arrangement.End
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        isSearchingUpdated(true)
                                        delay(500)
                                        focusRequester.requestFocus()
                                    }
                                }
                            ) {
                                Text(
                                    text = "Search",
                                    color = White,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(start = 3.dp)
                                )

                                Image(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "",
                                    modifier = Modifier.size(23.dp),
                                    colorFilter = ColorFilter.tint(White)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(corners())
                                    .background(Translucent)
                                    .clickable {
                                        onCreatePlaylist()
                                    }
                            ) {
                                Image(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(25.dp)
                                        .rotate(playlistButtonRotationAnim),
                                    colorFilter = ColorFilter.tint(Orange)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))


                        //analytics
                        Image(painter = painterResource(id = R.drawable.analytics), contentDescription = "", modifier = Modifier.clickable {
                            onShowAnalytics()
                        })


                        Spacer(modifier = Modifier.width(5.dp))

                        Image(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "",
                            modifier = Modifier
                                .clip(corners(90.dp))
                                .background(Translucent)
                                .padding(3.dp)
                                .size(25.dp)
                                .clickable {
                                    onSettings()
                                },
                            colorFilter = ColorFilter.tint(Orange)
                        )
                    }
                }
            }
        }

        if (showDeleteDialog) {
            Dialog(
                text = "Delete ${playlists[toDelete]?.name}?",
                negativeHint = "Cancel",
                positiveHint = "Delete"
            ) {
                if (it) {
                    onDeletePlaylist(toDelete)
                    showDeleteDialog = false
                } else {
                    toDelete = ""
                    showDeleteDialog = false
                }
            }
        }
    }
}