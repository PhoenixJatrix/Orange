package com.nullinnix.orange.song_managing

class MediaPlayerState {
    companion object{
        const val DATASOURCE_NOT_SET = -3
        const val DORMANT = -2
        const val PLAYING = -1
        const val PAUSED = 0
        const val COMPLETED = 1
        const val DATASOURCE_SET = 2
        const val PREPARING = 3
        const val RESET = 4
        const val PREPARED = 5

        fun validPlayState(state: Int) : Boolean {
            return listOf(PLAYING, PAUSED, COMPLETED, PREPARED).contains(state)
        }
    }
}