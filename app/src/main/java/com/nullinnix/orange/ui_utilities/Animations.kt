package com.nullinnix.orange.ui_utilities

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nullinnix.orange.misc.corners
import com.nullinnix.orange.misc.noGleamTaps
import com.nullinnix.orange.ui.theme.LighterGray
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.TranslucentBlack
import com.nullinnix.orange.ui.theme.White
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun LoadingAnimation(
    onCancel: () -> Unit
) {
    var rotate by remember {
        mutableStateOf(false)
    }

    var updateSweep by remember {
        mutableIntStateOf(0)
    }

    var showCancel by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = Unit) {
        delay(30000)

        showCancel = true
    }

    var sweepAngle by remember {
        mutableFloatStateOf(0f)
    }

    LaunchedEffect(key1 = Unit) {
        rotate = true
    }

    LaunchedEffect(key1 = updateSweep) {
        sweepAngle = 300f

        delay(3000)

        sweepAngle = 10f

        delay(3000)

        updateSweep += 1
    }

    val rotationAnim by animateFloatAsState(targetValue = if(rotate) 30000f else 0f, label = "", animationSpec = tween(100000, easing = LinearEasing))
    val sweepAngleAnim by animateFloatAsState(targetValue = sweepAngle, label = "", animationSpec = tween(3000))

    Box (
        Modifier
            .fillMaxSize()
            .background(TranslucentBlack)
            .noGleamTaps {

            }, contentAlignment = Alignment.Center
    ){
        Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()){
            Canvas(
                modifier = Modifier
                    .rotate(if (!showCancel) rotationAnim else 0f)
                    .size(50.dp)
            ) {
                drawArc(
                    color = Orange,
                    startAngle = 0f,
                    sweepAngle = if(!showCancel) sweepAngleAnim else 360f,
                    useCenter = false,
                    style = Stroke(width = 10f, cap = StrokeCap.Round)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            if(showCancel){
                Box(modifier = Modifier
                    .clip(corners(10.dp))
                    .background(Orange)
                    .clickable {
                        onCancel()
                    }
                    .padding(5.dp), contentAlignment = Alignment.Center){
                    Text(text = "Cancel", fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
fun PlayingIndicator() {
    Row(modifier = Modifier
        .size(23.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween){
        LoneIndicator()
        LoneIndicator()
        LoneIndicator()
    }
}

@Composable
fun LoneIndicator() {
    var shrink by remember {
        mutableStateOf(true)
    }

    var firstComposition by remember {
        mutableStateOf(true)
    }

    var update by remember {
        mutableIntStateOf(0)
    }

    val indicatorHeight by animateDpAsState(targetValue = if(shrink) Random.nextInt(0, 10).dp else Random.nextInt(5, 25).dp, label = "", animationSpec = spring(
        Spring.DampingRatioMediumBouncy)
    )

    LaunchedEffect(key1 = update) {
        if(firstComposition){
            delay(Random.nextLong(100, 1000))
            firstComposition = false
        }

        shrink = true

        delay(Random.nextLong(400, 700))

        shrink = false

        delay(Random.nextLong(400, 700))

        update += 1
    }

    Canvas(modifier = Modifier.height(indicatorHeight)) {
        drawLine(color = if(indicatorHeight.value.toInt() in 5..12) LighterGray else White, start = Offset.Zero, end = Offset(0f, this.size.height), strokeWidth = 20f, cap = StrokeCap.Round)
    }
}