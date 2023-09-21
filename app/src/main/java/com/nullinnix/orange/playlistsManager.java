package com.nullinnix.orange;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class playlistsManager {
    static Context context;
    static ArrayList<String> playlists = new ArrayList<>();
    static ArrayList<AudioModel> audioModels;
    static boolean playlistEmpty = false;
    public playlistsManager(Context context, ArrayList<AudioModel> audioModels){
        this.context = context;
        playlistsManager.audioModels = audioModels;
    }
    
    public static void addToPlaylists(String playlistName) throws IOException {
        try{
            File favoritesFile = new File(context.getDir("musicHistory", MODE_PRIVATE).toString(),  "playlists.txt");
            FileInputStream fis = new FileInputStream(favoritesFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            String line = bufferedReader.readLine();

            while(line != null){
                if(!playlists.contains(line)){
                    playlists.add(line);
                }
                line = bufferedReader.readLine();
            }

            playlists.add(playlistName);

            fis.close();
            bufferedReader.close();

            if(playlists != null){
                FileOutputStream fos = new FileOutputStream(favoritesFile);
                for (String song: playlists) {
                    String lineToWrite = song + "\n";
                    fos.write(lineToWrite.toLowerCase().getBytes());
                }
                fos.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    static ArrayList<String> getPlaylists(){
        ArrayList<String> playlistsToMain = new ArrayList<>();
        try{
            File musicHistoryDirectory = new File(context.getDir("musicHistory", MODE_PRIVATE).toString(), "playlists.txt");
            FileInputStream fis = new FileInputStream(musicHistoryDirectory);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            String line = bufferedReader.readLine();
            while(line != null){
                if(!playlistsToMain.contains(line.toUpperCase())){
                    playlistsToMain.add(line.toUpperCase());
                }
                line = bufferedReader.readLine();
            }
            fis.close();
            bufferedReader.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        return playlistsToMain;
    }
    static ArrayList<AudioModel> getPlaylistItems(String playlistName){
        ArrayList<AudioModel> songData = new ArrayList<>();
        try{
            File playlist = new File(context.getDir("musicHistory", MODE_PRIVATE).toString(), playlistName.toLowerCase() + ".txt");
            FileInputStream fis = new FileInputStream(playlist);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            String line = bufferedReader.readLine();
            ArrayList<String> songsInPlaylist = new ArrayList<>();

            while(line != null){
                if(!songsInPlaylist.contains(line)){
                    songsInPlaylist.add(line.toLowerCase());
                }
                line = bufferedReader.readLine();
            }
            fis.close();
            bufferedReader.close();

            for (AudioModel model:audioModels) {
                if(songsInPlaylist.contains(model.getTitle().toLowerCase())){
                    songData.add(model);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
            Toast.makeText(context, "Orange could not find that Playlist  :(", Toast.LENGTH_SHORT).show();
            if(!MainActivity.exemptedPlaylists.contains(playlistName.toLowerCase())){
                removePlaylist(playlistName);
            }
        }
        return songData;
    }
    static void createPlaylist(ArrayList<String> songs, String playlistName){
        try{
            File playlist = new File(context.getDir("musicHistory", MODE_PRIVATE).toString() + "/" + playlistName.toLowerCase() + ".txt");
            FileOutputStream fos = new FileOutputStream(playlist);
            for (String song:songs) {
                String songName = song + "\n";
                fos.write(songName.toLowerCase().getBytes());
            }
            fos.close();
            addToPlaylists(playlistName);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //removes an empty playlist from the playlist library
    static void removePlaylist(String playlistToRemove){
        try{
            File playlist = new File(context.getDir("musicHistory", MODE_PRIVATE).toString() + "/" + "playlists.txt");
            File playlistToBeRemoved = new File(context.getDir("musicHistory", MODE_PRIVATE).toString() + "/" + playlistToRemove.toLowerCase() + ".txt");
            FileInputStream fis = new FileInputStream(playlist);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));

            ArrayList<String> playlists = new ArrayList<>();

            String line = bufferedReader.readLine();
            while(line != null){
                playlists.add(line);
                line = bufferedReader.readLine();
            }

            FileOutputStream fos = new FileOutputStream(playlist);
            for (String song:playlists){
                if(!song.equalsIgnoreCase(playlistToRemove)){
                    fos.write((song + "\n").getBytes());
                }
            }
            fos.close();
            fis.close();
            bufferedReader.close();
            playlistToBeRemoved.delete();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //removes songs from a specified playlist
    static void removeFromPlaylist(ArrayList<String> songsToRemove, String playlistToBeRemovedFrom, Spinner playlistsSpinner, ArrayList<String> playlistsArranged){
        try{
            File playlist = new File(context.getDir("musicHistory", MODE_PRIVATE).toString() + "/" + playlistToBeRemovedFrom.toLowerCase() + ".txt");
            ArrayList<String> songs = new ArrayList<>();
            ArrayList<String> songsToRemoveLower = new ArrayList<>();
            songsToRemove.forEach((n) -> songsToRemoveLower.add(n.toLowerCase()));

            FileInputStream fis = new FileInputStream(playlist);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));

            String line = bufferedReader.readLine();
            while(line != null){
                songs.add(line);
                line = bufferedReader.readLine();
            }
            FileOutputStream fos = new FileOutputStream(playlist);
            for (String song:songs){
                if(!songsToRemoveLower.contains(song)){
                    fos.write((song + "\n").getBytes());
                }
            }
            fos.close();
            fis.close();
            bufferedReader.close();
            if (songsToRemove.size() == songsListAdapter.audioModels.size() && !MainActivity.exemptedPlaylists.contains(playlistToBeRemovedFrom.toLowerCase())){
                removePlaylist(playlistToBeRemovedFrom);
                playlistsArranged.remove(playlistToBeRemovedFrom);
                SharedPreferences sharedPreferences = context.getSharedPreferences("lastPlaylist", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("lastPlaylist", "ALL");
                editor.apply();
                Toast.makeText(context, String.format("Deleting %s", playlistToBeRemovedFrom), Toast.LENGTH_SHORT).show();
                playlistEmpty = true;
            }

            else{
                SharedPreferences sharedPreferences = context.getSharedPreferences("lastPlaylist", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("lastPlaylist", playlistsSpinner.getSelectedItem().toString().toUpperCase());
                editor.apply();
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
