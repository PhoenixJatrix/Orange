package com.nullinnix.orange.lyrics

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.nullinnix.orange.misc.lyricsDirectory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request.Builder
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.util.concurrent.TimeUnit

class LyricsManager (val context: Context){
    fun searchLyrics(artist: String, title: String, result: (String?) -> Unit) {
        val request = Builder()
            .url("https://private-anon-1af62979fc-lyricsovh.apiary-proxy.com/v1/$artist/$title")
            .method("GET", null)
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        try {
            client.build().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Log.d("", "onResponse: failed")
                        result(null)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val body = response.body?.string()
                        if(response.code == 200){
                            if(body != null) {
                                result(
                                    body.substring(
                                        body.indexOf("\\n") + 2,
                                        body.length - 2
                                    )
                                )
                            }
                        }
                        else{
                            result(null)
                        }

                        Log.d("", "onResponse: $body")
                        Log.d("", "onResponse: ${response.networkResponse}")
                    }
                }
            })
        } catch (e: Exception) {
            e.message
            CoroutineScope(Dispatchers.Main).launch {
                result(null)
                Log.d(ContentValues.TAG, "onResponse: ${e.message}")
            }
        }
    }

    fun saveLyric(songID: String, lyrics: String) {
        try {
            val saveDirectory = File(lyricsDirectory(context), "$songID.txt")

            val fos = FileOutputStream(saveDirectory)

            fos.write(lyrics.toByteArray())

            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getSongLyric(songID: String): String?{
        try {
            val lyricDirectory = File(lyricsDirectory(context), "$songID.txt")

            return if (lyricDirectory.exists()) {
                val fis = FileInputStream(lyricDirectory)
                val reader = BufferedReader(InputStreamReader(fis))
                val line = reader.readLine()

                fis.close()
                reader.close()
                return line
            } else {
                null
            }
        } catch (e: Exception){
            e.printStackTrace()
            return null
        }
    }

    fun deleteLyric(songID: String){
        File(lyricsDirectory(context), "$songID.txt").delete()
    }
}