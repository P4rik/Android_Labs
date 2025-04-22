package com.example.lab4.video.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab4.R;
import com.example.lab4.video.activity.VideoPlayerActivity;
import com.example.lab4.video.model.VideoModel;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private final ArrayList<VideoModel> videoList;
    private final Context context;

    public VideoAdapter(Context context, ArrayList<VideoModel> videoList) {
        this.videoList = videoList;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoAdapter.VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.video_item_view, parent, false);
        return new VideoAdapter.VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.VideoViewHolder holder, int position) {
        VideoModel videoModel = videoList.get(position);
        Bitmap preview = videoModel.getPreviewImage();

        if (preview != null) {
            holder.videoPreview.setImageBitmap(preview);
            holder.videoPreview.clearColorFilter();
        } else {
            holder.videoPreview.setImageResource(R.drawable.ic_video_file);
            holder.videoPreview.setColorFilter(ContextCompat.getColor(context, R.color.video_card_ic));
        }
        holder.videoName.setText(videoModel.getVideoName());
        holder.videoDuration.setText(videoModel.getVideoDuration());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra("videoPath", videoModel.getPath());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        ImageView videoPreview;
        TextView videoName, videoDuration;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            videoName = itemView.findViewById(R.id.videoName);
            videoDuration = itemView.findViewById(R.id.videoDuration);
            videoPreview = itemView.findViewById(R.id.videoPreview);

        }
    }
}
