package com.nullinnix.orange;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class favorites {
    public static ArrayList<String> favoritesArray = new ArrayList<>();
    Context context;
    public favorites(Context context){
        this.context = context;
    }

    public void loadUpFavorites() throws IOException {
        try{
            File historyFile = new File(context.getDir("musicHistory", MODE_PRIVATE).toString(), "favorites.txt");
            if(historyFile.exists()){
                FileInputStream fis = new FileInputStream(historyFile);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
                String line = bufferedReader.readLine();

                while(line != null){
                    if(!favoritesArray.contains(line)){
                        favoritesArray.add(line);
                    }
                    line = bufferedReader.readLine();
                }
                fis.close();
                bufferedReader.close();
            }

        }catch (IOException e){
        e.printStackTrace();
    }
    }

    public void addToFavorites(String songName) throws IOException{
        try{
            File historyFile = new File(context.getDir("musicHistory", MODE_PRIVATE).toString(), "favorites.txt");
            FileInputStream fis = new FileInputStream(historyFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            String line = bufferedReader.readLine();

            while(line != null){
                if(!favoritesArray.contains(line)){
                    favoritesArray.add(line);
                }
                line = bufferedReader.readLine();
            }

            favoritesArray.add(songName);

            fis.close();
            bufferedReader.close();

            if(favoritesArray != null){
                FileOutputStream fos = new FileOutputStream(historyFile);
                for (String song: favoritesArray) {
                    String lineToWrite = song + "\n";
                    fos.write(lineToWrite.getBytes());
                }
                fos.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void removeFromFavorites(String songName){
        try{
            if(favoritesArray != null){
                File historyFile = new File(context.getDir("musicHistory", MODE_PRIVATE).toString(), "favorites.txt");
                FileOutputStream fos = new FileOutputStream(historyFile);

                for (String song: favoritesArray) {
                    if(!song.equals(songName)){
                        String lineToWrite = song + "\n";
                        fos.write(lineToWrite.getBytes());
                    }
                }
                favoritesArray.remove(songName);
                fos.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void resetFavorites(){
        try{
            if(favoritesArray != null){
                File historyFile = new File(context.getDir("musicHistory", MODE_PRIVATE).toString(), "favorites.txt");
                FileOutputStream fos = new FileOutputStream(historyFile, false);
                fos.write("".getBytes());
                fos.close();
                favoritesArray.clear();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
