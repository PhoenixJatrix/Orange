package com.nullinnix.orange;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import Services.OnClearFromRecentService;

public class MainActivity extends AppCompatActivity {
    ArrayList<AudioModel> songsList = new ArrayList<>();
    static ArrayList<String> exemptedPlaylists = new ArrayList<>();
    ArrayList<String> playlistsArranged = new ArrayList<>();
    RecyclerView recyclerView, playlistRecyclerView;
    TextView noSongsFoundMessage, currentPlaylist, playlistLabel, tipTextView;
    Spinner playlistsSpinner;
    ConstraintLayout addPlaylistLayout, mainLayout, playlistLayout, tools, searchLayout, loadingScreenLayout, tipLayout;
    LinearLayoutCompat multiSelectLayout;
    EditText playlistName, searchEditText;
    static ArrayList<String> playlists = new ArrayList<>();
    InputMethodManager imm;
    CardView miniPlayerCardView;
    Button playPause, skipNext, skipPrevious, selectAll, deselectAll, selectInterval, removeFromPlaylist, settings, createPlaylist, addPlaylist, savePlaylist, deleteSelected, search, cancelSearch, clearTipButton;
    boolean isPaused, playingNext = false;
    MediaPlayer mediaPlayer = mediaPlayerInstance.getInstance();
    ImageView miniPlayerAlbumCover;
    ProgressBar miniPlayerProgress;
    static boolean selectingAll, isSelectingInterval, deselectedAll, updateNotification, searching, multiSelectingStopped, selected, updateMiniPlayer, playStateChanged, firstRun, scanDone = false;
    favorites favorites = new favorites(this);
    int clicks = 0;
    musicHistory musicHistory = new musicHistory();
    ActivityResultLauncher<IntentSenderRequest> songsDeleteLauncher;
    final songsListAdapter[] songsListAdapterInstance = new songsListAdapter[1];
    final historyAdapter[] historyAdapterInstance = new historyAdapter[1];
    static boolean absoluteSuspendMiniPlayer = false;
    musicResources musicResources;
    NotificationManager notificationManager;
    ArrayAdapter<String> playlistAdapterArray;
    static String currentPlaylistToPlayer;
    static boolean garbageCollecting = false;
    LinearLayoutManager linearLayoutManager;
    SharedPreferences sharedPreferencesHistory;
    Cursor cursor;

