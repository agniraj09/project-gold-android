package com.business.project.gold.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.business.project.gold.R;

import java.util.List;

public class ArtifactNameAdapter extends RecyclerView.Adapter<ArtifactNameAdapter.ViewHolder> {
    private final List<String> artifacts;

    public ArtifactNameAdapter(List<String> artifacts) {
        this.artifacts = artifacts;
    }

    public List<String> getArtifacts() {
        return artifacts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_artifact_name, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.artifactNameEditText.setText(artifacts.get(position));
        holder.artifactNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                artifacts.set(holder.getAdapterPosition(), s.toString());
            }

            // Other methods can be left empty
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        holder.removeButton.setOnClickListener(v -> {
            int positionToRemove = holder.getAdapterPosition();
            if (positionToRemove != RecyclerView.NO_POSITION) {
                artifacts.remove(positionToRemove);
                // Check if this is the last element, then notifyDataSetChanged
                if (artifacts.isEmpty()) {
                    notifyDataSetChanged();
                } else {
                    notifyItemRemoved(positionToRemove);
                    notifyItemRangeChanged(positionToRemove, getItemCount());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return artifacts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        EditText artifactNameEditText;
        View removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            artifactNameEditText = itemView.findViewById(R.id.artifactNameEditText);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}