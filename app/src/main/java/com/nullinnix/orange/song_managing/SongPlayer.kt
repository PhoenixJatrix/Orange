package com.nullinnix.orange.song_managing

import androidx.lifecycle.MutableLiveData

class SongPlayer {
    var currentPlaying = MutableLiveData(-1)
    fun skipNext(songsSize: Int) {
        if (currentPlaying.value != -1) {
            if (currentPlaying.value!! < songsSize) {
                currentPlaying = MutableLiveData(currentPlaying.value!! + 1)
            }
        }
    }

    fun skipPrevious() {
        if (currentPlaying.value != -1) {
            if (currentPlaying.value!! != 0) {
                currentPlaying = MutableLiveData(currentPlaying.value!! - 1)
            }
        }
    }
}