    String[] tips = {"You can delete a playlist by long clicking on the playlist name at the top of the screen",
            "You can remove all your favorite songs by long clicking on the favorite icon",
            "What came first? The Orange or the Color?",
            "You just have to paste a song's lyrics once and Orange saves it for you",
            "Don't know how when your song will end? Just click on the song duration to show the remaining time",
            "You can find out the next song in queue by long clicking 'Next' button",
            "Playing songs in the lyrics window automatically repeats that specific song till your switch to the main window",
            "Don't like all this Orange everywhere? You can change colors of almost anything in settings",
            "Are you still listening?",
            "We hope you get an Orange for every bug you report!",
            "You can see how many times you've played a specific song in the History section",
            "Why did the Orange go to the movies? To see 'Pulp' Fiction. Get it? 'Pulp'? I'll see myself out",
            "You will lose your old album cover if you change it. Reinstalling the app resets all album covers to default",
            "You get 3 Oranges if you leave a review pn Play Store :)"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!checkPermission()) {
            getPermission();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        recyclerView = findViewById(R.id.recyclerView);
        noSongsFoundMessage = findViewById(R.id.noSongsFoundMessage);
        playlistsSpinner = findViewById(R.id.playlistsSpinner);
        addPlaylist = findViewById(R.id.playlistsManager);
        savePlaylist = findViewById(R.id.savePlaylistButton);
        playlistName = findViewById(R.id.playlistNameEditText);
        addPlaylistLayout = findViewById(R.id.addPlaylistLayout);
        currentPlaylist = findViewById(R.id.currentPlaylist);
        miniPlayerCardView = findViewById(R.id.miniPlayer);
        playPause = findViewById(R.id.playPauseMiniPlayer);
        skipNext = findViewById(R.id.skipNextMiniPlayer);
        skipPrevious = findViewById(R.id.skipPreviousMiniPlayer);
        miniPlayerAlbumCover = findViewById(R.id.miniPlayerAlbumCover);
        miniPlayerProgress = findViewById(R.id.miniPlayerProgress);
        multiSelectLayout = findViewById(R.id.multiSelectLayout);
        selectAll = findViewById(R.id.selectAll);
        deselectAll = findViewById(R.id.deselectAll);
        selectInterval = findViewById(R.id.selectInterval);
        removeFromPlaylist = findViewById(R.id.remove_from_playlist);
        mainLayout = findViewById(R.id.mainLayout);
        settings = findViewById(R.id.settings);
        playlistRecyclerView = findViewById(R.id.playlistRecyclerView);
        playlistLayout = findViewById(R.id.playlistRecyclerViewLayout);
        createPlaylist = findViewById(R.id.createPlaylistButton);
        playlistLabel = findViewById(R.id.playlistLabel);
        deleteSelected = findViewById(R.id.deleteSelected);
        search = findViewById(R.id.search);
        searchEditText = findViewById(R.id.searchEditText);
        tools = findViewById(R.id.tools);
        searchLayout = findViewById(R.id.searchLayout);
        cancelSearch = findViewById(R.id.cancelSearch);
        loadingScreenLayout = findViewById(R.id.loadingScreenLayout);
        tipLayout = findViewById(R.id.tipLayout);
        tipTextView = findViewById(R.id.tipTextView);
        clearTipButton = findViewById(R.id.clearTipButton);

        String[] projection = {
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media._ID
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                "");

        while (cursor.moveToNext()) {
            AudioModel songData = new AudioModel(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
            if (new File(songData.getPath()).exists()) {
                songsList.add(songData);
            }
        }

        if (songsList.size() > 0) {
            noSongsFoundMessage.setVisibility(GONE);
        } else {
            noSongsFoundMessage.setBackgroundColor(Color.parseColor("black"));
        }

        linearLayoutManager = new LinearLayoutManager(MainActivity.this);

        musicHistory.reader(this);
        musicHistory.updateHistory(songsList, this);
        notificationManager = getSystemService(NotificationManager.class);

        sharedPreferencesHistory = getSharedPreferences("historyCreated", MODE_PRIVATE);

        SharedPreferences sharedPreferences = getSharedPreferences("numberOfSongs", MODE_PRIVATE);
        int numberOfSongs = sharedPreferences.getInt("numberOfSongs", 0);
        if (numberOfSongs != songsList.size()) {
            getMusicThumbnails();
            Toast.makeText(this, "Orange is updating your Music Library", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("numberOfSongs", songsList.size());
            editor.apply();
        }

        if (!sharedPreferencesHistory.getBoolean("historyCreated", false)) {
            SharedPreferences.Editor historyEditor = sharedPreferencesHistory.edit();
            historyEditor.putBoolean("historyCreated", true);
            historyEditor.apply();

            SharedPreferences sharedPreferencesShuffle = getSharedPreferences("shuffling", MODE_PRIVATE);
            SharedPreferences.Editor shuffle = sharedPreferencesShuffle.edit();
            shuffle.putBoolean("shuffling", false);
            shuffle.apply();

            if (songsList.size() > 0) {
                noSongsFoundMessage.setVisibility(GONE);
                try {
                    createSongHistory(songsList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            getPlayListsOnFirstLaunch();
        }

        registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));

        playlistsManager playlistsManager = new playlistsManager(this, songsList);
        playlists =  com.nullinnix.orange.playlistsManager.getPlaylists();
        onclickListeners();

        SharedPreferences iconColor = getSharedPreferences("Icon Color", MODE_PRIVATE);
        String[] iconColors = iconColor.getString("Icon Color", "254,83,20").split(",");

        SharedPreferences textColor = getSharedPreferences("Text Color", MODE_PRIVATE);
        String[] textColors = textColor.getString("Text Color", "254,83,20").split(",");

        SharedPreferences backgroundColor = getSharedPreferences("Background Color", MODE_PRIVATE);
        String[] backgroundColors = backgroundColor.getString("Background Color", "0,0,0").split(",");

        recyclerView.setPadding(0,0,0,150);

        Timer updater = new Timer();
        updater.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    SharedPreferences sharedPreferencesCurrentList = getSharedPreferences("lastPlaylist", MODE_PRIVATE);
                    if(selected){
                        if(songsListAdapter.multiSelecting[0]){
                            String songsSelected = songsListAdapter.selected.size() + "/" + songsListAdapter.audioModels.size();
                            currentPlaylist.post(() -> currentPlaylist.setText(songsSelected));
                            playlistsSpinner.post(() -> playlistsSpinner.setVisibility(INVISIBLE));
                            settings.post(() -> settings.setVisibility(INVISIBLE));
                            search.setVisibility(INVISIBLE);
                            notificationManager.cancel(6062);
                        }

                        if(songsListAdapter.selected.size() > 0 && songsListAdapter.multiSelecting[0]){
                            if(!sharedPreferencesCurrentList.getString("lastPlaylist", "ALL").equalsIgnoreCase("all") && !sharedPreferencesCurrentList.getString("lastPlaylist", "ALL").equalsIgnoreCase("favorites")){
                                removeFromPlaylist.post(() -> removeFromPlaylist.setVisibility(VISIBLE));
                            }
                            addPlaylist.post(() -> addPlaylist.setVisibility(View.VISIBLE));
                            miniPlayerCardView.post(() -> miniPlayerCardView.setVisibility(INVISIBLE));
                            multiSelectLayout.post(() -> multiSelectLayout.setVisibility(VISIBLE));
                        }else{
                            if(songsListAdapter.multiSelecting[0]){
                                addPlaylist.post(() -> addPlaylist.setVisibility(INVISIBLE));
                                multiSelectLayout.post(() -> multiSelectLayout.setVisibility(INVISIBLE));
                                removeFromPlaylist.post(() -> removeFromPlaylist.setVisibility(INVISIBLE));
                            }
                        }

                        if(songsListAdapter.multiSelecting[0] && songsListAdapter.selected.size() > 0 || miniPlayerCardView.getVisibility() == VISIBLE){
                            recyclerView.setPadding(0,0,0,300);
                        }

                        selected = false;
                    }

                    if(miniPlayerCardView.getVisibility() == VISIBLE && recyclerView.getPaddingBottom() != 300){
                        Log.d(TAG, "run: this done");
                        recyclerView.setPadding(0,0,0,300);
                    }

                    if(multiSelectingStopped){
                        songsListAdapterInstance[0].notifyDataSetChanged();
                        currentPlaylist.post(() -> currentPlaylist.setText(String.format("%s%s", String.valueOf(sharedPreferencesCurrentList.getString("lastPlaylist", "ALL").charAt(0)).toUpperCase(), sharedPreferencesCurrentList.getString("lastPlaylist", "ALL").substring(1).toLowerCase())));
                        Log.d(TAG, currentPlaylist.getText().toString());
                        playlistsSpinner.post(() -> playlistsSpinner.setVisibility(VISIBLE));
                        settings.post(() -> settings.setVisibility(VISIBLE));
                        search.setVisibility(VISIBLE);
                        multiSelectLayout.setVisibility(GONE);
                        addPlaylist.setVisibility(INVISIBLE);
                        removeFromPlaylist.setVisibility(INVISIBLE);
                        Log.d(TAG, "run: multiSelectingStopped");

                        recyclerView.setPadding(0,0,0,150);

                        Log.d(TAG, "run: setting to zero");

                        multiSelectingStopped = false;
                    }

                    if(updateMiniPlayer){
                        if(!songsListAdapter.multiSelecting[0] && !absoluteSuspendMiniPlayer){
                            miniPlayerCardView.post(() -> miniPlayerCardView.setVisibility(View.VISIBLE));
                            Log.d(TAG, "run: updated");
                            setBackground();
                        }
                        if(songsListAdapter.multiSelecting[0] || absoluteSuspendMiniPlayer){
                            miniPlayerCardView.post(() -> miniPlayerCardView.setVisibility(INVISIBLE));
                            if(absoluteSuspendMiniPlayer){

                                recyclerView.setPadding(0,0,0,150);

                                Log.d(TAG, "run: setting to zero");
                            }

                            Log.d(TAG, "run: made invisible");
                        }

                        ArrayList<String> songNamesFromAdapter = new ArrayList<>();
                        songsListAdapter.audioModels.forEach(v -> {
                            songNamesFromAdapter.add(v.getTitle());
                        });


                        linearLayoutManager.scrollToPosition(songNamesFromAdapter.indexOf( mediaPlayerInstance.songNames.get(mediaPlayerInstance.songNames.size() - 1)));
                        Log.d(TAG, String.valueOf(songNamesFromAdapter.indexOf( mediaPlayerInstance.songNames.get(mediaPlayerInstance.songNames.size() - 1))));
                        updateMiniPlayer = false;
                    }

                    if(playStateChanged && !garbageCollecting){
                        if(mediaPlayer.isPlaying()){
                            playPause.setBackgroundResource(R.drawable.ic_paused_icon);
                            playPause.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
                        }

                        else{
                            playPause.setBackgroundResource(R.drawable.ic_playing_icon);
                            playPause.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
                        }
                        playStateChanged = false;
                    }

                    if (miniPlayerCardView.getWindowVisibility() == VISIBLE){
                        if(playingNext){
                            setBackground();
                            playingNext = false;
                        }
                    }

                    if(!garbageCollecting){
                        if(mediaPlayer.isPlaying() && miniPlayerCardView.getWindowVisibility() == VISIBLE){
                            int duration = Integer.parseInt(musicPlayerWindowActivity.musicData.get(mediaPlayerInstance.currentIndex).getDuration());
                            int currentTime = mediaPlayer.getCurrentPosition();
                            int progress = currentTime * 100 / duration;
                            miniPlayerProgress.post(() -> miniPlayerProgress.setProgress(progress));
                        }
                        isPaused = !mediaPlayer.isPlaying();
                    }

                    if(playlistAdapter.edited){
                        playlistLayout.post(() -> playlistLayout.setVisibility(INVISIBLE));

                        playlists = com.nullinnix.orange.playlistsManager.getPlaylists();
                        playlists.remove(sharedPreferences.getString("lastPlaylist", "all"));
                        playlistsArranged.add(sharedPreferences.getString("lastPlaylist", "all"));
                        playlistsArranged.addAll(playlists);

                        playlistsArranged = removeDuplicates(playlistsArranged);
                        playlists = playlistsArranged;
                        playlistAdapterArray.notifyDataSetChanged();
                        playlistsSpinner.setAdapter(playlistAdapterArray);
                        playlistsSpinner.setSelection(playlists.indexOf(playlistAdapter.editedPlaylist.toUpperCase()));
                        searchEditText.setVisibility(INVISIBLE);
                        songsListAdapter.selected.clear();
                        songsListAdapter.multiSelecting[0] = false;
                        multiSelectingStopped = true;
                        playlistAdapter.edited = false;
                    }

                    if(currentPlaylist.getVisibility() == VISIBLE){
                        currentPlaylist.post(() -> currentPlaylist.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2]))));
                    }

