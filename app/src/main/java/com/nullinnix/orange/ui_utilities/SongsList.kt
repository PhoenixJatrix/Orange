package com.nullinnix.orange.ui_utilities

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullinnix.orange.MULTI_SELECT_SONGS
import com.nullinnix.orange.PLAY_SONG
import com.nullinnix.orange.Playlist
import com.nullinnix.orange.SongData
import com.nullinnix.orange.misc.corners
import com.nullinnix.orange.misc.durationMillisToStringMinutes
import com.nullinnix.orange.misc.getPercentage
import com.nullinnix.orange.misc.screenWidth
import com.nullinnix.orange.ui.theme.BarelyVisibleBlack
import com.nullinnix.orange.ui.theme.LighterGray
import com.nullinnix.orange.ui.theme.MildBlack
import com.nullinnix.orange.ui.theme.MildTranslucentOrange
import com.nullinnix.orange.ui.theme.Opaque
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.Translucent
import com.nullinnix.orange.ui.theme.TranslucentOrange
import com.nullinnix.orange.ui.theme.TranslucentOrangeSemi
import com.nullinnix.orange.ui.theme.White
import kotlinx.coroutines.delay

@Composable
fun SongsList(
    songIDs: List<String>,
    allDeviceSongs: Map<String, SongData>,
    currentlyPlaying: Int,
    isMiniPlayerVisible: Boolean,
    isMultiSelecting: Boolean,
    selectedSongs: List<String>,
    isSearching: Boolean,
    currentPlaylist: Playlist,
    onAction: (Int, String) -> Unit
) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(
                5.dp
            )
    ) {
        item {
            Spacer(modifier = Modifier.height(if(isMultiSelecting && isSearching) 140.dp else if(isSearching) 110.dp else 85.dp))

            Spacer(modifier = Modifier.height(10.dp))

            Column (horizontalAlignment = Alignment.CenterHorizontally){
                Text(
                    text = if (currentPlaylist.name.length > 1) currentPlaylist.name[0].uppercase() + currentPlaylist.name.substring(
                        1
                    ) else currentPlaylist.name.uppercase(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = White
                )

                Spacer(modifier = Modifier.height(5.dp))

                Canvas(modifier = Modifier.width(50.dp)) {
                    drawLine(color = Orange, start = Offset(x = 0f, y = 0f), end = Offset(y = 0f, x = this.size.width), strokeWidth = 15f, cap = StrokeCap.Round)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        items(songIDs.size) {index ->
            SongView(
                songData = allDeviceSongs[songIDs[index]]!!,
                currentlyPlaying = currentlyPlaying,
                id = index,
                isMultiSelecting = isMultiSelecting,
                isSelected = selectedSongs.contains(songIDs[index])
            ) {action, id ->
                onAction(action, id)
            }

            if(index != songIDs.size - 1) {
                Spacer(modifier = Modifier.height(5.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(if(isMultiSelecting || isMiniPlayerVisible) 100.dp else 50.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongView(
    songData: SongData,
    currentlyPlaying: Int,
    id: Int,
    isMultiSelecting: Boolean,
    isSelected: Boolean,
    onAction: (Int, String) -> Unit
) {
    var isFullyShown by remember {
        mutableStateOf(false)
    }

    val multiSelectingPadding by animateDpAsState(targetValue = if(isMultiSelecting) 35.dp else 0.dp, label = "")
    val checkMarkSize by animateDpAsState(targetValue = if(isSelected) 15.dp else 0.dp, label = "", animationSpec = tween(300))

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        if(isMultiSelecting && multiSelectingPadding == 35.dp) {
            Box(
                modifier = Modifier
                    .size(25.dp)
                    .align(Alignment.CenterStart)
                    .clip(corners(90.dp))
                    .clickable {
                        onAction(MULTI_SELECT_SONGS, songData.id)
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

        Row(
            modifier = Modifier
                .width(getPercentage(screenWidth(), if (!isMultiSelecting) 90 else 100).dp)
                .height(if (isFullyShown && !isMultiSelecting) 100.dp else 50.dp)
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
                        onAction(MULTI_SELECT_SONGS, songData.id)
                    },
                    onClick = {
                        if (!isMultiSelecting) {
                            onAction(PLAY_SONG, songData.id)
                        } else {
                            isFullyShown = false
                            onAction(MULTI_SELECT_SONGS, songData.id)
                        }
                    }
                )
                .padding(end = 3.dp)
                .animateContentSize(), verticalAlignment = Alignment.CenterVertically
        ) {
            if(songData.thumbnail != null) {
                Image(
                    bitmap = songData.thumbnail.asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier
                        .size(50.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(5.dp))

            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = songData.title,
                        modifier = Modifier.fillMaxWidth(0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium,
                        color = White
                    )

                    Text(
                        text = durationMillisToStringMinutes(songData.duration),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium,
                        color = White
                    )
                }

                Text(
                    text = "Artist: ${songData.artist}",
                    modifier = Modifier.fillMaxWidth(0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium,
                    color = LighterGray,
                    fontSize = 14.sp
                )

                if (isFullyShown) {
                    Text(
                        text = "Album: ${songData.album}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium,
                        color = LighterGray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Genre: ${songData.genre ?: "Unknown Genre"}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium,
                        color = LighterGray,
                        fontSize = 14.sp
                    )
                }
            }
        }

        if(!isMultiSelecting){
            Image(
                imageVector = Icons.Default.PlayArrow, contentDescription = "", modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clip(
                        corners(90.dp)
                    )
                    .size(35.dp)
                    .clickable {

                    }, colorFilter = ColorFilter.tint(Orange)
            )
        }
    }
}