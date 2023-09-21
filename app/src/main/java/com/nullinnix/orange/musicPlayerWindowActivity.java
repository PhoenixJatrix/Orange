package com.nullinnix.orange;

import static android.content.ContentValues.TAG;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.TextViewCompat;

import com.chibde.visualizer.CircleBarVisualizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
public class musicPlayerWindowActivity extends AppCompatActivity {
    TextView songNamePlayer, albumName, currentTime, totalTime, slow, fast, tip, currentPlaylist, currentPlaylistText;
    Button playPause, skipNext, skipPrevious, repeating, shuffle, lyrics, changeAlbumCover;
    SeekBar seekBar;
    ConstraintLayout musicPlayerWindowLayout, guideFast, guideSlow;
    LinearLayout albumCoverOnClick;
    ImageView albumCover, backgroundImageView, albumCoverGuide;
    MediaPlayer mediaPlayer = mediaPlayerInstance.getInstance();
    String state = "TOTAL_TIME";
    static String repeatState = "LOOP_ALL";
    String currentPath;
    static boolean shuffleOn, reachedEndOfPlaylist, updatePlayer, changed, speeding, slowing = false;
    static ArrayList<AudioModel> shuffledSongs = new ArrayList<>();
    static ArrayList<AudioModel> musicData = new ArrayList<>();
    static ArrayList<AudioModel> musicDataRaw = new ArrayList<>();
    ConstraintLayout playbackSpeedSlow, playbackSpeedFast, guideLayout;
    ObjectAnimator albumAnimator;
    boolean isPaused = false;
    long speed = 10000;
    File thumbnails;
    ArrayList<String> thumbnailsArrayLists;
    Timer timer;
    String[] iconColors, textColors, progressBarColors;
    SharedPreferences progressBarColor, totalRemaining, iconColor, textColor, guide;
    musicResources musicResources;
    int guideSteps = 0;
    static boolean speedChanged = false;
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.music_player_window);

        shuffledSongs.clear();
        musicData.clear();
        musicDataRaw.clear();

        musicDataRaw = getIntent().getParcelableArrayListExtra("audioModel");
        musicData = musicDataRaw;
        setIDs();

        thumbnails = new File(getDir("thumbnails", MODE_PRIVATE).toString());
        thumbnailsArrayLists = new ArrayList<>(Arrays.asList(thumbnails.list()));

        albumAnimator = ObjectAnimator.ofFloat(albumCover, View.ROTATION, 0f, 360f)
                .setDuration(speed);
        albumAnimator.setRepeatCount(Animation.INFINITE);
        albumAnimator.setInterpolator(new LinearInterpolator());
        albumAnimator.start();

        SharedPreferences preferences = getSharedPreferences("shuffling", MODE_PRIVATE);
        shuffleOn = preferences.getBoolean("shuffling", false);

        totalRemaining = getSharedPreferences("TotalRemaining", MODE_PRIVATE);
        state = totalRemaining.getString("TotalRemaining", "TOTAL_TIME");
        musicResources = new musicResources(musicData);

        progressBarColor = getSharedPreferences("ProgressBar Color", MODE_PRIVATE);
        progressBarColors = progressBarColor.getString("ProgressBar Color", "254,83,20").split(",");

        setResourcesWithMusic();

        if (shuffleOn){
            shuffle.setBackgroundResource(R.drawable.ic_shuffle);
        }

        iconColor = getSharedPreferences("Icon Color", MODE_PRIVATE);
        iconColors = iconColor.getString("Icon Color", "254,83,20").split(",");

        if (repeatState.equals("LOOP_ALL")){
            repeating.setBackgroundResource(R.drawable.ic_repeat_all_on);
        } else if (repeatState.equals("LOOP_SINGLE")) {
            repeating.setBackgroundResource(R.drawable.ic_repeat_one);
        }
        else {
            repeating.setBackgroundResource(R.drawable.ic_repeat_all_off);
        }

        playPause.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
        shuffle.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
        skipNext.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
        skipPrevious.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
        repeating.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
        lyrics.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));

        textColor = getSharedPreferences("Text Color", MODE_PRIVATE);
        textColors = textColor.getString("Text Color", "254,83,20").split(",");

        currentTime.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
        totalTime.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
        songNamePlayer.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
        albumName.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
        fast.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
        slow.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
        changeAlbumCover.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
        currentPlaylist.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
        currentPlaylistText.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));

        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed},
                        new int[]{}
                },
                new int[] {
                        Color.rgb(Integer.parseInt(progressBarColors[0]), Integer.parseInt(progressBarColors[1]), Integer.parseInt(progressBarColors[2])),
                        Color.rgb(Integer.parseInt(progressBarColors[0]), Integer.parseInt(progressBarColors[1]), Integer.parseInt(progressBarColors[2]))
                }
        );

        seekBar.setThumb(null);

        seekBar.setProgressTintList(colorStateList);
        changeAlbumCover.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
        TextViewCompat.setCompoundDrawableTintList(changeAlbumCover, colorStateList);

        guide = getSharedPreferences("firstPlay", MODE_PRIVATE);
        if(guide.getBoolean("firstPlay", true)){
            tip.setVisibility(View.VISIBLE);
            guideLayout.setVisibility(View.VISIBLE);
            guideLayout.setClickable(true);
            guideLayout.setActivated(true);
            albumCoverGuide.setVisibility(View.VISIBLE);
            repeating.setVisibility(View.INVISIBLE);
            skipPrevious.setVisibility(View.INVISIBLE);
            skipNext.setVisibility(View.INVISIBLE);
            playPause.setVisibility(View.INVISIBLE);
            lyrics.setVisibility(View.INVISIBLE);
            shuffle.setVisibility(View.INVISIBLE);
        }
    }

    void setResourcesWithMusic(){
        currentPath = musicResources.getCurrentPath();
        totalTime.setText(musicResources.milliToMinutes(musicResources.getTotalTime()));
        currentPlaylist.setText(String.format("%s%s", MainActivity.currentPlaylistToPlayer.charAt(0), MainActivity.currentPlaylistToPlayer.substring(1).toLowerCase()));

        if(!mediaPlayerInstance.playingSameSong){
            try{
                mediaPlayer.setDataSource(currentPath);
            }catch(IOException | IllegalStateException e){
                e.printStackTrace();
            }
            com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.SET);
            playMusic();
        } else {
            playMusicWithoutPrepare();
        }

        onClickListeners();

        CircleBarVisualizer circleBarVisualizer = findViewById(R.id.visualizerCircleBar);
        circleBarVisualizer.setColor((Color.rgb(Integer.parseInt(progressBarColors[0]), Integer.parseInt(progressBarColors[1]), Integer.parseInt(progressBarColors[2]))));
        circleBarVisualizer.setPlayer(mediaPlayer.getAudioSessionId());
    }

    void playMusic(){
        mediaPlayer.prepareAsync();
        com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.PREPARING);
        mediaPlayer.setOnPreparedListener(mp -> {
            if(!guide.getBoolean("firstPlay", true)){
                mediaPlayer.start();

                if(!songsListAdapter.resetSpeed){
                    if(speeding){
                        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed((2F)));
                    }

                    else if(slowing){
                        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed((0.5F)));
                    }
                }
            }
            com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.PLAYING);
            changed = true;
            MainActivity.updateNotification = true;
        });

        looperUpdate();
        mediaPlayer.setOnCompletionListener(mp -> {
            if (repeatState.equals("LOOP_SINGLE")){
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
                com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.PLAYING);
                musicHistory musicHistory = new musicHistory();
                musicHistory.reader(this);
                musicHistory.writer(musicResources.getCurrentSongName(), this);
            }
            else{
                try {
                    skipNext();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if(repeatState.equals("LOOP_OFF") && reachedEndOfPlaylist){
                mediaPlayer.pause();
                com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.PAUSED);
                playPause.setBackgroundResource(R.drawable.ic_playing_icon);
            }
            reachedEndOfPlaylist = false;
            playPause.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
            MainActivity.updateNotification = true;
            MainActivity.updateMiniPlayer = true;
        });

        mediaPlayerInstance.songNames.add(musicData.get(mediaPlayerInstance.currentIndex).getTitle());
        songNamePlayer.setText(com.nullinnix.orange.musicResources.setSongAndArtist()[0]);
        albumName.setText(com.nullinnix.orange.musicResources.setSongAndArtist()[1]);
        timerUpdateCurrentTime();
        setBackground();
        if(thumbnailsArrayLists.contains(musicResources.getCurrentSongName() + ".jpeg")){
            albumCover.setBackground(null);
            albumCover.setImageBitmap(BitmapFactory.decodeFile(getDir("thumbnails", MODE_PRIVATE).toString() + "/" + musicResources.getCurrentSongName() + ".jpeg"));
        }

        else{
            albumCover.setImageBitmap(null);
            albumCover.setBackgroundResource(R.drawable.ic_music_icon_alt);
        }

        if(albumAnimator.isPaused()){
            albumAnimator.resume();
        }

        musicHistory musicHistory = new musicHistory();
        musicHistory.reader(this);
        musicHistory.writer(musicResources.getCurrentSongName(), this);

        if(shuffleOn){
            while(shuffledSongs.size() != musicDataRaw.size()){
                Random randomMusicData = new Random();
                int randomIndex = randomMusicData.nextInt(musicDataRaw.size());
                if(!shuffledSongs.contains(musicDataRaw.get(randomIndex))){
                    shuffledSongs.add(musicDataRaw.get(randomIndex));
                }
            }
            musicData = shuffledSongs;
        }else{
            musicData = musicDataRaw;
        }
        timerUpdateRemainingTime();
    }

    void playMusicWithoutPrepare() {
        setBackground();
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.PLAYING);
        }
        songNamePlayer.setText(com.nullinnix.orange.musicResources.setSongAndArtist()[0]);
        albumName.setText(com.nullinnix.orange.musicResources.setSongAndArtist()[1]);
        timerUpdateCurrentTime();

        mediaPlayer.setOnCompletionListener(mp -> {
            if (repeatState.equals("LOOP_SINGLE")){
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
                com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.PLAYING);

                if(speeding){
                    mediaPlayer.getPlaybackParams().setSpeed(2.0F);
                }

                if(slowing){
                    mediaPlayer.getPlaybackParams().setSpeed(.5F);
                }

            }
            else{
                try {
                    skipNext();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if(repeatState.equals("LOOP_OFF") && reachedEndOfPlaylist){
                mediaPlayer.pause();
                com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.PAUSED);
                playPause.setBackgroundResource(R.drawable.ic_playing_icon);
            }
            reachedEndOfPlaylist = false;
            playPause.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
            changed = true;
            MainActivity.updateNotification = true;
            MainActivity.updateMiniPlayer = true;

        });

        for (AudioModel model:musicData) {
            if(model.getTitle().equalsIgnoreCase(mediaPlayerInstance.songNames.get( mediaPlayerInstance.songNames.size() - 1))){
                totalTime.setText(musicResources.milliToMinutes(model.getDuration()));
                File file = new File(getDir("thumbnails", MODE_PRIVATE).toString() + "/" + model.getTitle() + ".jpeg");
                if (file.exists()){
                    albumCover.setImageBitmap(BitmapFactory.decodeFile(file.toString()));
                    backgroundImageView.setImageBitmap(BitmapFactory.decodeFile(file.toString()));
                }
                else{
                    albumCover.setImageBitmap(null);
                    albumCover.setBackgroundResource(R.drawable.ic_music_icon_alt);
                    backgroundImageView.setImageBitmap(null);
                    backgroundImageView.setBackgroundResource(R.drawable.ic_music_icon_alt);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    backgroundImageView.setRenderEffect(
                            RenderEffect.createBlurEffect(
                                    360.0f, 360.0f, Shader.TileMode.CLAMP
                            )
                    );
                }
            }
        }

        if(shuffleOn){
            while(shuffledSongs.size() != musicDataRaw.size()){
                Random randomMusicData = new Random();
                int randomIndex = randomMusicData.nextInt(musicDataRaw.size());
                if(!shuffledSongs.contains(musicDataRaw.get(randomIndex))){
                    shuffledSongs.add(musicDataRaw.get(randomIndex));
                }
            }
            musicData = shuffledSongs;
        }else{
            musicData = musicDataRaw;
        }

        timerUpdateRemainingTime();
        MainActivity.updateNotification = true;
        speedChanged = true;
    }

    void playPause() {
        if (mediaPlayer.isPlaying()) {
            playPause.setBackgroundResource(R.drawable.ic_playing_icon);
            mediaPlayer.pause();
            com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.PAUSED);
            albumAnimator.pause();
            isPaused = true;
        } else {
            playPause.setBackgroundResource(R.drawable.ic_paused_icon);
            mediaPlayer.start();
            com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.PLAYING);
            albumAnimator.resume();
            isPaused = false;
        }
        playPause.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
        MainActivity.updateNotification = true;
        speedChanged = true;
    }

     public void skipNext() throws IOException {
        if(musicData.size() > 0) {
            if (shuffleOn) {
                musicData = shuffledSongs;
                ArrayList<String> shuffledSongsNames = new ArrayList<>();
                shuffledSongs.forEach((n) -> {
                    shuffledSongsNames.add(n.getTitle());
                });

                mediaPlayerInstance.currentIndex = shuffledSongsNames.indexOf(mediaPlayerInstance.songNames.get(mediaPlayerInstance.songNames.size() - 1));
            } else {
                musicData = musicDataRaw;
            }

            Log.d(TAG, String.valueOf(shuffledSongs.size()));
            Log.d(TAG, String.valueOf(musicDataRaw.size()));

            playPause.setBackgroundResource(R.drawable.ic_paused_icon);
            if (mediaPlayerInstance.currentIndex + 1 < musicData.size()) {
                mediaPlayer.reset();
                mediaPlayerInstance.songNames.add(musicData.get(mediaPlayerInstance.currentIndex + 1).getTitle());
                mediaPlayer.setDataSource(musicResources.getCurrentPath());
                com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.RESET);
                com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.SET);

                mediaPlayerInstance.currentIndex = mediaPlayerInstance.currentIndex + 1;
            } else {
                if (repeatState.equals("LOOP_ALL") || repeatState.equals("LOOP_SINGLE")) {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(musicData.get(0).getPath());
                    com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.RESET);
                    com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.SET);
                    mediaPlayerInstance.currentIndex = 0;
                } else {
                    reachedEndOfPlaylist = true;
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(musicData.get(mediaPlayerInstance.currentIndex).getPath());
                    com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.RESET);
                    com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.SET);
                    albumAnimator.pause();
                }
                MainActivity.updateNotification = true;
            }
            totalTime.setText(musicResources.milliToMinutes(musicData.get(mediaPlayerInstance.currentIndex).getDuration()));
            playMusic();
            timer.cancel();
            playPause.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));

            Log.d(TAG, String.valueOf(shuffledSongs.size()));
            Log.d(TAG, String.valueOf(musicDataRaw.size()));
        }
    }

    public void skipPrevious() throws IOException {
        if(musicData.size() > 0){
            playPause.setBackgroundResource(R.drawable.ic_paused_icon);
            if (mediaPlayerInstance.currentIndex - 1 >= 0) {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(musicData.get(mediaPlayerInstance.currentIndex - 1).getPath());
                com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.RESET);
                com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.SET);
                mediaPlayerInstance.currentIndex = mediaPlayerInstance.currentIndex - 1;
            } else {
                if(!repeatState.equals("LOOP_OFF")){
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(musicData.get(musicData.size() - 1).getPath());
                    com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.RESET);
                    com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.SET);
                    mediaPlayerInstance.currentIndex = musicData.size() - 1;
                }else{
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(musicData.get(0).getPath());
                    com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.RESET);
                    com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.SET);
                }
            }

            totalTime.setText(musicResources.milliToMinutes(musicData.get(mediaPlayerInstance.currentIndex).getDuration()));
            playMusic();
            playPause.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
            timer.cancel();
        }
    }

    void makeInvisible(View view){
        if(view.getVisibility() == View.VISIBLE){
            view.setVisibility(View.INVISIBLE);
        }
    }

    void onClickListeners(){
        playPause.setOnClickListener(v -> {
            makeInvisible(albumCoverOnClick);
            playPause();

        });

        skipNext.setOnClickListener(v -> {
            makeInvisible(albumCoverOnClick);
            try {
                skipNext();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        skipPrevious.setOnClickListener((v -> {
            makeInvisible(albumCoverOnClick);
            try {
                skipPrevious();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));

        musicPlayerWindowLayout.setOnClickListener(v -> {
            if(albumCoverOnClick.getVisibility() == View.VISIBLE){
                albumCoverOnClick.setVisibility(View.INVISIBLE);
            }
        });
        albumCover.setOnLongClickListener(v -> {
            if(albumCoverOnClick.getVisibility() == View.VISIBLE){
                makeInvisible(albumCoverOnClick);
            }

            else{
                albumCoverOnClick.setVisibility(View.VISIBLE);
            }
            return true;
        });

        albumCover.setOnClickListener(v -> {
            if(albumCoverOnClick.getVisibility() == View.VISIBLE){
                makeInvisible(albumCoverOnClick);
            }
            if(mediaPlayer.getPlaybackParams().getSpeed() != 1.0){
                if(mediaPlayer.getPlaybackParams().getSpeed() != 0){
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(((float) 1.0)));
                    fast.setVisibility(View.INVISIBLE);
                    slow.setVisibility(View.INVISIBLE);
                    albumAnimator.setDuration(speed);
                }
            }

            speedChanged = true;
        });

        totalTime.setOnClickListener(v -> {
            SharedPreferences.Editor editor = totalRemaining.edit();
            if (state.equals("TIME_REMAINING")){
                state = "TOTAL_TIME";
                editor.putString("TotalRemaining", state);
                totalTime.setText(musicResources.milliToMinutes(musicResources.getTotalTime()));
            }else{
                state = "TIME_REMAINING";
                editor.putString("TotalRemaining", state);
            }
            editor.apply();
            makeInvisible(albumCoverOnClick);
            timerUpdateRemainingTime();
        });

        changeAlbumCover.setOnClickListener(v -> {
            thumbnailPicker();
            makeInvisible(albumCoverOnClick);
        });

        int[] totalTime = {musicResources.getTotalTime()};
        Timer currentTimeTimer = new Timer();
        currentTimeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int percentage = mediaPlayer.getCurrentPosition() * 100;
                percentage = percentage / totalTime[0];
                if(mediaPlayer.isPlaying()){
                    currentTime.post(() -> currentTime.setText(musicResources.milliToMinutes(mediaPlayer.getCurrentPosition())));
                    seekBar.setProgress(percentage);
                }
                if(albumCover.getWindowVisibility() == View.VISIBLE){
                    totalTime[0] = musicResources.getTotalTime();
                }
            }
        }, 0, 1000);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
                com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.PAUSED);
                albumAnimator.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if(progress < 95 && progress > 5){
                    progress += 2;
                }
                int percentageChanged = musicResources.getTotalTime() * progress / 100;

                if(Integer.parseInt(musicData.get(mediaPlayerInstance.currentIndex).getDuration()) == 0 || progress == 0){
                    mediaPlayer.seekTo(0);
                }

                else{
                    if(percentageChanged >= Integer.parseInt(musicData.get(mediaPlayerInstance.currentIndex).getDuration())){
                        try {
                            skipNext();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    else {
                        mediaPlayer.seekTo(percentageChanged);
                    }
                }
                if(!isPaused){
                    mediaPlayer.start();
                    com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.PLAYING);
                }
                makeInvisible(albumCoverOnClick);
                albumAnimator.resume();
            }
        });

        repeating.setOnClickListener(v -> {
            if (repeatState.equals("LOOP_ALL")){
                repeatState = "LOOP_SINGLE";
                repeating.setBackgroundResource(R.drawable.ic_repeat_one);
                Toast.makeText(this, "Looping " + musicResources.getCurrentSongName(), Toast.LENGTH_SHORT).show();
            } else if (repeatState.equals("LOOP_SINGLE")) {
                repeatState = "LOOP_OFF";
                repeating.setBackgroundResource(R.drawable.ic_repeat_all_off);
                Toast.makeText(this, "Looping Off", Toast.LENGTH_SHORT).show();
            }
            else {
                repeatState = "LOOP_ALL";
                repeating.setBackgroundResource(R.drawable.ic_repeat_all_on);
                Toast.makeText(this, "Looping All", Toast.LENGTH_SHORT).show();
            }
            looperUpdate();
            makeInvisible(albumCoverOnClick);
            repeating.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
        });

        shuffle.setOnClickListener(v -> {
            SharedPreferences shuffling = getSharedPreferences("shuffling", MODE_PRIVATE);
            SharedPreferences.Editor editor = shuffling.edit();

            if(shuffleOn){
                shuffleOn = false;
                shuffle.setBackgroundResource(R.drawable.ic_shuffle_off);
                musicData = musicDataRaw;
                shuffledSongs.clear();
                editor.putBoolean("shuffling", false);
                Toast.makeText(this, "Shuffle is Off", Toast.LENGTH_SHORT).show();
            }
            else{
                shuffleOn = true;
                shuffle.setBackgroundResource(R.drawable.ic_shuffle);
                while(shuffledSongs.size() != musicDataRaw.size()){
                    Random randomMusicData = new Random();
                    int randomIndex = randomMusicData.nextInt(musicDataRaw.size());
                    if(!shuffledSongs.contains(musicDataRaw.get(randomIndex))){
                        shuffledSongs.add(musicDataRaw.get(randomIndex));
                    }
                }
                Toast.makeText(this, "Shuffle is On", Toast.LENGTH_SHORT).show();
                editor.putBoolean("shuffling", true);
            }

            editor.apply();
            makeInvisible(albumCoverOnClick);
            shuffle.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });

        playbackSpeedSlow.setOnLongClickListener(v -> {
            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(((float) 0.5)));
            albumAnimator.setDuration(speed * 4);
            playPause.setBackgroundResource(R.drawable.ic_paused_icon);
            if(albumAnimator.isPaused()){
                albumAnimator.resume();
            }
            makeInvisible(albumCoverOnClick);
            playPause.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
            speedChanged = true;
            songsListAdapter.resetSpeed = false;
            return true;
        });

        playbackSpeedFast.setOnLongClickListener(v -> {
            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(((float) 2.0)));
            albumAnimator.setDuration(speed / 4);
            playPause.setBackgroundResource(R.drawable.ic_paused_icon);
            if(albumAnimator.isPaused()){
                albumAnimator.resume();
            }
            makeInvisible(albumCoverOnClick);
            playPause.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
            speedChanged = true;
            songsListAdapter.resetSpeed = false;
            return true;
        });

        skipNext.setOnLongClickListener(v -> {
            if (mediaPlayerInstance.currentIndex + 1 < musicData.size()) {
                Toast.makeText(musicPlayerWindowActivity.this, "Next: " + musicData.get(mediaPlayerInstance.currentIndex + 1).getTitle(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(musicPlayerWindowActivity.this, "End of PlayList", Toast.LENGTH_SHORT).show();
            }
        return true;
        });

        lyrics.setOnClickListener(v -> {
            Intent intent = new Intent(this, Lyrics.class);
            startActivity(intent);
        });

        guideLayout.setOnClickListener(v -> {
            if(guideLayout.getVisibility() == View.VISIBLE){
                if(guideSteps == 0){
                    albumCoverGuide.setVisibility(View.GONE);
                    guideSlow.setVisibility(View.VISIBLE);
                    tip.setText("You can make the song play slower by long clicking on the left side of the screen, press the spinning thing to go back to normal speed. Press anywhere to continue");
                    tip.setVisibility(View.VISIBLE);
                }else if(guideSteps == 1){
                    guideSlow.setVisibility(View.GONE);
                    guideFast.setVisibility(View.VISIBLE);
                    tip.setText("You can make the song play faster by long clicking on the right side of the screen, press the spinning thing to go back to normal speed. Press anywhere to continue");
                } else if (guideSteps >= 2) {
                    guideFast.setVisibility(View.GONE);
                    guideLayout.setVisibility(View.GONE);
                    mediaPlayer.start();
                    com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.PLAYING);
                    tip.setText("");
                    tip.setVisibility(View.GONE);

                    repeating.setVisibility(View.VISIBLE);
                    skipPrevious.setVisibility(View.VISIBLE);
                    skipNext.setVisibility(View.VISIBLE);
                    playPause.setVisibility(View.VISIBLE);
                    lyrics.setVisibility(View.VISIBLE);
                    shuffle.setVisibility(View.VISIBLE);

                    SharedPreferences.Editor editor = guide.edit();
                    editor.putBoolean("firstPlay", false);
                    editor.apply();

                    changed = true;

                    Toast.makeText(this, "There's so much more to discover with Orange :)", Toast.LENGTH_SHORT).show();
                }
                guideSteps += 1;
            }
        });
    }

    void timerUpdateCurrentTime(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if(com.nullinnix.orange.musicResources.getState() != com.nullinnix.orange.musicResources.NOT_SET && albumCover.getWindowVisibility() == View.VISIBLE && speedChanged){
                        if(mediaPlayer.getPlaybackParams().getSpeed() != 1.0){
                            if(mediaPlayer.getPlaybackParams().getSpeed() > 1.0){
                                fast.setVisibility(View.VISIBLE);
                                slow.setVisibility(View.INVISIBLE);
                                albumAnimator.setDuration(speed / 4);
                                speeding = true;
                                slowing = false;
                            }

                            if(mediaPlayer.getPlaybackParams().getSpeed() < 1.0 && mediaPlayer.getPlaybackParams().getSpeed() > 0.1){
                                fast.setVisibility(View.INVISIBLE);
                                slow.setVisibility(View.VISIBLE);
                                albumAnimator.setDuration(speed * 4);
                                slowing = true;
                                speeding = false;
                            }

                        }else{
                            fast.setVisibility(View.INVISIBLE);
                            slow.setVisibility(View.INVISIBLE);
                            albumAnimator.setDuration(speed);
                            speeding = false;
                            slowing = false;
                        }

                        speedChanged = false;
                    }

                    if(changed){
                        if(!mediaPlayer.isPlaying() && albumCover.getWindowVisibility() == View.VISIBLE){
                            albumAnimator.pause();
                            playPause.setBackgroundResource(R.drawable.ic_playing_icon);
                        }
                        else{
                            albumAnimator.resume();
                            playPause.setBackgroundResource(R.drawable.ic_paused_icon);
                        }
                        playPause.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
                    }

                    if(updatePlayer && albumCover.getWindowVisibility() == View.VISIBLE){
                        updateOnNotification();
                        updatePlayer = false;
                    }

                });
            }
        }, 0, 10);
    }

    void timerUpdateRemainingTime(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if(state.equals("TOTAL_TIME") && albumCover.getWindowVisibility() == View.VISIBLE){
                        timer.cancel();
                    }
                    else {
                        if(albumCover.getWindowVisibility() == View.VISIBLE){
                            long remainingTime = musicResources.getTotalTime() - mediaPlayer.getCurrentPosition();
                            totalTime.post(() -> totalTime.setText(String.format("-%s", musicResources.milliToMinutes(remainingTime))));
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    void looperUpdate(){
        if(repeatState.equals("LOOP_OFF") && musicData.size() - 2 == mediaPlayerInstance.currentIndex - 1 ){
            skipNext.setClickable(false);
        }
        else {
            skipNext.setClickable(true);
        }
    }

    void setBackground(){
        thumbnailsArrayLists = new ArrayList<>(Arrays.asList(thumbnails.list()));
        if(thumbnailsArrayLists.contains(musicResources.getCurrentSongName() + ".jpeg")){
            backgroundImageView.setBackground(null);
            backgroundImageView.setImageBitmap(BitmapFactory.decodeFile(getDir("thumbnails", MODE_PRIVATE).toString() + "/" + musicResources.getCurrentSongName() + ".jpeg"));
        }

        else{
            backgroundImageView.setImageBitmap(null);
            backgroundImageView.setBackgroundResource(R.drawable.ic_music_icon_alt);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            backgroundImageView.setRenderEffect(
                    RenderEffect.createBlurEffect(
                            360.0f, 360.0f, Shader.TileMode.CLAMP
                    )
            );
        }
    }

    void thumbnailPicker(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Orange wants you to select a picture"), 200);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 200){
            Uri uri = data.getData();
            albumCover.setImageURI(uri);
            Toast.makeText(this, "Orange is Working on it :)", Toast.LENGTH_SHORT).show();
            setMusicThumbnails();
        }

        else{
            Toast.makeText(this, "Orange could not change that song's Album Art :(", Toast.LENGTH_LONG).show();
        }
    }
    void setIDs(){
        songNamePlayer = findViewById(R.id.songNamePlayer);
        albumName = findViewById(R.id.musicAlbum);
        playPause = findViewById(R.id.playPause);
        skipNext = findViewById(R.id.skipNext);
        skipPrevious = findViewById(R.id.skipPrevious);
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);
        albumCover = findViewById(R.id.songAlbumCover);
        seekBar = findViewById(R.id.seekBar);
        musicPlayerWindowLayout = findViewById(R.id.musicPlayerWindowLayout);
        albumCoverOnClick = findViewById(R.id.albumCoverOnClickLayout);
        repeating = findViewById(R.id.repeating);
        shuffle = findViewById(R.id.shuffle);
        playbackSpeedSlow = findViewById(R.id.playbackSpeedSlow);
        playbackSpeedFast = findViewById(R.id.playbackSpeedFast);
        slow = findViewById(R.id.slow);
        fast = findViewById(R.id.fast);
        backgroundImageView = findViewById(R.id.backgroundImageView);
        changeAlbumCover = findViewById(R.id.changeAlbumCover);
        lyrics = findViewById(R.id.lyrics);
        guideLayout = findViewById(R.id.guideLayout);
        tip = findViewById(R.id.tip);
        guideFast = findViewById(R.id.playbackSpeedFastGuide);
        guideSlow = findViewById(R.id.playbackSpeedSlowGuide);
        albumCoverGuide = findViewById(R.id.songAlbumCoverGuide);
        currentPlaylist = findViewById(R.id.currentPlaylist);
        currentPlaylistText = findViewById(R.id.currentPlaylistText);
    }

    void setMusicThumbnails(){
        Drawable pickedThumbnail = albumCover.getDrawable();
        Bitmap bitmap = ((BitmapDrawable)pickedThumbnail).getBitmap();

        File file = new File(getDir("thumbnails", MODE_PRIVATE).toString() + "/" + musicResources.getCurrentSongName() + ".jpeg");
        try{
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 15, fos);
            Toast.makeText(this, "Enjoy your new Album Art :)", Toast.LENGTH_SHORT).show();
            setBackground();

            songsListAdapter songsListAdapterInstance = new songsListAdapter(this, songsListAdapter.audioModels);

            ArrayList<String> songNames = new ArrayList<>();
            for(AudioModel model:songsListAdapter.audioModels){
                songNames.add(model.getTitle());
            }

            if(songNames.contains(musicResources.getCurrentSongName())){
                songsListAdapter.thumbnailsArrayList = new ArrayList<>(Arrays.asList(thumbnails.list()));
                songsListAdapterInstance.notifyItemChanged(songNames.indexOf(musicResources.getCurrentSongName()));
            }

        } catch(IOException e){
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong :(", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateOnNotification(){
        setBackground();
        totalTime.setText(musicResources.milliToMinutes(musicResources.getTotalTime()));
        songNamePlayer.setText(com.nullinnix.orange.musicResources.setSongAndArtist()[0]);
        albumName.setText(com.nullinnix.orange.musicResources.setSongAndArtist()[1]);

        if(thumbnailsArrayLists.contains(musicResources.getCurrentSongName() + ".jpeg")){
            albumCover.setBackground(null);
            albumCover.setImageBitmap(BitmapFactory.decodeFile(getDir("thumbnails", MODE_PRIVATE).toString() + "/" + musicResources.getCurrentSongName() + ".jpeg"));
        }

        else{
            albumCover.setImageBitmap(null);
            albumCover.setBackgroundResource(R.drawable.ic_music_icon_alt);
        }
    }

    @Override
    public void onBackPressed() {
        MainActivity.updateMiniPlayer = true;
        MainActivity.playStateChanged = true;
        super.onBackPressed();
    }
}