                    if(updateNotification && !absoluteSuspendMiniPlayer){
                        showNotification();
                        Log.d(TAG, "run: updating notification");
                        updateNotification = false;
                    }else if(absoluteSuspendMiniPlayer){
                        NotificationManager manager = getSystemService(NotificationManager.class);
                        manager.cancel(6062);
                    }

                    if(searchEditText.getVisibility() == VISIBLE && tools.getVisibility() != INVISIBLE){
                        tools.setVisibility(INVISIBLE);
                    }else if(searchEditText.getVisibility() != VISIBLE && tools.getVisibility() != VISIBLE){
                        tools.setVisibility(VISIBLE);
                    }

                    if (searching && songsListAdapter.selected.size() > 0 && addPlaylist.getVisibility() != VISIBLE){
                        addPlaylist.setVisibility(VISIBLE);
                    }

                    if(searching && cancelSearch.getVisibility() != VISIBLE){
                        cancelSearch.setVisibility(VISIBLE);
                    }else if(!searching && cancelSearch.getVisibility() != INVISIBLE){
                        cancelSearch.setVisibility(INVISIBLE);
                    }

                    if(songsListAdapter.updateCurrentPlaylist){
                        currentPlaylistToPlayer = playlistsSpinner.getSelectedItem().toString();
                        songsListAdapter.updateCurrentPlaylist = false;
                    }

