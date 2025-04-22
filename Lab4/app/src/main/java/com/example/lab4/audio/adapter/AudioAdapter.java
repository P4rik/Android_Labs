package com.example.lab4.audio.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab4.R;
import com.example.lab4.audio.activity.AudioPlayerActivity;
import com.example.lab4.audio.dataholder.AudioDataHolder;
import com.example.lab4.audio.model.AudioModel;

import java.util.ArrayList;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    private final ArrayList<AudioModel> audioList;
    private final Context context;

    public AudioAdapter(Context context, ArrayList<AudioModel> audioList) {
        this.audioList = audioList;
        this.context = context;


    }

    @NonNull
    @Override
    public AudioAdapter.AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.audio_item_view, parent, false);
        return new AudioAdapter.AudioViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull AudioAdapter.AudioViewHolder holder, int position) {
        AudioModel audioModel = audioList.get(position);
        byte[] bitmapBytes = audioModel.getBitmap();

        holder.audioName.setText(audioModel.getAudioName());
        holder.audioAuthor.setText(audioModel.getAudioAuthor());
        holder.audioDuration.setText(audioModel.getAudioDuration());

        if (bitmapBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            holder.audioName.setSelected(true);
            holder.audioAuthor.setSelected(true);
            holder.imageViewAudio.setImageBitmap(bitmap);
            holder.imageViewAudio.clearColorFilter();
        } else {
            holder.imageViewAudio.setImageResource(R.drawable.ic_audio_file);
            holder.imageViewAudio.setColorFilter(ContextCompat.getColor(context, R.color.audio_card_ic));
        }

        holder.itemView.setOnClickListener(v -> {
            AudioDataHolder.setAudioList(audioList);

            Intent intent = new Intent(context, AudioPlayerActivity.class);
            intent.putExtra("CURRENT_INDEX", position);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public static class AudioViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewAudio;
        TextView audioName, audioAuthor, audioDuration;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            audioName = itemView.findViewById(R.id.audioName);
            audioDuration = itemView.findViewById(R.id.audioDuration);
            imageViewAudio = itemView.findViewById(R.id.imageViewAudio);
            audioAuthor = itemView.findViewById(R.id.audioAuthor);
        }
    }
}
