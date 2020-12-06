package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.health.TimerStat;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.xml.datatype.Duration;

public class PlayMusic extends AppCompatActivity {
    private Button playBtn, nextBtn, prevBtn;
    private ArrayList<Songs> songs;
    private TextView duration, startTime, songName, artistName;
    private Thread thread, thread2;
    private ProgressBar progressBar;
    private ImageView songImage;
    private ConstraintLayout mainLayout;


    private long durationTime, minutes, seconds;
    private boolean isPlaying = true, running = true;
    private int currentlyPlaying, start = 0;
    private long second = 0;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        Intent getSongData = getIntent();
        currentlyPlaying = getSongData.getIntExtra("currentPlaying", 0);
        songs = (ArrayList<Songs>) getIntent().getSerializableExtra("songs");

        playBtn = findViewById(R.id.playBtn);
        nextBtn = findViewById(R.id.buttonNext);
        prevBtn = findViewById(R.id.buttonPrev);

        startTime = findViewById(R.id.startTime);
        duration = findViewById(R.id.duration);

        mainLayout = findViewById(R.id.mainLayout);

        durationTime = Integer.parseInt(songs.get(currentlyPlaying).getSongDur());
        minutes = durationTime/60000;
        seconds = durationTime/1000;


        duration.setText( String.format("%02d", minutes) + ":" + String.format("%02d", seconds%60));

        songName = findViewById(R.id.songName);
        artistName = findViewById(R.id.artistName);
        songImage = findViewById(R.id.songImage);
        progressBar = findViewById(R.id.progressBar);


        songName.setText(songs.get(currentlyPlaying).getSongName());
        songName.setSelected(true);
        artistName.setText(songs.get(currentlyPlaying).getArtistName());
        artistName.setSelected(true);
        progressBar.setMax( (int) seconds);







        final Intent musicService = new Intent(this, MusicService.class);
        musicService.putExtra("link", songs.get(currentlyPlaying).getSongImage());
        musicService.putExtra("name", songs.get(currentlyPlaying).getSongName());
        running = true;


        if(!isMusicPlaying(MusicService.class))
        {
            startService(musicService);
        }
        else{
            stopService(musicService);
            final Intent musicService2 = new Intent(this, MusicService.class);
            musicService2.putExtra("link", songs.get(currentlyPlaying).getSongImage());
            musicService2.putExtra("name", songs.get(currentlyPlaying).getSongName());
            startService(musicService2);

        }

        playBtn.setBackground(getDrawable(R.drawable.ic_pause_btn));

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        });



//      Thread for progressbar of music
//        parallelTasks(start);


        setImage(songImage, songs.get(currentlyPlaying).getSongImage());

        setColorTheme(R.color.colorWhite);

        runTimer();

    }

    public void playMusic(View v){
        if(MusicService.isPlaying()){
            playBtn.setBackground(getDrawable(R.drawable.ic_play_btn));
            MusicService.pauseMusic();
            running = false;

        }
        else{
            playBtn.setBackground(getDrawable(R.drawable.ic_pause_btn));
            MusicService.playMusic();
            running = true;
        }
    }

    private boolean isMusicPlaying(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void playNext(){
        Songs song;
        if( currentlyPlaying == songs.size()-1)
        {
            song = songs.get(0);
            currentlyPlaying = 0;
        }
        else
        {
            song = songs.get(currentlyPlaying+1);
            currentlyPlaying +=1;
        }

        MusicService.stopMusic();

        final Intent musicService2 = new Intent(this, MusicService.class);
        musicService2.putExtra("link", song.getSongImage());
        musicService2.putExtra("name", song.getSongName());


        songName.setText(song.getSongName());
        setImage(songImage, song.getSongImage());
        artistName.setText(song.getArtistName());

        startTime.setText("00:00");
        long time = Integer.valueOf(song.getSongDur());
        int minute = (int) time / 60000;
        String durations = String.format("%02d:%02d", minute, (minute*60)%60);
        duration.setText(durations);
        playBtn.setBackground(getDrawable(R.drawable.ic_pause_btn));

        startService(musicService2);
        second = 0;
        running = true;

    }

    private void playPrev(){
        Songs song;
        if(currentlyPlaying  == 0)
        {
            song = songs.get(songs.size()-1);
            currentlyPlaying = songs.size()-1;
        }
        else
        {
            song = songs.get(currentlyPlaying - 1);
            currentlyPlaying -= 1;
        }

        MusicService.stopMusic();

        final Intent musicService2 = new Intent(this, MusicService.class);
        musicService2.putExtra("link", song.getSongImage());
        musicService2.putExtra("name", song.getSongName());

        songName.setText(song.getSongName());
        setImage(songImage, song.getSongImage());
        artistName.setText(song.getArtistName());
        startTime.setText("00:00");
        long time = Integer.valueOf(song.getSongDur());
        int minute = (int) time / 60000;
        String durations = String.format("%02d:%02d", minute, (minute*60)%60);
        duration.setText(durations);


        playBtn.setBackground(getDrawable(R.drawable.ic_pause_btn));
        startService(musicService2);
        running = true;
        second = 0;

    }



    private void setText(final TextView text,final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }


    private void setImage(final ImageView imageView, String link){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(link);
        byte[] data = mmr.getEmbeddedPicture();
        if (data != null) {
            Drawable image = new BitmapDrawable(BitmapFactory.decodeByteArray(data, 0, data.length));
            imageView.setImageDrawable(image);
        }
    }

    private void setColorTheme(int colorID){
        playBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, colorID));
        nextBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, colorID));
        prevBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, colorID));

        progressBar.setProgressBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_black));
        progressBar.setProgressTintList(ContextCompat.getColorStateList(this, colorID));
        songName.setTextColor(ContextCompat.getColorStateList(this, colorID));
        artistName.setTextColor(ContextCompat.getColorStateList(this, colorID));
        startTime.setTextColor(ContextCompat.getColorStateList(this, colorID));
        duration.setTextColor(ContextCompat.getColorStateList(this, colorID));
    }



    private void runTimer(){
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long minute = second/60;
                String time = String.format("%02d:%02d", minute, second%60);

                startTime.setText(time);

                if(running)
                {
                    if(second < seconds){
                        second++;
                        progressBar.setProgress( (int)second );
                    }
                    else{
                        running = false;
                        playBtn.setBackground(getDrawable(R.drawable.custom_play_btn));
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
}
