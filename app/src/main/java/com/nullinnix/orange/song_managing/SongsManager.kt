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
import com.nullinnix.orange.misc.thumbnailsDirectory
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
}

const val UPDATE_SONGS = 0