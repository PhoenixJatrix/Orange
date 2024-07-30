package com.nullinnix.orange.ui_utilities

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.nullinnix.orange.SongData
import com.nullinnix.orange.misc.corners
import com.nullinnix.orange.misc.durationMillisToStringMinutes
import com.nullinnix.orange.ui.theme.BarelyVisibleBlack
import com.nullinnix.orange.ui.theme.LighterGray
import com.nullinnix.orange.ui.theme.MildBlack
import com.nullinnix.orange.ui.theme.Opaque
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.Translucent
import com.nullinnix.orange.ui.theme.White

@Composable
fun SongsList(songsData: List<SongData>, currentlyPlaying: Int, isMiniPlayerVisible: Boolean) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(
                5.dp
            )
    ) {
        item {
            Spacer(modifier = Modifier.height(70.dp))
        }

        items(songsData.size) {
            SongView(songData = songsData[it], currentlyPlaying = currentlyPlaying, id = it)

            if(it != songsData.size - 1) {
                Spacer(modifier = Modifier.height(5.dp))
                Spacer(modifier = Modifier.height(5.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun SongView(songData: SongData, currentlyPlaying: Int, id: Int) {
    var isFullyShown by remember {
        mutableStateOf(false)
    }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clip(corners(10.dp))
            .background(if(id % 2 == 0) BarelyVisibleBlack else Translucent)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        isFullyShown = !isFullyShown
                    }
                )
            }
            .padding(vertical = 3.dp, horizontal = 3.dp)
            .animateContentSize()
    ) {
        Image(imageVector = Icons.Default.PlayArrow, contentDescription = "", modifier = Modifier
            .align(Alignment.CenterVertically)
            .clip(
                corners(90.dp)
            )
            .size(35.dp)
            .clickable {

            }, colorFilter = ColorFilter.tint(Orange)
        )

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