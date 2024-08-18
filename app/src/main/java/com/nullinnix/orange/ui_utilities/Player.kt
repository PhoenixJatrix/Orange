package com.nullinnix.orange.ui_utilities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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
import com.nullinnix.orange.R
import com.nullinnix.orange.SongData
import com.nullinnix.orange.lyrics.LyricsManager
import com.nullinnix.orange.misc.allowedKeys
import com.nullinnix.orange.misc.cleanSearchParameter
import com.nullinnix.orange.misc.copyFromClipBoard
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
import com.nullinnix.orange.ui.theme.LighterGray
import com.nullinnix.orange.ui.theme.MildBlack
import com.nullinnix.orange.ui.theme.MildGray
import com.nullinnix.orange.ui.theme.MildTranslucentBlack
import com.nullinnix.orange.ui.theme.MildTranslucentOrange
import com.nullinnix.orange.ui.theme.Opaque
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.Translucent
import com.nullinnix.orange.ui.theme.TranslucentBlack
import com.nullinnix.orange.ui.theme.TranslucentOrange
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

    var showSpeeds by remember {
        mutableStateOf(false)
    }

    var showPitches by remember {
        mutableStateOf(false)
    }

    var isLyricsVisible by remember {
        mutableStateOf(false)
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

    val pitchColumnHeight by animateDpAsState(
        targetValue = if (showPitches) 200.dp else 50.dp,
        label = ""
    )
    val speedColumnHeight by animateDpAsState(
        targetValue = if (showSpeeds) 200.dp else 50.dp,
        label = ""
    )

    val onPitchClicked = { pitch: Float ->
        if (currentPitch == pitch) {
            showPitches = false
        } else {
            onPlaybackToggle(PITCH_TOGGLE, pitch)
        }
    }

    val onSpeedClicked = { speed: Float ->
        if (currentSpeed == speed) {
            showSpeeds = false
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
                    if (showPitches) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PlaybackSpeed(currentPitch, 0.25f, PITCH_TOGGLE, currentSpeed < 1.7) {
                                onPitchClicked(it)
                            }

                            PlaybackSpeed(currentPitch, 2f, PITCH_TOGGLE, currentSpeed != 0.25f) {
                                onPlaybackToggle(PITCH_TOGGLE, it)
                            }
                        }

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PlaybackSpeed(currentPitch, 0.5f, PITCH_TOGGLE) {
                                onPitchClicked(it)
                            }

                            PlaybackSpeed(currentPitch, 1.75f, PITCH_TOGGLE) {
                                onPitchClicked(it)
                            }
                        }

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PlaybackSpeed(currentPitch, 0.75f, PITCH_TOGGLE) {
                                onPitchClicked(it)
                            }

                            PlaybackSpeed(currentPitch, 1.5f, PITCH_TOGGLE) {
                                onPitchClicked(it)
                            }
                        }

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PlaybackSpeed(currentPitch, 1f, PITCH_TOGGLE) {
                                onPitchClicked(it)
                            }

                            PlaybackSpeed(currentPitch, 1.25f, PITCH_TOGGLE) {
                                onPitchClicked(it)
                            }
                        }
                    } else {
                        Row(modifier = Modifier
                            .clip(corners(8.dp))
                            .background(Translucent)
                            .clickable {

                                showPitches = true
                            }
                            .padding(2.dp), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PITCH",
                                color = White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.width(2.dp))

                            Row(
                                modifier = Modifier
                                    .clip(corners(5.dp))
                                    .background(TranslucentBlack),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$currentPitch",
                                    color = White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )

                                Icon(
                                    painter = painterResource(id = R.drawable.pitch),
                                    contentDescription = "",
                                    modifier = Modifier.size(12.dp),
                                    tint = White
                                )
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
                    if (showSpeeds) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PlaybackSpeed(currentSpeed, 0.25f, SPEED_TOGGLE) {
                                onSpeedClicked(it)
                            }

                            PlaybackSpeed(currentSpeed, 2f, SPEED_TOGGLE, currentPitch != 0.25f) {
                                onSpeedClicked(it)
                            }
                        }

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PlaybackSpeed(currentSpeed, 0.5f, SPEED_TOGGLE) {
                                onSpeedClicked(it)
                            }

                            PlaybackSpeed(
                                currentSpeed,
                                1.75f,
                                SPEED_TOGGLE,
                                currentPitch != 0.25f
                            ) {
                                onSpeedClicked(it)
                            }
                        }

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PlaybackSpeed(currentSpeed, 0.75f, SPEED_TOGGLE) {
                                onSpeedClicked(it)
                            }

                            PlaybackSpeed(currentSpeed, 1.5f, SPEED_TOGGLE) {
                                onSpeedClicked(it)
                            }
                        }

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PlaybackSpeed(currentSpeed, 1f, SPEED_TOGGLE) {
                                onSpeedClicked(it)
                            }

                            PlaybackSpeed(currentSpeed, 1.25f, SPEED_TOGGLE) {
                                onSpeedClicked(it)
                            }
                        }
                    } else {
                        Row(modifier = Modifier
                            .clip(corners(8.dp))
                            .background(Translucent)
                            .clickable {
                                showSpeeds = true
                            }
                            .padding(2.dp), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SPEED",
                                color = White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.width(2.dp))

                            Row(
                                modifier = Modifier
                                    .clip(corners(5.dp))
                                    .background(TranslucentBlack),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$currentSpeed",
                                    color = White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )

                                Icon(
                                    painter = painterResource(id = R.drawable.speed),
                                    contentDescription = "",
                                    modifier = Modifier.size(12.dp),
                                    tint = White
                                )
                            }
                        }
                    }
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

                Image(
                    painter = painterResource(id = R.drawable.lyrcis),
                    contentDescription = "",
                    modifier = Modifier
                        .clip(corners(90.dp))
                        .size(35.dp)
                        .background(Translucent)
                        .clickable {
                            isLyricsVisible = true
                        }
                        .padding(5.dp),
                    colorFilter = ColorFilter.tint(Orange)
                )
            }
        }

        Column(
            modifier = Modifier
                .height(if (!isLyricsVisible) 200.dp else 145.dp)
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

@Composable
fun LyricsView(
    context: Activity,
    lyricsManager: LyricsManager,
    currentSong: SongData,
    onClose: () -> Unit
) {
    var lyrics by remember {
        mutableStateOf(lyricsManager.getSongLyric(currentSong.id))
    }

    var showManualSearch by remember {
        mutableStateOf(false)
    }

    var isSearching by remember {
        mutableStateOf(false)
    }

    var isEditing by remember {
        mutableStateOf(false)
    }

    var editedLyrics by remember {
        mutableStateOf(lyrics)
    }

    var selectedVerse by remember {
        mutableIntStateOf(-1)
    }

    var verses by remember {
        mutableStateOf(listOf<String>())
    }

    val optimisedSearchParameters = cleanSearchParameter(currentSong.title) + "+lyrics"

    val googleSearch = remember {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/search?q=$optimisedSearchParameters")
        )
    }

    BackHandler(showManualSearch) {
        showManualSearch = false
    }

    LaunchedEffect(key1 = currentSong) {
        lyrics = lyricsManager.getSongLyric(currentSong.id)
    }

    LaunchedEffect(key1 = lyrics) {
        editedLyrics = lyrics
        if (lyrics != null) {
            verses = listOf()
            val allVerses = lyrics!!.split("\\n")

            for (verse in allVerses) {
                verses += verse
            }
        } else {
            verses = listOf()
        }
    }

    Box(
        Modifier
            .fillMaxSize()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "",
                modifier = Modifier
                    .clip(corners(90.dp))
                    .size(35.dp)
                    .background(Translucent)
                    .clickable {
                        onClose()
                    }
                    .padding(5.dp),
                colorFilter = ColorFilter.tint(Orange)
            )

            if (lyrics != null) {
                Row(
                    modifier = Modifier
                        .height(35.dp)
                        .clip(corners(10.dp))
                        .background(Translucent), verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEditing) {
                        Box(modifier = Modifier
                            .fillMaxHeight()
                            .clickable {
                                isEditing = false
                            }
                            .padding(5.dp)
                        ) {
                            Text(text = "Cancel", color = LighterGray)
                        }

                        Box(modifier = Modifier
                            .fillMaxHeight()
                            .clickable(!editedLyrics.isNullOrEmpty()) {
                                lyricsManager.saveLyric(currentSong.id, editedLyrics!!)
                                isEditing = false
                                lyrics = lyricsManager.getSongLyric(currentSong.id)
                            }
                            .padding(5.dp)
                        ) {
                            Text(
                                text = "Save",
                                color = if (editedLyrics.isNullOrEmpty()) Color.Gray else Orange,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                            .fillMaxHeight()
                            .background(Opaque)
                            .clickable {
                                isEditing = true
                            }
                            .padding(start = 2.5.dp, end = 2.5.dp)
                            .padding(3.dp)
                        ) {
                            Text(text = "Edit Lyrics", color = Orange, fontWeight = FontWeight.Bold)

                            Image(
                                painter = painterResource(id = R.drawable.edit),
                                contentDescription = "",
                                modifier = Modifier,
                                colorFilter = ColorFilter.tint(Orange)
                            )
                        }

                        Image(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "",
                            modifier = Modifier
                                .height(35.dp)
                                .clickable {
                                    lyricsManager.deleteLyric(currentSong.id)
                                    lyrics = lyricsManager.getSongLyric(currentSong.id)
                                }
                                .padding(start = 2.5.dp, end = 2.5.dp)
                                .padding(5.dp),
                            colorFilter = ColorFilter.tint(LighterGray)
                        )
                    }
                }
            }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .padding(top = 70.dp)
                .noGleamTaps {

                }
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(MildTranslucentBlack)
                .padding(bottom = 145.dp)
                .padding(start = 10.dp, end = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = currentSong.title,
                color = White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )

            if (currentSong.artist != "<unknown>") {
                Text(
                    text = currentSong.artist,
                    color = White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp
                )
            }

            LazyColumn(
                Modifier
                    .fillMaxSize()
            ) {
                if (verses.isNotEmpty() && editedLyrics != null) {
                    if (isEditing) {
                        item {
                            Text(
                                text = "Edit Lyrics. Use '\\n' to separate verses",
                                color = White,
                                fontWeight = FontWeight.ExtraBold
                            )
                            TextField(
                                value = editedLyrics!!,
                                onValueChange = {
                                    editedLyrics = it
                                },
                                shape = corners(5.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = TranslucentOrange,
                                    unfocusedIndicatorColor = Transparent,
                                    focusedIndicatorColor = Transparent,
                                    unfocusedContainerColor = TranslucentOrange
                                ),
                                modifier = Modifier.width(screenWidth().dp),
                                textStyle = TextStyle(color = White, fontSize = 17.sp)
                            )

                            Spacer(modifier = Modifier.height(200.dp))
                        }
                    } else {
                        items(verses.size) {
                            if(verses[it] == ""){
                                Spacer(modifier = Modifier.height(10.dp))
                            } else {
                                Text(
                                    text = verses[it],
                                    color = if (selectedVerse == it) Orange else White,
                                    fontWeight = if (selectedVerse == it) FontWeight.ExtraBold else FontWeight.Normal,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(corners(5.dp))
                                        .background(if (selectedVerse == it) MildTranslucentOrange else Transparent)
                                        .noGleamTaps {
                                            selectedVerse = it
                                        }
                                        .padding(3.dp)
                                )

                                Spacer(modifier = Modifier.height(3.dp))
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(50.dp))
                    }
                } else {
                    item {
                        Column(
                            modifier = Modifier.fillParentMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = buildAnnotatedString {
                                append("No lyrics saved for ")

                                withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                                    append(currentSong.title)
                                }

                                append(", start searching")

                            }, maxLines = 3, color = White)

                            Spacer(modifier = Modifier.height(15.dp))

                            Box(modifier = Modifier
                                .clip(corners(5.dp))
                                .background(Orange)
                                .clickable(!isSearching) {
                                    isSearching = true
                                    lyricsManager.searchLyrics(
                                        artist = currentSong.artist,
                                        title = currentSong.title
                                    ) {
                                        isSearching = false

                                        if (it != null) {
                                            lyrics = it
                                            lyricsManager.saveLyric(
                                                songID = currentSong.id,
                                                lyrics = it
                                            )
                                        } else {
                                            Toast
                                                .makeText(
                                                    context,
                                                    "No lyrics found, try other search methods",
                                                    Toast.LENGTH_LONG
                                                )
                                                .show()
                                        }
                                    }
                                }
                                .padding(5.dp)
                            ) {
                                Text(text = "Automatic search", fontWeight = FontWeight.ExtraBold)
                            }

                            Spacer(modifier = Modifier.height(5.dp))

                            Box(modifier = Modifier
                                .clip(corners(10.dp))
                                .background(LighterGray)
                                .clickable(!isSearching) {
                                    showManualSearch = true
                                }
                                .padding(5.dp)
                            ) {
                                Text(text = "Manual Search", fontWeight = FontWeight.ExtraBold)
                            }

                            Spacer(modifier = Modifier.height(5.dp))

                            Box(modifier = Modifier
                                .clip(corners(10.dp))
                                .background(LighterGray)
                                .padding(5.dp)
                                .clickable(!isSearching) {
                                    context.startActivity(googleSearch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                                }
                            ) {
                                Text(
                                    text = "Manual Google Search",
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Spacer(modifier = Modifier.height(5.dp))

                            Box(modifier = Modifier
                                .clip(corners(10.dp))
                                .background(LighterGray)
                                .clickable {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        if (!copyFromClipBoard(context).isNullOrEmpty()) {
                                            lyricsManager.saveLyric(
                                                songID = currentSong.id,
                                                lyrics = copyFromClipBoard(context)!!
                                            )

                                            lyrics = lyricsManager.getSongLyric(currentSong.id)
                                        }
                                    }
                                }
                                .padding(5.dp)
                            ) {
                                Text(
                                    text = "Paste copied lyrics",
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(if (isEditing) 200.dp else 50.dp))
                    }
                }
            }
        }

        if (showManualSearch) {
            LyricManualSearch(
                artist = currentSong.artist,
                title = currentSong.title,
                isSearching = isSearching,
                onSearch = { artistParameter, titleParameter ->
                    isSearching = true

                    lyricsManager.searchLyrics(
                        artist = artistParameter,
                        title = titleParameter
                    ) {
                        if (it != null) {
                            lyrics = it
                            lyricsManager.saveLyric(
                                songID = currentSong.id,
                                lyrics = it
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "No lyrics found, try other search methods",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        isSearching = false
                        showManualSearch = false
                    }
                },
                onClose = {
                    showManualSearch = false
                }
            )
        }
    }

    if (isSearching) {
        Box(modifier = Modifier.padding(top = 70.dp)) {
            LoadingAnimation {
                isSearching = false
            }
        }
    }
}

@Composable
fun LyricManualSearch(artist: String, title: String, isSearching: Boolean, onSearch: (artist: String, title: String) -> Unit, onClose: () -> Unit) {
    var titleSearch by remember {
        mutableStateOf(title)
    }

    var artistSearch by remember {
        mutableStateOf(artist)
    }

    LaunchedEffect(key1 = artist, title) {
        titleSearch = title
        artistSearch = artist
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp))
                .background(TranslucentBlack)
                .noGleamTaps { },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column {
                Text(text = "Artist", color = White, fontWeight = FontWeight.ExtraBold)
                TextField(
                    value = artistSearch,
                    onValueChange = {
                        if (it.length <= 150) {
                            artistSearch = it
                        }
                    },
                    maxLines = 1,
                    placeholder = {
                        Text(text = "Artist", color = MildGray)
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
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column {
                Text(text = "Title", color = White, fontWeight = FontWeight.ExtraBold)
                TextField(
                    value = titleSearch,
                    onValueChange = {
                        if (it.length <= 150) {
                            titleSearch = it
                        }
                    },
                    maxLines = 1,
                    placeholder = {
                        Text(text = "Title", color = MildGray)
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
            }

            Spacer(modifier = Modifier.height(5.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(40.dp)
                    .clip(corners(10.dp))
                    .background(if (isSearching) Color.Gray else Orange)
                    .clickable(!isSearching) {
                        onSearch(
                            cleanSearchParameter(artist),
                            cleanSearchParameter(titleSearch)
                        )
                    }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Search",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }
        }

        Icon(imageVector = Icons.Default.Clear, contentDescription = "", tint = Orange, modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = 15.dp, end = 15.dp)
            .size(25.dp)
            .clickable {
                onClose()
            })
    }
}