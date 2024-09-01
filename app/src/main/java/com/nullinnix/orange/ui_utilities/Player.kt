package com.nullinnix.orange.ui_utilities

import android.app.Activity
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.nullinnix.orange.lyrics.LyricsManager
import com.nullinnix.orange.misc.corners
import com.nullinnix.orange.misc.getPercent
import com.nullinnix.orange.misc.noGleamTaps
import com.nullinnix.orange.misc.screenHeight
import com.nullinnix.orange.misc.screenWidth
import com.nullinnix.orange.song_managing.PlayerClickActions.Companion.PITCH_TOGGLE
import com.nullinnix.orange.song_managing.PlayerClickActions.Companion.SKIP_NEXT
import com.nullinnix.orange.song_managing.PlayerClickActions.Companion.SKIP_PREVIOUS
import com.nullinnix.orange.song_managing.PlayerClickActions.Companion.SPEED_TOGGLE
import com.nullinnix.orange.ui.theme.MildBlack
import com.nullinnix.orange.ui.theme.MildTranslucentBlack
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.Translucent
import com.nullinnix.orange.ui.theme.TranslucentBlack
import com.nullinnix.orange.ui.theme.Transparent
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
    lyricsManager: LyricsManager,
    context: Activity,
    onAction: (Int) -> Unit,
    onSeek: (action: Int, seekPosition: Int?) -> Unit,
    onPlaybackToggle: (Int, Float) -> Unit,
    onClose: () -> Unit
) {
    var isPlayerVisible by remember {
        mutableStateOf(false)
    }

    val currentSpeedState by rememberUpdatedState(newValue = currentSpeed)
    val currentPitchState by rememberUpdatedState(newValue = currentPitch)

    var isLyricsVisible by remember {
        mutableStateOf(false)
    }

    var showMenu by remember {
        mutableStateOf(true)
    }

    var animateLyricsView by remember {
        mutableStateOf(false)
    }

    BackHandler(isPlayerVisible || isLyricsVisible) {
        if (isPlayerVisible && !isLyricsVisible) {
            isPlayerVisible = false
            CoroutineScope(Dispatchers.IO).launch {
                delay(300)
                onClose()
            }
        }

        if (isLyricsVisible) {
            CoroutineScope(Dispatchers.IO).launch {
                animateLyricsView = false
                delay(300)
                isLyricsVisible = false
            }
        }

        if(showMenu){
            showMenu = false
        }
    }

    LaunchedEffect(key1 = Unit) {
        isPlayerVisible = true
    }

    val playerOffset by animateDpAsState(
        targetValue = if (isPlayerVisible) 0.dp else screenHeight().dp,
        label = "",
        animationSpec = tween(500)
    )

    val skipNextAnim by animateDpAsState(
        targetValue = if (skipAnimationType == SKIP_NEXT) 0.dp else 2000.dp,
        label = "",
        animationSpec = tween(300)
    )
    val skipPreviousAnim by animateDpAsState(
        targetValue = if (skipAnimationType == SKIP_PREVIOUS) screenWidth().dp else 0.dp,
        label = "",
        animationSpec = tween(300)
    )

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

        if (!isLyricsVisible) {
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
                    .blur(if(showMenu) 10.dp else 0.dp)
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
                        .width((screenWidth() - 200).dp / 2)
                        .align(Alignment.CenterStart)
                        .animateContentSize(animationSpec = tween(1500)),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HoverSlider(
                        label = "PITCH",
                        currentValue = currentPitchState,
                        maxValue = 2f,
                        minValue = 0.25f,
                        onChange = {
                            when(it){
                                LOWER_HALF -> {
                                    if(currentPitchState > 0.25f) {
                                        if(currentPitchState - 0.25f > 0.5f){
                                            onPlaybackToggle(PITCH_TOGGLE, currentPitchState - 0.25f)
                                        } else if(currentSpeedState < 1.5f){
                                            onPlaybackToggle(PITCH_TOGGLE, currentPitchState - 0.25f)
                                        }
                                    }
                                }

                                TOP_HALF -> {
                                    if(currentPitchState < 2f) {
                                        onPlaybackToggle(PITCH_TOGGLE, currentPitchState + 0.25f)
                                    }
                                }

                                CENTER -> {
                                    onPlaybackToggle(PITCH_TOGGLE, 1f)
                                }
                            }
                        },
                    )
                }

                Column(
                    modifier = Modifier
                        .width((screenWidth() - 200).dp / 2)
                        .align(Alignment.CenterEnd)
                        .animateContentSize(animationSpec = tween(1500)),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HoverSlider(
                        label = "SPEED",
                        currentValue = currentSpeedState,
                        maxValue = 2f,
                        minValue = 0.25f,
                        onChange = {
                            when(it){
                                LOWER_HALF -> {
                                    if(currentSpeedState > 0.25f) {
                                        onPlaybackToggle(SPEED_TOGGLE, currentSpeedState - 0.25f)
                                    }
                                }

                                TOP_HALF -> {
                                    if(currentSpeedState < 2f) {
                                        if(currentPitchState > 0.5f){
                                            onPlaybackToggle(SPEED_TOGGLE, currentSpeedState + 0.25f)
                                        } else if (currentPitchState < 0.5f && currentSpeedState + 0.25f < 1.5f){
                                            onPlaybackToggle(SPEED_TOGGLE, currentSpeedState + 0.25f)
                                        }
                                    }
                                }

                                CENTER -> {
                                    onPlaybackToggle(SPEED_TOGGLE, 1f)
                                }
                            }
                        },
                    )
                }
            }
        }

        if (isLyricsVisible) {
            LaunchedEffect(key1 = Unit) {
                animateLyricsView = true
            }

            val lyricsOffset by animateDpAsState(
                targetValue = if (animateLyricsView) 0.dp else screenHeight().dp,
                label = "",
                animationSpec = tween(300)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = lyricsOffset)
            ) {
                LyricsView(
                    context = context,
                    lyricsManager = lyricsManager,
                    currentSong = currentSong
                ) {
                    CoroutineScope(Dispatchers.Main).launch {
                        animateLyricsView = false
                        delay(300)
                        isLyricsVisible = false
                    }
                }
            }
        }

        if (!isLyricsVisible) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "",
                    modifier = Modifier
                        .clip(corners(90.dp))
                        .size(35.dp)
                        .background(Translucent)
                        .clickable {
                            isPlayerVisible = false
                            CoroutineScope(Dispatchers.IO).launch {
                                delay(300)
                                onClose()
                            }
                        }
                        .padding(5.dp),
                    colorFilter = ColorFilter.tint(Orange)
                )

                Row(modifier = Modifier, horizontalArrangement = Arrangement.End) {
                    if(showMenu) {
                        Menu(
                            onClick = {
                                when(it){
                                    -1 -> {
                                        showMenu = false
                                    } else -> {
                                        onAction(it)
                                    }
                                }
                            }
                        )
                    }

                    Row(
                        Modifier
                            .clip(
                                corners(
                                    topEnd = 90.dp,
                                    bottomEnd = 90.dp,
                                    topStart = if (showMenu) 0.dp else 90.dp,
                                    bottomStart = if (showMenu) 0.dp else 90.dp
                                )
                            )
                            .background(Translucent)
                    ) {
                        Image(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "",
                            modifier = Modifier
                                .clip(
                                    corners(
                                        topEnd = 90.dp,
                                        bottomEnd = 90.dp,
                                        topStart = 0.dp,
                                        bottomStart = 0.dp
                                    )
                                )
                                .background(if (showMenu) TranslucentBlack else Transparent)
                                .size(35.dp)
                                .clickable {
                                    showMenu = !showMenu
                                }
                                .padding(5.dp),
                            colorFilter = ColorFilter.tint(if(showMenu) White else Orange)
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Image(
                            painter = painterResource(id = R.drawable.lyrcis),
                            contentDescription = "",
                            modifier = Modifier
                                .clip(corners(90.dp))
                                .size(35.dp)
                                .clickable {
                                    isLyricsVisible = true
                                    showMenu = false
                                }
                                .padding(5.dp),
                            colorFilter = ColorFilter.tint(Orange)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .height(if (!isLyricsVisible) 200.dp else 145.dp)
                .blur(if(showMenu) 10.dp else 0.dp)
                .align(Alignment.BottomCenter)
                .background(Translucent)
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
                isLyricView = isLyricsVisible,
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
fun MiniPlayer(
    modifier: Modifier,
    mediaPlayerState: Int,
    currentSong: SongData,
    currentPosition: Int,
    isLiked: Boolean,
    isShuffling: Boolean,
    onSongEndAction: Int,
    showTimeRemaining: Boolean,
    onAction: (Int) -> Unit,
    onSeek: (action: Int, seekPosition: Int?) -> Unit
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
        .height(250.dp)
        .animateContentSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Transparent,
                            MildTranslucentBlack
                        )
                    )
                )
        ) {

        }

        if (currentSong.thumbnail != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .align(Alignment.BottomCenter)
                    .blur(100.dp)
            ) {
                Image(
                    bitmap = currentSong.thumbnail.asImageBitmap(),
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

        MediaControlButtons(
            modifier = Modifier.align(Alignment.BottomCenter),
            mediaPlayerState = mediaPlayerState,
            currentSong = currentSong,
            currentPosition = currentPosition,
            isLiked = isLiked,
            isShuffling = isShuffling,
            onSongEndAction = onSongEndAction,
            showTimeRemaining = showTimeRemaining,
            showTitle = true,
            onAction = {
                onAction(it)
            },
            onSeek = {action: Int, seekPosition: Int? ->
                onSeek(action, seekPosition)
            }
        )
    }
}