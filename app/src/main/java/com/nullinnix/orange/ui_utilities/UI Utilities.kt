package com.nullinnix.orange.ui_utilities

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.nullinnix.orange.ADDED_TO_PLAYLIST
import com.nullinnix.orange.CANCEL_SELECTION
import com.nullinnix.orange.CREATED_NEW_PLAYLIST
import com.nullinnix.orange.DELETE_SELECTED
import com.nullinnix.orange.END_SEEKING
import com.nullinnix.orange.LOOPING_ALL
import com.nullinnix.orange.LOOPING_SINGLE
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
import com.nullinnix.orange.song_managing.PlayerClickActions.Companion.ADD_SONG_TO_PLAYLIST
import com.nullinnix.orange.song_managing.PlayerClickActions.Companion.CHANGE_ALBUM_COVER
import com.nullinnix.orange.song_managing.PlayerClickActions.Companion.DELETE_SONG
import com.nullinnix.orange.song_managing.PlayerClickActions.Companion.GO_TO_ARTIST
import com.nullinnix.orange.song_managing.PlayerClickActions.Companion.SHOW_PLAYER
import com.nullinnix.orange.song_managing.PlayerClickActions.Companion.SKIP_BACK5
import com.nullinnix.orange.song_managing.PlayerClickActions.Companion.SKIP_FORWARD5
import com.nullinnix.orange.song_managing.PlayerClickActions.Companion.TOGGLE_TIME_REMAINING
import com.nullinnix.orange.song_managing.PlaylistManager
import com.nullinnix.orange.ui.theme.Black
import com.nullinnix.orange.ui.theme.DarkerGray
import com.nullinnix.orange.ui.theme.LighterGray
import com.nullinnix.orange.ui.theme.MildBlack
import com.nullinnix.orange.ui.theme.MildGray
import com.nullinnix.orange.ui.theme.MildTranslucentBlack
import com.nullinnix.orange.ui.theme.MildTranslucentOrange
import com.nullinnix.orange.ui.theme.MilderGray
import com.nullinnix.orange.ui.theme.Opaque
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
import kotlin.time.Duration.Companion.days

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
fun Slider(
    currentValue: Int,
    maxValue: Int,
    onStartDrag: () -> Unit,
    onDragging: (Int) -> Unit,
    onStopDrag: (Float) -> Unit
) {
    val width = screenWidth()

    val minValue = 0

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

    var percentage by remember {
        mutableFloatStateOf(getPercent(sliderWidth.toFloat(), positionDp.value))
    }

    var percentageCovered by remember {
        mutableStateOf(if (percentage < 0) minValue else if (percentage >= 100) 100 else if (percentage == 0f) minValue else percentage)
    }

    var onDrag by remember {
        mutableStateOf(false)
    }

    val currentMaxValue by rememberUpdatedState(newValue = maxValue)
    val currentPosition by rememberUpdatedState(newValue = currentValue)

    val heightAnim by animateDpAsState(
        targetValue = if (onDrag) 20.dp else 10.dp,
        label = "",
        animationSpec = tween(500)
    )

    val positionAnim by animateDpAsState(
        targetValue = positionDp,
        label = "",
        animationSpec = tween(100)
    )

    LaunchedEffect(key1 = currentPosition) {
        if (!onDrag) {
            with(density) {
                positionPixels = getPercentage(sliderWidth.dp.toPx(), getPercent(currentMaxValue.toFloat(), currentPosition.toFloat()))

                positionDp = if (positionPixels.toDp() < 0.dp) 0.dp else if (positionPixels.toDp() > sliderWidth.dp) sliderWidth.dp else positionPixels.toDp()

                percentage = getPercent(sliderWidth.toFloat(), positionDp.value)

                percentageCovered = if (percentage < 0) minValue else if (percentage >= 100) 100 else if (percentage == 0f) minValue else percentage
            }
       }
    }

    Box(
        modifier = Modifier
            .width(sliderWidth.dp)
            .height(heightAnim)
            .clip(corners(90.dp))
            .clipToBounds()
        , contentAlignment = Alignment.CenterStart
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

                            percentage = getPercent(sliderWidth.toFloat(), positionDp.value)

                            percentageCovered =
                                if (percentage < 0) minValue else if (percentage >= 100) 100 else if (percentage == 0f) minValue else percentage
                            onStopDrag(
                                percentageCovered.toFloat()
                            )
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            onDrag = true
                        },
                        onDragEnd = {
                            onDrag = false
                            onStopDrag(
                                percentageCovered.toFloat()
                            )
                        },
                        onDrag = { change, _ ->
                            onDrag = true

                            onStartDrag()
                            positionPixels = change.position.x

                            positionDp = with(density) {
                                if (positionPixels.toDp() < 0.dp) 0.dp else if (positionPixels.toDp() > sliderWidth.dp) sliderWidth.dp else positionPixels.toDp()
                            }

                            percentage = getPercent(sliderWidth.toFloat(), positionDp.value)

                            percentageCovered =
                                if (percentage < 0) minValue else if (percentage >= 100) 100 else if (percentage == 0f) minValue else percentage

                            onDragging(
                                getPercentage(
                                    currentMaxValue.toFloat(),
                                    percentageCovered.toFloat()
                                ).toInt()
                            )
                        }
                    )
                }
        ) {

        }

        Row(
            Modifier
                .width(positionAnim)
                .height(heightAnim)
                .clip(
                    corners(
                        topStart = 90.dp,
                        bottomStart = 90.dp,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .background(Orange)
        ) {

        }
    }
}

