package com.business.project.gold.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.business.project.gold.R;
import com.business.project.gold.adapter.ArtifactAdapter;
import com.business.project.gold.config.RetrofitConfig;
import com.business.project.gold.domain.ArtifactDetailsDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArtifactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections_details);

        // Set up the toolbar as the action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Collections");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.artifact_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch artifacts data from the API
        Call<List<ArtifactDetailsDTO>> call = RetrofitConfig.getApiService().getAllArtifacts();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ArtifactDetailsDTO>> call, @NonNull Response<List<ArtifactDetailsDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ArtifactDetailsDTO> artifacts = response.body();
                    if (!artifacts.isEmpty()) {
                        // Group the artifacts by their artifactGroup
                        List<Object> groupedArtifacts = ArtifactAdapter.prepareData(artifacts);

                        // Set up the adapter with the grouped data
                        adapter = new ArtifactAdapter(groupedArtifacts);
                        recyclerView.setAdapter(adapter);
                    } else {
                        showNoOrdersMessage();
                    }
                } else {
                    showNoOrdersMessage();
                    Log.e("getAllArtifacts", "Failed to load data");
                    Toast.makeText(CollectionDetailsActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ArtifactDetailsDTO>> call, @NonNull Throwable t) {
                showNoOrdersMessage();
                Log.e("getAllArtifacts", "Failed to load data");
                Toast.makeText(CollectionDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showNoOrdersMessage() {
        findViewById(R.id.recyclerView).setVisibility(View.GONE);
        findViewById(R.id.noOrdersMessage).setVisibility(View.VISIBLE);
    }
}
