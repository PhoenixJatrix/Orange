package com.nullinnix.orange.ui_utilities

import android.util.Log
import android.widget.Space
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullinnix.orange.Playlist
import com.nullinnix.orange.R
import com.nullinnix.orange.misc.corners
import com.nullinnix.orange.misc.noGleamTaps
import com.nullinnix.orange.misc.screenHeight
import com.nullinnix.orange.ui.theme.Black
import com.nullinnix.orange.ui.theme.LighterGray
import com.nullinnix.orange.ui.theme.NinetyTranslucentBlack
import com.nullinnix.orange.ui.theme.Opaque
import com.nullinnix.orange.ui.theme.Orange
import com.nullinnix.orange.ui.theme.Translucent
import com.nullinnix.orange.ui.theme.TranslucentBlack
import com.nullinnix.orange.ui.theme.TranslucentOrange
import com.nullinnix.orange.ui.theme.TranslucentOrangeSemi
import com.nullinnix.orange.ui.theme.Transparent
import com.nullinnix.orange.ui.theme.White

@Composable
fun TopAppBar(
    currentPlaylist: String,
    playlists: Map<String, Playlist>,
    modifier: Modifier,
    onPlaylistChanged: (String) -> Unit,
    onCreatePlaylist: () -> Unit
) {
    var showingPlaylists by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(85.dp)
                .background(TranslucentOrange),
            contentAlignment = Alignment.Center
        ) {

        }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 45.dp)
                .padding(end = 5.dp, start = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clip(corners(10.dp))
                    .background(if (showingPlaylists) NinetyTranslucentBlack else Transparent)
                    .noGleamTaps {

                    }
                    .padding(
                        start = 5.dp,
                        top = if (showingPlaylists) 5.dp else 0.dp,
                        end = 5.dp,
                        bottom = 5.dp
                    )
                    .animateContentSize()
            ) {
                Row (verticalAlignment = Alignment.CenterVertically){
                    Row(
                        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                            .clip(corners())
                            .background(Translucent)
                            .clickable {
                                showingPlaylists = !showingPlaylists
                            }
                            .padding(vertical = 5.dp, horizontal = 10.dp)
                    ) {
                        Text(text = "Playlists", color = White, fontWeight = FontWeight.ExtraBold)

                        Spacer(modifier = Modifier.width(5.dp))

                        Image(
                            painter = painterResource(id = R.drawable.playlist),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(Orange),
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }

                    if (!showingPlaylists) {
                        Image(
                            imageVector = Icons.AutoMirrored.Default.ArrowForward,
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(Orange),
                            modifier = Modifier
                                .size(20.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                                .clip(corners())
                                .background(Translucent)
                                .padding(vertical = 5.dp, horizontal = 10.dp)
                        ) {
                            Text(
                                text = playlists[currentPlaylist]!!.name,
                                color = White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                if(showingPlaylists){
                    for(playlist in playlists.values){
                        Spacer(modifier = Modifier.height(10.dp))

                        Row (modifier = Modifier
                            .clip(corners(5.dp))
                            .background(if (currentPlaylist == playlist.id) TranslucentOrangeSemi else TranslucentOrange)
                            .clickable {
                                if (currentPlaylist != playlist.id) {
                                    onPlaylistChanged(playlist.id)
                                    showingPlaylists = false
                                }
                            }
                            .padding(5.dp), verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(text = playlist.name, fontWeight = FontWeight.ExtraBold, color = White, fontSize = 18.sp)

                            Spacer(modifier = Modifier.width(5.dp))

                            Text(text = "${playlist.songs.size} songs", color = LighterGray)
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(corners())
                    .background(Translucent)
                    .padding(3.dp)
            ) {
                Image(
                    imageVector = Icons.Default.Add,
                    contentDescription = "",
                    modifier = Modifier.size(25.dp),
                    colorFilter = ColorFilter.tint(White)
                )
            }
        }
    }
}