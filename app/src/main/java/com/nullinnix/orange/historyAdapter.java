package com.nullinnix.orange;

import static android.content.Context.MODE_PRIVATE;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class historyAdapter extends RecyclerView.Adapter<historyAdapter.ViewHolder> {
    Context context;
    static ArrayList<AudioModel> songsList;
    ArrayList<String> thumbnails = new ArrayList<>();

    public historyAdapter(Context context, ArrayList<AudioModel> songsList){
        this.context = context;
        historyAdapter.songsList = songsList;
    }

    @NonNull
    @Override
    public historyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.history_template, parent, false);
        return new historyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull historyAdapter.ViewHolder holder, int position) {
        SharedPreferences textColor = context.getSharedPreferences("Text Color", MODE_PRIVATE);
        String[] textColors = textColor.getString("Text Color", "254,83,20").split(",");
        holder.setIsRecyclable(false);

        SharedPreferences backgroundColor = context.getSharedPreferences("Background Color", MODE_PRIVATE);
        String[] backgroundColors = backgroundColor.getString("Background Color", "0,0,0").split(",");

        holder.songName.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
        holder.timesPlayed.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
        holder.historyTemplate.setBackgroundColor(Color.rgb(Integer.parseInt(backgroundColors[0]), Integer.parseInt(backgroundColors[1]), Integer.parseInt(backgroundColors[2])));

        File thumbnailFiles = new File(context.getDir("thumbnails", MODE_PRIVATE).toString());
        thumbnails = new ArrayList<>(Arrays.asList(thumbnailFiles.list()));

        if(getHistory().containsKey(songsList.get(holder.getAdapterPosition()).getTitle().replace(",", ""))){

            String songName;
            if (songsList.get(holder.getAdapterPosition()).getTitle().length() >= 26){
                songName = songsList.get(holder.getAdapterPosition()).getTitle().substring(0, 26) + "...";
                holder.songName.setText(songName);
            }

            else{
                holder.songName.setText(songsList.get(holder.getAdapterPosition()).getTitle());
            }

            holder.timesPlayed.setText(String.valueOf(getHistory().get(songsList.get(holder.getAdapterPosition()).getTitle().replace(",", ""))));

            if(thumbnails.contains(songsList.get(holder.getAdapterPosition()).getTitle() + ".jpeg")){
                Bitmap bitmapIcon = BitmapFactory.decodeFile(context.getDir("thumbnails", MODE_PRIVATE).toString() + "/" + songsList.get(holder.getAdapterPosition()).getTitle() + ".jpeg");
                holder.songAlbumCover.setImageBitmap(bitmapIcon);
            }else{
                holder.songAlbumCover.setBackgroundResource(R.drawable.ic_music_icon_alt);
            }
        }
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView songName, timesPlayed;
        ImageView songAlbumCover;
        ConstraintLayout historyTemplate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            super.setIsRecyclable(false);
            songName = itemView.findViewById(R.id.songNameHistory);
            timesPlayed = itemView.findViewById(R.id.timesPlayedHistory);
            songAlbumCover = itemView.findViewById(R.id.currentSongIconHistory);
            historyTemplate = itemView.findViewById(R.id.historyTemplate);
        }
    }

    public Map<String, Integer> getHistory(){
        Map<String, Integer> keyValues = new HashMap<>();
        try{
            File history = new File(context.getDir("musicHistory", Context.MODE_PRIVATE).toString() + "/" + "history.txt");
            FileInputStream fis = new FileInputStream(history);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            String line = bufferedReader.readLine();
            while(line != null){
                String[] kv = line.split(",");
                keyValues.put(kv[0], Integer.parseInt(kv[1]));
                line = bufferedReader.readLine();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return keyValues;
    }
}
