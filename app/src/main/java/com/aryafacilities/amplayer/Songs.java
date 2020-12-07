package com.aryafacilities.amplayer;

import java.io.Serializable;

public class Songs implements Comparable, Serializable {
    private String songName;
    private String artistName;
    private String songImage;
    private  String songDur;

    public Songs(String songName, String artistName, String songImage, String songDur) {
        this.songName = songName;
        this.artistName = artistName;
        this.songImage = songImage;
        this.songDur = songDur;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSongImage() {
        return songImage;
    }

    public void setSongImage(String songImage) {
        this.songImage = songImage;
    }

    public String getSongDur() {
        return songDur;
    }

    public void setSongDur(String songDur) {
        this.songDur = songDur;
    }

    @Override
    public String toString() {
        return "Songs{" +
                "songName='" + songName + '\'' +
                ", artistName='" + artistName + '\'' +
                ", songImage='" + songImage + '\'' +
                ", songDur=" + songDur +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        Songs s = (Songs)o;
        return this.songName.toLowerCase().compareTo(s.songName.toLowerCase());
    }
}
