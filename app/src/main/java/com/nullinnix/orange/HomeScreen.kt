package com.nullinnix.orange

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.nullinnix.orange.misc.RequestMediaPermission
import com.nullinnix.orange.misc.checkMediaPermission
import com.nullinnix.orange.misc.getInterval
import com.nullinnix.orange.misc.mediaPlayerStateAction
import com.nullinnix.orange.misc.playlistDirectory
import com.nullinnix.orange.misc.searchSongsByName
import com.nullinnix.orange.song_managing.ALL_SONGS
import com.nullinnix.orange.song_managing.LIKED_SONGS
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.COMPLETED
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.DORMANT
import com.nullinnix.orange.song_managing.MediaPlayerState.Companion.PLAYING
import com.nullinnix.orange.song_managing.PlayerClickActions
import com.nullinnix.orange.song_managing.PlayerClickActions.Companion.SHOW_PLAYER
import com.nullinnix.orange.song_managing.PlaylistManager
import com.nullinnix.orange.song_managing.SET
import com.nullinnix.orange.song_managing.SongPlayer
import com.nullinnix.orange.song_managing.SongsManager
import com.nullinnix.orange.song_managing.getSongDataFromIDs
import com.nullinnix.orange.ui.theme.MildBlack
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.White
import com.nullinnix.orange.ui_utilities.Dialog
import com.nullinnix.orange.ui_utilities.MiniPlayer
import com.nullinnix.orange.ui_utilities.MultiSelectOptions
import com.nullinnix.orange.ui_utilities.Player
import com.nullinnix.orange.ui_utilities.PlaylistsEditor
import com.nullinnix.orange.ui_utilities.SongsList
import com.nullinnix.orange.ui_utilities.TopAppBar
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun HomeScreen(context: Activity, songsManager: SongsManager, songPlayer: SongPlayer, playlistManager: PlaylistManager) {
    var requestMediaPermission by remember {
        mutableStateOf(false)
    }

    val hasMediaPermission = checkMediaPermission()

    var hasMediaPermissionUpdater by remember {
        mutableIntStateOf(0)
    }

    var showMediaPermissionDialog by remember {
        mutableStateOf(!hasMediaPermission)
    }

    var allDeviceSongs by remember {
        mutableStateOf(songsManager.songsList.value!!.toMap())
    }

    var playlists by remember {
        mutableStateOf(playlistManager.playlists.value!!.toMap())
    }

    var currentPlaylist by remember {
        mutableStateOf(playlistManager.currentPlaylist.value!!)
    }

    var allSongsInPlaylist by remember {
        mutableStateOf(playlistManager.playlists.value!![currentPlaylist]!!.songs)
    }

    var currentSong by remember {
        mutableStateOf(allDeviceSongs[songPlayer.currentPlaying.value])
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

    var currentSongSortType by remember {
        mutableIntStateOf(VIEWING_ALL)
    }

    var showPlayer by remember {
        mutableStateOf(false)
    }

    var mediaPlayerState by remember {
        mutableIntStateOf(songPlayer.mediaPlayerState.value!!)
    }

    var currentPosition by remember {
        mutableIntStateOf(NOT_SET)
    }

    var currentPositionUpdate by remember {
        mutableIntStateOf(NOT_SET)
    }

    var isShuffling by remember {
        mutableStateOf(false)
    }

    var onSongEndAction by remember {
        mutableIntStateOf(LOOPING_ALL)
    }

    val isMiniPlayerVisible = currentSong != null && !showPlayer && mediaPlayerState != DORMANT && !isMultiSelecting

    songPlayer.mediaPlayer.setOnCompletionListener {
        if(allSongsInPlaylist.isNotEmpty() && currentSong != null) {
            songPlayer.mediaPlayerState.value = COMPLETED

            when (songPlayer.onSongEndAction.value!!) {
                LOOPING_ALL -> {
                    if (currentSong!!.id != allSongsInPlaylist.last()) {
                        songPlayer.skipNext(getSongDataFromIDs(allSongsInPlaylist, allDeviceSongs).values.toList()) {
                            currentSong = allDeviceSongs[it]
                        }
                    } else {
                        songPlayer.setDataSourceAndPrepare(
                            data = allDeviceSongs[allSongsInPlaylist[0]]!!.path,
                            songID = allDeviceSongs[allSongsInPlaylist[0]]!!.id
                        ) {
                            currentSong = allDeviceSongs[it]
                        }
                    }
                }

                LOOPING_SINGLE -> {
                    songPlayer.mediaPlayer.seekTo(0)
                    songPlayer.play()
                }

                NOT_LOOPING -> {

                }
            }
        }
    }

    playlistManager.currentPlaylist.observeForever {
        if(playlistUpdated){
            playlistUpdated = false
            isSearching = false
            isMultiSelecting = false
            allSongsInPlaylist = playlistManager.getSongIDsFromPlaylist(it)
            currentPlaylist = it
            playlistManager.lastPlaylist(SET, it)

            if(currentSongSortType != VIEWING_ALL){
                currentSongSortType = VIEWING_ALL
            }
        }
    }

    playlistManager.playlists.observeForever {
        if(allPlaylistsUpdated){
            allPlaylistsUpdated = false
            playlists = it

            if(currentSongSortType != VIEWING_ALL){
                currentSongSortType = VIEWING_ALL
            }
        }
    }

    songPlayer.currentPlaying.observeForever {
        if(songUpdated){
            songUpdated = false
            currentSong = allDeviceSongs[it]
        }
    }

    songPlayer.mediaPlayerState.observeForever {
        mediaPlayerState = it

        if(it == PLAYING) {
            currentPositionUpdate += 1
        }

        Log.d("", "MediaPlayerState: ${mediaPlayerStateAction[mediaPlayerState]!!}")
    }

    LaunchedEffect(key1 = Unit) {
        delay(1000)
        playlistUpdated = true
    }

    LaunchedEffect(hasMediaPermissionUpdater) {
        if (hasMediaPermission && hasMediaPermissionUpdater != 0) {
            songsManager.getSongs {
                playlistUpdated = false
                allPlaylistsUpdated = false
                songUpdated = false

                songsManager.isSongsUpdated()
                allDeviceSongs = it
                allSongsInPlaylist = playlistManager.getSongIDsFromPlaylist(ALL_SONGS)
                playlists = playlistManager.getPlaylists().toMutableMap()
            }

        } else {
            if(!hasMediaPermission){
                delay(100)
                hasMediaPermissionUpdater += 1
            }
        }
    }

    LaunchedEffect(key1 = currentPositionUpdate) {
        currentPosition = songPlayer.mediaPlayer.currentPosition
        delay(1000)
        if(songPlayer.mediaPlayerState.value == PLAYING){
            currentPositionUpdate += 1
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
                currentSong = currentSong,
                isMiniPlayerVisible = isMiniPlayerVisible,
                isMultiSelecting = isMultiSelecting,
                selectedSongs = selectedSongs,
                isSearching = isSearching,
                currentPlaylist = playlistManager.playlists.value!![currentPlaylist]!!,
                currentSongSortType = currentSongSortType,
                mediaPlayerState = mediaPlayerState,
                onAction = { action, id, openPlayer ->
                    when (action) {
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
                            if(openPlayer){
                                songUpdated = true
                                if(songPlayer.currentPlaying.value != id){
                                    songPlayer.setDataSourceAndPrepare(data = allDeviceSongs[id]!!.path, songID = id){
                                        currentSong = allDeviceSongs[it]
                                    }
                                }

                                showPlayer = true
                            } else {
                                if(songPlayer.currentPlaying.value != id) {
                                    songUpdated = true
                                    songPlayer.setDataSourceAndPrepare(data = allDeviceSongs[id]!!.path, songID = id) {
                                        currentSong = allDeviceSongs[it]
                                    }
                                } else {
                                    if(songPlayer.mediaPlayerState.value == PLAYING){
                                        songPlayer.pause()
                                    } else {
                                        songPlayer.play()
                                    }
                                }
                            }
                        }
                    }
                },
                onSort = {sortType, sortedSongs ->
                    currentSongSortType = sortType
                    allSongsInPlaylist = when(sortedSongs){
                        null -> {
                            playlistManager.getSongIDsFromPlaylist(currentPlaylist)
                        }

                        else -> {
                            sortedSongs
                        }
                    }
                }
            )

            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                // MiniPlayer()
            }
        }

        if(isMiniPlayerVisible && currentSong != null){
            MiniPlayer(
                modifier = Modifier.align(Alignment.BottomCenter),
                mediaPlayerState = mediaPlayerState,
                currentPosition = currentPosition,
                allSongsInPlaylist = getSongDataFromIDs(allSongsInPlaylist, allDeviceSongs),
                currentPlaying = currentSong!!,
                currentPlaylist = playlists[currentPlaylist]!!,
                isLiked = playlists[LIKED_SONGS]!!.songs.contains(currentSong!!.id),
                isShuffling = isShuffling,
                onSongEndAction = onSongEndAction,
                onAction = {action ->
                    when (action) {
                        PlayerClickActions.TOGGLE_SONG_ON_END_ACTION -> {
                            val onSongEndActionUpdated = when(onSongEndAction){
                                LOOPING_ALL -> {
                                    LOOPING_SINGLE
                                }

                                LOOPING_SINGLE -> {
                                    NOT_LOOPING
                                }

                                else -> {
                                    LOOPING_ALL
                                }
                            }

                            onSongEndAction = onSongEndActionUpdated
                            songPlayer.onSongEndAction.value = onSongEndActionUpdated
                        }

                        PlayerClickActions.TOGGLE_PLAY -> {
                            if(songPlayer.mediaPlayerState.value == PLAYING){
                                songPlayer.pause()
                            } else {
                                songPlayer.play()
                            }
                        }

                        PlayerClickActions.SKIP_NEXT -> {
                            songPlayer.skipNext(getSongDataFromIDs(allSongsInPlaylist, allDeviceSongs).values.toList()){
                                currentSong = allDeviceSongs[it]
                            }
                        }

                        PlayerClickActions.SKIP_PREVIOUS -> {
                            songPlayer.skipPrevious(getSongDataFromIDs(allSongsInPlaylist, allDeviceSongs).values.toList()){
                                currentSong = allDeviceSongs[it]
                            }
                        }

                        PlayerClickActions.TOGGLE_LIKED -> {
                            playlistUpdated = true
                            allPlaylistsUpdated = true

                            if(playlists[LIKED_SONGS]!!.songs.contains(currentSong!!.id)){
                                if(currentPlaylist == LIKED_SONGS){
                                    songPlayer.pause()
                                    showPlayer = false
                                    songPlayer.currentPlaying.value = ""
                                    playlistManager.removeSongFromPlaylist(LIKED_SONGS, listOf(currentSong!!.id))
                                    currentSong = null
                                } else {
                                    playlistManager.removeSongFromPlaylist(LIKED_SONGS, listOf(currentSong!!.id))
                                }
                            } else {
                                playlistManager.addSongsToPlaylist(LIKED_SONGS, listOf(currentSong!!.id))
                            }
                        }

                        PlayerClickActions.TOGGLE_SHUFFLE -> {
                            if(!isShuffling){
                                isShuffling = true
                                allSongsInPlaylist = playlists[currentPlaylist]!!.songs
                            } else {
                                isShuffling = false
                                allSongsInPlaylist = songPlayer.shuffleSongs(allSongsInPlaylist)
                            }
                        }

                        SHOW_PLAYER -> {
                            showPlayer = true
                        }
                    }
                },
                onClose = {
                    showPlayer = false
                },
                onSeek = {action, seekPosition ->
                    when(action) {
                        START_SEEKING -> {
                            songPlayer.pause()
                            if(seekPosition != null){
                                currentPosition = seekPosition
                                songPlayer.mediaPlayer.seekTo(seekPosition)
                            }
                        }

                        END_SEEKING -> {
                            currentPosition = seekPosition!!
                            songPlayer.mediaPlayer.seekTo(seekPosition)
                            songPlayer.play()
                        }
                    }
                }
            )
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

                    if(currentSongSortType != VIEWING_ALL){
                        currentSongSortType = VIEWING_ALL
                    }
                },
                onCreatePlaylist = {
                    showPlaylistEditor = true

                    if(currentSongSortType != VIEWING_ALL){
                        currentSongSortType = VIEWING_ALL
                    }
                },
                onDeletePlaylist = {
                    playlistUpdated = true
                    playlistManager.currentPlaylist.value = ALL_SONGS

                    allPlaylistsUpdated = true
                    playlistManager.deletePlaylist(it)

                    if(currentSongSortType != VIEWING_ALL){
                        currentSongSortType = VIEWING_ALL
                    }
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

                    if(currentSongSortType != VIEWING_ALL){
                        currentSongSortType = VIEWING_ALL
                    }
                },
                isSearchingUpdated = {
                    isSearching = it

                    if(currentSongSortType != VIEWING_ALL){
                        currentSongSortType = VIEWING_ALL
                    }
                },
                onSettings = {

                }
            )
        }

        if(isMultiSelecting){
            MultiSelectOptions(modifier = Modifier.align(Alignment.BottomCenter), selectedSongs = selectedSongs, isDefaultPlaylist = ALL_SONGS == currentPlaylist, allSelected = selectedSongs.size == allSongsInPlaylist.size){
                when(it){
                    REMOVE_FROM_PLAYLIST -> {
                        playlistUpdated = true
                        if(selectedSongs.contains(songPlayer.currentPlaying.value)){
                            songPlayer.pause()
                            songUpdated = true
                            songPlayer.currentPlaying.value = ""
                        }

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

                        selectedSongs = listOf()
                        isMultiSelecting = false
                    }

                    ADDED_TO_PLAYLIST -> {
                        playlistUpdated = true
                        allPlaylistsUpdated = true
                        playlistManager.addSongsToPlaylist(id!!, selectedSongs)

                        if(File(playlistDirectory(context), "$id.txt").exists()){
                            playlistUpdated = true
                            playlistManager.currentPlaylist.value = id
                        }

                        selectedSongs = listOf()
                        isMultiSelecting = false
                    }
                }

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

    if(showPlayer && currentSong != null) {
        Player(
            mediaPlayerState = mediaPlayerState,
            currentPosition = currentPosition,
            currentSong = currentSong!!,
            currentPlaylist = playlists[currentPlaylist]!!,
            isLiked = playlists[LIKED_SONGS]!!.songs.contains(currentSong!!.id),
            isShuffling = isShuffling,
            onSongEndAction = onSongEndAction,
            onAction = {action ->
                when (action) {
                    PlayerClickActions.TOGGLE_SONG_ON_END_ACTION -> {
                        val onSongEndActionUpdated = when(onSongEndAction){
                            LOOPING_ALL -> {
                                LOOPING_SINGLE
                            }

                            LOOPING_SINGLE -> {
                                NOT_LOOPING
                            }

                            else -> {
                                LOOPING_ALL
                            }
                        }

                        onSongEndAction = onSongEndActionUpdated
                        songPlayer.onSongEndAction.value = onSongEndActionUpdated
                    }

                    PlayerClickActions.TOGGLE_PLAY -> {
                        if(songPlayer.mediaPlayerState.value == PLAYING){
                            songPlayer.pause()
                        } else {
                            songPlayer.play()
                        }
                    }

                    PlayerClickActions.SKIP_NEXT -> {
                        songPlayer.skipNext(getSongDataFromIDs(allSongsInPlaylist, allDeviceSongs).values.toList()){
                            currentSong = allDeviceSongs[it]
                        }
                    }

                    PlayerClickActions.SKIP_PREVIOUS -> {
                        songPlayer.skipPrevious(getSongDataFromIDs(allSongsInPlaylist, allDeviceSongs).values.toList()){
                            currentSong = allDeviceSongs[it]
                        }
                    }

                    PlayerClickActions.TOGGLE_LIKED -> {
                        playlistUpdated = true
                        allPlaylistsUpdated = true

                        if(playlists[LIKED_SONGS]!!.songs.contains(currentSong!!.id)){
                            playlistManager.removeSongFromPlaylist(LIKED_SONGS, listOf(currentSong!!.id))
                        } else {
                            playlistManager.addSongsToPlaylist(LIKED_SONGS, listOf(currentSong!!.id))
                        }
                    }

                    PlayerClickActions.TOGGLE_SHUFFLE -> {
                        if(!isShuffling){
                            isShuffling = true
                            allSongsInPlaylist = playlists[currentPlaylist]!!.songs
                        } else {
                            isShuffling = false
                            allSongsInPlaylist = songPlayer.shuffleSongs(allSongsInPlaylist)
                        }
                    }
                }
            },
            onClose = {
                showPlayer = false
            },
            onSeek = {action, seekPosition ->
                when(action) {
                    START_SEEKING -> {
                        songPlayer.pause()
                        if(seekPosition != null){
                            currentPosition = seekPosition
                            songPlayer.mediaPlayer.seekTo(seekPosition)
                        }
                    }

                    END_SEEKING -> {
                        currentPosition = seekPosition!!
                        songPlayer.mediaPlayer.seekTo(seekPosition)
                        songPlayer.play()
                    }
                }
            }
        )
    }
}