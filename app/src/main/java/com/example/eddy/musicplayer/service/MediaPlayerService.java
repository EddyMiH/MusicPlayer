package com.example.eddy.musicplayer.service;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.example.eddy.musicplayer.model.Song;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MediaPlayerService extends IntentService {

    private LocalBinder mLocalBinder;
    private MediaPlayer mMediaPlayer ;
    private List<Song> allSongData;
    private int currentSongIndex;

    private UiChangeListener mUiChangeListener;

    public void setUiChangeListener(UiChangeListener uiChangeListener){
        mUiChangeListener = uiChangeListener;
    }

    public MediaPlayerService() {
        super("MusicPlayerService");
        allSongData = new ArrayList<>();
    }

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mMediaPlayer.start();
        }
    };

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }
    };

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //TODO call playNext method of LocalBinder
            mLocalBinder.playNext();

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaPlayer();

    }

    private void initMediaPlayer(){
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
    }

    private int getDuration(){
        return mMediaPlayer.isPlaying() ? mMediaPlayer.getCurrentPosition() : 0;
    }

    private void release(){
        if(mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void setAllSongData(List<Song> allSongData) {
        this.allSongData = allSongData;
    }

    public void setCurrentSongIndex(int currentSongIndex) {
        this.currentSongIndex = currentSongIndex;
    }

    private boolean isPlayerPlaying(){
        return mMediaPlayer.isPlaying();
    }

    private void playSong(String path){
        try {
            Log.d(MediaPlayerService.class.getSimpleName(),  "playSong method");

            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resume(){
        if(mMediaPlayer != null && !mMediaPlayer.isPlaying()){
            mMediaPlayer.start();
        }
    }

    private void pause(){
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }
    }

    private void stop(){
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();

        }
    }

    private void seekTo(int position){
        if(mMediaPlayer != null ){
            mMediaPlayer.seekTo(position);
        }
    }

    private int getMediaPlayerDuration(){
        return  mMediaPlayer.getDuration();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }



    @Override
    protected void onHandleIntent( Intent intent) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        if(mLocalBinder == null){
            mLocalBinder = new LocalBinder();
        }
        return mLocalBinder;
    }

    public class LocalBinder extends Binder{
        public void play(String path){
            playSong(path);

        }
        public void pause(){
            //mUiChangeListener.setUi(allSongData.get(currentSongIndex));
            MediaPlayerService.this.pause();


        }
        public void resume(){
            MediaPlayerService.this.resume();
            mUiChangeListener.setUi(allSongData.get(currentSongIndex));
        }
        public void playNext(){
            if(currentSongIndex == allSongData.size()-1){
                currentSongIndex = 0;
            }
            playSong(allSongData.get(++currentSongIndex).getSongPath());
            mUiChangeListener.setUi(allSongData.get(currentSongIndex));
        }
        public void playPrevious(){
            if (currentSongIndex > 0){
                playSong(allSongData.get(--currentSongIndex).getSongPath());
            }else{
                currentSongIndex = allSongData.size() - 1;
                playSong(allSongData.get(currentSongIndex).getSongPath());
            }
            mUiChangeListener.setUi(allSongData.get(currentSongIndex));
        }
        public void stop(){
            MediaPlayerService.this.stop();
        }
        public void seekTo(int position){
            MediaPlayerService.this.seekTo(position);
            mUiChangeListener.setUi(allSongData.get(currentSongIndex));
        }
        public void setSongData(List<Song> songs){
            setAllSongData(songs);
        }

        public void currentSongIndex(int index){
            setCurrentSongIndex(index);
        }

        public boolean isPlaying(){
            return isPlayerPlaying();
        }
        public int getCurrentDuration(){
            return getDuration();
        }
        public void setUiListener(UiChangeListener uiListener){
            setUiChangeListener(uiListener);
        }
        public int durationOfMediaPlayer(){
            return getMediaPlayerDuration();
        }
    }

    public interface UiChangeListener{
        void setUi(Song song);
    }

}
