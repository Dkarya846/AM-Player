package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.URI;
import java.util.ArrayList;
import java.util.Currency;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mainListView;
    private ArrayList<Songs> songsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainListView = findViewById(R.id.mainListView);
        songsList = new ArrayList<>();




        String[] projection = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION};
        Cursor audioCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

        if (audioCursor!=null) {
            if (audioCursor.moveToFirst()) {
                do {
                    int dataIndex = audioCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                    int artistIndex = audioCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    int titleIndex = audioCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    int durationIndex = audioCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

                    songsList.add(new Songs(audioCursor.getString(titleIndex),
                            audioCursor.getString(artistIndex),
                            audioCursor.getString(dataIndex),
                            audioCursor.getString(durationIndex)));
                } while (audioCursor.moveToNext());
            }
        }
        SongRecViewAdapter adapter = new SongRecViewAdapter(this);

        if(mainListView == null){
                TextView textView = findViewById(R.id.emptyList);
                textView.setVisibility(View.VISIBLE);
                mainListView.setVisibility(View.GONE);
        }

        else {
            mainListView.setItemViewCacheSize(20);
            mainListView.setDrawingCacheEnabled(true);
            mainListView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            adapter.setSongs(songsList);
            mainListView.setAdapter(adapter);
            mainListView.setNestedScrollingEnabled(false);
            mainListView.setLayoutManager(new GridLayoutManager(this, 2));
        }
    }
}
