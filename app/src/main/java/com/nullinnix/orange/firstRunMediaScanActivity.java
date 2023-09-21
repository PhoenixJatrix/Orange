package com.nullinnix.orange;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_LONG;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class firstRunMediaScanActivity extends AppCompatActivity {
    Button scanForMusic;
    ConstraintLayout first_run_media_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_run_media_scan);
        first_run_media_scan = findViewById(R.id.firstRunLayout);
        scanForMusic = findViewById(R.id.scanForMusic);

        requestPermissions();
        SharedPreferences sharedPreferences = getSharedPreferences("numberOfSongs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("numberOfSongs", 0);
        editor.apply();
        Snackbar snackbar = Snackbar.make(first_run_media_scan, "Orange needs Access to your Music", Snackbar.LENGTH_INDEFINITE);

        scanForMusic.setOnClickListener(v -> {
            if(!checkPermissionReadMedia()){
                requestPermissions();
                Toast.makeText(firstRunMediaScanActivity.this, "Orange needs all permissions to work properly", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(firstRunMediaScanActivity.this, "Scanning for Music. This will take a second or 2. Maybe 10", LENGTH_LONG).show();

                SharedPreferences sharedPreferencesHistory = getSharedPreferences("historyCreated", MODE_PRIVATE);
                SharedPreferences.Editor historyEditor = sharedPreferencesHistory.edit();
                historyEditor.putBoolean("historyCreated", false);
                historyEditor.apply();

                MainActivity.firstRun = true;
                onBackPressed();
            }
        });

        if (!checkPermissionReadMedia()){
            snackbar.setAction("Give Access", v -> {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }).show();
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(checkPermissionReadMedia()){
                    snackbar.dismiss();
                    timer.cancel();
                }
            }
        }, 1000, 1000);

    }
    void requestPermissions(){
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
        };


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            permissions = new String[]{
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
            };
        }

        ActivityCompat.requestPermissions(this, permissions, 209);
    }

    boolean checkPermissionReadMedia(){
        int permissionReadAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO);
        int permissionRecordAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        Log.d(TAG, String.format("%s, %s", permissionReadAudio == PackageManager.PERMISSION_GRANTED, permissionRecordAudio == PackageManager.PERMISSION_GRANTED));

        return permissionReadAudio == PackageManager.PERMISSION_GRANTED && permissionRecordAudio == PackageManager.PERMISSION_GRANTED;

    }
}
