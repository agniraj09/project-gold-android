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
import com.business.project.gold.domain.ArtifactDetailsDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ArtifactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int GROUP_TYPE = 0;
    private static final int ARTIFACT_TYPE = 1;

    private final List<Object> itemList;

    public ArtifactAdapter(List<Object> itemList) {
        this.itemList = itemList;
    }

    @Override
    public int getItemViewType(int position) {
        if (itemList.get(position) instanceof String) {
            return GROUP_TYPE;  // Group name
        } else {
            return ARTIFACT_TYPE;  // Artifact
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == GROUP_TYPE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.artifact_group_header, parent, false);
            return new GroupViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_artifact, parent, false);
            return new ArtifactViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GroupViewHolder) {
            String groupName = (String) itemList.get(position);
            ((GroupViewHolder) holder).groupName.setText(groupName);
        } else {
            ArtifactDetailsDTO item = (ArtifactDetailsDTO) itemList.get(position);
            ArtifactViewHolder artifactViewHolder = (ArtifactViewHolder) holder;
            artifactViewHolder.artifactName.setText(item.getArtifact());
            artifactViewHolder.status.setText(item.getStatus());

            // Get the background drawable and change its color
            GradientDrawable background = (GradientDrawable) artifactViewHolder.status.getBackground();
            if ("Available".equalsIgnoreCase(item.getStatus())) {
                background.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
            } else {
                background.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;

        GroupViewHolder(View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_name);
        }
    }

    static class ArtifactViewHolder extends RecyclerView.ViewHolder {
        TextView artifactName, status;

        ArtifactViewHolder(View itemView) {
            super(itemView);
            artifactName = itemView.findViewById(R.id.artifact_name);
            status = itemView.findViewById(R.id.status);
        }
    }

    // Grouping artifacts by their group name
    public static List<Object> prepareData(List<ArtifactDetailsDTO> artifactDetailsDTOs) {
        Map<String, List<ArtifactDetailsDTO>> groupedData = new LinkedHashMap<>();

        artifactDetailsDTOs.sort((o1, o2) -> o1.getArtifactGroup().compareToIgnoreCase(o2.getArtifactGroup()));

        // Grouping the artifacts by artifactGroup
        for (ArtifactDetailsDTO item : artifactDetailsDTOs) {
            String groupName = item.getArtifactGroup();
            if (!groupedData.containsKey(groupName)) {
                groupedData.put(groupName, new ArrayList<>());
            }
            groupedData.get(groupName).add(item);
        }

        // Creating the final list for RecyclerView
        List<Object> finalList = new ArrayList<>();
        for (Map.Entry<String, List<ArtifactDetailsDTO>> entry : groupedData.entrySet()) {
            finalList.add(entry.getKey());  // Add group name
            finalList.addAll(entry.getValue());  // Add all artifacts in the group
        }

        return finalList;
    }
}
