package com.nullinnix.orange

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File

class SongsManager (private val context: Activity){
    @SuppressLint("InlinedApi")
    fun getSongs() {
        val songsList = mutableListOf<SongData>()

        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.RELATIVE_PATH,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.GENRE,
            MediaStore.Audio.Media.ARTIST
        )

        Log.d("", "getSongs: ")

        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            ""
        )

        while (cursor?.moveToNext() == true) {
            Log.d("", "getMusic: $cursor")

            val songData = SongData(
                title = cursor.getString(0),
                path = cursor.getString(1),
                id = cursor.getString(2),
                displayName = cursor.getString(3),
                duration = cursor.getString(4).toLong(),
                album = cursor.getString(5),
                genre = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) cursor.getString(6) else null,
                artist = cursor.getString(7)
            )

            if (File(songData.path).exists()) {
                songsList.add(songData);
            }
        }

        cursor?.close()
    }
}