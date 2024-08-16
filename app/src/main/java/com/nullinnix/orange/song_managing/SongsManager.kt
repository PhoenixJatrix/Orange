package com.nullinnix.orange.song_managing

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.shape.CornerSize
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nullinnix.orange.SongData
import com.nullinnix.orange.misc.lastMusicLog
import com.nullinnix.orange.misc.numbers
import com.nullinnix.orange.misc.playOptionsFile
import com.nullinnix.orange.misc.songsPlayCountFile
import com.nullinnix.orange.misc.thumbnailsDirectory
import com.nullinnix.orange.misc.totalPlaytimeFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader

class SongsManager (private val context: Activity) {
    var songsList = MutableLiveData(mutableMapOf<String, SongData>())
    var update = MutableLiveData(UPDATE_SONGS)

    @SuppressLint("InlinedApi")
    fun getSongs(onDone: (MutableMap<String, SongData>) -> Unit) {
        val protoSongs = mutableMapOf<String, SongData>()

        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.GENRE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.AUTHOR
        )

        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            ""
        )

        while (cursor?.moveToNext() == true) {
            val songData = SongData(
                title = cursor.getString(0),
                path = cursor.getString(1),
                id = cursor.getString(2),
                displayName = cursor.getString(3),
                duration = cursor.getString(4).toLong(),
                album = cursor.getString(5),
                genre = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) cursor.getString(6) else null,
                artist = cursor.getString(7),
                thumbnail = if(File(thumbnailsDirectory(context), "${cursor.getString(2)}.jpg").exists()) getThumbnailFromPath(cursor.getString(2)) else createSingleThumbnail(saveID = cursor.getString(2), originalFilePath = cursor.getString(1))
            )

            protoSongs[songData.id] = songData
        }

        cursor?.close()
        songsList.value = protoSongs
        onDone(songsList.value!!)
    }

    private fun removeUnusedThumbnails(){
        val logs = getLastLog()
        val allThumbnails = thumbnailsDirectory(context).listFiles()

        allThumbnails?.forEach {
            if(!logs.contains(it.name.split(".")[0])){
                it.delete()

            }
        }
    }

    fun isSongsUpdated() {
        val currentLog = mutableListOf<String>()
        var lastLog = getLastLog()

        if (songsList.value!!.isNotEmpty()) {
            for (song in songsList.value!!.keys) {
                currentLog.add(song)
            }
        }

        if (lastLog.isEmpty() && songsList.value!!.isNotEmpty()) {
            setLastLog(currentLog)
            lastLog = getLastLog()
        }

        for (log in lastLog) {
            if (!currentLog.contains(log)) {
                setLastLog(currentLog)
                removeUnusedThumbnails()
                break
            }
        }
    }

    fun getLastLog(): List<String>{
        val lastLog = mutableListOf<String>()

        try {
            val fis = FileInputStream(lastMusicLog(context))
            val reader = BufferedReader(InputStreamReader(fis))
            var line = reader.readLine()

            while (line != null){
                lastLog.add(line)
                line = reader.readLine()
            }

            fis.close()
            reader.close()
        } catch (e: Exception){
            e.printStackTrace()
        }

        return lastLog
    }

    private fun setLastLog(logs: List<String>?){
        try {
            val fos = FileOutputStream(lastMusicLog(context))

            if(logs != null){
                for(log in logs){
                    fos.write("$log\n".toByteArray())
                }
            } else {
                fos.write("".toByteArray())
            }

            fos.close()

        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun setPlayingOptions(oldPlayingOptions: Map<String, Any>){
        try {
            val fos = FileOutputStream(playOptionsFile(context))
            for(option in oldPlayingOptions){
                fos.write("${option.key}:${option.value}\n".toByteArray())
            }

            fos.close()

        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun getPlayingOptions(): Map<String, Any>{
        val playingOptions = mutableMapOf<String, Any>()

        try {
            val fis = FileInputStream(playOptionsFile(context))
            val reader = BufferedReader(InputStreamReader(fis))
            var line = reader.readLine()

            while (line != null){
                val key = line.split(":")[0]
                val value = line.split(":")[1]

                if(numbers.split("").contains(value)){
                    playingOptions[key] = value.toInt()
                } else {
                    playingOptions[key] = value == "true"
                }

                line = reader.readLine()
            }

            fis.close()
            reader.close()
        } catch (e: Exception){
            e.printStackTrace()
        }

        return playingOptions
    }

    private fun getThumbnailFromPath(id: String): Bitmap? {
        return if(File(thumbnailsDirectory(context),"${id}.jpg").exists()) BitmapFactory.decodeFile(File(thumbnailsDirectory(context),"${id}.jpg").toString()) else null
    }

    private fun createSingleThumbnail(saveID: String, originalFilePath:String): Bitmap? {
        val meta = MediaMetadataRetriever()
        meta.setDataSource(originalFilePath)
        val picture = meta.embeddedPicture
        if (picture != null) {
            val bitmapThumbnail =
                BitmapFactory.decodeByteArray(picture, 0, picture.size)

            bitmapThumbnail.compress(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP,
                80,
                FileOutputStream(File(thumbnailsDirectory(context), "$saveID.jpg"))
            )

            return bitmapThumbnail
        }

        return null
    }

    fun setTotalPlaytime(){
        try {
            val currentTime = getTotalPlaytime()

            val fos = FileOutputStream(totalPlaytimeFile(context))

            fos.write("${currentTime + 5}".toByteArray())

            fos.close()

        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun getTotalPlaytime(): Int {
        try {
            val fis = FileInputStream(totalPlaytimeFile(context))
            val reader = BufferedReader(InputStreamReader(fis))
            val line = reader.readLine()

            fis.close()
            reader.close()

            return line.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }

    fun setSongPlayCount(songID: String) {
        val oldPlayedSongs = getSongPlayCount()
        var alreadyInList = false

        try {
            val fos = FileOutputStream(songsPlayCountFile(context))
            for(song in oldPlayedSongs){
                if(song.key == songID){
                    alreadyInList = true
                    fos.write("${songID}:${song.value + 1}\n".toByteArray())
                } else {
                    fos.write("${song.key}:${song.value}\n".toByteArray())
                }
            }

            if(!alreadyInList){
                fos.write("${songID}:${1}\n".toByteArray())
            }

            fos.close()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun getSongPlayCount(): Map<String, Int>{
        val songPlayCount = mutableMapOf<String, Int>()

        try {
            val fis = FileInputStream(songsPlayCountFile(context))
            val reader = BufferedReader(InputStreamReader(fis))
            var line = reader.readLine()

            while (line != null){
                val key = line.split(":")[0]
                val value = line.split(":")[1]

                songPlayCount[key] = value.toInt()

                line = reader.readLine()
            }

            fis.close()
            reader.close()
        } catch (e: Exception){
            e.printStackTrace()
        }

        return songPlayCount
    }

    fun getMostPlayed(sourcePlaylist: List<String>? = null): Map<String, Int> {
        var source = arrayOf<Pair<String, Int>>()

        if(sourcePlaylist != null) {
            getSongPlayCount().toList().toTypedArray().forEach {
                if (sourcePlaylist.contains(it.first)) {
                    source += it
                }
            }
        } else {
            source = getSongPlayCount().toList().toTypedArray()
        }

        getSongPlayCount().toList().toTypedArray()
        while(true){
            var changed = false

            for(song in 0..<source.size - 1){
                if(source[song].second > source[song + 1].second){
                    changed = true
                    val placeHolder = source[song]
                    source[song] = source[song + 1]
                    source[song + 1] = placeHolder
                }
            }

            if(!changed){
                break
            }
        }

        val sortedPlayCount = mutableMapOf<String, Int>()

        for(song in source.reversed()){
            sortedPlayCount[song.first] = song.second
        }

        return sortedPlayCount
    }

    fun getUnPlayedSongs(played: List<String>, allSongsInPlaylist: List<String>): List<String>{
        val unPlayed = mutableListOf<String>()

        for(song in allSongsInPlaylist){
            if(!played.contains(song)){
                unPlayed.add(song)
            }
        }

        return unPlayed
    }
}

const val UPDATE_SONGS = 0