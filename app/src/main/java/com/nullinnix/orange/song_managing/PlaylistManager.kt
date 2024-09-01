package com.nullinnix.orange.song_managing

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.nullinnix.orange.Playlist
import com.nullinnix.orange.SongData
import com.nullinnix.orange.misc.allowedKeys
import com.nullinnix.orange.misc.getDate
import com.nullinnix.orange.misc.lastPlaylistFile
import com.nullinnix.orange.misc.playlistDirectory
import com.nullinnix.orange.misc.playlistsFile
import com.nullinnix.orange.misc.writeRedundantFile
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
        val protoPlaylist = mutableMapOf<String, Playlist>()

        val songsManager = SongsManager(context)

        protoPlaylist[ALL_SONGS] =
            Playlist(
                name = "All songs",
                id = ALL_SONGS,
                dateCreated = "Genesis",
                songs = songsManager.getLastLog(),
                deletable = false
            )

        protoPlaylist[LIKED_SONGS] =
            Playlist(
                name = "Liked songs",
                id = LIKED_SONGS,
                dateCreated = "Genesis",
                songs = getSongIDsFromPlaylist(LIKED_SONGS),
                deletable = false
            )

        try {
            val playlistsFis = FileInputStream(playlistsFile(context))
            val reader = BufferedReader(InputStreamReader(playlistsFis))
            var line = reader.readLine()

            while (line != null) {
                Log.d("", "createPlaylist: $line")
                val name = line.split(",")[0]
                val id = line.split(",")[1]
                val dateCreated = line.split(",")[2]

                if (File(playlistDirectory(context), "$id.txt").exists()) {
                    if(!listOf(ALL_SONGS, LIKED_SONGS).contains(id)){
                        protoPlaylist[id] =
                            Playlist(
                                name = name,
                                id = id,
                                dateCreated = dateCreated,
                                songs = getSongIDsFromPlaylist(id),
                                deletable = true
                            )
                    }
                }

                line = reader.readLine()
            }

            playlistsFis.close()
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        playlists.value = protoPlaylist

        return playlists.value!!
    }

    fun getSongIDsFromPlaylist(playlistID: String) : List<String> {
        if(playlistID != ALL_SONGS){
            val songs = mutableListOf<String>()
            try {
                val songFIS = FileInputStream(File(playlistDirectory(context), "$playlistID.txt"))
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

    fun createPlaylist(name: String, songsToAdd: List<String>?, onSuccess: (String) -> Unit): Boolean{
        val oldPlaylists = playlists.value!!.values

        val cleanedName = name.replace(",", "")

        try {
            val fos = FileOutputStream(playlistsFile(context))

            for(playlist in oldPlaylists){
                if(!listOf(ALL_SONGS, LIKED_SONGS).contains(playlist.id)){
                    fos.write("${playlist.name},${playlist.id},${playlist.dateCreated}\n".toByteArray())
                }
            }

            val id = Random.nextInt(1_000, 10_000_000)
            val dateCreated = getDate()
            fos.write("$cleanedName,$id,$dateCreated\n".toByteArray())

            writeRedundantFile(File(playlistDirectory(context), "$id.txt"))
            fos.close()

            if(songsToAdd != null){
                addSongsToPlaylist(id.toString(), songsToAdd)
            } else {
                getPlaylists()
            }

            if(File(playlistDirectory(context), "$id.txt").exists()){
                onSuccess(id.toString())
            }
        } catch (e: Exception){
            e.printStackTrace()
        }

        return true
    }

    fun deletePlaylist(playlistID: String){
        try {
            val fos = FileOutputStream(playlistsFile(context))

            for(playlist in playlists.value!!.values){
                if(playlist.id != playlistID){
                    if(!listOf(ALL_SONGS, LIKED_SONGS).contains(playlist.id)) {
                        fos.write("${playlist.name},${playlist.id},${playlist.dateCreated}\n".toByteArray())
                    }
                }
            }

            fos.close()
            File(playlistDirectory(context), "$playlistID.txt").delete()

            getPlaylists()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun addSongsToPlaylist(playlistID: String, songsToAdd: List<String>){
        try {
            val fos = FileOutputStream(File(playlistDirectory(context), "$playlistID.txt"))
            val previousSongsInPlaylist = playlists.value!![playlistID]?.songs ?: listOf()

            for(song in previousSongsInPlaylist){
                fos.write("${song}\n".toByteArray())
            }

            for(song in songsToAdd){
                if(!previousSongsInPlaylist.contains(song)){
                    fos.write("${song}\n".toByteArray())
                }
            }

            fos.close()
            getPlaylists()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun removeSongFromPlaylist(playlistID: String, songsToRemove: List<String>){
        try {
            val fos = FileOutputStream(File(playlistDirectory(context), "$playlistID.txt"))
            val previousSongsInPlaylist = playlists.value!![playlistID]?.songs ?: listOf()

            for(song in previousSongsInPlaylist){
                if(!songsToRemove.contains(song)){
                    fos.write("${song}\n".toByteArray())
                }
            }

            fos.close()
            getPlaylists()
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

                    return if(File(playlistDirectory(context), "$line.txt").exists() && playlists.value!!.containsKey(line)) line else ALL_SONGS
                } catch (e: Exception){
                    e.printStackTrace()
                    return ALL_SONGS
                }
            }

            SET -> {
                try {
                    val fos = FileOutputStream(lastPlaylistFile(context))
                    if(File(playlistDirectory(context), "$playlistID.txt").exists()){
                        fos.write("$playlistID".toByteArray())
                        currentPlaylist.value = playlistID
                    } else {
                        fos.write(ALL_SONGS.toByteArray())
                        currentPlaylist.value = ALL_SONGS
                    }

                    fos.close()
                } catch (e: Exception){
                    e.printStackTrace()
                    return ALL_SONGS
                }
            }
        }

        return null
    }

    fun getPlaylistDuration(songsInPlaylist: List<SongData>): Long {
        var totalDuration = 0L

        for (song in songsInPlaylist){
            totalDuration += song.duration
        }

        return totalDuration
    }
}

fun getSongDataFromIDs(allPlaylistSongs: List<String>, allDeviceSongs: Map<String, SongData>): Map<String, SongData>{
    val playlistToSearch = mutableMapOf<String, SongData>()

    allPlaylistSongs.forEach{ songID ->
        playlistToSearch[songID] = allDeviceSongs[songID]!!
    }

    return playlistToSearch
}

const val GET = 0
const val SET = 1
const val ALL_SONGS = "0"
const val LIKED_SONGS = "1"