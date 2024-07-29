package com.nullinnix.orange

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullinnix.orange.ui.theme.DarkerGray
import com.nullinnix.orange.ui.theme.LighterGray
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.Red
import com.nullinnix.orange.ui.theme.TranslucentBlack
import com.nullinnix.orange.ui.theme.White

@Composable
fun Dialog(
    text: AnnotatedString,
    negativeHint: String,
    positiveHint: String,
    onAction: (Boolean) -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(TranslucentBlack)
        .padding(15.dp),
        contentAlignment = Alignment.Center
    ){
        Column(modifier = Modifier
            .clip(corners(10.dp))
            .fillMaxWidth()
            .background(White)
        ) {
            Spacer(modifier = Modifier.height(5.dp))

            Text(text = text, modifier = Modifier.padding(horizontal = 5.dp))

            Spacer(modifier = Modifier.height(15.dp))

            Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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
    Box(modifier = Modifier
        .fillMaxSize()
        .background(TranslucentBlack)
        .padding(15.dp),
        contentAlignment = Alignment.Center
    ){
        Column(modifier = Modifier
            .clip(corners(10.dp))
            .fillMaxWidth()
            .background(White)
        ) {
            Spacer(modifier = Modifier.height(5.dp))

            Text(text = text, modifier = Modifier.padding(horizontal = 5.dp))

            Spacer(modifier = Modifier.height(15.dp))

            Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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