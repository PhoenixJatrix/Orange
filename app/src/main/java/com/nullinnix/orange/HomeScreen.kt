package com.nullinnix.orange

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import com.nullinnix.orange.misc.RequestMediaPermission
import com.nullinnix.orange.misc.checkMediaPermission
import com.nullinnix.orange.misc.getInterval
import com.nullinnix.orange.misc.playlistDirectory
import com.nullinnix.orange.misc.searchSongsByName
import com.nullinnix.orange.song_managing.ALL_SONGS
import com.nullinnix.orange.song_managing.GET
import com.nullinnix.orange.song_managing.LIKED_SONGS
import com.nullinnix.orange.song_managing.PAUSED
import com.nullinnix.orange.song_managing.PLAYING
import com.nullinnix.orange.song_managing.PlaylistManager
import com.nullinnix.orange.song_managing.SET
import com.nullinnix.orange.song_managing.SongPlayer
import com.nullinnix.orange.song_managing.SongsManager
import com.nullinnix.orange.ui.theme.MildBlack
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.White
import com.nullinnix.orange.ui_utilities.Dialog
import com.nullinnix.orange.ui_utilities.MultiSelectOptions
import com.nullinnix.orange.ui_utilities.PlaylistsEditor
import com.nullinnix.orange.ui_utilities.SongsList
import com.nullinnix.orange.ui_utilities.TopAppBar
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun HomeScreen(context: Activity, songsManager: SongsManager, songsPlayer: SongPlayer, playlistManager: PlaylistManager) {
    var requestMediaPermission by remember {
        mutableStateOf(false)
    }

    val hasMediaPermission = checkMediaPermission()

    var showMediaPermissionDialog by remember {
        mutableStateOf(!hasMediaPermission)
    }

    val allDeviceSongs by remember {
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

    var currentSong by remember {
        mutableIntStateOf(songsPlayer.currentPlaying.value!!)
    }

    var playlistUpdated by remember {
        mutableStateOf(false)
    }

    var allPlaylistsUpdated by remember {
        mutableStateOf(false)
    }

    var songUpdated by remember {
        mutableStateOf(false)
    }

    var isMultiSelecting by remember {
        mutableStateOf(false)
    }

    var selectedSongs by remember {
        mutableStateOf(listOf<String>())
    }

    var showPlaylistEditor by remember {
        mutableStateOf(false)
    }

    var isSearching by remember {
        mutableStateOf(false)
    }

    playlistManager.currentPlaylist.observeForever {
        if(playlistUpdated){
            playlistUpdated = false
            isSearching = false
            isMultiSelecting = false
            allSongsInPlaylist = playlistManager.getSongIDsFromPlaylist(it)
            currentPlaylist = it
            playlistManager.lastPlaylist(SET, it)
        }
    }

    playlistManager.playlists.observeForever {
        if(allPlaylistsUpdated){
            allPlaylistsUpdated = false
            playlists = it
        }
    }

    songsPlayer.currentPlaying.observeForever {
        if(songUpdated){
            songUpdated = false
            currentSong = it
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
            playlistManager.currentPlaylist.value = playlistManager.lastPlaylist(GET, null)!!
            playlistManager.playlists.value = playlistManager.getPlaylists().toMutableMap()
            songsManager.isSongsUpdated()
        }
    }

    BackHandler(isMultiSelecting) {
        if(isMultiSelecting){
            isMultiSelecting = false
            selectedSongs = listOf()
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
            SongsList(
                songIDs = allSongsInPlaylist,
                allDeviceSongs = allDeviceSongs,
                currentlyPlaying = currentSong,
                isMiniPlayerVisible = false,
                isMultiSelecting = isMultiSelecting,
                selectedSongs = selectedSongs,
                isSearching = isSearching,
                currentPlaylist = playlistManager.playlists.value!![currentPlaylist]!!
            ) {action, id ->
                when(action){
                    MULTI_SELECT_SONGS -> {
                        isMultiSelecting = true
                        if (!selectedSongs.contains(id)) {
                            val mutableProto = mutableListOf<String>()
                            mutableProto.addAll(selectedSongs)
                            mutableProto.add(id)

                            selectedSongs = mutableProto.toList()
                        } else {
                            val mutableProto = mutableListOf<String>()
                            mutableProto.addAll(selectedSongs)
                            mutableProto.remove(id)
                            selectedSongs = mutableProto.toList()
                        }
                    }

                    PLAY_SONG -> {
                        songsPlayer.currentSongState.value = if(songsPlayer.currentSongState.value == PAUSED) PLAYING else PAUSED
                    }
                }
            }

            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
               // MiniPlayer()
            }
        }

        if(hasMediaPermission && playlists.isNotEmpty()) {
            TopAppBar(
                currentPlaylist = currentPlaylist,
                playlists = playlists,
                modifier = Modifier.align(Alignment.TopCenter),
                isMultiSelecting = isMultiSelecting,
                selectedSongs = selectedSongs,
                numberOfSongsInPlaylist = allSongsInPlaylist.size,
                isSearching = isSearching,
                onPlaylistChanged = {
                    playlistUpdated = true
                    playlistManager.currentPlaylist.value = it
                },
                onCreatePlaylist = {
                    showPlaylistEditor = true
                },
                onDeletePlaylist = {
                    playlistUpdated = true
                    playlistManager.currentPlaylist.value = ALL_SONGS

                    allPlaylistsUpdated = true
                    playlistManager.deletePlaylist(it)
                },
                onSearchParametersUpdate = {
                    if(!it.isNullOrEmpty()) {
                        val playlistToSearch = mutableMapOf<String, SongData>()

                        playlistManager.getSongIDsFromPlaylist(currentPlaylist).forEach{ songID ->
                            playlistToSearch[songID] = allDeviceSongs[songID]!!
                        }

                        allSongsInPlaylist = searchSongsByName(searchParameter = it.lowercase(), songsInPlaylist = playlistToSearch)
                    } else {
                        allSongsInPlaylist = playlistManager.playlists.value!![currentPlaylist]!!.songs
                    }
                },
                isSearchingUpdated = {
                    isSearching = it
                }
            )
        }

        if(isMultiSelecting){
            MultiSelectOptions(modifier = Modifier.align(Alignment.BottomCenter), selectedSongs = selectedSongs, isDefaultPlaylist = ALL_SONGS == currentPlaylist, allSelected = selectedSongs.size == allSongsInPlaylist.size){
                when(it){
                    REMOVE_FROM_PLAYLIST -> {
                        playlistUpdated = true
                        playlistManager.removeSongFromPlaylist(currentPlaylist, selectedSongs)

                        allPlaylistsUpdated = true
                        playlistManager.getPlaylists()

                        selectedSongs = listOf()
                        isMultiSelecting = false
                    }

                    DELETE_SELECTED -> {

                    }

                    SELECT_INTERVAL -> {
                        selectedSongs = getInterval(selectedSongs = selectedSongs, allSongsInPlaylist = allSongsInPlaylist)
                    }

                    CANCEL_SELECTION -> {
                        selectedSongs = listOf()
                        isMultiSelecting = false
                    }

                    SELECT_ALL -> {
                        selectedSongs = allSongsInPlaylist
                    }
                }
            }
        }

        if(showPlaylistEditor){
            PlaylistsEditor(playlistManager = playlistManager, isMultiSelecting = isMultiSelecting, selectedSongs = selectedSongs){action, id ->
                when(action){
                    CREATED_NEW_PLAYLIST -> {
                        allPlaylistsUpdated = true
                        playlistManager.createPlaylist(id!!, selectedSongs){
                            playlistUpdated = true
                            playlistManager.currentPlaylist.value = it
                        }
                    }

                    ADDED_TO_PLAYLIST -> {
                        playlistUpdated = true
                        allPlaylistsUpdated = true
                        playlistManager.addSongsToPlaylist(id!!, selectedSongs)

                        if(File(playlistDirectory(context), "$id.txt").exists()){
                            playlistUpdated = true
                            playlistManager.currentPlaylist.value = id
                        }
                    }
                }

                selectedSongs = listOf()
                isMultiSelecting = false



                showPlaylistEditor = false
            }
        }
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