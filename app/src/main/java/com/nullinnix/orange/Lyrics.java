package com.nullinnix.orange;

import static android.view.View.VISIBLE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Lyrics extends AppCompatActivity {
    Button searchOnline, pasteLyrics, deleteLyrics, playPauseMediaPlayer, rewindBackwards, rewindForwards;
    ScrollView lyricsScrollview;
    ConstraintLayout noLyricsConstraintLayout, lyricsSongInfo;
    TextView lyrics;
    ClipboardManager clipboardManager;
    LyricsManager lyricsManager;
    musicResources musicResources = new musicResources(songsListAdapter.audioModels);
    File lyricsDirectory;
    ImageView background;
    MediaPlayer mediaPlayer = mediaPlayerInstance.getInstance();
    TextView songNameLyrics, songArtistLyrics, timesPlayedLyrics, currentTimeLyrics;
    ProgressBar progressBarLyrics;
    CardView lyricsMediaControl;
    String lyricsOf = "";
    Map<String, Integer> history = new HashMap<>();
    String[] acceptableChars = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", " "};
    ArrayList<String> acceptableCharsArray = new ArrayList<>(Arrays.asList(acceptableChars));
    Timer timer, currentTimeTimer;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.lyrics_layout);
        searchOnline = findViewById(R.id.searchOnline);
        pasteLyrics = findViewById(R.id.pasteLyrics);
        lyricsScrollview = findViewById(R.id.lyricsScrollView);
        noLyricsConstraintLayout = findViewById(R.id.noLyricsFoundLayout);
        lyrics = findViewById(R.id.lyrics);
        deleteLyrics = findViewById(R.id.deleteLyrics);
        playPauseMediaPlayer = findViewById(R.id.lyricsPlayPause);
        rewindBackwards = findViewById(R.id.rewindBackwards);
        rewindForwards = findViewById(R.id.rewindForwards);
        background = findViewById(R.id.lyricsAlbumCover);
        songNameLyrics = findViewById(R.id.lyricsSongName);
        songArtistLyrics = findViewById(R.id.lyricsArtistName);
        progressBarLyrics = findViewById(R.id.lyricsProgressBar);
        lyricsSongInfo = findViewById(R.id.lyricsSongInfo);
        currentTimeLyrics = findViewById(R.id.currentTimeLyrics);
        timesPlayedLyrics = findViewById(R.id.timesPlayed);
        lyricsMediaControl = findViewById(R.id.lyricsMediaControllerCardView);
        history = getHistory();

        onClickListeners();
        lyricsManager = new LyricsManager(this, musicResources.getCurrentSongName());
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        lyricsDirectory = new File(getDir("lyrics", MODE_PRIVATE).toString());

        lyricsOf = musicResources.getCurrentSongName();

        ArrayList<String> lyricsArray = new ArrayList<>(Arrays.asList(lyricsDirectory.list()));
        if (lyricsArray.contains(musicResources.getCurrentSongName() + ".txt")){
            lyrics.setText(lyricsManager.loadLyrics());
            noLyricsConstraintLayout.setVisibility(View.INVISIBLE);
            lyricsScrollview.setVisibility(VISIBLE);
            if(history.get(musicResources.getCurrentSongName().replace(",", "")) != null){
                timesPlayedLyrics.setText(String.valueOf(history.get(musicResources.getCurrentSongName().replace(",", ""))));
            }
        }else{
            lyrics.setText("");
            noLyricsConstraintLayout.setVisibility(VISIBLE);
            lyricsScrollview.setVisibility(View.INVISIBLE);
            lyricsMediaControl.setVisibility(View.INVISIBLE);
            lyricsSongInfo.setVisibility(View.INVISIBLE);
        }

        File backgroundImage = new File(getDir("thumbnails", MODE_PRIVATE).toString() + "/" + musicResources.getCurrentSongName() + ".jpeg");
        if(backgroundImage.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(backgroundImage.toString());
            background.setImageBitmap(bitmap);
        }else{
            background.setBackgroundResource(R.drawable.ic_music_icon_alt);
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if(lyrics.getVisibility() == VISIBLE){
                        musicPlayerWindowActivity.repeatState = "LOOP_SINGLE";
                    }
                    if(mediaPlayer.isPlaying() && playPauseMediaPlayer.getVisibility() == VISIBLE){
                        playPauseMediaPlayer.setBackgroundResource(R.drawable.ic_paused_icon);
                    }else{
                        playPauseMediaPlayer.setBackgroundResource(R.drawable.ic_playing_icon);
                    }
                });
            }
        }, 1000, 200);

        songNameLyrics.setText(com.nullinnix.orange.musicResources.setSongAndArtist()[0]);
        songArtistLyrics.setText(com.nullinnix.orange.musicResources.setSongAndArtist()[1]);

        int[] totalTime = {musicResources.getTotalTime()};
        currentTimeTimer = new Timer();
        currentTimeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    int percentage = mediaPlayer.getCurrentPosition() * 100;
                    percentage = percentage / totalTime[0];
                    if(mediaPlayer.isPlaying()){
                        progressBarLyrics.setProgress(percentage);
                        currentTimeLyrics.setText(musicResources.milliToMinutes(mediaPlayer.getCurrentPosition()));
                    }
                    if(lyrics.getVisibility() != VISIBLE){
                        timer.cancel();
                    }
                });
            }
        }, 0, 1000);
    }

    void onClickListeners(){
        searchOnline.setOnClickListener(v -> {
            String modifiedLink = "";
            for (int i = 0; i < lyricsOf.length(); i++) {
                if(acceptableCharsArray.contains(String.valueOf(lyricsOf.charAt(i)).toLowerCase())){
                   modifiedLink = modifiedLink.concat(String.valueOf(lyricsOf.charAt(i)));
                }
            }
            String link = String.format("https://www.songlyrics.com/index.php?section=search&searchW=%s&submit=Search&searchIn1=artist&searchIn3=song", modifiedLink.replace(" ", "+") + "+lyrics");

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            ClipData clip = ClipData.newPlainText(lyricsOf, lyricsOf + " lyrics");
            clipboardManager.setPrimaryClip(clip);
            startActivity(browserIntent);
        });

        pasteLyrics.setOnClickListener(v -> {
            ClipData.Item clipItem;
            if(clipboardManager.getPrimaryClip() != null){
                clipItem = clipboardManager.getPrimaryClip().getItemAt(0);
                if(clipItem.coerceToText(this).length() > 0){
                    lyricsScrollview.setVisibility(VISIBLE);
                    lyrics.setText(clipItem.coerceToText(this));
                    noLyricsConstraintLayout.setVisibility(View.GONE);
                    lyricsManager.saveLyrics(clipItem.coerceToText(this).toString());
                    lyricsMediaControl.setVisibility(VISIBLE);
                    lyricsSongInfo.setVisibility(VISIBLE);
                    if(history.get(musicResources.getCurrentSongName().replace(",", "")) != null){
                        timesPlayedLyrics.setText(String.valueOf(history.get(musicResources.getCurrentSongName().replace(",", ""))));
                    }
                    Toast.makeText(this, "Orange Saved the Lyrics, no need to search next time :)", Toast.LENGTH_SHORT).show();
                }
            }

            else {
                Toast.makeText(this, "Clipboard is Empty", Toast.LENGTH_SHORT).show();
            }
        });

        deleteLyrics.setOnClickListener(v -> {
            lyricsManager.deleteLyrics();
            lyricsScrollview.setVisibility(View.INVISIBLE);
            noLyricsConstraintLayout.setVisibility(VISIBLE);
            lyricsMediaControl.setVisibility(View.INVISIBLE);
            lyricsSongInfo.setVisibility(View.INVISIBLE);
        });

        playPauseMediaPlayer.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }else{
                mediaPlayer.start();
            }
            MainActivity.updateNotification = true;
        });

        rewindBackwards.setOnClickListener(v -> {
            if(mediaPlayer.getCurrentPosition() - 10000 > 0){
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
            }else if(mediaPlayer.getCurrentPosition() - 10000 < 0){
                mediaPlayer.seekTo(0);
            }

            int[] totalTime = {musicResources.getTotalTime()};
            int percentage = mediaPlayer.getCurrentPosition() * 100;
            percentage = percentage / totalTime[0];
            progressBarLyrics.setProgress(percentage);
            currentTimeLyrics.setText(musicResources.milliToMinutes(mediaPlayer.getCurrentPosition()));
        });

        rewindForwards.setOnClickListener(v -> {
            if(mediaPlayer.getCurrentPosition() + 10000 < musicResources.getTotalTime()){
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
                int[] totalTime = {musicResources.getTotalTime()};
                int percentage = mediaPlayer.getCurrentPosition() * 100;
                percentage = percentage / totalTime[0];
                progressBarLyrics.setProgress(percentage);
                currentTimeLyrics.setText(musicResources.milliToMinutes(mediaPlayer.getCurrentPosition()));
            }
        });

        rewindBackwards.setOnLongClickListener(v -> {
            mediaPlayer.seekTo(0);
            return true;
        });

        rewindForwards.setOnLongClickListener(v -> {
            mediaPlayer.seekTo(0);
            return true;
        });

    }

    public Map<String, Integer> getHistory(){
        Map<String, Integer> keyValues = new HashMap<>();
        try{
            File history = new File(getDir("musicHistory", Context.MODE_PRIVATE).toString() + "/" + "history.txt");
            FileInputStream fis = new FileInputStream(history);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            String line = bufferedReader.readLine();
            while(line != null){
                String[] kv = line.split(",");
                keyValues.put(kv[0], Integer.parseInt(kv[1]));
                line = bufferedReader.readLine();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return keyValues;
    }

    @Override
    public void onBackPressed() {
        timer.cancel();
        currentTimeTimer.cancel();
        musicPlayerWindowActivity.repeatState = "LOOP_ALL";
        super.onBackPressed();
    }
}