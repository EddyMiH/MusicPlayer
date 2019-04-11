package com.example.eddy.musicplayer.fragment;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.eddy.musicplayer.MainActivity;
import com.example.eddy.musicplayer.R;
import com.example.eddy.musicplayer.adapter.SongRecyclerAdapter;
import com.example.eddy.musicplayer.model.Song;
import com.example.eddy.musicplayer.service.MediaPlayerService;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewFragment extends Fragment {

    private SongRecyclerAdapter recyclerAdapter;
    private List<Song> songMediaData;

    private MediaPlayerService.LocalBinder mLocalBinder;

    private ImageView previousButtonView;
    private ImageView playButtonView;
    private ImageView nextButtonView;

    private TextView currentDurationTextView;
    private TextView durationTextView;
    private TextView currentSongLabelTextView;

    private Handler handler;

    private SeekBar seekBarView;

    private SongRecyclerAdapter.OnItemSelectedListener mOnItemSelectedListener = new SongRecyclerAdapter.OnItemSelectedListener() {
        @Override
        public void onItemSelected(Song song) {
            //TODO call playSong method mLocalBinder.play with passing path of song
            mLocalBinder.currentSongIndex(songMediaData.indexOf(song));
//            String str = song.getSongArtist() + " " + song.getSongTitle();
//            currentSongLabelTextView.setText(str);
//            durationTextView.setText(song.getStringDuration());
            playButtonView.setImageDrawable(ContextCompat.getDrawable(MainActivity.getContextOfApplication(),
                    android.R.drawable.ic_media_pause));
            mLocalBinder.play(song.getSongPath());
            startHandler(song);
        }
    };

    private MediaPlayerService.UiChangeListener mUiChangeListener = new MediaPlayerService.UiChangeListener() {
        @Override
        public void setUi(Song song) {
            startHandler(song);
        }
    };

    public String castMillisecondToMinuteAndSecond(int milliseconds){
        long minutes = (milliseconds) / 1000 / 60;
        long sec = ( milliseconds / 1000 ) % 60;
        return (String.valueOf(minutes) + ":" + String.valueOf(sec) );
    }

    public void startHandler(Song song){
        handler = new Handler();
        String str = song.getSongArtist() + " " + song.getSongTitle();
        currentSongLabelTextView.setText(str);
        durationTextView.setText(song.getStringDuration());
        seekBarView.setMax(mLocalBinder.durationOfMediaPlayer());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mLocalBinder.isPlaying()){
                    currentDurationTextView.setText(castMillisecondToMinuteAndSecond(mLocalBinder.getCurrentDuration()));
                    int mCurrentPosition = mLocalBinder.getCurrentDuration() ;
                    seekBarView.setProgress(mCurrentPosition);
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mLocalBinder = (MediaPlayerService.LocalBinder) service;
            mLocalBinder.setSongData(songMediaData);
            mLocalBinder.setUiListener(mUiChangeListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLocalBinder = null;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(MainActivity.getContextOfApplication(), MediaPlayerService.class);
        getActivity().bindService(intent,mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocalBinder.stop();
        getActivity().unbindService(mServiceConnection);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        songMediaData = loadMusicFilesFromDevice();

        recyclerAdapter = new SongRecyclerAdapter();
        recyclerAdapter.setOnItemSelectedListener(mOnItemSelectedListener);
        recyclerAdapter.addAll(songMediaData);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);

        previousButtonView = rootView.findViewById(R.id.previous_song_btn);
        playButtonView = rootView.findViewById(R.id.play_song_btn);
        nextButtonView = rootView.findViewById(R.id.next_song_btn);
        currentDurationTextView = rootView.findViewById(R.id.current_duration_position_text_view);
        durationTextView = rootView.findViewById(R.id.whole_duration_position_text_view);
        seekBarView = rootView.findViewById(R.id.seek_bar_song_duration);
        currentSongLabelTextView = rootView.findViewById(R.id.current_song_label);

        startTrackingSeekBar();

        previousButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocalBinder.playPrevious();
            }
        });

        nextButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocalBinder.playNext();
            }
        });

        playButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLocalBinder.isPlaying()){
//                    playButtonView.setImageResource(getResources().getIdentifier("@android:drawable/ic_media_pause",
//                            null,null));
                    playButtonView.setImageDrawable(ContextCompat.getDrawable(MainActivity.getContextOfApplication(),
                            android.R.drawable.ic_media_play));
                    mLocalBinder.pause();
                }else{
                    playButtonView.setImageDrawable(ContextCompat.getDrawable(MainActivity.getContextOfApplication(),
                            android.R.drawable.ic_media_pause));
                    mLocalBinder.resume();
                }
            }
        });

        return rootView;
    }

    public  void startTrackingSeekBar(){
        seekBarView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //seekBar.setMax(mLocalBinder.durationOfMediaPlayer());
                if(fromUser){
                    mLocalBinder.seekTo(progress );
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //mLocalBinder.seekTo(seekBar.getProgress());
            }
        });
    }

    public List<Song> loadMusicFilesFromDevice(){

        List<Song> arrayOfSongs = new ArrayList<>();
        ContentResolver contentResolver = MainActivity.getContextOfApplication().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        Cursor cursor = contentResolver.query(songUri, null, selection, null, null);

        if(cursor != null && cursor.getCount() > 0){
            while(cursor.moveToNext()){
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));


                arrayOfSongs.add(new Song(id, title, artist, duration, path));
            }
            cursor.close();
        }
        return arrayOfSongs;
    }

    public RecyclerViewFragment() {
        //required
    }

}
