package com.example.eddy.musicplayer.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eddy.musicplayer.R;
import com.example.eddy.musicplayer.model.Song;
import java.util.ArrayList;
import java.util.List;

public class SongRecyclerAdapter extends RecyclerView.Adapter<SongViewHolder> {

    private List<Song> mData;
    private OnItemSelectedListener mOnItemSelectedListener;

    public SongRecyclerAdapter(){
        mData = new ArrayList<>();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_song_item,viewGroup,false);
        final SongViewHolder mSongViewHolder = new SongViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemSelectedListener != null){
                    mOnItemSelectedListener.onItemSelected(mData.get(mSongViewHolder.getAdapterPosition()));
                }
            }
        });

        return mSongViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder songViewHolder, int i) {
        Song mSong = mData.get(i);
        songViewHolder.setTitle(mSong.getSongTitle());
        long minutes = mSong.getSongDuration() / 1000 / 60;
        long sec = ( mSong.getSongDuration() / 1000 ) % 60;
        songViewHolder.setDuration(String.valueOf(minutes) + ":" + String.valueOf(sec) );
        songViewHolder.setArtist(mSong.getSongArtist());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addAll(List<Song> songs){
        mData.addAll(songs);
        notifyDataSetChanged();

    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener){
        mOnItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(Song song);
    }
}
