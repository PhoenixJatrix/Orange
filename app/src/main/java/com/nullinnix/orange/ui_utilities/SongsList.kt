package com.nullinnix.orange.ui_utilities

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullinnix.orange.MULTI_SELECT_SONGS
import com.nullinnix.orange.PLAY_SONG
import com.nullinnix.orange.Playlist
import com.nullinnix.orange.SongData
import com.nullinnix.orange.VIEWING_ALBUMS
import com.nullinnix.orange.VIEWING_ALL
import com.nullinnix.orange.VIEWING_ARTISTS
import com.nullinnix.orange.VIEWING_GENRE
import com.nullinnix.orange.misc.corners
import com.nullinnix.orange.misc.durationMillisToStringMinutes
import com.nullinnix.orange.misc.getAnyAvailableAlbumCover
import com.nullinnix.orange.misc.getPercentage
import com.nullinnix.orange.misc.noGleamTaps
import com.nullinnix.orange.misc.screenWidth
import com.nullinnix.orange.misc.sortBy
import com.nullinnix.orange.song_managing.ALL_SONGS
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.PLAYING
import com.nullinnix.orange.song_managing.getSongDataFromIDs
import com.nullinnix.orange.ui.theme.Black
import com.nullinnix.orange.ui.theme.LighterGray
import com.nullinnix.orange.ui.theme.MildGray
import com.nullinnix.orange.ui.theme.MildTranslucentOrange
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.Red
import com.nullinnix.orange.ui.theme.Translucent
import com.nullinnix.orange.ui.theme.TranslucentOrange
import com.nullinnix.orange.ui.theme.TranslucentOrangeSemi
import com.nullinnix.orange.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun SongsList(
    songIDs: List<String>,
    allDeviceSongs: Map<String, SongData>,
    currentSong: SongData?,
    currentPosition: Int,
    isMiniPlayerVisible: Boolean,
    isMultiSelecting: Boolean,
    selectedSongs: List<String>,
    isSearching: Boolean,
    currentPlaylist: Playlist,
    currentSongSortType: Int,
    mediaPlayerState: Int,
    onAction: (action:Int, id:String, openPlayer:Boolean) -> Unit,
    onSort: (Int, List<String>?) -> Unit
) {
    var currentSortList by remember {
        mutableStateOf(mapOf<String, List<String>>())
    }

    var showSortList by remember {
        mutableStateOf(false)
    }

    val sortBy = { sortType: Int ->
        currentSortList =
            sortBy(
                sortType, getSongDataFromIDs(currentPlaylist.songs, allDeviceSongs)
            )

        Log.d("", "SongsList: $sortType")

        showSortList = true
    }

    BackHandler(currentSongSortType != VIEWING_ALL) {
        onSort(VIEWING_ALL, null)
        sortBy(VIEWING_ALL)
    }

    val lazyListState = rememberLazyListState()
    val songsLazyListState = rememberLazyListState()

    val coroutine = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(5.dp), state = songsLazyListState
        ) {
            items(songIDs.size) { index ->
                if(allDeviceSongs[songIDs[index]] != null) {
                    if(index == 0){
                        Spacer(modifier = Modifier.height(if (isMultiSelecting && isSearching) 150.dp else if (isSearching) 120.dp else if(isMultiSelecting) 100.dp else 135.dp))
                    }

                    SongView(
                        songData = allDeviceSongs[songIDs[index]]!!,
                        currentSong = currentSong,
                        currentPosition = currentPosition,
                        id = index,
                        isMultiSelecting = isMultiSelecting,
                        isSelected = selectedSongs.contains(songIDs[index]),
                        mediaPlayerState = mediaPlayerState
                    ) { action, id, openPlayer ->
                        onAction(action, id, openPlayer)
                    }

                    if (index != songIDs.size - 1) {
                        Spacer(modifier = Modifier.height(5.dp))
                    }

                    if (index == songIDs.size - 1) {
                        Spacer(modifier = Modifier.height(if (isMultiSelecting) 120.dp else if(isMiniPlayerVisible) 220.dp else 70.dp))
                    }
                }
            }
        }

        Column (Modifier.fillMaxSize()){
            if(!isSearching && !isMultiSelecting) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .noGleamTaps {

                        }
                        .padding(top = 85.dp)
                        .background(
                            Color(
                                Black.red,
                                Black.green,
                                Black.blue,
                                0.8f
                            )
                        )
                        .padding(bottom = 10.dp), horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SongsViewSorter(
                        currentSelected = currentSongSortType,
                        id = VIEWING_ALL,
                        label = currentPlaylist.name.uppercase()
                    ) {
                        onSort(VIEWING_ALL, null)
                        sortBy(VIEWING_ALL)
                    }

                    SongsViewSorter(
                        currentSelected = currentSongSortType,
                        id = VIEWING_GENRE,
                        label = "GENRE"
                    ) {
                        onSort(VIEWING_GENRE, null)
                        sortBy(VIEWING_GENRE)

                        if(currentSortList.isNotEmpty()){
                            coroutine.launch {
                                lazyListState.scrollToItem(0)
                            }
                        }
                    }

                    SongsViewSorter(
                        currentSelected = currentSongSortType,
                        id = VIEWING_ALBUMS,
                        label = "ALBUMS"
                    ) {
                        onSort(VIEWING_ALBUMS, null)
                        sortBy(VIEWING_ALBUMS)

                        if(currentSortList.isNotEmpty()){
                            coroutine.launch {
                                lazyListState.scrollToItem(0)
                            }
                        }
                    }

                    SongsViewSorter(
                        currentSelected = currentSongSortType,
                        id = VIEWING_ARTISTS,
                        label = "ARTISTS"
                    ) {
                        onSort(VIEWING_ARTISTS, null)
                        sortBy(VIEWING_ARTISTS)

                        if(currentSortList.isNotEmpty()){
                            coroutine.launch {
                                lazyListState.scrollToItem(0)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        if(showSortList && currentSongSortType != VIEWING_ALL){
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .animateContentSize()
                    .padding(top = if (isMultiSelecting && isSearching) 180.dp else if (isSearching) 150.dp else 125.dp)
                    .noGleamTaps {

                    }
                    .clip(
                        RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                    )
                    .background(
                        Color(
                            Black.red,
                            Black.green,
                            Black.blue,
                            0.91f
                        )
                    ), horizontalAlignment = Alignment.CenterHorizontally, state = lazyListState
            ) {
                item {
                    Spacer(modifier = Modifier.height(15.dp))

                    if (currentSortList.isNotEmpty()) {
                        for (sortedType in currentSortList) {
                            Row(modifier = Modifier
                                .height(150.dp)
                                .clip(corners(15.dp))
                                .background(Translucent)
                                .clickable {
                                    onSort(currentSongSortType, sortedType.value)
                                    showSortList = false
                                }
                            ) {
                                Box(modifier = Modifier
                                    .width(80.dp)
                                    .background(Red)) {
                                    val availableThumbnail = getAnyAvailableAlbumCover(
                                        allSongsInPlaylist = getSongDataFromIDs(
                                            allPlaylistSongs = sortedType.value,
                                            allDeviceSongs = allDeviceSongs
                                        ).values.toList(), context = LocalContext.current
                                    )

                                    if (availableThumbnail != null) {
                                        Image(
                                            bitmap = availableThumbnail.asImageBitmap(),
                                            contentDescription = "",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                        .padding(start = 3.dp), verticalArrangement = Arrangement.SpaceAround
                                ) {
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)) {
                                                append("Number of songs ".uppercase() + if(currentSongSortType == VIEWING_GENRE) "in Genre:".uppercase() else if(currentSongSortType == VIEWING_ALBUMS) "in Album:".uppercase() else "by Artist:".uppercase())
                                            }

                                            append(" " + sortedType.value.size)
                                        },
                                        color = White, overflow = TextOverflow.Ellipsis, fontSize = 14.sp
                                    )

                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)) {
                                                append(if(currentSongSortType == VIEWING_GENRE) "GENRE:" else if(currentSongSortType == VIEWING_ALBUMS) "ALBUM:" else "ARTIST:")
                                            }

                                            append(" " + sortedType.key)
                                        },
                                        color = White, overflow = TextOverflow.Ellipsis, fontSize = 14.sp
                                    )

                                    Text(text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)) {
                                            append("SONGS: ")
                                        }

                                        when (sortedType.value.size) {
                                            1 -> {
                                                append(allDeviceSongs[sortedType.value[0]]!!.title)
                                            }

                                            2 -> {
                                                append(allDeviceSongs[sortedType.value[0]]!!.title)
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                                                    append(" and ")
                                                }
                                                append(allDeviceSongs[sortedType.value[1]]!!.title)
                                            }

                                            3 -> {
                                                append(allDeviceSongs[sortedType.value[0]]!!.title + ", ")
                                                append(allDeviceSongs[sortedType.value[1]]!!.title)
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                                                    append(" and ")
                                                }
                                                append(allDeviceSongs[sortedType.value[2]]!!.title)
                                            }

                                            else -> {
                                                append(allDeviceSongs[sortedType.value[0]]!!.title + ", ")
                                                append(allDeviceSongs[sortedType.value[1]]!!.title + ", ")
                                                append(allDeviceSongs[sortedType.value[2]]!!.title)

                                                withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                                                    append(" and ${sortedType.value.size - 3} other songs")
                                                }
                                            }
                                        }
                                    }, color = White, overflow = TextOverflow.Ellipsis, fontSize = 14.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(15.dp))

                        }
                    } else {
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(text = "No songs to show", color = White)
                        Spacer(modifier = Modifier.height(15.dp))
                    }

                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongView(
    songData: SongData,
    currentSong: SongData?,
    currentPosition: Int,
    mediaPlayerState: Int,
    id: Int,
    isMultiSelecting: Boolean,
    isSelected: Boolean,
    onAction: (action:Int, id:String, openPlayer:Boolean) -> Unit
) {
    var isFullyShown by remember {
        mutableStateOf(false)
    }

    var animateSize by remember {
        mutableStateOf(false)
    }

    if(currentSong != null) {
        LaunchedEffect(key1 = currentSong.id == songData.id) {
            if (currentSong.id == songData.id) {
                animateSize = true
                delay(300)
                animateSize = false
            }
        }
    }

    val height by animateDpAsState(targetValue = if(!animateSize) 50.dp else 80.dp, label = "", animationSpec = tween(500))

    val multiSelectingPadding by animateDpAsState(
        targetValue = if (isMultiSelecting) 35.dp else 0.dp,
        label = ""
    )
    val checkMarkSize by animateDpAsState(
        targetValue = if (isSelected) 15.dp else 0.dp,
        label = "",
        animationSpec = tween(300)
    )

    val isCurrentPlaying = currentSong != null && currentSong.id == songData.id

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        if (isMultiSelecting && multiSelectingPadding == 35.dp) {
            Box(
                modifier = Modifier
                    .size(25.dp)
                    .align(Alignment.CenterStart)
                    .clip(corners(90.dp))
                    .clickable {
                        onAction(MULTI_SELECT_SONGS, songData.id, false)
                    }
                    .border(
                        width = if (isSelected) 3.dp else 2.dp,
                        color = if (isSelected) TranslucentOrangeSemi else TranslucentOrange,
                        shape = corners(90.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Image(
                        imageVector = Icons.Default.Check,
                        contentDescription = "",
                        modifier = Modifier.size(checkMarkSize),
                        colorFilter = ColorFilter.tint(Orange),
                    )
                }
            }
        }

        if(songData.thumbnail != null && isCurrentPlaying) {
            Row(
                modifier = Modifier
                    .width(getPercentage(screenWidth(), if (!isMultiSelecting) 90 else 100).dp)
                    .height(if (isFullyShown && !isMultiSelecting) 100.dp else height)
                    .align(Alignment.CenterStart)
                    .clip(corners(5.dp))
                    .padding(start = multiSelectingPadding)
                    .clip(corners(5.dp))
                    .blur(200.dp)
            ) {
                Image(
                    bitmap = songData.thumbnail.asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }
        }

        Row(
            modifier = Modifier
                .width(getPercentage(screenWidth(), if (!isMultiSelecting) 90 else 100).dp)
                .height(if (isFullyShown && !isMultiSelecting) 100.dp else height)
                .align(Alignment.CenterStart)
                .clip(corners(5.dp))
                .padding(start = multiSelectingPadding)
                .clip(corners(5.dp))
                .background(if (id % 2 == 0) TranslucentOrange else MildTranslucentOrange)
                .combinedClickable(
                    onDoubleClick = {
                        if (!isMultiSelecting) {
                            isFullyShown = !isFullyShown
                        }
                    },
                    onLongClick = {
                        isFullyShown = false
                        onAction(MULTI_SELECT_SONGS, songData.id, false)
                    },
                    onClick = {
                        if (!isMultiSelecting) {
                            onAction(PLAY_SONG, songData.id, true)
                        } else {
                            isFullyShown = false
                            onAction(MULTI_SELECT_SONGS, songData.id, false)
                        }
                    }
                )
                .padding(end = 3.dp)
                .animateContentSize(), verticalAlignment = Alignment.CenterVertically
        ) {
            if (songData.thumbnail != null) {
                Image(
                    bitmap = songData.thumbnail.asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier
                        .height(if (isFullyShown && !isMultiSelecting) 100.dp else height)
                        .width(60.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(3.dp))

            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = songData.title,
                        modifier = Modifier.fillMaxWidth(0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = if(isCurrentPlaying) FontWeight.ExtraBold else FontWeight.Normal,
                        color = White
                    )

                    Text(
                        text = if(isCurrentPlaying) durationMillisToStringMinutes(currentPosition.toLong()) else durationMillisToStringMinutes(songData.duration),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = if(isCurrentPlaying) FontWeight.ExtraBold else FontWeight.Normal,
                        color = White
                    )
                }

                Text(
                    text = "Artist: ${songData.artist}",
                    modifier = Modifier.fillMaxWidth(0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if(isCurrentPlaying) FontWeight.ExtraBold else FontWeight.Normal,
                    color = LighterGray,
                    fontSize = 14.sp
                )

                if (isFullyShown) {
                    Text(
                        text = "Album: ${songData.album}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = if(isCurrentPlaying) FontWeight.ExtraBold else FontWeight.Normal,
                        color = LighterGray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Genre: ${songData.genre ?: "Unknown Genre"}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = if(isCurrentPlaying) FontWeight.ExtraBold else FontWeight.Normal,
                        color = LighterGray,
                        fontSize = 14.sp
                    )
                }
            }
        }

        if (!isMultiSelecting) {
            Box(modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .noGleamTaps {
                    onAction(PLAY_SONG, songData.id, false)
                }
            ) {
                if (isCurrentPlaying && mediaPlayerState == PLAYING) {
                    PlayingIndicator()
                } else {
                    Image(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "",
                        modifier = Modifier
                            .clip(
                                corners(90.dp)
                            )
                            .size(35.dp),
                        colorFilter = ColorFilter.tint(Orange)
                    )
                }
            }
        }
    }
}

@Composable
fun SongsViewSorter(currentSelected: Int, id: Int, label: String, onSelect: (Int) -> Unit) {
    val indicatorWidth by animateDpAsState(targetValue = if(currentSelected == id) 30.dp else 0.dp, label = "")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center, modifier =
        if(id == VIEWING_ALL)
            Modifier
                .width(((screenWidth() - 20) / 4).dp)
                .clip(corners(7.dp))
                .background(
                    Translucent
                )
                .padding(3.dp)
                .clickable {
                    onSelect(id)
                }
        else
            Modifier
                .width(((screenWidth() - 20) / 4).dp)
                .padding(3.dp)
                .clickable {
                    onSelect(id)
                }
    ) {
        Row {
            Text(
                text = label,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = if(currentSelected == id) White else MildGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.width(3.dp))
        }

        Spacer(modifier = Modifier.height(5.dp))

        if(currentSelected == id) {
            Canvas(modifier = Modifier.width(indicatorWidth)) {
                drawLine(
                    color = Orange,
                    start = Offset(x = 0f, y = 0f),
                    end = Offset(y = 0f, x = this.size.width),
                    strokeWidth = 12f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}