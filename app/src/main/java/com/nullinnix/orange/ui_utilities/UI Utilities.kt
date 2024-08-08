package com.nullinnix.orange.ui_utilities

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullinnix.orange.ADDED_TO_PLAYLIST
import com.nullinnix.orange.CANCEL_SELECTION
import com.nullinnix.orange.CREATED_NEW_PLAYLIST
import com.nullinnix.orange.DELETE_SELECTED
import com.nullinnix.orange.END_SEEKING
import com.nullinnix.orange.LOOPING_ALL
import com.nullinnix.orange.NOT_LOOPING
import com.nullinnix.orange.Playlist
import com.nullinnix.orange.R
import com.nullinnix.orange.REMOVE_FROM_PLAYLIST
import com.nullinnix.orange.SELECT_ALL
import com.nullinnix.orange.SELECT_INTERVAL
import com.nullinnix.orange.START_SEEKING
import com.nullinnix.orange.SongData
import com.nullinnix.orange.misc.corners
import com.nullinnix.orange.misc.durationMillisToStringMinutes
import com.nullinnix.orange.misc.getDate
import com.nullinnix.orange.misc.getPercent
import com.nullinnix.orange.misc.getPercentage
import com.nullinnix.orange.misc.noGleamTaps
import com.nullinnix.orange.misc.screenWidth
import com.nullinnix.orange.misc.translucentColor
import com.nullinnix.orange.song_managing.ALL_SONGS
import com.nullinnix.orange.song_managing.GET
import com.nullinnix.orange.song_managing.LIKED_SONGS
import com.nullinnix.orange.song_managing.MediaPlayerState
import com.nullinnix.orange.song_managing.PlayerClickActions
import com.nullinnix.orange.song_managing.PlayerClickActions.Companion.SHOW_PLAYER
import com.nullinnix.orange.song_managing.PlaylistManager
import com.nullinnix.orange.ui.theme.Black
import com.nullinnix.orange.ui.theme.DarkerGray
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TranslucentBlack)
            .padding(15.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .clip(corners(10.dp))
                .fillMaxWidth()
                .background(White)
        ) {
            Spacer(modifier = Modifier.height(5.dp))

            Text(text = text, modifier = Modifier.padding(horizontal = 5.dp))

            Spacer(modifier = Modifier.height(15.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TranslucentBlack)
            .padding(15.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .clip(corners(10.dp))
                .fillMaxWidth()
                .background(White)
        ) {
            Spacer(modifier = Modifier.height(5.dp))

            Text(text = text, modifier = Modifier.padding(horizontal = 5.dp))

            Spacer(modifier = Modifier.height(15.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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
fun MultiSelectOptions(
    modifier: Modifier = Modifier,
    selectedSongs: List<String>,
    isDefaultPlaylist: Boolean,
    allSelected: Boolean,
    onClick: (Int) -> Unit
) {
    Column(
        modifier = modifier
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

        Spacer(modifier = Modifier.height(40.dp))
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
    ) {
        Text(
            text = label.uppercase(),
            color = if (enabled) White else MildGray,
            fontSize = 12.sp,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
    }
}

@Composable
fun MiniPlayer(
    modifier: Modifier,
    mediaPlayerState: Int,
    allSongsInPlaylist: Map<String, SongData>,
    currentPlaying: SongData,
    currentPlaylist: Playlist,
    currentPosition: Int,
    isLiked: Boolean,
    isShuffling: Boolean,
    onSongEndAction: Int,
    onAction: (Int) -> Unit,
    onSeek: (action: Int, seekPosition: Int?) -> Unit,
    onClose: () -> Unit
) {
    var launched by remember {
        mutableStateOf(false)
    }

    val width by animateDpAsState(targetValue = if(launched) screenWidth().dp else 0.dp, label = "")

    LaunchedEffect(key1 = Unit) {
        launched = true
    }

    Box(modifier = modifier
        .width(width)
        .height(200.dp)
        .background(Opaque)
        .clickable {
            onAction(SHOW_PLAYER)
        }
    ) {

        if (currentPlaying.thumbnail != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(70.dp)
            ) {
                Image(
                    bitmap = currentPlaying.thumbnail.asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )

                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Translucent)
                )
            }
        }

        Column()
        {
            Row(
                Modifier
                    .fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Slider(
                    currentValue = currentPosition,
                    minValue = 0,
                    maxValue = currentPlaying.duration.toInt(),
                    onStartDrag = {
                        onSeek(START_SEEKING, null)
                    },
                    onDragging = {
                        onSeek(START_SEEKING, it)
                    },
                    onStopDrag = {
                        onSeek(END_SEEKING, it)
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp), horizontalArrangement = Arrangement.SpaceAround
            ) {
                Image(
                    painter = painterResource(id = R.drawable.skip_previous),
                    contentDescription = "",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            onAction(PlayerClickActions.SKIP_PREVIOUS)
                        },
                    colorFilter = ColorFilter.tint(White)
                )

                Image(
                    painter = painterResource(id = if (mediaPlayerState == MediaPlayerState.PLAYING) R.drawable.pause else R.drawable.play),
                    contentDescription = "",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            onAction(PlayerClickActions.TOGGLE_PLAY)
                        },
                    colorFilter = ColorFilter.tint(White)
                )

                Image(
                    painter = painterResource(id = R.drawable.skip_next),
                    contentDescription = "",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            onAction(PlayerClickActions.SKIP_NEXT)
                        },
                    colorFilter = ColorFilter.tint(White)
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp), horizontalArrangement = Arrangement.SpaceAround
            ) {
                Image(
                    painter = painterResource(id = R.drawable.shuffle),
                    contentDescription = "",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            onAction(PlayerClickActions.TOGGLE_SHUFFLE)
                        },
                    colorFilter = ColorFilter.tint(White)
                )

                Image(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            onAction(PlayerClickActions.TOGGLE_LIKED)
                        },
                    colorFilter = ColorFilter.tint(if (isLiked) White else Black)
                )

                Image(
                    painter = painterResource(id = if (onSongEndAction == LOOPING_ALL || onSongEndAction == NOT_LOOPING) R.drawable.looping_all else R.drawable.looping_single),
                    contentDescription = "",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            onAction(PlayerClickActions.TOGGLE_SONG_ON_END_ACTION)
                        },
                    colorFilter = ColorFilter.tint(if (onSongEndAction == NOT_LOOPING) Translucent else White)
                )
            }
        }
    }
}

