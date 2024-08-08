package com.nullinnix.orange.misc

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nullinnix.orange.SongData
import com.nullinnix.orange.VIEWING_ALBUMS
import com.nullinnix.orange.VIEWING_ARTISTS
import com.nullinnix.orange.VIEWING_GENRE
import com.nullinnix.orange.song_managing.ALL_SONGS
import com.nullinnix.orange.song_managing.LIKED_SONGS
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.COMPLETED
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.DORMANT
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.DATASOURCE_NOT_SET
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.DATASOURCE_SET
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.PAUSED
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.PLAYING
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.PREPARED
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.PREPARING
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.RESET
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate

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

fun translucentColor(color: Color, alpha: Float = 0.3f): Color {
    return Color(
        red = color.red,
        green = color.green,
        blue = color.blue,
        alpha = if(alpha >= 1f) 1f else alpha
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
    if (!thumbnailsDirectory(context).exists()) {
        thumbnailsDirectory(context).mkdir()
    }

    if (!miscDirectory(context).exists()) {
        miscDirectory(context).mkdir()
    }

    if (!playlistDirectory(context).exists()) {
        playlistDirectory(context).mkdir()
    }

    if(!playlistsFile(context).exists()){
        writeRedundantFile(playlistsFile(context))
    }

    if (!lastMusicLog(context).exists()) {
        writeRedundantFile(lastMusicLog(context))
    }

    if (!likedSongsFile(context).exists()) {
        writeRedundantFile(likedSongsFile(context))
    }

    if (!lastPlaylistFile(context).exists()) {
        writeRedundantFile(file = lastPlaylistFile(context), data = ALL_SONGS)
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

fun playlistsFile(context: Context): File{
    return File(playlistDirectory(context), "playlists.txt")
}

fun playlistDirectory(context: Context): File{
    return File(context.filesDir, "Playlists")
}

fun likedSongsFile(context: Context): File{
    return File(playlistDirectory(context), "$LIKED_SONGS.txt")
}

fun lastPlaylistFile(context: Context): File{
    return File(playlistDirectory(context), "lastPlaylist.txt")
}

//fun checkFirstRun(context: Context): Boolean {
//    return firstRunFile(context).exists()
//}

fun writeRedundantFile(file: File, data: String = "") {
    try {
        val fos = FileOutputStream(file)

        fos.write(data.toByteArray())

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

fun getDate(): String {
    return "${LocalDate.now().dayOfMonth}:${LocalDate.now().monthValue}:${LocalDate.now().year}"
}

fun getInterval(allSongsInPlaylist: List<String>, selectedSongs: List<String>): List<String>{
    val intersection = mutableListOf<String>()
    var startIndex = -1
    var endIndex = -1

    for(song in allSongsInPlaylist.indices){
        if(selectedSongs.contains(allSongsInPlaylist[song])){
            startIndex = song
            break
        }
    }

    for(song in allSongsInPlaylist.indices.reversed()){
        if(selectedSongs.contains(allSongsInPlaylist[song])){
            endIndex = song
            break
        }
    }

    IntRange(startIndex, endIndex).forEach {
        intersection.add(allSongsInPlaylist[it])
    }

    return intersection
}

fun searchSongsByName(searchParameter: String, songsInPlaylist: Map<String, SongData>): List<String> {
    val searchResults = mutableListOf<String>()

    for (song in songsInPlaylist) {
        if (
            song.value.displayName.lowercase().contains(searchParameter) ||
            song.value.title.lowercase().contains(searchParameter) ||
            song.value.album.lowercase().contains(searchParameter) ||
            song.value.artist.lowercase().contains(searchParameter)
        ) {
            searchResults.add(song.value.id)
        }
    }

    return searchResults
}

fun sortBy(sortType: Int, songsInPlaylist: Map<String, SongData>): Map<String, List<String>>{
    val filteredKeyValues = mutableMapOf<String, List<String>>()

    for(song in songsInPlaylist){
        when(sortType){
            VIEWING_ALBUMS -> {
                if(filteredKeyValues[song.value.album] != null){
                    filteredKeyValues[song.value.album] = filteredKeyValues[song.value.album]!! + song.value.id
                } else {
                    filteredKeyValues[song.value.album] = listOf(song.value.id)
                }
            }

            VIEWING_GENRE -> {
                if(song.value.genre != null) {
                    if(filteredKeyValues[song.value.genre] != null){
                        filteredKeyValues[song.value.genre!!] = filteredKeyValues[song.value.genre!!]!! + song.value.id
                    } else {
                        filteredKeyValues[song.value.genre!!] = listOf(song.value.id)
                    }
                }
            }

            VIEWING_ARTISTS -> {
                if(filteredKeyValues[song.value.artist] != null){
                    filteredKeyValues[song.value.artist] = filteredKeyValues[song.value.artist]!! + song.value.id
                } else {
                    filteredKeyValues[song.value.artist] = listOf(song.value.id)
                }
            }
        }
    }

    return filteredKeyValues
}

fun getAnyAvailableAlbumCover(allSongsInPlaylist: List<SongData>, context: Context) : Bitmap? {
    for(song in allSongsInPlaylist){
        if(song.thumbnail != null){
            return BitmapFactory.decodeFile(File(thumbnailsDirectory(context), "${song.id}.jpg").toString())
        }
    }

    return null
}

const val allowedKeys = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890 ()-"

const val ON_SKIP_NEXT = 1
const val ON_SKIP_PREVIOUS = -1
const val ON_TOGGLE_PLAY= 0
const val ON_CLOSE_MINI_PLAYER = 2
const val ON_OPEN_FULL_PLAYER = 3

val mediaPlayerStateAction = mapOf(
    Pair(DORMANT, "DORMANT"),
    Pair(PLAYING, "PLAYING"),
    Pair(PAUSED, "PAUSED"),
    Pair(COMPLETED, "COMPLETED"),
    Pair(DATASOURCE_SET, "DATASOURCE_SET"),
    Pair(PREPARING, "PREPARING"),
    Pair(RESET, "RESET"),
    Pair(PREPARED, "PREPARED"),
    Pair(DATASOURCE_NOT_SET, "DATASOURCE_NOT_SET")
)