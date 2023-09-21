package com.nullinnix.orange;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import static com.nullinnix.orange.favorites.favoritesArray;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class songsListAdapter extends RecyclerView.Adapter<songsListAdapter.ViewHolder>{
    Context context;
    static ArrayList<AudioModel> audioModels;
    MediaPlayer mediaPlayer = mediaPlayerInstance.getInstance();
    static ArrayList<String> thumbnailsArrayList;
    static boolean[] multiSelecting = {false};
    static ArrayList<String> selected = new ArrayList<>();
    static Map<String, Integer> intervalMap = new HashMap<>();
    static ArrayList<Integer> colored = new ArrayList<>();
    static boolean firstPlay = false;
    favorites favorites;
    SharedPreferences textColor;
    SharedPreferences backgroundColor;
    SharedPreferences iconColor;
    String[] textColors;
    String[] backgroundColors;
    String[] iconColors;
    static boolean updateCurrentPlaylist = false;
    int runs = 0;
    static boolean resetSpeed = true;
    public songsListAdapter(Context context, ArrayList<AudioModel> audioModels){
        this.context = context;
        songsListAdapter.audioModels = audioModels;
        File thumbnailFiles = new File(context.getDir("thumbnails", MODE_PRIVATE).toString());
        thumbnailsArrayList = new ArrayList<>(Arrays.asList(thumbnailFiles.list()));
        favorites = new favorites(context);

        try {
            favorites.loadUpFavorites();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        textColor = context.getSharedPreferences("Text Color", MODE_PRIVATE);
        backgroundColor = context.getSharedPreferences("Background Color", MODE_PRIVATE);
        iconColor = context.getSharedPreferences("Icon Color", MODE_PRIVATE);

        textColors = textColor.getString("Text Color", "224,93,41").split(",");
        backgroundColors = backgroundColor.getString("Background Color", "0,0,0").split(",");
        iconColors = iconColor.getString("Icon Color", "224,93,41").split(",");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.song_list_template, parent, false);
        return new songsListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mediaPlayerInstance.playerOpened = false;

        holder.setIsRecyclable(false);

        boolean[] isFavorite = {false};

        int durationMilli = Integer.parseInt(audioModels.get(position).getDuration());
        durationMilli = durationMilli / 1000;
        int durationMin = durationMilli / 60;
        int durationSec = durationMilli % 60;

        String durationSecString = Integer.toString(durationSec);

        if (durationSec < 10){
            durationSecString = "0" + durationSec;
        }
        String duration = durationMin + ":" + durationSecString;

        holder.parent.setBackgroundColor(Color.rgb(Integer.parseInt(backgroundColors[0]), Integer.parseInt(backgroundColors[1]), Integer.parseInt(backgroundColors[2])));
        holder.songName.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
        holder.songDuration.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
        holder.favorite.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));

        if(thumbnailsArrayList.contains(audioModels.get(holder.getAdapterPosition()).getTitle() + ".jpeg")){
            Bitmap bitmapIcon = BitmapFactory.decodeFile(context.getDir("thumbnails", MODE_PRIVATE).toString() + "/" + audioModels.get(holder.getAdapterPosition()).getTitle() + ".jpeg");
            holder.songIcon.setImageBitmap(bitmapIcon);
        }
        else{
            holder.songIcon.setBackgroundResource(R.drawable.ic_music_icon_alt);
        }

        String songName;
        if (audioModels.get(position).getTitle().length() >= 30){
            songName = audioModels.get(position).getTitle().substring(0, 30) + "...";
            holder.songName.setText(songName);
        }

        else{
            holder.songName.setText(audioModels.get(position).getTitle());
        }

        if(favoritesArray.contains(audioModels.get(holder.getAdapterPosition()).getTitle())){
            holder.favorite.setBackgroundResource(R.drawable.ic_favorite);
            isFavorite[0] = true;
            holder.favorite.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
        }
        holder.songDuration.setText(duration);

        holder.itemView.setOnClickListener(v -> {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if(runs == 0){
                        holder.parent.setBackgroundColor(context.getColor(R.color.translucent));
                        runs += 1;
                        Log.d(TAG, "run: coloring");
                    } else if(runs == 1){
                        holder.parent.setBackgroundColor(Color.rgb(Integer.parseInt(backgroundColors[0]), Integer.parseInt(backgroundColors[1]), Integer.parseInt(backgroundColors[2])));
                        Log.d(TAG, "run: coloring to default");
                        timer.cancel();
                        runs = 0;
                    }
                    Log.d(TAG, "run: running for translucency");
                }
            }, 0, 100);

            Intent intent = new Intent(context, musicPlayerWindowActivity.class);
            if (mediaPlayerInstance.songNames.isEmpty() && !MainActivity.searching) {
                mediaPlayerInstance.playingSameSong = false;
                resetSpeed = true;
                if(!multiSelecting[0]){
                    mediaPlayerInstance.songNames.add(audioModels.get(holder.getAdapterPosition()).getTitle());
                }
            }

            else{
                if(!MainActivity.searching){
                    if(!mediaPlayerInstance.songNames.get(mediaPlayerInstance.songNames.size() - 1).equals(audioModels.get(holder.getAdapterPosition()).getTitle())){
                        mediaPlayerInstance.getInstance().reset();
                        mediaPlayerInstance.playingSameSong = false;
                        resetSpeed = true;
                    }else{
                        if(MainActivity.absoluteSuspendMiniPlayer){
                            mediaPlayerInstance.getInstance().reset();
                            mediaPlayerInstance.playingSameSong = false;
                            resetSpeed = true;
                        }
                        else{
                            mediaPlayerInstance.playingSameSong = true;
                            resetSpeed = false;
                        }
                    }
                }
            }

            if(!multiSelecting[0] && !MainActivity.searching){
                mediaPlayerInstance.songNames.add(audioModels.get(holder.getAdapterPosition()).getTitle());
            }

            intervalMap.put(audioModels.get(holder.getAdapterPosition()).getTitle(), holder.getAdapterPosition());

            if(!multiSelecting[0] && !MainActivity.searching){
                MainActivity.updateNotification = true;
                mediaPlayerInstance.currentIndex = holder.getAdapterPosition();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putParcelableArrayListExtra("audioModel", audioModels);
                context.startActivity(intent);
                updateCurrentPlaylist = true;
            }

            else{
                if(selected.contains(audioModels.get(holder.getAdapterPosition()).getTitle()) && multiSelecting[0]){
                    selected.remove(audioModels.get(holder.getAdapterPosition()).getTitle());
                    holder.parent.setX(holder.parent.getX() - 100);
                }

                else{
                    if(!selected.contains((audioModels.get(holder.getAdapterPosition()).getTitle())) && multiSelecting[0]){
                        selected.add(audioModels.get(holder.getAdapterPosition()).getTitle());
                        holder.parent.setX(holder.parent.getX() + 100);
                    }
                }
            }

            if(!multiSelecting[0] && !MainActivity.searching){
                musicPlayerWindowActivity.changed = true;
                MainActivity.updateMiniPlayer = true;
                colored.add(holder.getAdapterPosition());

                firstPlay = true;
                MainActivity.absoluteSuspendMiniPlayer = false;

                if(colored.size() > 1){
                    notifyItemChanged(colored.get(colored.size() - 2));
                }
            }

            if(multiSelecting[0]){
                MainActivity.selected = true;
            }
    });

        holder.itemView.setOnLongClickListener(v -> {
            intervalMap.put(audioModels.get(holder.getAdapterPosition()).getTitle(), holder.getAdapterPosition());
            if(!multiSelecting[0]){
                multiSelecting[0] = true;
                mediaPlayer.stop();
                holder.parent.setX(holder.parent.getX() + 100);
                selected.add(audioModels.get(holder.getAdapterPosition()).getTitle());
                MainActivity.selected = true;
            }

            else{
                multiSelecting[0] = false;
                selected.clear();
                MainActivity.selectingAll = false;
                MainActivity.isSelectingInterval = false;
                mediaPlayerInstance.songNames.clear();
                MainActivity.selectingAll = false;
                songsListAdapter.multiSelecting[0] = false;
                MainActivity.multiSelectingStopped = true;

                NotificationManager manager = context.getSystemService(NotificationManager.class);
                manager.cancel(6062);
            }

            return true;
        });

        holder.favorite.setOnClickListener(v -> {
            if(!multiSelecting[0]){
                if(!isFavorite[0]){
                    holder.favorite.setBackgroundResource(R.drawable.ic_favorite);
                    isFavorite[0] = true;
                    Toast.makeText(context, audioModels.get(holder.getAdapterPosition()).getTitle() + " Added to Favorites", Toast.LENGTH_SHORT).show();
                    try{
                        favorites.addToFavorites(audioModels.get(holder.getAdapterPosition()).getTitle());
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                else{
                    holder.favorite.setBackgroundResource(R.drawable.ic_not_favorite);
                    isFavorite[0] = false;
                    favorites.removeFromFavorites(audioModels.get(holder.getAdapterPosition()).getTitle());
                    Toast.makeText(context, audioModels.get(holder.getAdapterPosition()).getTitle() + " Removed from Favorites", Toast.LENGTH_SHORT).show();
                }

                try{
                    favorites.loadUpFavorites();
                }catch (IOException e){
                    e.printStackTrace();
                }
                holder.favorite.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
            }
        });

        holder.favorite.setOnLongClickListener(v -> {
            if(!multiSelecting[0]){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                        .setMessage("Remove all Favorites?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(favoritesArray.isEmpty()){
                                    Toast.makeText(context, "You have no favorites", Toast.LENGTH_SHORT).show();
                                }

                                else{
                                    Toast.makeText(context, "Orange removed " + favoritesArray.size() + " song(s) from favorites", Toast.LENGTH_SHORT).show();
                                    favorites.resetFavorites();
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(intent);
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                alertDialog.show();
            }
            return true;
        });

        if(MainActivity.selectingAll){
            selected.clear();
            multiSelecting[0] = true;
            for (AudioModel model:audioModels) {
                selected.add(model.getTitle());
            }
            MainActivity.selectingAll = false;
            MainActivity.selected = true;
        }

        if(selected.contains(audioModels.get(holder.getAdapterPosition()).getTitle()) && multiSelecting[0]){
            holder.parent.setX(holder.parent.getX() + 100);
        }

        if(mediaPlayerInstance.songNames.size() > 0 && !multiSelecting[0]){
            if(audioModels.get(holder.getAdapterPosition()).getTitle().equalsIgnoreCase(mediaPlayerInstance.songNames.get(mediaPlayerInstance.songNames.size() - 1))){
                int[] colors = {255, 0, 255};
                holder.songName.setTextColor(Color.rgb(colors[0], colors[1], colors[2]));
                holder.songDuration.setTextColor(Color.rgb(colors[0], colors[1], colors[2]));
            }
        }
    }

    @Override
    public int getItemCount() {
        return audioModels.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView songName;
        TextView songDuration;
        ConstraintLayout parent;
        ImageView songIcon;
        RecyclerView recyclerView;
        Button favorite;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            super.setIsRecyclable(false);
            songName = itemView.findViewById(R.id.songName);
            songDuration = itemView.findViewById(R.id.songDuration);
            parent = itemView.findViewById(R.id.cardViewItemsHolder);
            songIcon = itemView.findViewById(R.id.currentSongIcon);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            favorite = itemView.findViewById(R.id.favorite);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}