@Composable
fun PlaylistsEditor(
    playlistManager: PlaylistManager,
    isMultiSelecting: Boolean,
    selectedSongs: List<String>,
    onAction: (Int?, String?) -> Unit
) {
    val allPlaylists by remember {
        mutableStateOf(playlistManager.playlists.value!!.toMap())
    }

    var playlistName by remember {
        mutableStateOf("")
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
                text = "Create or add to playlist",
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

@Composable
fun Slider(
    currentValue: Int,
    minValue: Int,
    maxValue: Int,
    onStartDrag: () -> Unit,
    onDragging: (Int) -> Unit,
    onStopDrag: (Int) -> Unit
) {
    val width = screenWidth()

    val sliderWidth = getPercentage(width, 85)

    val density = LocalDensity.current

    var positionPixels by remember {
        mutableFloatStateOf(20f)
    }

    var positionDp by remember {
        mutableStateOf(
            with(density) {
                if (positionPixels.toDp() < 0.dp) 0.dp else if (positionPixels.toDp() > sliderWidth.dp) sliderWidth.dp else positionPixels.toDp()
            }
        )
    }

    val maxOffset = sliderWidth.dp
    val minOffset = 0.dp

    val tickOffset =
        if (positionDp >= maxOffset) maxOffset else if (positionDp <= minOffset) minOffset else positionDp

    var percentage by remember {
        mutableIntStateOf(getPercent(sliderWidth, positionDp.value.toInt()))
    }

    var percentageCovered by remember {
        mutableIntStateOf(if (percentage < 0) minValue else if (percentage >= 100) 100 else if (percentage == 0) minValue else percentage)
    }

    var onDrag by remember {
        mutableStateOf(false)
    }

    val tickSize = if (!onDrag) 12.dp else 15.dp

    val heightAnim by animateDpAsState(
        targetValue = if (onDrag) 15.dp else 7.dp,
        label = "",
        animationSpec = tween(1000)
    )

    val positionAnim by animateDpAsState(
        targetValue = positionDp,
        label = "",
        animationSpec = tween(100)
    )

    val tickOffsetAnim by animateDpAsState(
        targetValue = tickOffset,
        label = "",
        animationSpec = tween(100)
    )

    LaunchedEffect(key1 = currentValue) {
        if (!onDrag) {
            with(density) {
                positionPixels = getPercentage(
                    sliderWidth.dp.toPx().toInt() + tickSize.toPx().toInt(),
                    getPercent(maxValue, currentValue)
                ).toFloat()
                positionDp = with(density) {
                    if (positionPixels.toDp() < 0.dp) 0.dp else if (positionPixels.toDp() > sliderWidth.dp) sliderWidth.dp else positionPixels.toDp()
                }
                percentage = getPercent(sliderWidth, positionDp.value.toInt())
                percentageCovered =
                    if (percentage < 0) minValue else if (percentage >= 100) 100 else if (percentage == 0) minValue else percentage
            }
        }
    }

    Box(
        modifier = Modifier
            .width(sliderWidth.dp)
            .height(45.dp), contentAlignment = Alignment.CenterStart
    ) {
        //Slide bar
        Row(
            Modifier
                .fillMaxWidth()
                .height(heightAnim)
                .clip(corners(90.dp))
                .background(TranslucentOrange)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            positionPixels = it.x
                            positionDp = with(density) {
                                if (positionPixels.toDp() < 0.dp) 0.dp else if (positionPixels.toDp() > sliderWidth.dp) sliderWidth.dp else positionPixels.toDp()
                            }
                            percentage = getPercent(sliderWidth, positionDp.value.toInt())
                            percentageCovered =
                                if (percentage < 0) minValue else if (percentage >= 100) 100 else if (percentage == 0) minValue else percentage

                            onStopDrag(getPercentage(maxValue, percentageCovered))
                        }
                    )
                }
                .pointerInput(1) {
                    detectDragGestures(
                        onDragStart = {

                        },
                        onDragEnd = {
                            onDrag = false
                            onStopDrag(getPercentage(maxValue, percentageCovered))
                        },
                        onDragCancel = {

                        },
                        onDrag = { change, _ ->
                            onStartDrag()
                            positionPixels = change.position.x
                            positionDp = with(density) {
                                if (positionPixels.toDp() < 0.dp) 0.dp else if (positionPixels.toDp() > sliderWidth.dp) sliderWidth.dp else positionPixels.toDp()
                            }
                            percentage = getPercent(sliderWidth, positionDp.value.toInt())
                            percentageCovered =
                                if (percentage < 0) minValue else if (percentage >= 100) 100 else if (percentage == 0) minValue else percentage

                            onDragging(getPercentage(maxValue, percentageCovered))

                            onDrag = true
                        }
                    )
                }
        ) {

        }

        Row(
            Modifier
                .width(positionAnim)
                .height(heightAnim)
                .clip(corners(topStart = 90.dp, bottomStart = 90.dp, topEnd = if(onDrag) 0.dp else 90.dp, bottomEnd = if(onDrag) 0.dp else 90.dp))
                .background(
                    translucentColor(
                        Orange,
                        if (getPercentage(
                                1f,
                                getPercent(maxValue, currentValue).toFloat()
                            ) <= 0.5f
                        ) 0.5f else getPercentage(1f, getPercent(maxValue, currentValue).toFloat())
                    )
                )
        ) {

        }

        Box(
            modifier = Modifier.offset(
                x = (tickOffsetAnim - (tickSize / 2)) - 15.dp,
                y = if (onDrag) (-24).dp else (-25).dp
            )
        ) {
            if (onDrag) {
                Text(
                    text = durationMillisToStringMinutes(
                        getPercentage(
                            maxValue,
                            percentage
                        ).toLong()
                    ), color = White
                )
            }
        }
    }
}