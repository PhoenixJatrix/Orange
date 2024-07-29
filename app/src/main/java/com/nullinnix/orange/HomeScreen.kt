package com.nullinnix.orange

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.nullinnix.orange.ui.theme.Orange

@Composable
fun HomeScreen() {
    var requestMediaPermission by remember {
        mutableStateOf(false)
    }

    val hasMediaPermission = checkMediaPermission()

    var showMediaPermissionDialog by remember {
        mutableStateOf(!hasMediaPermission)
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