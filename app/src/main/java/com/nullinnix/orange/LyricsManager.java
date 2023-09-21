package com.nullinnix.orange;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LyricsManager {
    String  songName;
    Context context;

    LyricsManager(Context context, String songName){
        this.context = context;
        this.songName = songName;
    }

    void saveLyrics(String lyrics){
        try{
            File lyricsTxt = new File(context.getDir("lyrics", Context.MODE_PRIVATE).toString() + "/" + songName + ".txt");
            FileOutputStream fos = new FileOutputStream(lyricsTxt);
            fos.write(lyrics.getBytes());
            fos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    String loadLyrics(){
        String songLyrics  = "";
        try{
            File lyricsTxt = new File(context.getDir("lyrics", Context.MODE_PRIVATE).toString() + "/" + songName + ".txt");
            if(lyricsTxt.exists()){
                FileInputStream fis = new FileInputStream(lyricsTxt);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
                String line = bufferedReader.readLine();

                while(line != null){
                    songLyrics += line + "\n";
                    line = bufferedReader.readLine();
                }
                fis.close();
                bufferedReader.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return songLyrics;
    }

    void deleteLyrics(){
        File lyrics = new File(context.getDir("lyrics", Context.MODE_PRIVATE).toString() + "/" + songName + ".txt");
        if(lyrics.delete()){
            Toast.makeText(context, "Lyrics Deleted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }
}
