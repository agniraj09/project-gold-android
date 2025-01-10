package com.business.project.gold.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.business.project.gold.R;
import com.business.project.gold.domain.ArtifactDetailsDTO;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

public class ArtifactEditAdapter extends RecyclerView.Adapter<ArtifactEditAdapter.ViewHolder> {
    private final List<ArtifactDetailsDTO> artifacts;

    public ArtifactEditAdapter(List<ArtifactDetailsDTO> artifacts) {
        this.artifacts = artifacts;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText artifactName;
        SwitchMaterial availabilitySwitch;
        View removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            artifactName = itemView.findViewById(R.id.artifact_name_input);
            availabilitySwitch = itemView.findViewById(R.id.availability_switch);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_edit_artifact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArtifactDetailsDTO artifact = artifacts.get(position);
        holder.artifactName.setText(artifact.getArtifact());
        holder.availabilitySwitch.setChecked(("Available".equalsIgnoreCase(artifact.getStatus())));

        // Update artifact details on user interaction
        holder.artifactName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed during text changes
            }

            @Override
            public void afterTextChanged(Editable s) {
                artifact.setArtifact(s.toString());
            }
        });

        holder.availabilitySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> artifact.setStatus(isChecked ? "Available" : "Unavailable"));

        holder.removeButton.setOnClickListener(v -> {
            int positionToRemove = holder.getBindingAdapterPosition();
            removeArtifact(positionToRemove);
        });
    }

    public void removeArtifact(int position) {
        if (position != RecyclerView.NO_POSITION) {
            artifacts.remove(position);
            notifyItemRemoved(position);
            if (position < artifacts.size()) {
                notifyItemRangeChanged(position, artifacts.size() - position);
            }
        }
    }


    public void addArtifact(ArtifactDetailsDTO artifact) {
        artifacts.add(artifact);  // Add the new artifact
        notifyItemInserted(artifacts.size() - 1);  // Notify RecyclerView about the new item
    }


    @Override
    public int getItemCount() {
        return artifacts.size();
    }

    public List<ArtifactDetailsDTO> getArtifacts() {
        return artifacts;
    }
}
