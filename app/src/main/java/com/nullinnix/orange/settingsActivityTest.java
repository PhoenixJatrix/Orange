package com.nullinnix.orange;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class settingsActivityTest extends AppCompatActivity {
    Button black, gray, white, red, green, blue, cyan, lime, orange;
    Button textColor, backgroundColor, iconColor;
    ConstraintLayout colorsLayout, options;
    TextView action;
    TextView songNameSample, songDurationSample;
    ImageView favoriteSample;
    ConstraintLayout sampleCardView;
    ConstraintLayout settingsActivity;
    Button apply;
    SharedPreferences textColorPrefs;
    SharedPreferences backgroundColorPrefs;
    SharedPreferences iconColorPrefs;
    SharedPreferences progressBarColorPrefs;
    Button progressBarColor;
    ProgressBar progressBarSample;

    ColorStateList colorStateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        setIDs();
        onClickListeners();

        textColorPrefs = getSharedPreferences("Text Color", MODE_PRIVATE);
        String[] textColors = textColorPrefs.getString("Text Color", "254,83,20").split(",");
        songNameSample.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
        songDurationSample.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));

        backgroundColorPrefs = getSharedPreferences("Background Color", MODE_PRIVATE);
        String[] backgroundColors = backgroundColorPrefs.getString("Background Color", "0,0,0").split(",");
        sampleCardView.setBackgroundColor(Color.rgb(Integer.parseInt(backgroundColors[0]), Integer.parseInt(backgroundColors[1]), Integer.parseInt(backgroundColors[2])));

        iconColorPrefs = getSharedPreferences("Icon Color", MODE_PRIVATE);
        String[] iconColors = iconColorPrefs.getString("Icon Color", "254,83,20").split(",");
        favoriteSample.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));

        progressBarColorPrefs = getSharedPreferences("ProgressBar Color", MODE_PRIVATE);
        String[] progressBarColors = progressBarColorPrefs.getString("ProgressBar Color", "254,83,20").split(",");

        colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed},
                        new int[]{}
                },
                new int[] {
                        Color.rgb(Integer.parseInt(progressBarColors[0]), Integer.parseInt(progressBarColors[1]), Integer.parseInt(progressBarColors[2])),
                        Color.rgb(Integer.parseInt(progressBarColors[0]), Integer.parseInt(progressBarColors[1]), Integer.parseInt(progressBarColors[2]))
                }
        );

        progressBarSample.setProgressTintList(colorStateList);
    }

    void setIDs(){
        black = findViewById(R.id.black);
        gray = findViewById(R.id.gray);
        white = findViewById(R.id.white);
        red = findViewById(R.id.red);
        green = findViewById(R.id.green);
        blue = findViewById(R.id.blue);
        cyan = findViewById(R.id.cyan);
        lime = findViewById(R.id.lime);
        orange = findViewById(R.id.orange);

        colorsLayout = findViewById(R.id.colorsLayout);
        options = findViewById(R.id.options);
        action = findViewById(R.id.action);

        textColor = findViewById(R.id.textColor);
        backgroundColor = findViewById(R.id.backgroundColor);
        iconColor = findViewById(R.id.iconColor);

        songNameSample = findViewById(R.id.songNameSample);
        songDurationSample = findViewById(R.id.songDurationSample);
        favoriteSample = findViewById(R.id.favoriteSample);
        sampleCardView = findViewById(R.id.cardViewItemsHolder);
        settingsActivity = findViewById(R.id.settingsActivity);
        apply = findViewById(R.id.apply);

        progressBarColor = findViewById(R.id.progressBarColor);
        progressBarSample = findViewById(R.id.progressBarSample);
    }

    void onClickListeners(){
        black.setOnClickListener(v -> setColor(black));
        gray.setOnClickListener(v -> setColor(gray));
        white.setOnClickListener(v -> setColor(white));
        red.setOnClickListener(v -> setColor(red));
        green.setOnClickListener(v -> setColor(green));
        blue.setOnClickListener(v -> setColor(blue));
        cyan.setOnClickListener(v -> setColor(cyan));
        lime.setOnClickListener(v -> setColor(lime));
        orange.setOnClickListener(v -> setColor(orange));

        textColor.setOnClickListener(v -> {
            action.setText("Text Color");
            toggleColors();
        });

        backgroundColor.setOnClickListener(v -> {
            action.setText("Background Color");
            toggleColors();
        });

        iconColor.setOnClickListener(v -> {
            action.setText("Icon Color");
            toggleColors();
        });

        progressBarColor.setOnClickListener(v -> {
            action.setText("ProgressBar Color");
            toggleColors();
        });

        settingsActivity.setOnClickListener(v ->{
            colorsLayout.setVisibility(View.INVISIBLE);
            options.setVisibility(View.VISIBLE);
        });

        apply.setOnClickListener(v -> {
            Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName());
            Toast.makeText(this, "Applying....", Toast.LENGTH_SHORT).show();
            if(mediaPlayerInstance.songNames.size() > 0){
                ShowNotifications.hideNotification();
            }
            startActivity(Intent.makeRestartActivityTask(intent.getComponent()));
            Runtime.getRuntime().exit(0);
        });
    }

    void toggleColors(){
        if(colorsLayout.getVisibility() == View.INVISIBLE){
            colorsLayout.setVisibility(View.VISIBLE);
        }else{
            colorsLayout.setVisibility(View.INVISIBLE);
        }
        if(options.getVisibility() == View.INVISIBLE){
            options.setVisibility(View.VISIBLE);
        }else{
            options.setVisibility(View.INVISIBLE);
        }
    }

    void setColor(Button button){
        if(backgroundColorPrefs.getString("Background Color", "back").equalsIgnoreCase(button.getTag().toString()) && action.getText().toString().equalsIgnoreCase("text color")){
            Toast.makeText(this, "With that color scheme, some text won't be visible :). Try something else", Toast.LENGTH_LONG).show();
        }

        else if(backgroundColorPrefs.getString("Background Color", "back").equalsIgnoreCase(button.getTag().toString()) && action.getText().toString().equalsIgnoreCase("icon color")){
            Toast.makeText(this, "With that color scheme, some icons won't show up :). Try something else", Toast.LENGTH_LONG).show();
        }

        else if (button.getTag().toString().equalsIgnoreCase(iconColorPrefs.getString("Icon Color", "icon")) && action.getText().toString().equalsIgnoreCase("background color")){
            Toast.makeText(this, "With that color scheme, some icons won't show up :). Try something else", Toast.LENGTH_LONG).show();
        }

        else if (button.getTag().toString().equalsIgnoreCase(iconColorPrefs.getString("Text Color", "text")) && action.getText().toString().equalsIgnoreCase("background color")){
            Toast.makeText(this, "With that color scheme, some text won't be visible :). Try something else", Toast.LENGTH_LONG).show();
        }

        else{
            SharedPreferences sharedPreferences = getSharedPreferences(action.getText().toString(), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(action.getText().toString(), button.getTag().toString());
            editor.apply();

            apply.setVisibility(View.VISIBLE);
            if(action.getText().toString().equalsIgnoreCase("text color")){
                SharedPreferences textColor = getSharedPreferences("Text Color", MODE_PRIVATE);
                String[] textColors = textColor.getString("Text Color", "254,83,20").split(",");
                songNameSample.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
                songDurationSample.setTextColor(Color.rgb(Integer.parseInt(textColors[0]), Integer.parseInt(textColors[1]), Integer.parseInt(textColors[2])));
            }

            else if (action.getText().toString().equalsIgnoreCase("background color")){
                SharedPreferences backgroundColor = getSharedPreferences("Background Color", MODE_PRIVATE);
                String[] backgroundColors = backgroundColor.getString("Background Color", "0,0,0").split(",");
                sampleCardView.setBackgroundColor(Color.rgb(Integer.parseInt(backgroundColors[0]), Integer.parseInt(backgroundColors[1]), Integer.parseInt(backgroundColors[2])));
            }

            else if (action.getText().toString().equalsIgnoreCase("icon color")){
                SharedPreferences iconColor = getSharedPreferences("Icon Color", MODE_PRIVATE);
                String[] iconColors = iconColor.getString("Icon Color", "254,83,20").split(",");
                favoriteSample.getBackground().setTint(Color.rgb(Integer.parseInt(iconColors[0]), Integer.parseInt(iconColors[1]), Integer.parseInt(iconColors[2])));
            }

            else{
                SharedPreferences progressBarColor = getSharedPreferences("ProgressBar Color", MODE_PRIVATE);
                String[] progressBarColors = progressBarColor.getString("ProgressBar Color", "254,83,20").split(",");

                colorStateList = new ColorStateList(
                        new int[][]{
                                new int[]{android.R.attr.state_pressed},
                                new int[]{}
                        },
                        new int[] {
                                Color.rgb(Integer.parseInt(progressBarColors[0]), Integer.parseInt(progressBarColors[1]), Integer.parseInt(progressBarColors[2])),
                                Color.rgb(Integer.parseInt(progressBarColors[0]), Integer.parseInt(progressBarColors[1]), Integer.parseInt(progressBarColors[2]))
                        }
                );
                progressBarSample.setProgressTintList(colorStateList);
            }
            toggleColors();
        }
    }
}
