package com.nullinnix.orange.ui_utilities

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullinnix.orange.R
import com.nullinnix.orange.SongData
import com.nullinnix.orange.misc.corners
import com.nullinnix.orange.misc.getPercent
import com.nullinnix.orange.misc.noGleamTaps
import com.nullinnix.orange.misc.screenHeight
import com.nullinnix.orange.misc.screenWidth
import com.nullinnix.orange.misc.translucentColor
import com.nullinnix.orange.song_managing.PlayerClickActions.Companion.PITCH_TOGGLE
import com.nullinnix.orange.song_managing.PlayerClickActions.Companion.SKIP_NEXT
import com.nullinnix.orange.song_managing.PlayerClickActions.Companion.SKIP_PREVIOUS
import com.nullinnix.orange.song_managing.PlayerClickActions.Companion.SPEED_TOGGLE
import com.nullinnix.orange.ui.theme.MildBlack
import com.nullinnix.orange.ui.theme.Opaque
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.Red
import com.nullinnix.orange.ui.theme.Translucent
import com.nullinnix.orange.ui.theme.TranslucentBlack
import com.nullinnix.orange.ui.theme.TranslucentOrange
import com.nullinnix.orange.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Player(
    mediaPlayerState: Int,
    currentSong: SongData,
    previousSongData: SongData?,
    nextSongData: SongData?,
    allSongsInPlaylist: Map<String, SongData>,
    currentPosition: Int,
    isLiked: Boolean,
    isShuffling: Boolean,
    onSongEndAction: Int,
    showTimeRemaining: Boolean,
    skipAnimationType: Int,
    currentSpeed: Float,
    currentPitch: Float,
    onAction: (Int) -> Unit,
    onSeek: (action: Int, seekPosition: Int?) -> Unit,
    onPlaybackToggle: (Int, Float) -> Unit,
    onClose: () -> Unit
) {
    var isPlayerVisible by remember {
        mutableStateOf(false)
    }

    var showSpeeds by remember {
        mutableStateOf(false)
    }

    var showPitches by remember {
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

    val playerOffset by animateDpAsState(targetValue = if(isPlayerVisible) 0.dp else screenHeight().dp, label = "", animationSpec = tween(500))

    val skipNextAnim by animateDpAsState(targetValue = if(skipAnimationType == SKIP_NEXT) 0.dp  else 2000.dp, label = "", animationSpec = tween(300))
    val skipPreviousAnim by animateDpAsState(targetValue = if(skipAnimationType == SKIP_PREVIOUS) screenWidth().dp  else 0.dp, label = "", animationSpec = tween(300))

    val pitchColumnHeight by animateDpAsState(targetValue = if(showPitches) 200.dp else 50.dp, label = "")
    val speedColumnHeight by animateDpAsState(targetValue = if(showSpeeds) 200.dp else 50.dp, label = "")

    val onPitchClicked = {pitch: Float ->
        if(currentPitch == pitch){
            showPitches = false
        } else {
            onPlaybackToggle(PITCH_TOGGLE, pitch)
        }
    }

    val onSpeedClicked = {speed: Float ->
        if(currentSpeed == speed){
            showSpeeds= false
        } else {
            onPlaybackToggle(SPEED_TOGGLE, speed)
        }
    }

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
                        .blur(100.dp)
                ) {
                    Image(
                        bitmap = currentSong.thumbnail.asImageBitmap(),
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Translucent)
                    )
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
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )

            if (currentSong.artist != "<unknown>")
                Text(
                    text = currentSong.artist,
                    color = White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
        }

        Box(
            modifier = Modifier
                .padding(bottom = 150.dp)
                .height(200.dp)
                .fillMaxWidth()
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            if (currentSong.thumbnail != null) {
                Image(
                    bitmap = currentSong.thumbnail.asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(corners(10.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            if (skipAnimationType == SKIP_NEXT && nextSongData != null) {
                if (nextSongData.thumbnail != null) {
                    Image(
                        bitmap = nextSongData.thumbnail.asImageBitmap(),
                        contentDescription = "",
                        modifier = Modifier
                            .size(200.dp)
                            //  .clip(corners(10.dp))
                            .offset(x = skipNextAnim),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            if (skipAnimationType == SKIP_PREVIOUS && nextSongData != null) {
                if (nextSongData.thumbnail != null) {
                    Image(
                        bitmap = nextSongData.thumbnail.asImageBitmap(),
                        contentDescription = "",
                        modifier = Modifier
                            .size(200.dp)
                            //  .clip(corners(10.dp))
                            .offset(x = skipPreviousAnim),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column(
                modifier = Modifier
                    .height(pitchColumnHeight)
                    .width((screenWidth() - 200).dp / 2)
                    .align(Alignment.CenterStart)
                    .animateContentSize(animationSpec = tween(1500)),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(showPitches) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 5.dp), horizontalArrangement = Arrangement.SpaceBetween){
                        PlaybackSpeed(currentPitch, 0.25f, PITCH_TOGGLE, currentSpeed < 1.7) {
                            onPitchClicked(it)
                        }

                        PlaybackSpeed(currentPitch, 2f, PITCH_TOGGLE, currentSpeed != 0.25f) {
                            onPlaybackToggle(PITCH_TOGGLE, it)
                        }
                    }

                    Row(Modifier.fillMaxWidth().padding(horizontal = 5.dp), horizontalArrangement = Arrangement.SpaceBetween){
                        PlaybackSpeed(currentPitch, 0.5f, PITCH_TOGGLE) {
                            onPitchClicked(it)
                        }

                        PlaybackSpeed(currentPitch, 1.75f, PITCH_TOGGLE) {
                            onPitchClicked(it)
                        }
                    }

                    Row(Modifier.fillMaxWidth().padding(horizontal = 5.dp), horizontalArrangement = Arrangement.SpaceBetween){
                        PlaybackSpeed(currentPitch, 0.75f, PITCH_TOGGLE) {
                            onPitchClicked(it)
                        }

                        PlaybackSpeed(currentPitch, 1.5f, PITCH_TOGGLE) {
                            onPitchClicked(it)
                        }
                    }

                    Row(Modifier.fillMaxWidth().padding(horizontal = 5.dp), horizontalArrangement = Arrangement.SpaceBetween){
                        PlaybackSpeed(currentPitch, 1f, PITCH_TOGGLE) {
                            onPitchClicked(it)
                        }

                        PlaybackSpeed(currentPitch, 1.25f, PITCH_TOGGLE) {
                            onPitchClicked(it)
                        }
                    }
                } else {
                    Row (modifier = Modifier
                        .clip(corners(8.dp))
                        .background(Translucent)
                        .clickable {
                            
                            showPitches = true
                        }
                        .padding(2.dp), verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(text = "PITCH", color = White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)

                        Spacer(modifier = Modifier.width(2.dp))

                        Row (modifier = Modifier
                            .clip(corners(5.dp))
                            .background(TranslucentBlack), verticalAlignment = Alignment.CenterVertically){
                            Text(
                                text = "$currentPitch",
                                color = White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )

                            Icon(painter = painterResource(id = R.drawable.pitch), contentDescription = "", modifier = Modifier.size(12.dp), tint = White)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .height(speedColumnHeight)
                    .width((screenWidth() - 200).dp / 2)
                    .align(Alignment.CenterEnd)
                    .animateContentSize(animationSpec = tween(1500)),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(showSpeeds) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 5.dp), horizontalArrangement = Arrangement.SpaceBetween){
                        PlaybackSpeed(currentSpeed, 0.25f, SPEED_TOGGLE) {
                            onSpeedClicked(it)
                        }

                        PlaybackSpeed(currentSpeed, 2f, SPEED_TOGGLE, currentPitch != 0.25f) {
                            onSpeedClicked(it)
                        }
                    }

                    Row(Modifier.fillMaxWidth().padding(horizontal = 5.dp), horizontalArrangement = Arrangement.SpaceBetween){
                        PlaybackSpeed(currentSpeed, 0.5f, SPEED_TOGGLE) {
                            onSpeedClicked(it)
                        }

                        PlaybackSpeed(currentSpeed, 1.75f, SPEED_TOGGLE, currentPitch != 0.25f) {
                            onSpeedClicked(it)
                        }
                    }

                    Row(Modifier.fillMaxWidth().padding(horizontal = 5.dp), horizontalArrangement = Arrangement.SpaceBetween){
                        PlaybackSpeed(currentSpeed, 0.75f, SPEED_TOGGLE) {
                            onSpeedClicked(it)
                        }

                        PlaybackSpeed(currentSpeed, 1.5f, SPEED_TOGGLE) {
                            onSpeedClicked(it)
                        }
                    }

                    Row(Modifier.fillMaxWidth().padding(horizontal = 5.dp), horizontalArrangement = Arrangement.SpaceBetween){
                        PlaybackSpeed(currentSpeed, 1f, SPEED_TOGGLE) {
                            onSpeedClicked(it)
                        }

                        PlaybackSpeed(currentSpeed, 1.25f, SPEED_TOGGLE) {
                            onSpeedClicked(it)
                        }
                    }
                } else {
                    Row (modifier = Modifier
                        .clip(corners(8.dp))
                        .background(Translucent)
                        .clickable {
                            showSpeeds = true
                        }
                        .padding(2.dp), verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(text = "SPEED", color = White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)

                        Spacer(modifier = Modifier.width(2.dp))

                        Row (modifier = Modifier
                            .clip(corners(5.dp))
                            .background(TranslucentBlack), verticalAlignment = Alignment.CenterVertically){
                            Text(
                                text = "$currentSpeed",
                                color = White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )

                            Icon(painter = painterResource(id = R.drawable.speed), contentDescription = "", modifier = Modifier.size(12.dp), tint = White)
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .height(200.dp)
                .align(Alignment.BottomCenter)
                .background(Opaque)
                .padding(horizontal = 5.dp)
        ) {
            MediaControlButtons(
                modifier = Modifier,
                mediaPlayerState = mediaPlayerState,
                currentSong = currentSong,
                currentPosition = currentPosition,
                isLiked = isLiked,
                isShuffling = isShuffling,
                onSongEndAction = onSongEndAction,
                showTimeRemaining = showTimeRemaining,
                showTitle = false,
                onAction = {
                    onAction(it)
                },
                onSeek = { action: Int, seekPosition: Int? ->
                    onSeek(action, seekPosition)
                }
            )
        }
    }
}

@Composable
fun PlaybackSpeed(currentValue: Float, value: Float, type: Int, enabled: Boolean = true, onClick: (Float) -> Unit) {
    val isSelected = currentValue == value

    Row(
        Modifier
            .clip(corners(10.dp))
            .background(color = translucentColor(Orange, value / 2f))
            .clickable(enabled) {
                onClick(value)
            }
            .padding(3.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = "$value",
            color = White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp
        )

             if(isSelected){
            Spacer(modifier = Modifier.width(3.dp))

            Icon(painter = painterResource(id = if(type == PITCH_TOGGLE) R.drawable.pitch else R.drawable.speed), contentDescription = "", modifier = Modifier.size(12.dp), tint = if(value == 4f) White else Orange)
        }
    }
}