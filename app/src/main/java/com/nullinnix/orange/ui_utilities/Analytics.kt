package com.nullinnix.orange.ui_utilities

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.nullinnix.orange.SongData
import com.nullinnix.orange.misc.corners
import com.nullinnix.orange.misc.durationSecondsToDays
import com.nullinnix.orange.misc.getAnyAvailableAlbumCover
import com.nullinnix.orange.misc.getDate
import com.nullinnix.orange.misc.noGleamTaps
import com.nullinnix.orange.misc.screenWidth
import com.nullinnix.orange.song_managing.SongsManager
import com.nullinnix.orange.song_managing.getSongDataFromIDs
import com.nullinnix.orange.ui.theme.MildBlack
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.Red
import com.nullinnix.orange.ui.theme.Translucent
import com.nullinnix.orange.ui.theme.TranslucentBlack
import com.nullinnix.orange.ui.theme.TranslucentGray
import com.nullinnix.orange.ui.theme.White
import kotlinx.coroutines.delay

//total hours played
//list of most played songs
//songs never played
//make shuffle add liked songs first if present in the playlist to shuffle
//add a recommended songs tab on launch that features most played and liked songs
//

@Composable
fun Analytics(
    allSongsInPlaylist: Map<String, SongData>,
    songsManager: SongsManager,
    playlistName: String,
    onPreview: (List<String>, String) -> Unit,
    onClose: () -> Unit
) {
    var mostPlayed by remember {
        mutableStateOf(songsManager.getMostPlayed(allSongsInPlaylist.keys.toList()))
    }

    var unPlayedSongs by remember {
        mutableStateOf(songsManager.getUnPlayedSongs(played = mostPlayed.keys.toList(), allSongsInPlaylist = allSongsInPlaylist.keys.toList()))
    }

    var totalPlayTime by remember {
        mutableIntStateOf(songsManager.getTotalPlaytime())
    }

    var updater by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(key1 = updater) {
        mostPlayed = songsManager.getMostPlayed(allSongsInPlaylist.keys.toList())
        unPlayedSongs = songsManager.getUnPlayedSongs(played = mostPlayed.keys.toList(), allSongsInPlaylist = allSongsInPlaylist.keys.toList())
        totalPlayTime = songsManager.getTotalPlaytime()
        delay(5000)

        updater += 1
    }

    BackHandler (true){
        onClose()
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(MildBlack)
            .noGleamTaps {

            }
            .padding(start = 5.dp, end = 5.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(50.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(corners(10.dp))
                    .background(TranslucentGray)
            ) {
                Text(
                    text = "Total listening time",
                    color = White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Orange)
                        .padding(5.dp),
                    fontWeight = FontWeight.ExtraBold
                )

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                ) {
                    Text(text = durationSecondsToDays(totalPlayTime), color = White)
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            if (mostPlayed.isNotEmpty()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(corners(10.dp))
                        .background(TranslucentGray)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("Most played song in ")

                            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)){
                                append(playlistName)
                            }
                        },
                        color = White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Orange)
                            .padding(5.dp),
                        maxLines = 1
                    )

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append("'${allSongsInPlaylist[mostPlayed.keys.toList()[0]]!!.title}' ")

                                withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                                    append("played ${mostPlayed.values.toList()[0]} times")
                                }
                            },
                            color = White
                        )
                    }
                }
            }

            if(getSongDataFromIDs(unPlayedSongs, allSongsInPlaylist).values.isNotEmpty()) {
                Spacer(modifier = Modifier.height(15.dp))
                AnalyticDetailView(
                    songs = getSongDataFromIDs(
                        unPlayedSongs,
                        allSongsInPlaylist
                    ).values.toList(),
                    label = buildAnnotatedString {
                        append("Songs never played in ")

                        withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)){
                            append(playlistName)
                        }
                    },
                    previewPlaylistName = "Never played on ${getDate()}",
                    timesPlayed = null
                ) { songs, previewPlaylistName ->
                    onPreview(songs, previewPlaylistName)
                }
            }

            if(mostPlayed.isNotEmpty()) {
                Spacer(modifier = Modifier.height(15.dp))
                AnalyticDetailView(
                    songs = getSongDataFromIDs(
                        if (mostPlayed.size > 10) mostPlayed.keys.toList()
                            .subList(0, 10) else mostPlayed.keys.toList(), allSongsInPlaylist
                    ).values.toList(),
                    label = buildAnnotatedString {
                        append("Top #10 most played songs in ")

                        withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)){
                            append(playlistName)
                        }
                    },
                    previewPlaylistName = "Top played on ${getDate()}",
                    timesPlayed =
                    if (mostPlayed.size > 10)
                        mostPlayed.values.toList().subList(0, 10)
                    else
                        mostPlayed.values.toList()
                ) { songs, previewPlaylistName ->
                    onPreview(songs, previewPlaylistName)
                }
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun AnalyticDetailView(songs: List<SongData>, timesPlayed: List<Int>?, label: AnnotatedString, previewPlaylistName: String, onPreview: (List<String>, String) -> Unit) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    Box (
        Modifier
            .clip(corners(10.dp))
            .background(TranslucentGray)
            .height(if (isExpanded) 500.dp else 185.dp)
    ){
        if (getAnyAvailableAlbumCover(songs, LocalContext.current) != null) {
            Image(
                bitmap = getAnyAvailableAlbumCover(
                    songs,
                    LocalContext.current
                )!!.asImageBitmap(),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxSize()
                    .blur(70.dp),
                contentScale = ContentScale.Crop
            )
        }

        Text(
            text = label,
            color = White,
            modifier = Modifier
                .fillMaxWidth()
                .background(Orange)
                .padding(5.dp),
            maxLines = 1
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isExpanded) 420.dp else 105.dp)
                .align(Alignment.Center)
        ) {
            item {
                if (isExpanded) {
                    if(timesPlayed != null) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Song", color = White, fontWeight = FontWeight.ExtraBold)
                            Text(
                                text = "Times Played",
                                color = White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    for (song in songs.indices) {
                        if(timesPlayed != null){
                            SongInfoViewTimesPlayed(songData = songs[song], timesPlayed = timesPlayed[song])
                        } else {
                            SongInfoView(songData = songs[song])
                        }
                    }
                } else {
                    Text(
                        text = buildAnnotatedString {
                            if (songs.size > 3) {
                                append("${songs[0].title}, ")
                                append("${songs[1].title}, ")
                                append(songs[2].title)

                                withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                                    append(" and ${songs.size - 3} other ${if(songs.size - 3 != 1) "songs" else "song"}")
                                }
                            } else if (songs.size == 3) {
                                append("${songs[0].title}, ")
                                append(songs[1].title)

                                withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                                    append(" and ")
                                }

                                append(songs[2].title)
                            } else if (songs.size == 2) {
                                append(songs[0].title)

                                withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                                    append(" and ")
                                }

                                append(songs[1].title)
                            } else {
                                append(songs[0].title)
                            }
                        },
                        color = White,
                        modifier = Modifier
                            .padding(5.dp),
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Row (
            Modifier
                .fillMaxWidth()
                .padding(bottom = 3.dp)
                .align(Alignment.BottomCenter), horizontalArrangement = Arrangement.SpaceAround){
            Box(
                modifier = Modifier
                    .width(screenWidth().dp / 2.3f)
                    .height(35.dp)
                    .clip(corners(90.dp))
                    .background(Orange)
                    .clickable {
                        isExpanded = !isExpanded
                    }
                    .padding(vertical = 0.dp, horizontal = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isExpanded) "Collapse" else "Expand",
                    fontWeight = FontWeight.ExtraBold,
                    color = White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .height(35.dp)
                    .width(screenWidth().dp / 2.3f)
                    .clip(corners(90.dp))
                    .background(Orange)
                    .clickable {
                        val songIds = mutableListOf<String>()

                        for (song in songs) {
                            songIds.add(song.id)
                        }

                        onPreview(songIds, previewPlaylistName)
                    }
                    .padding(vertical = 0.dp, horizontal = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Export to a playlist",
                    fontWeight = FontWeight.ExtraBold,
                    color = White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SongInfoView(songData: SongData) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .padding(3.dp)
    ){
        if(songData.thumbnail != null){
            Image(
                bitmap = songData.thumbnail.asImageBitmap(),
                contentDescription = "",
                modifier = Modifier
                    .size(50.dp)
                    .clip(corners(5.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(5.dp))

        Column (Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceAround){
            Text(text = songData.title, maxLines = 1, overflow = TextOverflow.Ellipsis, color = White)

            Text(text = "Artist: ${songData.artist}", maxLines = 1, overflow = TextOverflow.Ellipsis, color = White, fontSize = 13.sp)
        }
    }
}

@Composable
fun SongInfoViewTimesPlayed(songData: SongData, timesPlayed: Int) {
    Column (Modifier.fillMaxWidth()){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(3.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                if (songData.thumbnail != null) {
                    Image(
                        bitmap = songData.thumbnail.asImageBitmap(),
                        contentDescription = "",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(corners(5.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                Column(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.8f), verticalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = songData.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = White
                    )

                    Text(
                        text = "Artist: ${songData.artist}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = White,
                        fontSize = 13.sp
                    )
                }
            }

            Box(modifier = Modifier
                .fillMaxHeight()
                .clip(corners(5.dp))
                .background(Translucent)
                .padding(1.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "$timesPlayed", color = White, fontSize = 15.sp
                )
            }
        }
    }
}