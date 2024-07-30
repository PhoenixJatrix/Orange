package com.nullinnix.orange.song_managing

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.nullinnix.orange.SongData
import com.nullinnix.orange.misc.lastMusicLog
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader

class SongsManager (private val context: Activity){
    var songsList = MutableLiveData(mutableListOf<SongData>())
    var update = MutableLiveData(UPDATE_SONGS)

    @SuppressLint("InlinedApi")
    fun getSongs(onDone: (List<SongData>) -> Unit) {
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media._ID,
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

        songsList = MutableLiveData(mutableListOf())

        while (cursor?.moveToNext() == true) {
            val songData = SongData(
                title = cursor.getString(0),
                path = cursor.getString(1),
                id = cursor.getString(2),
                duration = cursor.getString(3).toLong(),
                album = cursor.getString(4),
                genre = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) cursor.getString(5) else null,
                artist = cursor.getString(6),
                thumbnail = getThumbnailFromPath("${cursor.getString(1).split(".")[0]}.jpg")
            )

            songsList.value!!.add(songData)
            Log.d("", "getSongs: $songsList")
        }

        cursor?.close()
        onDone(songsList.value!!)
    }

    private fun updateThumbnails(){
        if(songsList.value != null) {
            for (song in songsList.value!!) {
                if(!File("${song.path}.jpg").exists()){
                    val meta = MediaMetadataRetriever()
                    meta.setDataSource(song.path)
                    val picture = meta.embeddedPicture
                    if(picture != null){
                        val bitmapThumbnail = BitmapFactory.decodeByteArray(picture, 0, picture.size)
                        bitmapThumbnail.compress(
                            Bitmap.CompressFormat.JPEG,
                            100,
                            FileOutputStream(File("${song.path}.jpg"))
                        )
                    }
                }
            }
        }

        update = MutableLiveData(UPDATE_SONGS)
    }

    fun isSongsUpdated(){
        val currentLog = mutableListOf<String>()

        if(songsList.value != null) {
            for (song in songsList.value!!) {
                currentLog.add(song.path)
            }
        }

        val lastLog = getLastLog()

        for(log in lastLog){
            if(!currentLog.contains(log)){
                setLastLog(currentLog)
                updateThumbnails()
                break
            }
        }
    }

    private fun getLastLog(): List<String>{
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

    private fun setLastLog(logs: List<String>){
        try {
            val fos = FileOutputStream(lastMusicLog(context))

            for(log in logs){
                fos.write("$log\n".toByteArray())
            }

        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun getThumbnailFromPath(path: String): Bitmap? {
        return if(File(path).exists()) BitmapFactory.decodeFile(path) else null
    }
}

const val UPDATE_SONGS = 0