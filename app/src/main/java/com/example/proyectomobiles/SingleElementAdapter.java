package com.example.proyectomobiles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SingleElementAdapter extends RecyclerView.Adapter<SingleElementAdapter.UserViewHolder> {

    private List<String> data;
    private View.OnClickListener l;

    public SingleElementAdapter(List<String> data, View.OnClickListener l) {
        this.data = data;
        this.l = l;
    }

    @NonNull
    @NotNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_element_row, parent, false);
        v.setOnClickListener(l);
        return new SingleElementAdapter.UserViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserViewHolder holder, int position) {
        holder.content.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        public UserViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.elementText);
        }
    }
}
