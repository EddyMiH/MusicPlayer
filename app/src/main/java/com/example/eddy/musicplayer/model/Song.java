package com.example.eddy.musicplayer.model;

public class Song {

    private long songId;
    private String songTitle;
    private String songArtist;
    private long songDuration;
    private String songPath;

    public Song(long songId, String songTitle,String songArtist, long songDuration, String songPath) {
        this.songId = songId;
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.songDuration = songDuration;
        this.songPath = songPath;
    }

    public long getSongId() {
        return songId;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public long getSongDuration() {
        return songDuration;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public void setSongDuration(long songDuration) {
        this.songDuration = songDuration;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public String getStringDuration(){
        long minutes = ( songDuration / 1000 ) / 60;
        long sec = ( songDuration / 1000 ) % 60;
        return String.valueOf(minutes) + ":" + String.valueOf(sec) ;
    }
}
