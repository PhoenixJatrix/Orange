package com.nullinnix.orange;

import android.media.MediaPlayer;

import java.util.ArrayList;

public class mediaPlayerInstance {
    public static ArrayList<String> songNames = new ArrayList<>();
    public static boolean playerOpened = false;
    static MediaPlayer instance;
    public static boolean playingSameSong = false;
    public static MediaPlayer getInstance(){
        if (instance == null){
            instance = new MediaPlayer();
        }
        return instance;
    }

    public static int currentIndex = -1;
}
