package com.nullinnix.orange

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.nullinnix.orange.misc.RequestMediaPermission
import com.nullinnix.orange.misc.checkMediaPermission
import com.nullinnix.orange.song_managing.LIKED_SONGS
import com.nullinnix.orange.song_managing.PlaylistManager
import com.nullinnix.orange.song_managing.SET
import com.nullinnix.orange.song_managing.SongPlayer
import com.nullinnix.orange.song_managing.SongsManager
import com.nullinnix.orange.ui.theme.MildBlack
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.White
import com.nullinnix.orange.ui_utilities.Dialog
import com.nullinnix.orange.ui_utilities.SongsList
import com.nullinnix.orange.ui_utilities.TopAppBar
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(context: Activity, songsManager: SongsManager, songsPlayer: SongPlayer, playlistManager: PlaylistManager) {
    var requestMediaPermission by remember {
        mutableStateOf(false)
    }

    val hasMediaPermission = checkMediaPermission()

    var showMediaPermissionDialog by remember {
        mutableStateOf(!hasMediaPermission)
    }

    var allDeviceSongs by remember {
        mutableStateOf(songsManager.songsList.value!!.toMap())
    }

    var allSongsInPlaylist by remember {
        mutableStateOf(listOf<String>())
    }

    var playlists by remember {
        mutableStateOf(playlistManager.playlists.value!!.toMap())
    }

    var likedSongs by remember {
        mutableStateOf(listOf<String>())
    }

    var currentPlaylist by remember {
        mutableStateOf(playlistManager.currentPlaylist.value!!)
    }

    var playlistUpdated by remember {
        mutableStateOf(false)
    }

    songsPlayer.currentPlaying.observeForever {

    }

    playlistManager.currentPlaylist.observeForever {
        if(playlistUpdated){
            playlistUpdated = false
            allSongsInPlaylist = playlistManager.getSongIDsFromPlaylist(it)
            currentPlaylist = it
            playlistManager.lastPlaylist(SET, it)
        }
    }

    LaunchedEffect(key1 = Unit) {
        delay(1000)
        playlistUpdated = true
    }

    LaunchedEffect(hasMediaPermission) {
        if (hasMediaPermission) {
            likedSongs = playlistManager.getSongIDsFromPlaylist(LIKED_SONGS)
            allSongsInPlaylist = playlistManager.getSongIDsFromPlaylist(currentPlaylist)
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MildBlack),
        contentAlignment = Alignment.Center
    ){
        if(allSongsInPlaylist.isEmpty()) {
            Text(
                text = if (!hasMediaPermission) buildAnnotatedString {
                    append("No songs found, make sure")
                    withStyle(
                        style = SpanStyle(
                            color = Orange,
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append(" Orange ")
                    }
                    append("has media permission")
                } else buildAnnotatedString {
                    append("No songs in playlist")
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = White
            )
        } else {
            SongsList(songIDs = allSongsInPlaylist, allDeviceSongs = allDeviceSongs, currentlyPlaying = 3, isMiniPlayerVisible = false)

            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
               // MiniPlayer()
            }
        }

        TopAppBar(
            currentPlaylist = currentPlaylist,
            playlists = playlists,
            modifier = Modifier.align(Alignment.TopCenter),
            onPlaylistChanged = {
                playlistUpdated = true
                playlistManager.currentPlaylist.value = it
            },
            onCreatePlaylist = {

            }
        )
    }

    if(showMediaPermissionDialog){
        Dialog(
            text = buildAnnotatedString {
                append("Grant Permission to")
                withStyle(SpanStyle(color = Orange, fontWeight = FontWeight.ExtraBold)) {
                    append(" Orange ")
                }

                append("to fetch music on your device")
            },
            negativeHint = "Deny",
            positiveHint = "Grant Permission",
        ){
            if(it){
                requestMediaPermission = true
            }

            showMediaPermissionDialog = false
        }
    }

    if(requestMediaPermission){
        RequestMediaPermission()
    }
}