@Composable
fun MediaControlButtons(
    modifier: Modifier,
    mediaPlayerState: Int,
    currentSong: SongData,
    currentPosition: Int,
    isLiked: Boolean,
    isShuffling: Boolean,
    onSongEndAction: Int,
    showTimeRemaining: Boolean,
    showTitle: Boolean,
    isLyricView: Boolean = false,
    onAction: (Int) -> Unit,
    onSeek: (action: Int, seekPosition: Int?) -> Unit
) {
    var showSlider by remember{
        mutableStateOf(true)
    }

    var showMenu by remember{
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = isLyricView) {
        if(isLyricView){
            onAction(LOOPING_SINGLE)
        } else {
            onAction(LOOPING_ALL)
        }
    }

    LaunchedEffect(key1 = currentSong) {
        showSlider = false
        delay(50)
        showSlider = true
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(if (!isLyricView) 200.dp else 145.dp)
            .animateContentSize()
            .noGleamTaps {
                onAction(SHOW_PLAYER)
            }
            .padding(horizontal = 5.dp)
    ) {
        if(showTitle) {
            Text(
                text = currentSong.title,
                color = White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = 14.sp
            )
        }

        Row (
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = durationMillisToStringMinutes(currentPosition.toLong()),
                color = White,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = 14.sp
            )

            Text(
                text = if(showTimeRemaining) "-" + durationMillisToStringMinutes(currentSong.duration - currentPosition) else durationMillisToStringMinutes(currentSong.duration),
                color = White,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.noGleamTaps {
                    onAction(TOGGLE_TIME_REMAINING)
                },
                fontSize = 14.sp
            )
        }

        Column(
            Modifier
                .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            if(showSlider) {
                Slider(
                    currentValue = currentPosition,
                    maxValue = currentSong.duration.toInt(),
                    onStartDrag = {
                        onSeek(START_SEEKING, null)
                    },
                    onDragging = {
                        onSeek(START_SEEKING, it)
                    },
                    onStopDrag = {
                        onSeek(
                            END_SEEKING,
                            getPercentage(currentSong.duration.toFloat(), it).toInt()
                        )
                    }
                )
            } else {
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically){
                Image(
                    painter = painterResource(id = if(isLyricView) R.drawable.skip_backwards_5 else R.drawable.skip_previous),
                    contentDescription = "",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            onAction(
                                if (isLyricView)
                                    SKIP_BACK5
                                else
                                    PlayerClickActions.SKIP_PREVIOUS
                            )
                        },
                    colorFilter = ColorFilter.tint(White)
                )

                Spacer(modifier = Modifier.width(40.dp))

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

                Spacer(modifier = Modifier.width(40.dp))

                Image(
                    painter = painterResource(id = if(isLyricView) R.drawable.skip_forward_5 else R.drawable.skip_next),
                    contentDescription = "",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            onAction(
                                if (isLyricView)
                                    SKIP_FORWARD5
                                else
                                    PlayerClickActions.SKIP_NEXT
                            )
                        },
                    colorFilter = ColorFilter.tint(White)
                )
            }
        }

        if(!isLyricView) {
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
                        .width(25.dp)
                        .height(21.dp)
                        .clickable {
                            onAction(PlayerClickActions.TOGGLE_SHUFFLE)
                        },
                    colorFilter = ColorFilter.tint(if (isShuffling) White else Translucent), contentScale = ContentScale.FillBounds
                )

                Image(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            onAction(PlayerClickActions.TOGGLE_LIKED)
                        },
                    colorFilter = ColorFilter.tint(White)
                )

                Image(
                    painter = painterResource(id = if (onSongEndAction == LOOPING_ALL || onSongEndAction == NOT_LOOPING) R.drawable.looping_all else R.drawable.looping_single),
                    contentDescription = "",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            onAction(PlayerClickActions.TOGGLE_SONG_ON_END_ACTION)
                        },
                    colorFilter = ColorFilter.tint(if (onSongEndAction == NOT_LOOPING) Translucent else White)
                )
            }
            Spacer(modifier = Modifier.height(55.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HoverSlider(
    label: String,
    currentValue: Float,
    maxValue: Float,
    minValue: Float,
    onChange: (Int) -> Unit,
) {
    var isHovering by remember {
        mutableStateOf(false)
    }

    var ignoreReset by remember {
        mutableStateOf(true)
    }

    var settledOnCenter by remember {
        mutableStateOf(true)
    }

    var hoverIconsOffset by remember {
        mutableStateOf((-15).dp)
    }

    var hoverAnimUpdater by remember {
        mutableIntStateOf(0)
    }

    val density = LocalDensity.current

    var hoverDirection by remember {
        mutableIntStateOf(CENTER)
    }
    
    val currentValueState by rememberUpdatedState(newValue = currentValue)

    val topColorAnim by animateColorAsState(targetValue = translucentColor(Orange, getPercent(maxValue, currentValueState) / 100f), label = "", animationSpec = tween(1000))
    val bottomColorAnim by animateColorAsState(targetValue = translucentColor(Orange, getPercent(maxValue, maxValue - currentValueState) / 100f), label = "", animationSpec = tween(1000))

    LaunchedEffect(key1 = hoverAnimUpdater) {
        if(isHovering && hoverDirection == CENTER && settledOnCenter) {
            hoverIconsOffset = (-40).dp
            delay(1000)

            if(isHovering) {
                hoverIconsOffset = (-15).dp
                delay(1000)
            }
        } else {
            hoverIconsOffset = (-40).dp
        }

        hoverAnimUpdater += 1
    }

    LaunchedEffect(key1 = hoverDirection, isHovering) {
        delay(100)

        if (isHovering) {
            for (value in 0 until 18) {
                when (hoverDirection) {
                    TOP_HALF -> {
                        if (currentValueState < maxValue) {
                            onChange(TOP_HALF)
                        } else {
                            break
                        }
                    }

                    LOWER_HALF -> {
                        if (currentValueState > minValue) {
                            onChange(LOWER_HALF)
                        } else {
                            break
                        }
                    }

                    else -> {
                        if (!ignoreReset) {
                            delay(350)
                            settledOnCenter = true
                            if (hoverDirection == CENTER && isHovering) {
                                onChange(CENTER)
                            } else {
                                break
                            }
                        } else {
                            break
                        }
                    }
                }
                delay(if(currentValueState == 0.75f || currentValueState == 1.25f) 1200 else 700)
            }
        }
    }

    val hoverIconsOffsetAnim by animateDpAsState(targetValue = hoverIconsOffset, label = "", animationSpec = tween(if(isHovering) 1000 else 100))

    val lowerArrowSize by animateDpAsState(targetValue = if (hoverDirection == LOWER_HALF && isHovering) 50.dp else 35.dp, label = "", animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy))
    val topArrowSize by animateDpAsState(targetValue = if (hoverDirection == TOP_HALF && isHovering) 50.dp else 35.dp, label = "", animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy))

    Box(modifier = Modifier) {
        Column(
            modifier = Modifier
                .height(200.dp)
                .width((screenWidth() - 200).dp / 2)
                .padding(horizontal = 15.dp)
                .clip(corners(90.dp))
                .background(brush = Brush.verticalGradient(listOf(topColorAnim, bottomColorAnim)))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            settledOnCenter = false
                            isHovering = true
                        },
                        onDrag = { change, _ ->
                            with(density) {
                                hoverDirection = when (change.position.y.toDp()) {
                                    in Int.MIN_VALUE.dp..50.dp -> {
                                        ignoreReset = false
                                        settledOnCenter = false
                                        TOP_HALF
                                    }

                                    in 150.dp..Int.MAX_VALUE.dp -> {
                                        ignoreReset = false
                                        settledOnCenter = false
                                        LOWER_HALF
                                    }

                                    else -> {
                                        CENTER
                                    }
                                }
                            }
                        },
                        onDragEnd = {
                            settledOnCenter = false
                            ignoreReset = true
                            isHovering = false
                            hoverIconsOffset = 0.dp
                        }
                    )
                }
                .combinedClickable(
                    onDoubleClick = {
                        onChange(CENTER)
                    },
                    onLongClick = {
                        onChange(CENTER)
                    },
                    onClick = {

                    }
                ),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier
                .offset(y = hoverIconsOffsetAnim)
                .fillMaxWidth()
                .height(50.dp)
                .clickable(currentValueState < maxValue) {
                    if (currentValueState < maxValue) {
                        onChange(TOP_HALF)
                    }
                }, contentAlignment = Alignment.BottomCenter){
                Image(
                    imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "",
                    modifier = Modifier
                        .size(topArrowSize),
                    colorFilter = ColorFilter.tint(if (currentValueState < maxValue) White else Color.Gray),
                )
            }

            Box(modifier = Modifier
                .offset(y = -(hoverIconsOffsetAnim))
                .fillMaxWidth()
                .height(50.dp)
                .clickable(currentValueState > minValue) {
                    if (currentValueState > minValue) {
                        onChange(LOWER_HALF)
                    }
                }, contentAlignment = Alignment.TopCenter){
                Image(
                    imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "",
                    modifier = Modifier
                        .size(lowerArrowSize),
                    colorFilter = ColorFilter.tint(if (currentValueState > minValue) White else Color.Gray),
                )
            }
        }


        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isHovering) {
                for(letter in label){
                    Text(
                        text = letter.toString(),
                        fontWeight = FontWeight.ExtraBold,
                        color = White,
                        fontSize = 14.sp,
                        letterSpacing = 0.sp,
                        lineHeight = 14.sp, textAlign = TextAlign.Center
                    )
                }

                Text(
                    text = "\n${if(currentValueState == 1f) "Normal" else currentValueState}",
                    fontWeight = FontWeight.ExtraBold,
                    color = White,
                    fontSize = 14.sp,
                    letterSpacing = 0.sp,
                    lineHeight = 14.sp, textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "${if(currentValueState == 1f) "Normal" else currentValueState}",
                    fontWeight = FontWeight.ExtraBold,
                    color = White,
                    fontSize = 18.sp
                )
            }
        }
    }
}

