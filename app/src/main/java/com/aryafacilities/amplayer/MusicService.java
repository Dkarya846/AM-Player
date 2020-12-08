package com.aryafacilities.amplayer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MusicService extends Service {

    private static final String CHANNEL_ID = "notification_app";
    private static final String CHANNEL_NAME = "Music Player";
    private static final String CHANNEL_DESC = "This is a notification trial";

    private static MediaPlayer mediaPlayer;
    private static String name;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Uri song = Uri.parse(intent.getStringExtra("link"));

        name = intent.getStringExtra("name");
        mediaPlayer = MediaPlayer.create(this, song);
        mediaPlayer.start();


        mediaPlayer.getTrackInfo();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(CHANNEL_DESC);

            channel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        displayNotification();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
    }


    public static boolean isPlaying(){
        if(mediaPlayer!=null)
        {
            if (mediaPlayer.isPlaying()){
                return true;
            }
        }

        return false;
    }

    public static void playMusic(){
        mediaPlayer.start();
    }

    public static void pauseMusic(){
        mediaPlayer.pause();
    }

    public static void stopMusic(){
        mediaPlayer.stop();
    }

    private void displayNotification(){
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.ic_play);


        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        nBuilder.setContentTitle(CHANNEL_NAME)
                .setLargeIcon(icon)
                .setSmallIcon(R.drawable.ic_play_btn)
                .setContentText(name +" is playing in background")
                .setPriority(NotificationCompat.PRIORITY_MIN);
        nBuilder.setDefaults(0)
        .setSound(null, AudioManager.STREAM_NOTIFICATION);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1, nBuilder.build());
    }
}
