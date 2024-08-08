package com.nullinnix.orange.song_managing

import android.media.MediaPlayer
import androidx.lifecycle.MutableLiveData
import com.nullinnix.orange.NOT_SET
import com.nullinnix.orange.LOOPING_ALL
import com.nullinnix.orange.SongData
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.DATASOURCE_NOT_SET
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.DATASOURCE_SET
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.PAUSED
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.PLAYING
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.PREPARED
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.PREPARING
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.RESET
import kotlin.random.Random

class SongPlayer {
    var currentPlaying = MutableLiveData("")
    var onSongEndAction = MutableLiveData(LOOPING_ALL)

    var mediaPlayerState = MutableLiveData(NOT_SET)

    val mediaPlayer = MediaPlayer()

    fun play(){
        mediaPlayerState.value = PLAYING
        mediaPlayer.start()
    }

    fun pause(){
        mediaPlayerState.value = PAUSED
        mediaPlayer.pause()
    }

    fun skipNext(songsInPlaylist: List<SongData>, onDone: (String) -> Unit) {
        for(song in songsInPlaylist.indices){
            if(songsInPlaylist[song].id == currentPlaying.value){
                if(song < songsInPlaylist.size -1){
                    setDataSourceAndPrepare(data = songsInPlaylist[song + 1].path, songID = songsInPlaylist[song + 1].id){
                        onDone(it)
                    }
                }

                break
            }
        }
    }

    fun skipPrevious(songsInPlaylist: List<SongData>, onDone: (String) -> Unit) {
        for(song in songsInPlaylist.indices){
            if(songsInPlaylist[song].id == currentPlaying.value){
                if(song != 0){
                    setDataSourceAndPrepare(data = songsInPlaylist[song - 1].path, songID = songsInPlaylist[song - 1].id){
                        onDone(it)
                    }
                }

                break
            }
        }
    }

    fun setDataSourceAndPrepare(data: String, songID: String, onDone: (String) -> Unit) {
        if(currentPlaying.value != songID) {
            currentPlaying.value = songID
            mediaPlayer.reset()
            mediaPlayerState.value = RESET
            mediaPlayerState.value = DATASOURCE_NOT_SET
            mediaPlayer.setDataSource(data)
            mediaPlayerState.value = DATASOURCE_SET
            mediaPlayerState.value = PREPARING
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                mediaPlayerState.value = PREPARED
                play()
                onDone(songID)
            }
        }
    }

    fun shuffleSongs(songsInPlaylist: List<String>): List<String>{
        val sourceSongs = songsInPlaylist.toMutableList()
        val shuffledList = mutableListOf<String>()

        for(song in songsInPlaylist){
            shuffledList.add(sourceSongs.removeAt(Random.nextInt(0, sourceSongs.size)))
        }

        return shuffledList
    }
}