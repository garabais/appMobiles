package com.example.proyectomobiles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder>{

    private List<Media> data;
    private View.OnClickListener clickL;

    public MediaAdapter(List<Media> data, View.OnClickListener l) {
        this.data = data;
        this.clickL = l;
    }

    @NonNull
    @NotNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_row, parent, false);
        v.setOnClickListener(clickL);
        return new MediaAdapter.MediaViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MediaViewHolder holder, int position) {
        holder.name.setText(data.get(position).getName());
        holder.score.setText(data.get(position).getScore() + "");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder {
        TextView name, score;
        public MediaViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.mediaName);
            score = itemView.findViewById(R.id.mediaScore);
        }
    }
}
