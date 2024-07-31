package com.nullinnix.orange.misc

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.nullinnix.orange.misc.corners
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.TranslucentBlack
import com.nullinnix.orange.ui.theme.White
import kotlinx.coroutines.delay

@Composable
fun PermissionDialog() {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(TranslucentBlack)
        .padding(15.dp),
        contentAlignment = Alignment.Center
    ){
        Column(modifier = Modifier
            .clip(corners())
            .fillMaxWidth()
            .background(White)) {
            Text(text = buildAnnotatedString {
                append("Grant Permission to")
                withStyle(SpanStyle(color = Orange, fontWeight = FontWeight.ExtraBold)){
                    append(" Orange ")
                }

                append("to fetch music on your device")
            })
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun readImagePermissions(): MultiplePermissionsState {
    return rememberMultiplePermissionsState(
        when (Build.VERSION.SDK_INT) {
            Build.VERSION_CODES.TIRAMISU -> {
                listOf(Manifest.permission.READ_MEDIA_IMAGES)
            }

            in Build.VERSION_CODES.Q..Build.VERSION_CODES.S_V2 -> {
                listOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }

            in Build.VERSION_CODES.UPSIDE_DOWN_CAKE..Int.MAX_VALUE -> {
                listOf(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
            }

            else -> {
                listOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun checkMediaPermission(): Boolean{
    val mediaPermission = rememberMultiplePermissionsState(
        permissions = when(Build.VERSION.SDK_INT){
            in Int.MIN_VALUE..Build.VERSION_CODES.Q -> {
                listOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }

            else -> {
                listOf(
                    Manifest.permission.READ_MEDIA_AUDIO
                )
            }
        }
    )

    return mediaPermission.allPermissionsGranted
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestMediaPermission() {
    val mediaPermission = rememberMultiplePermissionsState(
        permissions = when(Build.VERSION.SDK_INT){
            in Int.MIN_VALUE..Build.VERSION_CODES.Q -> {
                listOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }

            else -> {
                listOf(
                    Manifest.permission.READ_MEDIA_AUDIO
                )
            }
        }
    )

    SideEffect {
        mediaPermission.launchMultiplePermissionRequest()
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun checkNotificationPermission(): Boolean {
    return rememberMultiplePermissionsState(permissions = listOf(Manifest.permission.POST_NOTIFICATIONS)).allPermissionsGranted || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun requestNotificationPermission(): MultiplePermissionsState {
    return rememberMultiplePermissionsState(permissions = listOf(Manifest.permission.POST_NOTIFICATIONS))
}

@OptIn(ExperimentalPermissionsApi::class)
fun getInitImage(onRequestPermission:() -> Unit, onGetImage: () -> Unit, permissions: MultiplePermissionsState) {
    if (permissions.permissions.size == 1) {
        if (permissions.permissions[0].status == PermissionStatus.Granted || Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            onGetImage()
        } else {
            onRequestPermission()
        }
    } else {
        if (permissions.permissions[1].status == PermissionStatus.Granted) {
            onGetImage()
        } else {
            onRequestPermission()
        }
    }
}

fun hasAllMediaPermissions(context: Context): Boolean {
    val mediaPermission = when(Build.VERSION.SDK_INT) {
        in Int.MIN_VALUE..Build.VERSION_CODES.Q -> {
            listOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        else -> {
            listOf(
                Manifest.permission.READ_MEDIA_AUDIO
            )
        }
    }

    for(permission in mediaPermission){
        if(context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
            return false
        }
    }

    return true
}