                    absoluteSuspendMiniPlayer = searching;
                });

                if(firstRun && songsList.size() < 1){
                    firstRun = false;
                    loadingScreenLayout.setVisibility(VISIBLE);
                    getMusicThumbnails();

                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(() -> {
                                Log.d(TAG, "run: running in Time");
                                if(scanDone){
                                    Toast.makeText(MainActivity.this, "Done Scanning. Restarting...", Toast.LENGTH_SHORT).show();
                                    Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                                    startActivity(Intent.makeRestartActivityTask(intent.getComponent()));
                                    Runtime.getRuntime().exit(0);
                                }
                            });
                        }
                    }, 0, 1000);
                }
            }
        }, 10, 10);

        mainLayout.setBackgroundColor(Color.rgb(Integer.parseInt(backgroundColors[0]), Integer.parseInt(backgroundColors[1]), Integer.parseInt(backgroundColors[2])));
        skipPrevious.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
        skipNext.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
        playPause.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
        multiSelectLayout.setBackgroundColor(Color.rgb(Integer.parseInt(backgroundColors[0]), Integer.parseInt(backgroundColors[1]), Integer.parseInt(backgroundColors[2])));

        selectAll.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
        selectInterval.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
        deselectAll.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
        deleteSelected.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));

        removeFromPlaylist.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
        addPlaylist.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
        playlistsSpinner.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
        settings.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));

        playlistLayout.setBackgroundColor(getColor(R.color.translucent));
        addPlaylist.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
        playlistLabel.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));

        addPlaylistLayout.setBackgroundColor(getColor(R.color.translucent));
        createPlaylist.getBackground().setTint(getColor(R.color.translucent));
        createPlaylist.setTextColor(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));

        search.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
        SharedPreferences progressBarColor = getSharedPreferences("ProgressBar Color", MODE_PRIVATE);
        String[] progressBarColors = progressBarColor.getString("ProgressBar Color", "254,83,20").split(",");

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

        miniPlayerProgress.setProgressTintList(colorStateList);

        TextViewCompat.setCompoundDrawableTintList(createPlaylist, colorStateList);

        exemptedPlaylists.add("all");
        exemptedPlaylists.add("favorites");
        exemptedPlaylists.add("history");

        final MediaSession.Callback mediaSessionCallback = new MediaSession.Callback() {
            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
                KeyEvent event = mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (event != null && mediaPlayerInstance.songNames.size() > 0 && event.getAction() == KeyEvent.ACTION_UP) {
                    switch(event.getKeyCode()){
                        case(KeyEvent.KEYCODE_MEDIA_PLAY):
                        case(KeyEvent.KEYCODE_MEDIA_PAUSE):
                            playPause();
                            break;

                        case(KeyEvent.KEYCODE_MEDIA_NEXT):
                            try {
                                skipNextMiniPlayer();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;

                        case(KeyEvent.KEYCODE_MEDIA_PREVIOUS):
                            try {
                                skipPreviousMiniPlayer();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                    }
                }
                return super.onMediaButtonEvent(mediaButtonIntent);
            }
        };

        BluetoothProfile.ServiceListener bluetoothProfileListener = new BluetoothProfile.ServiceListener() {
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                MediaSession audioSession = new MediaSession(MainActivity.this, "BLUETOOTH");
                audioSession.setActive(true);
                audioSession.setCallback(mediaSessionCallback);
            }

            public void onServiceDisconnected(int profile) {
                Toast.makeText(MainActivity.this, "BlueTooth Disconnected", Toast.LENGTH_SHORT).show();
            }
        };

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter != null){
            bluetoothAdapter.getProfileProxy(this, bluetoothProfileListener, 2);
        }

        songsDeleteLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result ->  {
            if(result.getResultCode() == Activity.RESULT_OK){
                if(songsListAdapter.selected.size() > 1){
                    Toast.makeText(this, ("Deleted " + songsListAdapter.selected.get(0) + " and " + (songsListAdapter.selected.size() - 1) + " other song(s)"), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, ("Deleted " + songsListAdapter.selected.get(0)), Toast.LENGTH_SHORT).show();
                }

                ArrayList<AudioModel> modelsAdapter = new ArrayList<>();
                if(songsListAdapter.selected.size() == songsListAdapter.audioModels.size()){
                    songsListAdapter.audioModels.clear();
                }else{
                    for (AudioModel model:songsListAdapter.audioModels) {
                        if(songsListAdapter.selected.contains(model.getTitle())){
                            modelsAdapter.add(model);
                        }
                    }
                    songsListAdapter.audioModels.removeAll(modelsAdapter);
                }

                if(mediaPlayerInstance.songNames.size() > 0){
                    ArrayList<AudioModel> modelsPlayer = new ArrayList<>();
                    for (AudioModel model:musicPlayerWindowActivity.musicData) {
                        if(songsListAdapter.selected.contains(model.getTitle())){
                            modelsPlayer.add(model);
                        }
                    }
                    musicPlayerWindowActivity.musicData.removeAll(modelsPlayer);
                }

            }else{
                Toast.makeText(this, "Orange could not delete that :(", Toast.LENGTH_SHORT).show();
            }
            
            songsListAdapterInstance[0].notifyDataSetChanged();
            
            songsListAdapter.multiSelecting[0] = false;
            songsListAdapter.selected.clear();
            absoluteSuspendMiniPlayer = true;
        });

        Random random = new Random();
        if(random.nextInt(2) == 0){
            tipLayout.setVisibility(VISIBLE);
            tipTextView.setText(tips[random.nextInt(tips.length)]);
        }
    }

    void setBackground(){
        if(mediaPlayerInstance.songNames.size() > 0){
            File thumbnail = new File(getDir("thumbnails", MODE_PRIVATE).toString() + "/" + mediaPlayerInstance.songNames.get(mediaPlayerInstance.songNames.size() - 1) + ".jpeg");
            if(thumbnail.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(thumbnail.toString());
                miniPlayerAlbumCover.post(() -> miniPlayerAlbumCover.setBackground(null));
                miniPlayerAlbumCover.post(() -> miniPlayerAlbumCover.setImageBitmap(bitmap));

            }
            else {
                miniPlayerAlbumCover.post(() -> miniPlayerAlbumCover.setImageBitmap(null));
                miniPlayerAlbumCover.post(() -> miniPlayerAlbumCover.setBackgroundResource(R.drawable.ic_music_icon_alt));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(songsListAdapter.multiSelecting[0]){
            selectingAll = false;
            songsListAdapter.multiSelecting[0] = false;
            multiSelectingStopped = true;
            songsListAdapter.selected.clear();
            selectingAll = false;
            isSelectingInterval = false;
            mediaPlayerInstance.songNames.clear();
            MainActivity.multiSelectingStopped = true;

            recyclerView.setPadding(0,150,0,300);

            Log.d(TAG, "run: setting to zero");

            Log.d(TAG, "onBackPressed: \n back pressed \n");

            notificationManager.cancel(6062);
        }

        else if(searching){
            searching = false;
            searchEditText.setVisibility(INVISIBLE);
            searchLayout.setVisibility(INVISIBLE);

            if(playlistsSpinner.getSelectedItem().toString().equalsIgnoreCase("all")){
                songsListAdapterInstance[0] = new songsListAdapter(MainActivity.this, songsList);
            }else{
                songsListAdapterInstance[0] = new songsListAdapter(MainActivity.this, playlistsManager.getPlaylistItems(playlistsSpinner.getSelectedItem().toString().toLowerCase()));
            }
            recyclerView.setAdapter(songsListAdapterInstance[0]);
        }

        else if(playlistLayout.getVisibility() == VISIBLE){
            playlistLayout.setVisibility(INVISIBLE);
        }

        else{
            super.onBackPressed();
        }
    }
    public boolean checkPermission(){
        int permissionReadAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO);
        int permissionRecordAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        return permissionReadAudio == PackageManager.PERMISSION_GRANTED && permissionRecordAudio == PackageManager.PERMISSION_GRANTED;
    }
    public void getPermission(){
        Intent intent = new Intent(this, firstRunMediaScanActivity.class);
        startActivity(intent);
    }

    void getMusicThumbnails(){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("thumbnails", MODE_PRIVATE);
        ArrayList<String> thumbnailsArrayList = new ArrayList<>(Arrays.asList(directory.list()));
        ArrayList<String> songNames = new ArrayList<>();

        for(AudioModel model:songsList){
            songNames.add(model.getTitle() + ".jpeg");
        }

        for(String songName: thumbnailsArrayList){
            if(!songNames.contains(songName)){
                File file = new File(getDir("thumbnails", MODE_PRIVATE).toString() + "/" + songName);
                file.delete();
            }
        }

        if(songsList.size() < 1){
            String[] projection = {
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media._ID
            };

            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

            cursor = getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    "");

            while (cursor.moveToNext()) {
                AudioModel songData = new AudioModel(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
                if (new File(songData.getPath()).exists()) {
                    songsList.add(songData);
                }
            }

            SharedPreferences sharedPreferences = getSharedPreferences("numberOfSongs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("numberOfSongs", songsList.size());
            editor.apply();
        }

        try{
            for (AudioModel model:songsList) {
                try{
                    if(!thumbnailsArrayList.contains(model.getTitle() + ".jpeg")){
                        MediaMetadataRetriever meta = new MediaMetadataRetriever();
                        meta.setDataSource(model.getPath());
                        byte[] picture = meta.getEmbeddedPicture();
                        if(picture != null){
                            File filePath = new File(directory, model.getTitle().replace("/", "") + ".jpeg");
                            FileOutputStream fos = new FileOutputStream(filePath);
                            Bitmap b = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.close();
                        }
                        meta.release();
                    }
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }catch(IllegalArgumentException e){
            Toast.makeText(cw, "Something's not quite right :(", Toast.LENGTH_LONG).show();
        }

        Log.d(TAG, "getMusicThumbnails: done");
        scanDone = true;
    }
    void getPlayListsOnFirstLaunch(){
        try{
            File musicHistoryDirectory = new File(getDir("musicHistory", MODE_PRIVATE).toString(), "playlists.txt");
            FileOutputStream fos = new FileOutputStream(musicHistoryDirectory, false);
            fos.write("all\n".getBytes());
            fos.write("favorites\n".getBytes());
            fos.write("history\n".getBytes());
            fos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    void createSongHistory(ArrayList<AudioModel> songsData) throws IOException{
        File musicHistoryDirectory = new File(getDir("musicHistory", MODE_PRIVATE).toString(), "history.txt");
        FileOutputStream fos = new FileOutputStream(musicHistoryDirectory);
        File favorites = new File(getDir("musicHistory", MODE_PRIVATE).toString(), "favorites.txt");
        FileOutputStream fosFavorites = new FileOutputStream(favorites);
        fosFavorites.write("".getBytes());
        fosFavorites.close();

        fos.write("".getBytes());
        for (AudioModel model:songsData) {
            String line = model.getTitle().replace(",", "") + "," + 0 + "\n";
            fos.write(line.getBytes());
        }
    }

    void onclickListeners(){
        SharedPreferences sharedPreferences = getSharedPreferences("lastPlaylist", MODE_PRIVATE);
        playlists.remove(sharedPreferences.getString("lastPlaylist", "all"));
        playlistsArranged.add(sharedPreferences.getString("lastPlaylist", "all"));
        playlistsArranged.addAll(playlists);

        playlistsArranged = removeDuplicates(playlistsArranged);
        playlists = playlistsArranged;

        playlistAdapterArray = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, playlists);
        playlistsSpinner.setAdapter(playlistAdapterArray);
        playlistsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(playlistsSpinner.getSelectedItem().toString().equalsIgnoreCase("all")){
                    songsListAdapterInstance[0] = new songsListAdapter(MainActivity.this, songsList);
                    recyclerView.setAdapter(songsListAdapterInstance[0]);
                }else if(playlistsSpinner.getSelectedItem().toString().equalsIgnoreCase("history")){
                    historyAdapterInstance[0] = new historyAdapter(MainActivity.this, songsList);
                    recyclerView.setAdapter(historyAdapterInstance[0]);
                }
                else{
                    if(playlistsManager.getPlaylistItems(playlistsSpinner.getSelectedItem().toString().toLowerCase()).size() > 0){
                        songsListAdapterInstance[0] = new songsListAdapter(MainActivity.this, playlistsManager.getPlaylistItems(playlistsSpinner.getSelectedItem().toString().toLowerCase()));
                        recyclerView.setAdapter(songsListAdapterInstance[0]);
                    }else{
                        Toast.makeText(MainActivity.this, "That Playlist is Empty or does not Exist, switching to main playlist...", Toast.LENGTH_LONG).show();
                        songsListAdapterInstance[0] = new songsListAdapter(MainActivity.this, songsList);
                        playlistsSpinner.setSelection(playlists.indexOf("ALL"));
                    }
                }

                SharedPreferences sharedPreferences = getSharedPreferences("lastPlaylist", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("lastPlaylist", playlistsSpinner.getSelectedItem().toString());
                editor.apply();

                searchEditText.setText("");
                searching = false;

                if(mediaPlayerInstance.songNames.size() > 0){
                    notificationManager.cancel(6062);
                }

                recyclerView.setLayoutManager(linearLayoutManager);
                currentPlaylist.setText(String.format("%s%s", String.valueOf(playlistsSpinner.getSelectedItem().toString().charAt(0)).toUpperCase(), playlistsSpinner.getSelectedItem().toString().substring(1).toLowerCase()));
                Log.d(TAG, currentPlaylist.getText().toString());
                if(com.nullinnix.orange.musicResources.getState() != com.nullinnix.orange.musicResources.NOT_SET){
                    mediaPlayerInstance.getInstance().reset();
                }
                mediaPlayerInstance.songNames.clear();
                miniPlayerCardView.setVisibility(INVISIBLE);
                Log.d(TAG, "onItemSelected: just switched");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){

            }
        });


        currentPlaylist.setOnLongClickListener(v -> {
            if(!exemptedPlaylists.contains(currentPlaylist.getText().toString().toLowerCase())){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage(String.format("Delete %s?", playlistsSpinner.getSelectedItem().toString()))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                playlistsManager.removePlaylist(playlistsSpinner.getSelectedItem().toString());
                                Toast.makeText(MainActivity.this, playlistsSpinner.getSelectedItem().toString() + " deleted", Toast.LENGTH_SHORT).show();

                                playlistsArranged.remove(playlistsSpinner.getSelectedItem().toString());
                                SharedPreferences sharedPreferences1 = getSharedPreferences("lastPlaylist", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences1.edit();
                                editor.putString("lastPlaylist", "ALL");
                                editor.apply();

                                playlists = playlistsArranged;
                                playlistAdapterArray.notifyDataSetChanged();
                                playlistsSpinner.setAdapter(playlistAdapterArray);
                                songsListAdapter.selected.clear();
                                songsListAdapter.multiSelecting[0] = false;
                                multiSelectingStopped = true;
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                alertDialog.show();
            }else{
                if(currentPlaylist.getText().toString().equalsIgnoreCase("history")){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Reset History?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    musicHistory.resetHistory(songsList, MainActivity.this);

                                    historyAdapterInstance[0] = new historyAdapter(MainActivity.this, songsList);
                                    recyclerView.setAdapter(historyAdapterInstance[0]);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    alertDialog.show();
                }
            }
            return true;
        });

        addPlaylist.setOnClickListener(v -> {
            playlistLayout.setVisibility(VISIBLE);
            playlistAdapter playlistsAdapter = new playlistAdapter(this);
            playlistRecyclerView.setAdapter(playlistsAdapter);
            playlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            tools.setVisibility(INVISIBLE);
            multiSelectLayout.setVisibility(INVISIBLE);
            addPlaylist.setVisibility(INVISIBLE);
        });

        addPlaylistLayout.setOnClickListener(v -> {
            addPlaylistLayout.setVisibility(GONE);
            if (imm.isActive())
                imm.hideSoftInputFromWindow(addPlaylistLayout.getWindowToken(), 0);
        });

        savePlaylist.setOnClickListener(v -> {
            if(playlistName.getText().toString().length() < 1){
                Toast.makeText(this, "Orange thinks that name is too short", Toast.LENGTH_SHORT).show();
            } else if (playlists.contains(playlistName.getText().toString().toUpperCase())) {
                Toast.makeText(this, "Orange says thinks have that already, choose another name", Toast.LENGTH_SHORT).show();
            } else{
                playlistsManager.createPlaylist(songsListAdapter.selected, playlistName.getText().toString());
                if (imm.isActive())
                    imm.hideSoftInputFromWindow(addPlaylistLayout.getWindowToken(), 0);
                addPlaylistLayout.setVisibility(GONE);
                Toast.makeText(this, playlistName.getText().toString() + " Created", Toast.LENGTH_SHORT).show();
                selectingAll = false;

                SharedPreferences sharedPreferencesPlaylist = getSharedPreferences("lastPlaylist", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferencesPlaylist.edit();
                editor.putString("lastPlaylist", playlistName.getText().toString().toLowerCase());
                editor.apply();

                playlists = playlistsManager.getPlaylists();
                playlists.remove(sharedPreferences.getString("lastPlaylist", "all"));
                playlistsArranged.add(sharedPreferences.getString("lastPlaylist", "all"));
                playlistsArranged.addAll(playlists);

                playlistsArranged = removeDuplicates(playlistsArranged);
                playlists = playlistsArranged;
                playlistAdapterArray.notifyDataSetChanged();
                playlistsSpinner.setAdapter(playlistAdapterArray);
                playlistsSpinner.setSelection(playlists.indexOf(playlistName.getText().toString().toUpperCase()));
                playlistName.setText("");
                searchEditText.setVisibility(INVISIBLE);

                songsListAdapter.selected.clear();
                songsListAdapter.multiSelecting[0] = false;
                multiSelectingStopped = true;
            }
        });

        playPause.setOnClickListener(v -> playPause());

        skipNext.setOnClickListener(v -> {
            try {
                skipNextMiniPlayer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        skipPrevious.setOnClickListener(v -> {
            try {
                skipPreviousMiniPlayer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        miniPlayerCardView.setOnClickListener(v -> startPlayer());

        selectAll.setOnClickListener(v -> {
            selectingAll = true;
            if(songsListAdapter.selected.size() < songsList.size()){
                songsListAdapterInstance[0].notifyDataSetChanged();
            }
        });

        deselectAll.setOnClickListener(v -> {
            selectingAll = false;
            deselectedAll = true;
            songsListAdapter.selected.clear();
            songsListAdapterInstance[0].notifyDataSetChanged();
            songsListAdapter.multiSelecting[0] = false;
            multiSelectingStopped = true;
        });

        selectInterval.setOnClickListener(v -> {
            if(songsListAdapter.selected.size() > 1){
                int firstIndex = 0;
                int lastIndex = 0;

                for (int i = 0; i < songsListAdapter.audioModels.size(); i++) {
                    if(songsListAdapter.selected.contains(songsListAdapter.audioModels.get(i).getTitle())){
                        firstIndex = i;
                        break;
                    }
                }

                for (int i = songsListAdapter.audioModels.size() - 1; i > 0; i--) {
                    if(songsListAdapter.selected.contains(songsListAdapter.audioModels.get(i).getTitle())){
                        lastIndex = i;
                        break;
                    }
                }

                songsListAdapter.selected.clear();

                for (int i = firstIndex; i < lastIndex + 1; i++) {
                    songsListAdapter.selected.add(songsListAdapter.audioModels.get(i).getTitle());
                }

                selected = true;
                songsListAdapterInstance[0].notifyDataSetChanged();
            }else{
                Toast.makeText(this, "Select more songs to use this feature", Toast.LENGTH_SHORT).show();
            }
        });

        removeFromPlaylist.setOnClickListener(v -> {
            if(songsListAdapter.selected.size() > 0){
                playlistsManager.removeFromPlaylist(songsListAdapter.selected, playlistsSpinner.getSelectedItem().toString(), playlistsSpinner, playlistsArranged);
                selectingAll = false;
                deselectedAll = true;
                songsListAdapter.selected.clear();

                if(playlistsManager.playlistEmpty){
                    playlistsSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, playlistsManager.getPlaylists()));
                    playlistsManager.playlistEmpty = false;
                }

                songsListAdapterInstance[0] = new songsListAdapter(MainActivity.this, playlistsManager.getPlaylistItems(playlistsSpinner.getSelectedItem().toString()));
                recyclerView.setAdapter(songsListAdapterInstance[0]);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                songsListAdapter.multiSelecting[0] = false;
                multiSelectingStopped = true;
            }
        });

        settings.setOnClickListener(v -> {
            Intent intent = new Intent(this, settingsActivityTest.class);
            startActivity(intent);
        });

        createPlaylist.setOnClickListener(v -> {
            addPlaylistLayout.setVisibility(VISIBLE);
            playlistLayout.setVisibility(GONE);
        });

        playlistLayout.setOnClickListener(v -> playlistLayout.setVisibility(INVISIBLE));

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() { runOnUiThread(() -> {
                if(musicPlayerWindowActivity.changed && !searching){
                    if(songsListAdapter.colored.size() > 0){
                        if(songsListAdapterInstance[0] != null){
                            songsListAdapterInstance[0].notifyItemChanged(songsListAdapter.colored.get(songsListAdapter.colored.size() - 1));
                            ArrayList<String> songNames = new ArrayList<>();
                            if(playlistsSpinner.getSelectedItem().toString().equalsIgnoreCase("all")){
                                for (AudioModel song:songsList) {
                                    songNames.add(song.getTitle());
                                }
                            }else {
                                for (AudioModel song:playlistsManager.getPlaylistItems(playlistsSpinner.getSelectedItem().toString().toLowerCase())) {
                                    songNames.add(song.getTitle());
                                }
                            }

                            songsListAdapterInstance[0].notifyDataSetChanged();
                            songsListAdapter.colored.add(songNames.indexOf(mediaPlayerInstance.songNames.get(mediaPlayerInstance.songNames.size() - 1)));
                            songsListAdapterInstance[0].notifyItemChanged(songsListAdapter.colored.get(songsListAdapter.colored.size() - 1));
                            Log.d(TAG, "updating colored");
                        }
                    }

                    musicPlayerWindowActivity.changed = false;
                    Log.d(TAG, String.valueOf(songsListAdapter.colored));
                }
                    });
            }
        }, 100, 100);

        deleteSelected.setOnClickListener(v -> {
            deleteSongsSelected();
            multiSelectingStopped = true;
        });

        search.setOnClickListener(v -> {
            tools.setVisibility(INVISIBLE);
            searchEditText.setVisibility(VISIBLE);
            searchLayout.setVisibility(VISIBLE);
            searching = true;
            int[] searchInputSize = {searchEditText.getText().length()};
            ArrayList<AudioModel> searchResults = new ArrayList<>();

            ArrayList<AudioModel> searchData;

            if(playlistsSpinner.getSelectedItem().toString().equalsIgnoreCase("all")){
                searchData = songsList;
            }else if(playlistsSpinner.getSelectedItem().toString().equalsIgnoreCase("history")){
                searchData = songsList;
            }else{
                searchData = playlistsManager.getPlaylistItems(playlistsSpinner.getSelectedItem().toString().toLowerCase());
            }

            Timer searchResultsTimer = new Timer();
            searchResultsTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        if(searchEditText.getVisibility() == VISIBLE && searchInputSize[0] != searchEditText.getText().length() && searching){
                            if(!playlistsSpinner.getSelectedItem().toString().equalsIgnoreCase("history")){
                                searchResults.clear();
                                for (AudioModel model:searchData) {
                                    if(model.getTitle().toLowerCase().contains(searchEditText.getText().toString().toLowerCase())){
                                        searchResults.add(model);
                                    }
                                }
                                songsListAdapterInstance[0] = new songsListAdapter(MainActivity.this, searchResults);
                                recyclerView.setAdapter(songsListAdapterInstance[0]);
                                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                searchInputSize[0] = searchEditText.getText().length();
                                Log.d(TAG, "run: caught this");
                            }else{
                                searchResults.clear();
                                for (AudioModel model:searchData) {
                                    if(model.getTitle().toLowerCase().contains(searchEditText.getText().toString().toLowerCase())){
                                        searchResults.add(model);
                                    }
                                }
                                
                                searchResults.forEach(it -> Log.d(TAG, it.getTitle()));
                                System.out.println();
                                
                                historyAdapterInstance[0] = new historyAdapter(MainActivity.this, searchResults);
                                recyclerView.setAdapter(historyAdapterInstance[0]);
                                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                searchInputSize[0] = searchEditText.getText().length();
                                
                            }
                        }if(!searching){
                            searchResultsTimer.cancel();
                        }
                    });
                }
            }, 0, 500);
        });

        searchLayout.setOnClickListener(v -> {
            if(searchEditText.getText().length() == 0){
                searchEditText.setVisibility(INVISIBLE);
                searchLayout.setVisibility(INVISIBLE);
                searching = false;
            }
            searchLayout.setVisibility(INVISIBLE);
            imm.hideSoftInputFromWindow(searchLayout.getWindowToken(), 0);
        });

        searchEditText.setOnClickListener(v -> searchLayout.setVisibility(VISIBLE));

        cancelSearch.setOnClickListener(v -> {
            searchEditText.setText("");
            searchEditText.setVisibility(INVISIBLE);
            searchLayout.setVisibility(INVISIBLE);
            imm.hideSoftInputFromWindow(searchLayout.getWindowToken(), 0);
            searching = false;

            if(playlistsSpinner.getSelectedItem().toString().equalsIgnoreCase("all")){
                songsListAdapterInstance[0] = new songsListAdapter(MainActivity.this, songsList);
                recyclerView.setAdapter(songsListAdapterInstance[0]);
            }else if(playlistsSpinner.getSelectedItem().toString().equalsIgnoreCase("history")){
                historyAdapterInstance[0] = new historyAdapter(MainActivity.this, songsList);
                recyclerView.setAdapter(historyAdapterInstance[0]);
            }else{
                songsListAdapterInstance[0] = new songsListAdapter(MainActivity.this, playlistsManager.getPlaylistItems(playlistsSpinner.getSelectedItem().toString().toLowerCase()));
                recyclerView.setAdapter(songsListAdapterInstance[0]);
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        });

        clearTipButton.setOnClickListener(v -> {
            tipLayout.setVisibility(INVISIBLE);
        });

        tipLayout.setOnClickListener(v -> {
            tipLayout.setVisibility(INVISIBLE);
        });
    }

    public void playPause(){
        if(mediaPlayerInstance.songNames.size() > 0){
            if(isPaused){
                isPaused = false;
                mediaPlayer.start();
                com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.PLAYING);
            }

            else{
                isPaused = true;
                mediaPlayer.pause();
                com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.PAUSED);
            }

            playStateChanged = true;
            MainActivity.updateNotification = true;
            musicPlayerWindowActivity.changed = true;
        }
    }

    public void skipNextMiniPlayer() throws IOException {
        musicResources = new musicResources(songsListAdapter.audioModels);
        if(musicPlayerWindowActivity.musicData.size() > 0){
            if(musicPlayerWindowActivity.shuffleOn){
                musicPlayerWindowActivity.musicData = musicPlayerWindowActivity.shuffledSongs;
            } else{
                musicPlayerWindowActivity.musicData = musicPlayerWindowActivity.musicDataRaw;
            }

            Log.d(TAG, String.valueOf(musicPlayerWindowActivity.musicData.size()));
            Log.d(TAG, String.valueOf(musicPlayerWindowActivity.shuffledSongs.size()));

            if (mediaPlayerInstance.currentIndex + 1 < musicPlayerWindowActivity.musicData.size()) {
                mediaPlayer.reset();
                com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.RESET);
                mediaPlayer.setDataSource(musicPlayerWindowActivity.musicData.get(mediaPlayerInstance.currentIndex + 1).getPath());
                com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.SET);
                mediaPlayerInstance.currentIndex = mediaPlayerInstance.currentIndex + 1;
            } else {
                if(musicPlayerWindowActivity.repeatState.equals("LOOP_ALL") || musicPlayerWindowActivity.repeatState.equals("LOOP_SINGLE")){
                    mediaPlayer.reset();
                    com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.RESET);
                    mediaPlayer.setDataSource(musicPlayerWindowActivity.musicData.get(0).getPath());
                    com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.SET);
                    mediaPlayerInstance.currentIndex = 0;
                }
                else{
                    musicPlayerWindowActivity.reachedEndOfPlaylist = true;
                    mediaPlayer.reset();
                    com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.RESET);
                    mediaPlayer.setDataSource(musicResources.getCurrentPath());
                    com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.SET);
                }
            }
            playMusicMiniPlayer();
        }

        Log.d(TAG, String.valueOf(musicPlayerWindowActivity.musicData.size()));
        Log.d(TAG, String.valueOf(musicPlayerWindowActivity.shuffledSongs.size()));
    }
    public void skipPreviousMiniPlayer() throws IOException {
        if(musicPlayerWindowActivity.musicData.size() > 0){
            if (mediaPlayerInstance.currentIndex - 1 >= 0) {
                mediaPlayer.reset();
                com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.RESET);

                mediaPlayer.setDataSource(musicPlayerWindowActivity.musicData.get(mediaPlayerInstance.currentIndex - 1).getPath());
                com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.SET);
                mediaPlayerInstance.currentIndex = mediaPlayerInstance.currentIndex - 1;
            } else {
                if(!musicPlayerWindowActivity.repeatState.equals("LOOP_OFF")){
                    mediaPlayer.reset();
                    com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.RESET);

                    mediaPlayer.setDataSource(musicPlayerWindowActivity.musicData.get(musicPlayerWindowActivity.musicData.size() - 1).getPath());
                    com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.SET);

                    mediaPlayerInstance.currentIndex = musicPlayerWindowActivity.musicData.size() - 1;
                }else{
                    mediaPlayer.reset();
                    com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.RESET);

                    mediaPlayer.setDataSource(musicPlayerWindowActivity.musicData.get(0).getPath());
                    com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.SET);
                }
            }
            playMusicMiniPlayer();
        }
    }

    void playMusicMiniPlayer(){
        playingNext = true;
        mediaPlayerInstance.songNames.add(musicPlayerWindowActivity.musicData.get(mediaPlayerInstance.currentIndex).getTitle());
        mediaPlayer.prepareAsync();
        com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.PREPARING);

        mediaPlayer.setOnPreparedListener(mp -> {
            mediaPlayer.start();
            com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.PLAYING);
            musicPlayerWindowActivity.changed = true;
            MainActivity.updateNotification = true;
        });
        mediaPlayer.start();
        com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.PLAYING);
        musicHistory.reader(this);
        musicHistory.writer((musicPlayerWindowActivity.musicData.get(mediaPlayerInstance.currentIndex).getTitle()), this);
    }

    public ArrayList<String> removeDuplicates(ArrayList<String> deDuplicate){
        ArrayList<String> sample = new ArrayList<>();
        deDuplicate.forEach((n) -> {
            if(!sample.contains(n.toLowerCase())){
                sample.add(n.toLowerCase());
            }
        });

        deDuplicate.clear();

        sample.forEach((n) -> deDuplicate.add(n.toUpperCase()));

        return deDuplicate;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionName");
            switch (action){
                case ShowNotifications.ACTIONPREVIOUS:
                    try {
                        skipPreviousMiniPlayer();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    musicPlayerWindowActivity.updatePlayer = true;
                    break;

                case ShowNotifications.ACTIONPLAY:
                    playPause();
                    break;

                case ShowNotifications.ACTIONNEXT:
                    try {
                        skipNextMiniPlayer();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    musicPlayerWindowActivity.updatePlayer = true;
                    break;

                case ShowNotifications.ACTIONFAVORITE:
                    try {
                        if(com.nullinnix.orange.favorites.favoritesArray.contains(mediaPlayerInstance.songNames.get(mediaPlayerInstance.songNames.size() - 1))){
                            favorites.removeFromFavorites(mediaPlayerInstance.songNames.get(mediaPlayerInstance.songNames.size() - 1));
                        }else{
                            favorites.addToFavorites(mediaPlayerInstance.songNames.get(mediaPlayerInstance.songNames.size() - 1));
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    updateNotification = true;
                    musicPlayerWindowActivity.changed = true;
                    break;
            }
        }
    };

    public void showNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(getBaseContext(), OnClearFromRecentService.class));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }

        int playPause;

        if(com.nullinnix.orange.musicResources.getState() != com.nullinnix.orange.musicResources.NOT_SET){
            if(mediaPlayer.isPlaying()){
                playPause = R.drawable.ic_pause_notification;
            } else if(songsListAdapter.firstPlay){
                playPause = R.drawable.ic_pause_notification;
                songsListAdapter.firstPlay = false;
            }else{
                playPause = R.drawable.ic_play_notification;
            }
            ShowNotifications showNotifications = new ShowNotifications(MainActivity.this, playPause);
        }


    }

    public void startPlayer(){
        mediaPlayerInstance.playingSameSong = true;
        Intent intent = new Intent(this, musicPlayerWindowActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putParcelableArrayListExtra("audioModel", songsListAdapter.audioModels);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
            clicks += 1;

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        if(mediaPlayerInstance.songNames.size() > 0){
                            if(clicks == 2){
                                try {
                                    skipNextMiniPlayer();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } else if (clicks == 3) {
                                try {
                                    skipPreviousMiniPlayer();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } else if(clicks == 1){
                                playPause();
                            }
                        }
                        clicks = 0;
                        timer.cancel();
                    });
                }
            }, 800, 10);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void deleteSongsSelected() {
        Uri uri = MediaStore.Audio.Media.getContentUri("external");

        ArrayList<Uri> urisArray = new ArrayList<>();
        for (AudioModel model:songsListAdapter.audioModels) {
            if (songsListAdapter.selected.contains(model.getTitle())){
                urisArray.add(ContentUris.withAppendedId(uri, Long.parseLong(model.getDocumentID())));
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            PendingIntent pendingIntent = MediaStore.createDeleteRequest(getContentResolver(), urisArray);
            IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent.getIntentSender()).build();
            songsDeleteLauncher.launch(intentSenderRequest);
        }
    }

    @Override
    protected void onDestroy() {
        garbageCollecting = true;
        notificationManager.cancel(6062);
        mediaPlayer.release();
        com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.RELEASED);
        com.nullinnix.orange.musicResources.setState(com.nullinnix.orange.musicResources.NOT_SET);
        mediaPlayerInstance.instance.release();

        songsListAdapter.multiSelecting[0] = false;
        songsListAdapter.selected.clear();
        songsListAdapter.intervalMap.clear();
        songsListAdapter.colored.clear();

        exemptedPlaylists.clear();

        selectingAll = false;
        isSelectingInterval = false;
        deselectedAll = false;
        updateNotification = false;
        searching = false;
        multiSelectingStopped= false;
        selected = false;
        updateMiniPlayer = false;
        playStateChanged = false;
        absoluteSuspendMiniPlayer = false;
        mediaPlayerInstance.songNames.clear();
        mediaPlayerInstance.playerOpened = false;
        mediaPlayerInstance.currentIndex = -1;

        super.onDestroy();
    }
}