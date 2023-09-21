package com.nullinnix.orange;

import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class musicHistory {
    static Map<String, Integer> keyValues = new HashMap<>();

    public void writer(String songNameFromConstructor, Context context) {
        songNameFromConstructor = songNameFromConstructor.replace(",", "");
        File musicHistoryDirectory = new File(context.getDir("musicHistory", Context.MODE_PRIVATE).toString(), "history.txt");
        try{
            FileOutputStream fos = new FileOutputStream(musicHistoryDirectory, false);
            for (String songName: keyValues.keySet()) {
                String line = songName.replace(",", "") + "," + 0 + "\n";
                if(keyValues != null && keyValues.containsKey(songName.replace(",", ""))){
                    if (songName.equals(songNameFromConstructor)){
                        keyValues.replace(songName.replace(",", ""), keyValues.get(songName.replace(",", "")) + 1);
                        line = songName.replace(",", "") + "," + keyValues.get(songName.replace(",", "")) + "\n";
                    }

                    if(keyValues.get(songName.replace(",", "")) > 0 && !songName.equals(songNameFromConstructor)){
                        line = songName.replace(",", "") + "," + keyValues.get(songName.replace(",", "")) + "\n";
                    }
                }
                fos.write(line.getBytes());
            }
            fos.close();
            keyValues.clear();

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void reader(Context context){
        File musicHistoryDirectory = new File(context.getDir("musicHistory", Context.MODE_PRIVATE).toString(), "history.txt");
        try{
            FileInputStream fis = new FileInputStream(musicHistoryDirectory);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            String line = bufferedReader.readLine();
            while(line != null){
                String[] songData = line.split(",");
                keyValues.put(songData[0], Integer.parseInt(songData[1]));
                line = bufferedReader.readLine();
            }
            fis.close();
            bufferedReader.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void updateHistory(ArrayList<AudioModel> audioModels, Context context){
        try{
            File musicHistoryDirectory = new File(context.getDir("musicHistory", Context.MODE_PRIVATE).toString(), "history.txt");
            FileOutputStream fos = new FileOutputStream(musicHistoryDirectory, false);
            for (AudioModel song:audioModels) {
                if(!keyValues.containsKey(song.getTitle().replace("," ,""))){
                    keyValues.put(song.getTitle().replace(",", ""), 0);
                }
            }

            for (String songName: keyValues.keySet()) {
                String line = songName.replace(",", "") + "," + 0 + "\n";
                if(keyValues != null && keyValues.containsKey(songName.replace(",", ""))){
                    line = songName.replace(",", "") + "," + keyValues.get(songName.replace(",", "")) + "\n";
                }

                if(keyValues.get(songName.replace(",", "")) > 0){
                    line = songName.replace(",", "") + "," + keyValues.get(songName.replace(",", "")) + "\n";
                }
                fos.write(line.getBytes());
            }
            fos.close();
            keyValues.clear();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void resetHistory(ArrayList<AudioModel> audioModels, Context context){
        try{
            File musicHistoryDirectory = new File(context.getDir("musicHistory", Context.MODE_PRIVATE).toString(), "history.txt");
            FileOutputStream fos = new FileOutputStream(musicHistoryDirectory);
            for (AudioModel song: audioModels) {
                fos.write((song.getTitle().replace(",", "") + "," + 0 + "\n").getBytes());
            }
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
