package com.example.eddy.musicplayer.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.eddy.musicplayer.R;

public class SongViewHolder extends RecyclerView.ViewHolder {

    private TextView title;
    private TextView artist;
    private TextView duration;

    public SongViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.label_title_recycler);
        duration = itemView.findViewById(R.id.label_duration_recycler);
        artist= itemView.findViewById(R.id.label_artist_recycler);
    }

    public void setArtist(String artist){
        this.artist.setText(artist);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setDuration(String duration) {

        this.duration.setText(duration);
    }

//    private OnItemClickListener mOnItemClickListener;
//
//    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if(mOnItemClickListener != null){
//                mOnItemClickListener.onItemClicked(getAdapterPosition());
//            }
//        }
//    };

//    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
//        mOnItemClickListener = onItemClickListener;
//    }
//
//    public interface OnItemClickListener{
//        void onItemClicked(int position);
//    }
}
