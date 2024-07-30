package com.nullinnix.orange.misc

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.io.File
import java.io.FileOutputStream

fun getPercentage(total: Int, percent: Int): Int {
    return (total * percent) / 100
}

fun getPercent(total: Int, valueFromTotal: Int, getMinimum: Int? = null): Int {
    try {
        return if (getMinimum != null && ((valueFromTotal * 100) / total) <= getMinimum) getMinimum else ((valueFromTotal * 100) / total)
    } catch (e: ArithmeticException) {
        e.printStackTrace()
    }

    return 0
}

fun getPercentage(total: Float, percent: Float): Float {
    try {
        return (total * percent) / 100f
    } catch (e: ArithmeticException) {
        e.printStackTrace()
    }

    return 0f
}

fun getPercent(total: Float, valueFromTotal: Float): Float {
    try {
        return ((valueFromTotal * 100f) / total)
    } catch (e: ArithmeticException) {
        e.printStackTrace()
    }

    return 0f
}

fun getDifference(min: Int, max: Int, percent: Int): Int {
    try {
        return if (percent >= 95) max else getPercentage(max - min, percent) + min
    } catch (e: ArithmeticException) {
        e.printStackTrace()
    }

    return 0
}

fun Modifier.noGleamTaps(enabled: Boolean = true, onClick: () -> Unit): Modifier = composed {
    val emptyClick = {}
    this then Modifier.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = if (enabled) onClick else emptyClick
    )
}

@Composable
fun screenHeight(): Int {
    return LocalConfiguration.current.screenHeightDp
}

@Composable
fun screenWidth(): Int {
    return LocalConfiguration.current.screenWidthDp
}

fun createPersistentFilesOnLaunch(context: Context) {
    if(!thumbnailsDirectory(context).exists()){
        thumbnailsDirectory(context).mkdir()
    }

    if(!miscDirectory(context).exists()){
        miscDirectory(context).mkdir()
    }

    if(!lastMusicLog(context).exists()){
        writeRedundantFile(lastMusicLog(context))
    }
}

fun thumbnailsDirectory(context: Context): File{
    return File(context.filesDir, "Thumbnails")
}

fun miscDirectory(context: Context): File{
    return File(context.filesDir, "Misc")
}

fun lastMusicLog(context: Context): File{
    return File(miscDirectory(context), "lastMusicLog.txt")
}

//fun checkFirstRun(context: Context): Boolean {
//    return firstRunFile(context).exists()
//}

fun writeRedundantFile(file: File) {
    try {
        val fos = FileOutputStream(file)

        fos.write("redundant".toByteArray())

        fos.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun corners(
    topStart: Dp = 20.dp,
    topEnd: Dp = 20.dp,
    bottomEnd: Dp = 20.dp,
    bottomStart: Dp = 20.dp
) = RoundedCornerShape(
    topStart = CornerSize(topStart),
    topEnd = CornerSize(topEnd),
    bottomEnd = CornerSize(bottomEnd),
    bottomStart = CornerSize(bottomStart)
)

fun corners(
    radius: Dp
) = RoundedCornerShape(
    topStart = CornerSize(radius),
    topEnd = CornerSize(radius),
    bottomEnd = CornerSize(radius),
    bottomStart = CornerSize(radius)
)

fun pasteToClipBoard(label: String, text: String, context: Context, toast: Boolean = true) {
    val clipBoardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    clipBoardManager.setPrimaryClip(ClipData.newPlainText(label, text))
    if (toast) {
        Toast.makeText(context, "$label Copied", Toast.LENGTH_SHORT).show()
    }
}

fun copyFromClipBoard(context: Context): String? {
    val clipBoardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    var clipData: String? = null

    if (clipBoardManager.hasPrimaryClip()) {
        clipData = clipBoardManager.primaryClip!!.getItemAt(0).coerceToText(context).toString()
    }

    return if (clipData == null) {
        Toast.makeText(context, "Clipboard is empty.", Toast.LENGTH_SHORT).show()
        null
    } else {
        clipData
    }
}

fun cleanJSON(json: String): List<String> {
    val stripedJson = json.substring(1, json.length - 1)
    val validatedPairs = mutableListOf<String>()

    var kv = ""
    var quotesCounter = 0
    var curlyClosed = true
    var squareClosed = true

    for (index in stripedJson.indices) {
        if (stripedJson[index] == '"') {
            quotesCounter++
        }

        if (stripedJson[index] == '{') {
            curlyClosed = false
        }

        if (stripedJson[index] == '[') {
            squareClosed = false
        }

        if (stripedJson[index] == '}') {
            curlyClosed = true
        }

        if (stripedJson[index] == ']') {
            squareClosed = true
        }

        if (stripedJson[index] == ',') {
            if (!curlyClosed) {
                kv += stripedJson[index]
                continue
            }

            if (!squareClosed) {
                kv += stripedJson[index]
                continue
            }

            if (quotesCounter % 2 == 1) {
                kv += stripedJson[index]
                continue
            } else {
                validatedPairs.add(kv)
                kv = ""
                quotesCounter = 0
            }
        } else {
            kv += stripedJson[index]
            if (index == stripedJson.length - 1) {
                validatedPairs.add(kv)
            }
        }
    }
    return validatedPairs
}

fun durationMillisToStringMinutes(duration: Long): String{
    val durationInSeconds = duration / 1000
    var minutes = "${durationInSeconds / 60}"
    var seconds = "${durationInSeconds % 60}"

    minutes = if(minutes.toInt() < 10) "0$minutes" else minutes
    seconds = if(seconds.toInt() < 10) "0$seconds" else seconds
    return "$minutes:$seconds"
}

const val ON_SKIP_NEXT = 1
const val ON_SKIP_PREVIOUS = -1
const val ON_TOGGLE_PLAY= 0
const val ON_CLOSE_MINI_PLAYER = 2
const val ON_OPEN_FULL_PLAYER = 3