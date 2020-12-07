package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.DrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class SongRecViewAdapter extends RecyclerView.Adapter<SongRecViewAdapter.ViewHolder> {
    private ArrayList<Songs> songs = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private Context context;
    public SongRecViewAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.songlist, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Collections.sort(songs);
        holder.songName.setText(songs.get(position).getSongName());
        holder.artistName.setText(songs.get(position).getArtistName());
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(songs.get(position).getSongImage());
        byte[] data = mmr.getEmbeddedPicture();
        if (data != null) {
            Drawable image = new BitmapDrawable(BitmapFactory.decodeByteArray(data, 0, data.length));
            holder.songImage.setImageDrawable(image);
        }
        else{
            holder.songImage.setImageDrawable(context.getDrawable(R.drawable.albumart));
        }
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSecond = new Intent();
                goToSecond.setClass(context, PlayMusic.class);
//                goToSecond.putExtra("songData", new String[]{songs.get(position).getSongName(), songs.get(position).getArtistName(), songs.get(position).getSongDur(), songs.get(position).getSongImage(), String.valueOf(position)});
                goToSecond.putExtra("currentPlaying", position);

                goToSecond.putExtra("songs", songs);
                context.startActivity(goToSecond);
            }
        });

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void setSongs(ArrayList<Songs> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView songName;
        private CardView parent;
        private TextView artistName;
        private ImageView songImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            songImage = itemView.findViewById(R.id.songImage);
            songName = itemView.findViewById(R.id.songName);
            artistName = itemView.findViewById(R.id.artistName);
        }
    }
}
