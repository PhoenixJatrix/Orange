package com.nullinnix.orange.ui_utilities

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullinnix.orange.END_SEEKING
import com.nullinnix.orange.LOOPING_ALL
import com.nullinnix.orange.NOT_LOOPING
import com.nullinnix.orange.Playlist
import com.nullinnix.orange.R
import com.nullinnix.orange.START_SEEKING
import com.nullinnix.orange.SongData
import com.nullinnix.orange.misc.corners
import com.nullinnix.orange.misc.getPercent
import com.nullinnix.orange.misc.noGleamTaps
import com.nullinnix.orange.misc.screenHeight
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.PLAYING
import com.nullinnix.orange.song_managing.PlayerClickActions
import com.nullinnix.orange.ui.theme.Black
import com.nullinnix.orange.ui.theme.MildBlack
import com.nullinnix.orange.ui.theme.Opaque
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.Translucent
import com.nullinnix.orange.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Player(
    mediaPlayerState: Int,
    currentSong: SongData,
    currentPlaylist: Playlist,
    currentPosition: Int,
    isLiked: Boolean,
    isShuffling: Boolean,
    onSongEndAction: Int,
    onAction: (Int) -> Unit,
    onSeek: (action: Int, seekPosition: Int?) -> Unit,
    onClose: () -> Unit
) {
    var isPlayerVisible by remember {
        mutableStateOf(false)
    }

    BackHandler(isPlayerVisible) {
        isPlayerVisible = false
        CoroutineScope(Dispatchers.IO).launch {
            delay(300)
            onClose()
        }
    }

    LaunchedEffect(key1 = Unit) {
        isPlayerVisible = true
    }

    val screenHeight = screenHeight()

    val playerOffset by animateDpAsState(targetValue = if(isPlayerVisible) 0.dp else screenHeight().dp, label = "", animationSpec = tween(500))

    Box(modifier = Modifier
        .fillMaxSize()
        .noGleamTaps {

        }
        .background(
            Color(
                MildBlack.red,
                MildBlack.green,
                MildBlack.blue,
                getPercent(screenHeight(), screenHeight() - playerOffset.value.toInt()) / 100f
            )
        )
        .offset(y = playerOffset)
    ) {
        if (playerOffset == 0.dp) {
            if (currentSong.thumbnail != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(200.dp)
                ) {
                    Image(
                        bitmap = currentSong.thumbnail.asImageBitmap(),
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )

                    Box(Modifier.fillMaxSize().background(Translucent))
                }
            }
        }

        Column(
            Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = currentSong.title,
                color = White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
            if (currentSong.artist != "<unknown>")
                Text(text = currentSong.artist, color = White)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(bottom = 150.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(corners(10.dp))
                    .background(Orange)
            ) {
                if (currentSong.thumbnail != null) {
                    Image(
                        bitmap = currentSong.thumbnail.asImageBitmap(),
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Column (modifier = Modifier
            .height(200.dp)
            .align(Alignment.BottomCenter)
            .background(Opaque)
        ){
            Row(
                Modifier
                    .fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Slider(
                    currentValue = currentPosition,
                    minValue = 0,
                    maxValue = currentSong.duration.toInt(),
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
            ){
                Image(painter = painterResource(id = R.drawable.skip_previous), contentDescription = "", modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        onAction(PlayerClickActions.SKIP_PREVIOUS)
                    })

                Image(painter = painterResource(id = if(mediaPlayerState == PLAYING) R.drawable.pause else R.drawable.play), contentDescription = "", modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        onAction(PlayerClickActions.TOGGLE_PLAY)
                    })

                Image(painter = painterResource(id = R.drawable.skip_next), contentDescription = "", modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        onAction(PlayerClickActions.SKIP_NEXT)
                    })
            }

            Spacer(modifier = Modifier.height(25.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp), horizontalArrangement = Arrangement.SpaceAround
            ){
                Image(painter = painterResource(id = R.drawable.shuffle), contentDescription = "", modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        onAction(PlayerClickActions.TOGGLE_SHUFFLE)
                    })

                Image(imageVector = if(isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder , contentDescription = "", modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        onAction(PlayerClickActions.TOGGLE_LIKED)
                    }, colorFilter = ColorFilter.tint(if(isLiked) White else Black)
                )

                Image(
                    painter = painterResource(id = if (onSongEndAction == LOOPING_ALL || onSongEndAction == NOT_LOOPING) R.drawable.looping_all else R.drawable.looping_single),
                    contentDescription = "",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            onAction(PlayerClickActions.TOGGLE_SONG_ON_END_ACTION)
                        },
                    colorFilter = ColorFilter.tint(if(onSongEndAction == NOT_LOOPING) Translucent else Black)
                )
            }
        }
    }
}