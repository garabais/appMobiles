package com.example.proyectomobiles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ElementListAdapter extends RecyclerView.Adapter<ElementListAdapter.ViewHolder> {

    private List<String> names, scores;

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, score;

        public ViewHolder(View itemView) {

            super(itemView);

            name = itemView.findViewById(R.id.element_name);
            score = itemView.findViewById(R.id.element_score);
        }

    }

    public ElementListAdapter(List<String> names, List<String> scores){
        this.names = names;
        this.scores = scores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_element_list, null, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(names.get(position));
        holder.score.setText(scores.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }
}
