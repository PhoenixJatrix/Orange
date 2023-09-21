package com.nullinnix.orange;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class playlistAdapter extends RecyclerView.Adapter<playlistAdapter.ViewHolder> {
    Context context;
    ArrayList<String> playlistArrayList = new ArrayList<>();
    ArrayList<Integer> songCountArrayList = new ArrayList<>();
    static boolean edited = false;
    static String editedPlaylist;

    playlistAdapter(Context context){
        this.context = context;
        System.out.println("size " +  getPlaylistsCounts().size());

        playlistArrayList.clear();
        playlistArrayList.addAll(getPlaylistsCounts().keySet());
        songCountArrayList.clear();
        songCountArrayList.addAll(getPlaylistsCounts().values());
    }

    @NonNull
    @Override
    public playlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.playlist_template, parent, false);
        return new playlistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull playlistAdapter.ViewHolder holder, int position) {
        SharedPreferences textColor = context.getSharedPreferences("Text Color", MODE_PRIVATE);
        String[] textColors = textColor.getString("Text Color", "254,83,20").split(",");
        SharedPreferences backgroundColor = context.getSharedPreferences("Background Color", MODE_PRIVATE);
        String[] backgroundColors = backgroundColor.getString("Background Color", "0,0,0").split(",");

        String playlistNameCapitalized = playlistArrayList.get(holder.getAdapterPosition()).substring(0, 1).toUpperCase() + playlistArrayList.get(holder.getAdapterPosition()).substring(1);
        String songsCountEdited = songCountArrayList.get(holder.getAdapterPosition()) + " Songs";
        holder.playlistName.setText(playlistNameCapitalized);
        holder.songsCount.setText(songsCountEdited);

        int[] playlistIcons = {R.drawable.playlist_icon_1, R.drawable.playlist_icon_2, R.drawable.playlist_icon_3};

        Random random = new Random();

        holder.playlistIcon.setBackgroundResource(playlistIcons[random.nextInt(3)]);

        holder.songsCount.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
        holder.playlistName.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
        holder.playlistTemplateLayout.setBackgroundColor(Color.rgb(Integer.parseInt(backgroundColors[0]), Integer.parseInt(backgroundColors[1]), Integer.parseInt(backgroundColors[2])));

        holder.itemView.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("lastPlaylist", MODE_PRIVATE);

            if(sharedPreferences.getString("lastPlaylist", "all").equalsIgnoreCase(playlistArrayList.get(holder.getAdapterPosition()))){
                Toast.makeText(context, "That's not allowed. ```\\_(-_-)_/```", Toast.LENGTH_SHORT).show();
            }

            else{
                if(!addToPlayList(songsListAdapter.selected, playlistArrayList.get(holder.getAdapterPosition()))){
                    Toast.makeText(context, "Added " + songsListAdapter.selected.size() + " song(s) to " + playlistArrayList.get(holder.getAdapterPosition()), Toast.LENGTH_SHORT).show();
                    edited = true;
                    editedPlaylist = playlistArrayList.get(holder.getAdapterPosition());
                }else{
                    Toast.makeText(context, playlistArrayList.get(holder.getAdapterPosition()) + " already contains all selected songs", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlistArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView playlistName;
        TextView songsCount;
        ImageView playlistIcon;
        ConstraintLayout playlistTemplateLayout;
        CardView playlistCardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.playlistName);
            songsCount = itemView.findViewById(R.id.songsCount);
            playlistIcon = itemView.findViewById(R.id.playlistIcon);
            playlistTemplateLayout = itemView.findViewById(R.id.playlistTemplateLayout);
            playlistCardView = itemView.findViewById(R.id.playlistCardView);
        }
    }

    boolean addToPlayList(ArrayList<String> songs, String playlistName){
        int added = 0;
        try{
            File playlist = new File(context.getDir("musicHistory", MODE_PRIVATE).toString() + "/" + playlistName.toLowerCase() + ".txt");
            ArrayList<String> songsArray = getSongsInPlaylist(playlistName);
            FileOutputStream fos = new FileOutputStream(playlist, true);

            for (String song:songs) {
                String songName = song + "\n";
                if(!songsArray.contains(song.toLowerCase())){
                    fos.write(songName.toLowerCase().getBytes());
                }else {
                    added += 1;
                }
            }

            fos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        return songs.size() == added;
    }

    Map<String, Integer> getPlaylistsCounts(){
        ArrayList<String> exemptedPlaylists = new ArrayList<>();
        exemptedPlaylists.add("all");
        exemptedPlaylists.add("favorites");
        exemptedPlaylists.add("history");

        Map<String, Integer> playlistsAndCounts = new HashMap<>();

        try{
            File playlistsDirectory = new File(context.getDir("musicHistory", MODE_PRIVATE).toString(), "playlists.txt");
            FileInputStream fis = new FileInputStream(playlistsDirectory);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            String line = bufferedReader.readLine();

            while(line != null){
                if(!exemptedPlaylists.contains(line)){
                    File playlist = new File(context.getDir("musicHistory", MODE_PRIVATE).toString(), "/" + line.toLowerCase() + ".txt");
                    FileInputStream fisPlaylist = new FileInputStream(playlist);
                    BufferedReader bufferedReaderPlaylist = new BufferedReader(new InputStreamReader(fisPlaylist));
                    String linePlaylist = bufferedReaderPlaylist.readLine();
                    int songsCount = 0;

                    while(linePlaylist != null){
                        songsCount += 1;
                        linePlaylist = bufferedReaderPlaylist.readLine();
                        playlistsAndCounts.put(line, songsCount);
                    }
                }
                line = bufferedReader.readLine();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return playlistsAndCounts;
    }

    ArrayList<String> getSongsInPlaylist(String playlistName){
        ArrayList<String> songsInPlaylist = new ArrayList<>();
        try{
            File playlist = new File(context.getDir("musicHistory", MODE_PRIVATE).toString(), "/" + playlistName.toLowerCase() + ".txt");
            FileInputStream fis = new FileInputStream(playlist);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            String line = bufferedReader.readLine();

            while(line != null){
                songsInPlaylist.add(line);
                System.out.println(line);
                line = bufferedReader.readLine();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return songsInPlaylist;
    }
}