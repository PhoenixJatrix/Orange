package com.nullinnix.orange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.MutableLiveData
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nullinnix.orange.misc.Screens
import com.nullinnix.orange.misc.checkMediaPermission
import com.nullinnix.orange.misc.createPersistentFilesOnLaunch
import com.nullinnix.orange.misc.hasAllMediaPermissions
import com.nullinnix.orange.song_managing.GET
import com.nullinnix.orange.song_managing.PlaylistManager
import com.nullinnix.orange.song_managing.SongPlayer
import com.nullinnix.orange.song_managing.SongsManager

import com.nullinnix.orange.ui.theme.OrangeTheme

class MainActivity : ComponentActivity() {
    private lateinit var songsManager: MutableLiveData<SongsManager>
    private lateinit var songsPlayer: MutableLiveData<SongPlayer>
    private lateinit var playlistManager: MutableLiveData<PlaylistManager>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        songsManager = MutableLiveData(SongsManager(this))
        songsPlayer = MutableLiveData(SongPlayer())
        playlistManager = MutableLiveData(PlaylistManager(this))

        if(hasAllMediaPermissions(this)){
            songsManager.value!!.getSongs {
                playlistManager.value!!.currentPlaylist.value = playlistManager.value!!.lastPlaylist(GET, null)!!
                playlistManager.value!!.playlists.value = playlistManager.value!!.getPlaylists().toMutableMap()
                songsManager.value!!.isSongsUpdated()
            }
        }

        enableEdgeToEdge()
        setContent {
            LaunchedEffect (Unit){
                createPersistentFilesOnLaunch(this@MainActivity)
            }
            val navHostController = rememberNavController()

            OrangeTheme {
                NavHost(
                    navController = navHostController,
                    startDestination = Screens.HomeScreen.route
                ) {
                    composable(
                        route = Screens.HomeScreen.route
                    ){
                        HomeScreen(
                            context = this@MainActivity,
                            songsManager = songsManager.value!!,
                            songsPlayer = songsPlayer.value!!,
                            playlistManager = playlistManager.value!!
                        )
                    }

                    composable(
                        route = Screens.Settings.route
                    ){
                        //HomeScreen()
                    }
                }
            }
        }
    }
}