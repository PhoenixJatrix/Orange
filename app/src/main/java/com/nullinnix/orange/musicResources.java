package com.nullinnix.orange;

import java.util.ArrayList;

public class musicResources {
    ArrayList<AudioModel> musicData;
    public musicResources(ArrayList<AudioModel> musicData){
        this.musicData = musicData;
    }

    int getTotalTime(){
        int time = 0;
        for (AudioModel model:musicData) {
            if(model.getTitle().equalsIgnoreCase(mediaPlayerInstance.songNames.get( mediaPlayerInstance.songNames.size() - 1))){
                time = Integer.parseInt(model.getDuration());
            }
        }
        return time;
    }

    String getCurrentSongName(){
        String name = "";
        for (AudioModel model:musicData) {
            if(model.getTitle().equalsIgnoreCase(mediaPlayerInstance.songNames.get(mediaPlayerInstance.songNames.size() - 1))){
                name = model.getTitle();
            }
        }
        return name;
    }

    String getCurrentPath(){
        String name = "";
        for (AudioModel model:musicData) {
            if(model.getTitle().equalsIgnoreCase(mediaPlayerInstance.songNames.get(mediaPlayerInstance.songNames.size() - 1))){
                name = model.getPath();
            }
        }
        return name;
    }

    String getCurrentAlbumName(){
        String name = "";
        for (AudioModel model:musicData) {
            if(model.getTitle().equalsIgnoreCase(mediaPlayerInstance.songNames.get(mediaPlayerInstance.songNames.size() - 1))){
                name = model.getAlbum();
            }
        }
        return name;
    }

    String milliToMinutes(String milliSeconds) {
        int durationMilli = Integer.parseInt(milliSeconds);
        durationMilli = durationMilli / 1000;
        int durationMin = durationMilli / 60;
        int durationSec = durationMilli % 60;

        String durationSecString = Integer.toString(durationSec);
        String durationMinString = Integer.toString(durationMin);

        if (durationSec < 10){
            durationSecString = "0" + durationSec;
        }

        if (durationMin < 10){
            durationMinString = "0" + durationMin;
        }
        return durationMinString + ":" + durationSecString;
    }

    String milliToMinutes(long milliSeconds) {
        int durationMilli = (int) milliSeconds;
        durationMilli = durationMilli / 1000;
        int durationMin = durationMilli / 60;
        int durationSec = durationMilli % 60;

        String durationSecString = Integer.toString(durationSec);
        String durationMinString = Integer.toString(durationMin);

        if (durationSec < 10){
            durationSecString = "0" + durationSec;
        }

        if (durationMin < 10){
            durationMinString = "0" + durationMin;
        }
        return durationMinString + ":" + durationSecString;
    }

    public static String[] setSongAndArtist(){
        String song = mediaPlayerInstance.songNames.get(mediaPlayerInstance.songNames.size() - 1);
        String songArtist = "";
        String songName = "";

        ArrayList<Integer> charCount = new ArrayList<>();
        for (int i = 0; i < song.length(); i++) {
            if(song.charAt(i) == '-'){
                charCount.add(i);
            }
        }

        if(song.contains("-") && charCount.size() > 1){
            songArtist = song.substring(0, charCount.get(1));
            songName = song.substring(charCount.get(1));
            songName = songName.substring(songName.indexOf("-") + 1);
            songName = songName.trim();
        }
        else if(song.contains("-") && charCount.size() == 1){
            songArtist = song.substring(0, song.indexOf("-"));
            songName = song.substring(song.indexOf("-") + 2);
        }else{
            songName = song;
            songArtist = "";
        }

        return new String[]{songName, songArtist};
    }

    static int state = 0;
    static final int NOT_SET = 0;
    static final int PREPARING = 1;
    static final int SET = 2;
    static final int PLAYING = 3;
    static final int PAUSED = 4;
    static final int RESET = 5;
    static final int RELEASED = 6;

    static void setState(int setState){
        state = setState;
    }

    static int getState(){
        return state;
    }
}
