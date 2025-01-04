package com.business.project.gold.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.business.project.gold.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ArtifactAvailabilityAdapter extends RecyclerView.Adapter<ArtifactAvailabilityAdapter.ViewHolder> {

    private Map<String, String> artifactData;

    public ArtifactAvailabilityAdapter(Map<String, String> artifactData) {
        this.artifactData = new LinkedHashMap<>(artifactData);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_availability_check_artifact_and_status, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String artifactName = new ArrayList<>(artifactData.keySet()).get(position);
        String status = artifactData.get(artifactName);

        holder.artifactName.setText(artifactName);
        holder.artifactStatus.setText(status);

        // Get the background drawable and change its color
        GradientDrawable background = (GradientDrawable) holder.artifactStatus.getBackground();
        if ("Available".equalsIgnoreCase(status)) {
            background.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
        } else {
            background.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
        }
    }

    @Override
    public int getItemCount() {
        return artifactData.size();
    }

    public void updateData(Map<String, String> newData) {
        this.artifactData.clear();
        this.artifactData.putAll(newData);
        notifyDataSetChanged(); // Notify RecyclerView about the data changes
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView artifactName;
        TextView artifactStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            artifactName = itemView.findViewById(R.id.artifact_name);
            artifactStatus = itemView.findViewById(R.id.artifact_status);
        }
    }
}

