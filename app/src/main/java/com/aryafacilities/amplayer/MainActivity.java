package com.aryafacilities.amplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mainListView;
    private ArrayList<Songs> songsList;
    private int STORAGE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainListView = findViewById(R.id.mainListView);
        songsList = new ArrayList<>();

        if (!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
        else{
            TextView textView = findViewById(R.id.emptyList);
            textView.setText("Permission not granted to the app. \n \uD83D\uDE14");
            textView.setVisibility(View.VISIBLE);
            mainListView.setVisibility(View.GONE);
        }


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

        if(mainListView.getChildCount() == 0){
                TextView textView = findViewById(R.id.emptyList);
                textView.setText("No songs are available \n \uD83D\uDE14");
                textView.setVisibility(View.VISIBLE);
                mainListView.setVisibility(View.GONE);
        }

        else {
            mainListView.setItemViewCacheSize(300);
            mainListView.setDrawingCacheEnabled(true);
            mainListView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            adapter.setSongs(songsList);
            mainListView.setAdapter(adapter);
            mainListView.setNestedScrollingEnabled(false);
            mainListView.setLayoutManager(new GridLayoutManager(this, 2));
        }
    }

    private void requestStoragePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission for Storage")
                    .setMessage("This permission required for searching songs in device")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Permission not Granted", Toast.LENGTH_SHORT).show();
        }
    }
}
