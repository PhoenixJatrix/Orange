package com.nullinnix.orange.song_managing

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.nullinnix.orange.Playlist
import com.nullinnix.orange.misc.allowedKeys
import com.nullinnix.orange.misc.getDate
import com.nullinnix.orange.misc.lastPlaylistFile
import com.nullinnix.orange.misc.playlistDirectory
import com.nullinnix.orange.misc.playlistsFile
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import kotlin.random.Random

class PlaylistManager (private val context: Activity){
    var currentPlaylist = MutableLiveData(ALL_SONGS)
    var playlists = MutableLiveData(mutableMapOf<String, Playlist>())
    fun getPlaylists(): Map<String, Playlist> {
        playlists.value = mutableMapOf()

        try {
            val playlistsFis = FileInputStream(playlistsFile(context))
            val reader = BufferedReader(InputStreamReader(playlistsFis))
            var line = reader.readLine()

            while (line != null) {
                if (File(playlistDirectory(context), line).exists()) {
                    val name = line.split(",")[0]
                    val id = line.split(",")[1]
                    val dateCreated = line.split(",")[1]

                    playlists.value!![id] =
                        Playlist(
                            name = name,
                            id = id,
                            dateCreated = dateCreated,
                            songs = getSongIDsFromPlaylist(id)
                        )
                }

                line = reader.readLine()
            }

            playlistsFis.close()
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val songsManager = SongsManager(context)

        playlists.value!![ALL_SONGS] =
            Playlist(
                name = "All songs",
                id = ALL_SONGS,
                dateCreated = "Genesis",
                songs = songsManager.getLastLog()
            )

        playlists.value!![LIKED_SONGS] =
            Playlist(
                name = "Liked songs",
                id = LIKED_SONGS,
                dateCreated = "Genesis",
                songs = getSongIDsFromPlaylist(LIKED_SONGS)
            )

        return playlists.value!!
    }

    fun getSongIDsFromPlaylist(playlist: String) : List<String> {
        if(playlist != ALL_SONGS){
            val songs = mutableListOf<String>()
            try {
                val songFIS = FileInputStream(File(playlistDirectory(context), "$playlist.txt"))
                val reader = BufferedReader(InputStreamReader(songFIS))
                var line = reader.readLine()

                while (line != null) {
                    songs.add(line)

                    line = reader.readLine()
                }

                reader.close()
                songFIS.close()

                return songs
            } catch (e: Exception) {
                e.printStackTrace()
                return listOf()
            }
        } else {
            return SongsManager(context).getLastLog()
        }
    }

    fun createPlaylist(name: String): Boolean{
        for(letter in name){
            if(!allowedKeys.contains(letter.lowercase())){
                return false
            }
        }

        val oldPlaylists = getPlaylists().values

        try {
            val fos = FileOutputStream(playlistsFile(context))

            for(playlist in oldPlaylists){
                fos.write("${playlist.name},${playlist.id},${playlist.dateCreated}\n".toByteArray())
            }

            val id = Random.nextInt(1_000, 10_000_000)
            val dateCreated = getDate()
            fos.write("$name,$id,$dateCreated\n".toByteArray())

            fos.close()
        } catch (e: Exception){
            e.printStackTrace()
        }

        return true
    }

    fun deletePlaylist(name: String){
        try {
            val fos = FileOutputStream(playlistsFile(context))

            for(playlist in getPlaylists().values){
                if(playlist.name != name){
                    fos.write("${playlist.name},${playlist.id},${playlist.dateCreated}\n".toByteArray())
                }
            }

            fos.close()

            File(playlistDirectory(context), name).delete()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun addSongsToPlaylist(playlistID: String, songsToAdd: List<String>, previousSongsInPlaylist: List<String>){
        try {
            val fos = FileOutputStream(File(playlistDirectory(context), "$playlistID.txt"))

            for(song in previousSongsInPlaylist){
                fos.write("${song}\n".toByteArray())
            }

            for(song in songsToAdd){
                fos.write("${song}\n".toByteArray())
            }

            fos.close()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun removeSongFromPlaylist(playlistID: String, songToRemove: String, previousSongsInPlaylist: List<String>){
        try {
            val fos = FileOutputStream(File(playlistDirectory(context), "$playlistID.txt"))

            for(song in previousSongsInPlaylist){
                if(song != songToRemove){
                    fos.write("${song}\n".toByteArray())
                }
            }

            fos.close()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun lastPlaylist(editCommand: Int, playlistID: String?): String? {
        when(editCommand){
            GET -> {
                try {
                    val fis = FileInputStream(lastPlaylistFile(context))
                    val reader = BufferedReader(InputStreamReader(fis))
                    val line = reader.readLine()

                    fis.close()
                    reader.close()

                    return line
                } catch (e: Exception){
                    e.printStackTrace()
                    return ALL_SONGS
                }
            }

            SET -> {
                try {
                    val fos = FileOutputStream(lastPlaylistFile(context))
                    fos.write("$playlistID".toByteArray())

                    fos.close()
                    currentPlaylist.value = playlistID
                } catch (e: Exception){
                    e.printStackTrace()
                    return ALL_SONGS
                }
            }
        }

        return null
    }
}

const val GET = 0
const val SET = 1
const val ALL_SONGS = "0"
const val LIKED_SONGS = "1"