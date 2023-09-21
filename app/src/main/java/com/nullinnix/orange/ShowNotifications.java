package com.nullinnix.orange;

import static android.app.NotificationManager.IMPORTANCE_HIGH;

import static androidx.core.app.NotificationCompat.FLAG_ONGOING_EVENT;
import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;

import java.io.File;

import Services.NotificationActionReceiver;

public class ShowNotifications {
    static final String ACTIONPREVIOUS = "previous";
    static final String ACTIONPLAY = "play";
    static final String ACTIONNEXT = "next";
    static final String ACTIONFAVORITE = "favorite";
    public static MediaSessionCompat mediaSessionCompat;
    static NotificationManager manager;
    static Context context;

    public ShowNotifications(Context context, int playPause){
        String songNameLastPlayed = mediaPlayerInstance.songNames.get(mediaPlayerInstance.songNames.size() - 1);
        String songArtist;
        String songName;
        this.context = context;
        manager = context.getSystemService(NotificationManager.class);
        mediaSessionCompat = new MediaSessionCompat(context, "sessionTag");
        mediaSessionCompat.setActive(true);

        songName = musicResources.setSongAndArtist()[1];
        songArtist = musicResources.setSongAndArtist()[0];

        File file = new File(context.getDir("thumbnails", Context.MODE_PRIVATE).toString() + "/" +  mediaPlayerInstance.songNames.get(mediaPlayerInstance.songNames.size() - 1) + ".jpeg");
        Bitmap songIconNotification;
        if(file.exists()){
            songIconNotification = BitmapFactory.decodeFile(file.toString());
        }else{
            songIconNotification = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_music_icon_alt);
        }

        PendingIntent pendingIntentPrevious;
        if (mediaPlayerInstance.currentIndex == 0){
            pendingIntentPrevious = null;
        }else{
            Intent intentPrevious = new Intent(context, NotificationActionReceiver.class)
                    .setAction(ACTIONPREVIOUS);
            pendingIntentPrevious = PendingIntent.getBroadcast(context, 0, intentPrevious, PendingIntent.FLAG_IMMUTABLE);
        }

        PendingIntent pendingIntentNext;
        if (mediaPlayerInstance.currentIndex + 1 < songsListAdapter.audioModels.size()){
            Intent intentNext = new Intent(context, NotificationActionReceiver.class)
                    .setAction(ACTIONNEXT);
            pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_IMMUTABLE);
        }else{
            pendingIntentNext = null;
        }

        Intent intentPlay = new Intent(context, NotificationActionReceiver.class)
                .setAction(ACTIONPLAY);
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay, PendingIntent.FLAG_IMMUTABLE);


        Intent intentFavorite = new Intent(context, NotificationActionReceiver.class)
                .setAction(ACTIONFAVORITE);
        PendingIntent pendingIntentFavorite = PendingIntent.getBroadcast(context, 0, intentFavorite, PendingIntent.FLAG_IMMUTABLE);


        int favorite;
        if (favorites.favoritesArray.contains(songNameLastPlayed)){
            favorite = R.drawable.ic_favorite;
        }else {
            favorite = R.drawable.ic_not_favorite;
        }

        Notification notification = new NotificationCompat.Builder(context, "playerNotificationID")
                .setContentTitle(songArtist)
                .setContentText(songName)
                .setLargeIcon(songIconNotification)
                .addAction(R.drawable.ic_skip_previous_notification, "skipPrevious", pendingIntentPrevious)
                .addAction(playPause, "playPause", pendingIntentPlay)
                .addAction(R.drawable.ic_skip_next_notification, "skipNext", pendingIntentNext)
                .addAction(favorite, "favorite", pendingIntentFavorite)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setSilent(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setOngoing(true)
                .build();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    "playerNotificationID",
                    "Player Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setLockscreenVisibility(VISIBILITY_PUBLIC);
            channel.enableLights(true);
            manager.createNotificationChannel(channel);
            manager.notify(6062, notification);
        }
    }
    static void hideNotification(){
        manager.cancel(6062);
    }
}
