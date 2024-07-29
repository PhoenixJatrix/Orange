package com.nullinnix.orange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nullinnix.orange.misc.Screens
import com.nullinnix.orange.song_managing.SongsManager

import com.nullinnix.orange.ui.theme.OrangeTheme

class MainActivity : ComponentActivity() {
    private lateinit var songsManager: SongsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        songsManager = SongsManager(this)
        enableEdgeToEdge()
        setContent {
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
                            context = this@MainActivity
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