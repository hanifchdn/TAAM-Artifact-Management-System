package com.example.b07demosummer2024;

import android.widget.Toast;

import androidx.fragment.app.Fragment;
//TODO: B07-203 B07-205 B07-206

public class ExpandedViewFragment extends Fragment {
    private ArtifactDatabaseReader reader = new ArtifactDatabaseReader();
    private String missingData(String value){
        if(value == null || value.trim().isEmpty()){
            return "N/A";
        }
        return value.trim();
    }
    private void loadArtifact(String lot){
        reader.getItem(lot, new ArtifactDatabaseReader.GetArtifactItemCallback(){
            @Override
            public void onSuccess(Artifact artifact){
                if(artifact == null){
                    showArtifactNotFound();
                    return;
                }
            }
            @Override
            public void onFailure(String e){
                showArtifactLoadError(e);
            }

        });
    }
    private void showArtifactNotFound(){
        Toast.makeText(requireContext(), "Artifact not found.", Toast.LENGTH_SHORT).show();
    }
    private void showArtifactLoadError(String e){
        Toast.makeText(requireContext(), "Failed to load artifact.", Toast.LENGTH_SHORT).show();
    }
}
