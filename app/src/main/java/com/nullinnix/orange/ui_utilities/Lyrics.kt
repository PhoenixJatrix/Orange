package com.nullinnix.orange.ui_utilities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullinnix.orange.R
import com.nullinnix.orange.SongData
import com.nullinnix.orange.lyrics.LyricsManager
import com.nullinnix.orange.misc.cleanSearchParameter
import com.nullinnix.orange.misc.copyFromClipBoard
import com.nullinnix.orange.misc.corners
import com.nullinnix.orange.misc.noGleamTaps
import com.nullinnix.orange.misc.screenWidth
import com.nullinnix.orange.ui.theme.Blue
import com.nullinnix.orange.ui.theme.Green
import com.nullinnix.orange.ui.theme.LighterGray
import com.nullinnix.orange.ui.theme.MildGray
import com.nullinnix.orange.ui.theme.MildTranslucentBlack
import com.nullinnix.orange.ui.theme.MildTranslucentOrange
import com.nullinnix.orange.ui.theme.Opaque
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.Red
import com.nullinnix.orange.ui.theme.Translucent
import com.nullinnix.orange.ui.theme.TranslucentBlack
import com.nullinnix.orange.ui.theme.TranslucentOrange
import com.nullinnix.orange.ui.theme.Transparent
import com.nullinnix.orange.ui.theme.White
import com.nullinnix.orange.ui.theme.Yellow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun LyricsView(
    context: Activity,
    lyricsManager: LyricsManager,
    currentSong: SongData,
    onClose: () -> Unit
) {
    var lyrics by remember {
        mutableStateOf(lyricsManager.getSongLyric(currentSong.id))
    }

    var showManualSearch by remember {
        mutableStateOf(false)
    }

    var isSearching by remember {
        mutableStateOf(false)
    }

    var isEditing by remember {
        mutableStateOf(false)
    }

    var editedLyrics by remember {
        mutableStateOf(lyrics)
    }

    var selectedVerse by remember {
        mutableIntStateOf(0)
    }

    var verses by remember {
        mutableStateOf(listOf<String>())
    }

    val optimisedSearchParameters = cleanSearchParameter(currentSong.title) + "+lyrics"

    val scrollState = rememberLazyListState()

    val coroutine = rememberCoroutineScope()

    val googleSearch = remember {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/search?q=$optimisedSearchParameters")
        )
    }

    val colorByCode = { text: String ->
        if (text.contains("col_b"))
            Blue
        else if (text.contains("col_g"))
            Green
        else if (text.contains("col_y"))
            Yellow
        else if (text.contains("col_r"))
            Red
        else
            White
    }

    BackHandler(showManualSearch) {
        showManualSearch = false
    }

    LaunchedEffect(key1 = currentSong) {
        lyrics = lyricsManager.getSongLyric(currentSong.id)
    }

    LaunchedEffect(key1 = lyrics) {
        editedLyrics = lyrics
        if (lyrics != null) {
            verses = listOf()
            val allVerses = lyrics!!.split("\\n")

            for (verse in allVerses) {
                verses += verse
            }
        } else {
            verses = listOf()
        }
    }

    Box(
        Modifier
            .fillMaxSize()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "",
                modifier = Modifier
                    .clip(corners(90.dp))
                    .size(35.dp)
                    .background(Translucent)
                    .clickable {
                        onClose()
                    }
                    .padding(5.dp),
                colorFilter = ColorFilter.tint(Orange)
            )

            if (lyrics != null) {
                Row(
                    modifier = Modifier
                        .height(35.dp)
                        .clip(corners(10.dp))
                        .background(Translucent), verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEditing) {
                        Box(modifier = Modifier
                            .fillMaxHeight()
                            .clickable {
                                isEditing = false
                            }
                            .padding(5.dp)
                        ) {
                            Text(text = "Cancel", color = LighterGray)
                        }

                        Box(modifier = Modifier
                            .fillMaxHeight()
                            .clickable(!editedLyrics.isNullOrEmpty()) {
                                lyricsManager.saveLyric(currentSong.id, editedLyrics!!)
                                isEditing = false
                                lyrics = lyricsManager.getSongLyric(currentSong.id)
                            }
                            .padding(5.dp)
                        ) {
                            Text(
                                text = "Save",
                                color = if (editedLyrics.isNullOrEmpty()) Color.Gray else Orange,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                            .fillMaxHeight()
                            .background(Opaque)
                            .clickable {
                                isEditing = true
                            }
                            .padding(start = 2.5.dp, end = 2.5.dp)
                            .padding(3.dp)
                        ) {
                            Text(text = "Edit Lyrics", color = Orange, fontWeight = FontWeight.Bold)

                            Image(
                                painter = painterResource(id = R.drawable.edit),
                                contentDescription = "",
                                modifier = Modifier,
                                colorFilter = ColorFilter.tint(Orange)
                            )
                        }

                        Image(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "",
                            modifier = Modifier
                                .height(35.dp)
                                .clickable {
                                    lyricsManager.deleteLyric(currentSong.id)
                                    lyrics = lyricsManager.getSongLyric(currentSong.id)
                                }
                                .padding(start = 2.5.dp, end = 2.5.dp)
                                .padding(5.dp),
                            colorFilter = ColorFilter.tint(LighterGray)
                        )
                    }
                }
            }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .padding(top = 70.dp)
                .noGleamTaps {

                }
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(MildTranslucentBlack)
                .padding(bottom = 145.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = currentSong.title,
                color = White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                textAlign = TextAlign.Center,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )

            if (currentSong.artist != "<unknown>") {
                Text(
                    text = currentSong.artist,
                    color = White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp),
                    textAlign = TextAlign.Center,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp
                )
            }

            Box(
                Modifier
                    .fillMaxSize()
            ) {
                LazyColumn (modifier = Modifier.padding(start = 10.dp, end = 10.dp), state = scrollState){
                    if (verses.isNotEmpty() && editedLyrics != null) {
                        if (isEditing) {
                            item {
                                Text(
                                    text = "Edit Lyrics. Use '\\n' to separate verses\nUse \\b to make a verse bolder\nUse \\i to italicize a verse\nUse \\col_b to highlight in Blue, col_g for green col_r for red, col_y for yellow",
                                    color = White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                TextField(
                                    value = editedLyrics!!,
                                    onValueChange = {
                                        editedLyrics = it
                                    },
                                    shape = corners(5.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = TranslucentOrange,
                                        unfocusedIndicatorColor = Transparent,
                                        focusedIndicatorColor = Transparent,
                                        unfocusedContainerColor = TranslucentOrange
                                    ),
                                    modifier = Modifier.width(screenWidth().dp),
                                    textStyle = TextStyle(color = White, fontSize = 17.sp)
                                )

                                Spacer(modifier = Modifier.height(200.dp))
                            }
                        } else {
                            items(verses.size) {
                                if (verses[it] == "") {
                                    Spacer(modifier = Modifier.height(10.dp))
                                } else {
                                    Text(
                                        text = verses[it].replace("\\b", "").replace("\\i", "")
                                            .replace("\\col_r", "").replace("\\col_b", "")
                                            .replace("\\col_g", "").replace("\\col_y", ""),
                                        color = if (selectedVerse == it) Orange else if (verses[it].contains(
                                                "col_"
                                            )
                                        ) colorByCode(verses[it]) else White,
                                        fontWeight = if (selectedVerse == it) FontWeight.ExtraBold else if (verses[it].contains(
                                                "\\b"
                                            )
                                        ) FontWeight.ExtraBold else FontWeight.Normal,
                                        fontStyle = if (verses[it].contains("\\i")) FontStyle.Italic else FontStyle.Normal,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(corners(5.dp))
                                            .background(if (selectedVerse == it) MildTranslucentOrange else Transparent)
                                            .noGleamTaps {
                                                selectedVerse = it
                                            }
                                            .padding(3.dp)
                                    )

                                    Spacer(modifier = Modifier.height(3.dp))
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(50.dp))
                        }
                    } else {
                        item {
                            Column(
                                modifier = Modifier.fillParentMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = buildAnnotatedString {
                                    append("No lyrics saved for ")

                                    withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                                        append(currentSong.title)
                                    }

                                    append(", start searching")

                                }, maxLines = 3, color = White)

                                Spacer(modifier = Modifier.height(15.dp))

                                Box(modifier = Modifier
                                    .clip(corners(5.dp))
                                    .background(Orange)
                                    .clickable(!isSearching) {
                                        isSearching = true
                                        lyricsManager.searchLyrics(
                                            artist = currentSong.artist,
                                            title = currentSong.title
                                        ) {
                                            isSearching = false

                                            if (it != null) {
                                                lyrics = it
                                                lyricsManager.saveLyric(
                                                    songID = currentSong.id,
                                                    lyrics = it
                                                )
                                            } else {
                                                Toast
                                                    .makeText(
                                                        context,
                                                        "No lyrics found, try other search methods",
                                                        Toast.LENGTH_LONG
                                                    )
                                                    .show()
                                            }
                                        }
                                    }
                                    .padding(5.dp)
                                ) {
                                    Text(
                                        text = "Automatic search",
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(5.dp))

                                Box(modifier = Modifier
                                    .clip(corners(10.dp))
                                    .background(LighterGray)
                                    .clickable(!isSearching) {
                                        showManualSearch = true
                                    }
                                    .padding(5.dp)
                                ) {
                                    Text(text = "Manual Search", fontWeight = FontWeight.ExtraBold)
                                }

                                Spacer(modifier = Modifier.height(5.dp))

                                Box(modifier = Modifier
                                    .clip(corners(10.dp))
                                    .background(LighterGray)
                                    .padding(5.dp)
                                    .clickable(!isSearching) {
                                        context.startActivity(googleSearch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                                    }
                                ) {
                                    Text(
                                        text = "Manual Google Search",
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(5.dp))

                                Box(modifier = Modifier
                                    .clip(corners(10.dp))
                                    .background(LighterGray)
                                    .clickable {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            if (!copyFromClipBoard(context).isNullOrEmpty()) {
                                                lyricsManager.saveLyric(
                                                    songID = currentSong.id,
                                                    lyrics = copyFromClipBoard(context)!!
                                                )

                                                lyrics = lyricsManager.getSongLyric(currentSong.id)
                                            }
                                        }
                                    }
                                    .padding(5.dp)
                                ) {
                                    Text(
                                        text = "Paste copied lyrics",
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(if (isEditing) 200.dp else 50.dp))
                        }
                    }
                }

                if(verses.isNotEmpty()){
                    Box(modifier = Modifier
                        .width(50.dp)
                        .fillMaxHeight(0.5f)
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(topStart = 90.dp))
                        .clickable {
                            if (selectedVerse > 0) {
                                selectedVerse -= 1
                            }

                            coroutine.launch {
                                scrollState.animateScrollToItem(selectedVerse)
                            }

                            while (verses[selectedVerse] == "") {
                                if (selectedVerse > 0) {
                                    selectedVerse -= 1
                                } else {
                                    break
                                }

                                coroutine.launch {
                                    scrollState.animateScrollToItem(selectedVerse)
                                }
                            }
                        }){

                    }

                    Box(modifier = Modifier
                        .width(50.dp)
                        .fillMaxHeight(0.5f)
                        .align(Alignment.BottomEnd)
                        .clip(RoundedCornerShape(bottomStart = 90.dp))
                        .clickable {
                            if (selectedVerse < verses.size - 1) {
                                selectedVerse += 1
                            }

                            coroutine.launch {
                                scrollState.animateScrollToItem(selectedVerse)
                            }

                            while (verses[selectedVerse] == "") {
                                if (selectedVerse < verses.size - 1) {
                                    selectedVerse += 1
                                } else {
                                    break
                                }

                                coroutine.launch {
                                    scrollState.animateScrollToItem(selectedVerse)
                                }
                            }
                        }){

                    }
                }
            }
        }

        if (showManualSearch) {
            LyricManualSearch(
                artist = currentSong.artist,
                title = currentSong.title,
                isSearching = isSearching,
                onSearch = { artistParameter, titleParameter ->
                    isSearching = true

                    lyricsManager.searchLyrics(
                        artist = artistParameter,
                        title = titleParameter
                    ) {
                        if (it != null) {
                            lyrics = it
                            lyricsManager.saveLyric(
                                songID = currentSong.id,
                                lyrics = it
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "No lyrics found, try other search methods",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        isSearching = false
                        showManualSearch = false
                    }
                },
                onClose = {
                    showManualSearch = false
                }
            )
        }
    }

    if (isSearching) {
        Box(modifier = Modifier.padding(top = 70.dp)) {
            LoadingAnimation {
                isSearching = false
            }
        }
    }
}

@Composable
fun LyricManualSearch(artist: String, title: String, isSearching: Boolean, onSearch: (artist: String, title: String) -> Unit, onClose: () -> Unit) {
    var titleSearch by remember {
        mutableStateOf(title)
    }

    var artistSearch by remember {
        mutableStateOf(artist)
    }

    LaunchedEffect(key1 = artist, title) {
        titleSearch = title
        artistSearch = artist
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp))
                .background(TranslucentBlack)
                .noGleamTaps { },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column {
                Text(text = "Artist", color = White, fontWeight = FontWeight.ExtraBold)
                TextField(
                    value = artistSearch,
                    onValueChange = {
                        if (it.length <= 150) {
                            artistSearch = it
                        }
                    },
                    maxLines = 1,
                    placeholder = {
                        Text(text = "Artist", color = MildGray)
                    },
                    singleLine = true,
                    shape = corners(10.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = TranslucentOrange,
                        unfocusedIndicatorColor = Transparent,
                        focusedIndicatorColor = Transparent,
                        unfocusedContainerColor = TranslucentOrange
                    ),
                    modifier = Modifier.width(screenWidth().dp - 90.dp),
                    textStyle = TextStyle(color = White, fontSize = 17.sp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column {
                Text(text = "Title", color = White, fontWeight = FontWeight.ExtraBold)
                TextField(
                    value = titleSearch,
                    onValueChange = {
                        if (it.length <= 150) {
                            titleSearch = it
                        }
                    },
                    maxLines = 1,
                    placeholder = {
                        Text(text = "Title", color = MildGray)
                    },
                    singleLine = true,
                    shape = corners(10.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = TranslucentOrange,
                        unfocusedIndicatorColor = Transparent,
                        focusedIndicatorColor = Transparent,
                        unfocusedContainerColor = TranslucentOrange
                    ),
                    modifier = Modifier.width(screenWidth().dp - 90.dp),
                    textStyle = TextStyle(color = White, fontSize = 17.sp)
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(40.dp)
                    .clip(corners(10.dp))
                    .background(if (isSearching) Color.Gray else Orange)
                    .clickable(!isSearching) {
                        onSearch(
                            cleanSearchParameter(artist),
                            cleanSearchParameter(titleSearch)
                        )
                    }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Search",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }
        }

        Icon(imageVector = Icons.Default.Clear, contentDescription = "", tint = Orange, modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = 15.dp, end = 15.dp)
            .size(25.dp)
            .clickable {
                onClose()
            })
    }
}