package com.nullinnix.orange

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.nullinnix.orange.misc.RequestMediaPermission
import com.nullinnix.orange.misc.checkMediaPermission
import com.nullinnix.orange.song_managing.SongsManager
import com.nullinnix.orange.ui.theme.Green
import com.nullinnix.orange.ui.theme.MildBlack
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.White
import com.nullinnix.orange.ui_utilities.Dialog

@Composable
fun HomeScreen(context: Activity) {
    var requestMediaPermission by remember {
        mutableStateOf(false)
    }

    val hasMediaPermission = checkMediaPermission()

    var showMediaPermissionDialog by remember {
        mutableStateOf(!hasMediaPermission)
    }

    val songsManager by remember {
        mutableStateOf(SongsManager(context))
    }

    val songsList = songsManager.songsList.value

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MildBlack)){
        if(songsList?.isEmpty() == true) {
            Text(
                text = if (!hasMediaPermission) buildAnnotatedString {
                    append("No songs found, make sure")
                    withStyle(
                        style = SpanStyle(
                            color = Orange,
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append(" Orange ")
                    }
                    append("has media permission")
                } else buildAnnotatedString {
                    append("No songs found")
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = White
            )
        }
    }

    if(showMediaPermissionDialog){
        Dialog(
            text = buildAnnotatedString {
                append("Grant Permission to")
                withStyle(SpanStyle(color = Orange, fontWeight = FontWeight.ExtraBold)) {
                    append(" Orange ")
                }

                append("to fetch music on your device")
            },
            negativeHint = "Deny",
            positiveHint = "Grant Permission",
        ){
            if(it){
                requestMediaPermission = true
            } else {
                showMediaPermissionDialog = false
            }
        }
    }

    if(requestMediaPermission){
        RequestMediaPermission()
    }
}