const val LOWER_HALF = -1
const val CENTER = 0
const val TOP_HALF = 1

@Composable
fun Menu(
    onClick: (Int) -> Unit
) {
    var animateWidth by remember {
        mutableStateOf(false)
    }

    val widthAnim by animateDpAsState(targetValue = if(animateWidth) 250.dp else 0.dp, label = "")

    LaunchedEffect(key1 = Unit) {
        animateWidth = true
    }

    BackHandler(true) {
        CoroutineScope(Dispatchers.Main).launch {
            animateWidth = false

            delay(300)

            onClick(-1)
        }
    }

    Column(
        modifier = Modifier
            .height(200.dp)
            .width(widthAnim)
            .clip(corners(topEnd = 0.dp))
            .background(TranslucentBlack)
            .padding(top = 15.dp, start = 5.dp, end = 5.dp, bottom = 15.dp), verticalArrangement = Arrangement.SpaceAround
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(ADD_SONG_TO_PLAYLIST)
            }, verticalAlignment = Alignment.CenterVertically){
            Image(painter = painterResource(id = R.drawable.add_to_playlist), contentDescription = "", modifier = Modifier.size(25.dp))

            Spacer(Modifier.width(5.dp))

            Text(text = "Add to playlist", color = White, fontWeight = FontWeight.ExtraBold)
        }

        Spacer(Modifier.height(15.dp))

        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(GO_TO_ARTIST)
            }, verticalAlignment = Alignment.CenterVertically){
            Image(painter = painterResource(id = R.drawable.artist), contentDescription = "", modifier = Modifier.size(25.dp))

            Spacer(Modifier.width(5.dp))

            Text(text = "Songs by the same artist", color = White, fontWeight = FontWeight.ExtraBold)
        }

        Spacer(Modifier.height(15.dp))

        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(CHANGE_ALBUM_COVER)
            }, verticalAlignment = Alignment.CenterVertically){
            Image(painter = painterResource(id = R.drawable.album_cover), contentDescription = "", modifier = Modifier.size(25.dp))

            Spacer(Modifier.width(5.dp))

            Text(text = "Change album cover", color = White, fontWeight = FontWeight.ExtraBold)
        }

        Spacer(Modifier.height(15.dp))

        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(DELETE_SONG)
            }, verticalAlignment = Alignment.CenterVertically){
            Image(painter = painterResource(id = R.drawable.delete), contentDescription = "", modifier = Modifier.size(25.dp), colorFilter = ColorFilter.tint(Red))

            Spacer(Modifier.width(5.dp))

            Text(text = "Delete song", color = Red, fontWeight = FontWeight.ExtraBold)
        }
    }
}