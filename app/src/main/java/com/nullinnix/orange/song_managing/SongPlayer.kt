package com.nullinnix.orange.song_managing

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.MutableLiveData
import com.nullinnix.orange.NOT_SET
import com.nullinnix.orange.LOOPING_ALL
import com.nullinnix.orange.NOT_LOOPING
import com.nullinnix.orange.SongData
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.DATASOURCE_NOT_SET
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.DATASOURCE_SET
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.PAUSED
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.PLAYING
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.PREPARED
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.PREPARING
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.RESET
import kotlin.random.Random

class SongPlayer (val context: Activity){
    var currentPlaying = MutableLiveData("")
    var currentSpeed = MutableLiveData(1f)
    var currentPitch = MutableLiveData(1f)

    var onSongEndAction = MutableLiveData(LOOPING_ALL)
    var showTimeRemaining = MutableLiveData(true)
    var isShuffling = MutableLiveData(true)

    var mediaPlayerState = MutableLiveData(NOT_SET)

    val mediaPlayer = MediaPlayer()

    var updatePlayingOptions = MutableLiveData(false)

    fun play(){
        mediaPlayerState.value = PLAYING
        if(currentPitch.value != 1f){
            mediaPlayer.playbackParams = mediaPlayer.playbackParams.setPitch(currentPitch.value!!)
        }

        if(currentSpeed.value != 1f){
            mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(currentSpeed.value!!)
        }

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
                } else if(onSongEndAction.value != NOT_LOOPING){
                    setDataSourceAndPrepare(data = songsInPlaylist[0].path, songID = songsInPlaylist[0].id){
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
                SongsManager(context = context).setSongPlayCount(songID)
                mediaPlayerState.value = PREPARED
                play()
                onDone(songID)
            }
        }
    }

    fun shuffleSongs(songsInPlaylist: List<String>, mostPlayed: List<String>, liked: List<String>): List<String>{
        val sourceSongs = songsInPlaylist.toMutableList()
        val shuffledList = mutableListOf<String>()

        val shortenedMostPlayed = if(mostPlayed.size > 5) mostPlayed.subList(0, 5) else mostPlayed

        if(Random.nextInt(0, 2) == 1){
            for(song in shortenedMostPlayed){
                if(!shuffledList.contains(song) && songsInPlaylist.contains(song) && Random.nextInt(0, 2) == 1){
                    shuffledList.add(song)
                }
            }

            for(song in liked){
                if(!shuffledList.contains(song) && songsInPlaylist.contains(song) && Random.nextInt(0, 2) == 1){
                    shuffledList.add(song)
                }
            }
        } else {
            for(song in liked){
                if(!shuffledList.contains(song) && songsInPlaylist.contains(song) && Random.nextInt(0, 2) == 1){
                    shuffledList.add(song)
                }
            }

            for(song in shortenedMostPlayed){
                if(!shuffledList.contains(song) && songsInPlaylist.contains(song) && Random.nextInt(0, 2) == 1){
                    shuffledList.add(song)
                }
            }
        }

        for(song in songsInPlaylist){
            val selectedSong = sourceSongs.removeAt(Random.nextInt(0, sourceSongs.size))
            if(!shuffledList.contains(selectedSong)) {
                shuffledList.add(selectedSong)
            }
        }

        return shuffledList
    }

    fun getNextSongData(songsInPlaylist: List<SongData>, onDone: (SongData?) -> Unit) {
        for(song in songsInPlaylist.indices){
            if(songsInPlaylist[song].id == currentPlaying.value){
                if(song < songsInPlaylist.size - 1){
                    onDone(songsInPlaylist[song + 1])
                } else if(onSongEndAction.value != NOT_LOOPING){
                    onDone(songsInPlaylist[0])
                } else {
                    onDone(null)
                }

                break
            }
        }
    }

    fun getPreviousSongData(songsInPlaylist: List<SongData>, onDone: (SongData?) -> Unit) {
        for(song in songsInPlaylist.indices){
            if(songsInPlaylist[song].id == currentPlaying.value){
                if(song != 0){
                    onDone(songsInPlaylist[song - 1])
                } else {
                    onDone(null)
                }

                break
            }
        }
    }
}