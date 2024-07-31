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

        songsList = MutableLiveData(mutableMapOf())

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
                thumbnail = getThumbnailFromPath(cursor.getString(2))
            )

            songsList.value!![songData.id] = songData
            Log.d("", "getSongs: ${songData.id}")
        }

        cursor?.close()
        onDone(songsList.value!!)
    }

    private fun updateThumbnails(){
        if(songsList.value != null) {
            for (song in songsList.value!!.keys) {
                if(!File("${song}.jpg").exists()){
                    val meta = MediaMetadataRetriever()
                    meta.setDataSource(songsList.value!![song]!!.path)
                    val picture = meta.embeddedPicture
                    if(picture != null){
                        val bitmapThumbnail = BitmapFactory.decodeByteArray(picture, 0, picture.size)
                        bitmapThumbnail.compress(
                            Bitmap.CompressFormat.JPEG,
                            100,
                            FileOutputStream(File(thumbnailsDirectory(context),"${songsList.value!![song]!!.id}.jpg"))
                        )
                    }
                }
            }
        }

        //removeUnusedThumbnails()
        update.value = UPDATE_SONGS
    }
    private fun removeUnusedThumbnails(){
        val logs = getLastLog()
        val allThumbnails = thumbnailsDirectory(context).listFiles()

        allThumbnails?.forEach {
            if(!logs.contains(it.name)){
                it.delete()
            }
        }
    }

    fun isSongsUpdated() {
        Log.d("", "isSongsUpdated: called")
        val currentLog = mutableListOf<String>()
        var lastLog = getLastLog()

        if (songsList.value!!.isNotEmpty()) {
            for (song in songsList.value!!.keys) {
                currentLog.add(song)
            }
        }

        if (lastLog.isEmpty() && songsList.value!!.isNotEmpty()) {
            Log.d("", "isSongsUpdated: called 2")
            updateThumbnails()
            setLastLog(currentLog)
            lastLog = getLastLog()
        }

        for (log in lastLog) {
            if (!currentLog.contains(log)) {
                setLastLog(currentLog)
                updateThumbnails()
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

    fun refresh(){
        setLastLog(null)
        Log.d("", "HomeScreen: 333")
        CoroutineScope(Dispatchers.Main).launch {
            update = MutableLiveData(-1)
            update = MutableLiveData(UPDATE_SONGS)
        }
    }
}

const val UPDATE_SONGS = 0