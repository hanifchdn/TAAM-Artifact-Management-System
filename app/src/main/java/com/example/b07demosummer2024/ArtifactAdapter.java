package com.example.b07demosummer2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.bumptech.glide.Glide;

public class ArtifactAdapter extends RecyclerView.Adapter<ArtifactAdapter.ArtifactViewHolder> {
    private final List<Artifact> artifactList;
    private final OnArtifactClickListener listener;

    /**
     * Callback interface for handling clicks on an artifact card.
     */
    public interface OnArtifactClickListener {
        void onArtifactClick(Artifact artifact);
    }


    /**
     * Creates an adapter that displays the given list of artifacts.
     *
     * @param artifactList artifacts to display
     */
    public ArtifactAdapter(List<Artifact> artifactList, OnArtifactClickListener listener) {
        this.artifactList = artifactList;
        this.listener = listener;
    }

    /**
     * Creates a ViewHolder for one artifact.
     */
    @NonNull
    @Override
    public ArtifactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artifact_item_layout, parent, false);
        return new ArtifactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtifactViewHolder holder, int position) {
        Artifact artifact = artifactList.get(position);
        holder.textViewName.setText(artifact.getName());
        holder.textViewDescription.setText(artifact.getDescription());
        holder.textViewCategory.setText(artifact.getCategory());
        holder.textViewMaterial.setText(artifact.getMaterial());
        holder.textViewDynasty.setText(artifact.getDynasty());
        Glide.with(holder.itemView.getContext())
                .load(artifact.getImageUrl())
                .placeholder(R.drawable.artifact_placeholder)
                .error(R.drawable.artifact_placeholder)
                .fallback(R.drawable.artifact_placeholder)
                .fitCenter()
                .into(holder.imageViewArtifact);
        holder.itemView.setOnClickListener(v -> listener.onArtifactClick(artifact));
    }

    /**
     * Returns the number of artifacts currently displayed.
     */
    @Override
    public int getItemCount() {
        return artifactList.size();
    }

    public static class ArtifactViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewArtifact;
        private TextView textViewName;
        private TextView textViewDescription;
        private TextView textViewCategory;
        private TextView textViewMaterial;
        private TextView textViewDynasty;
        public ArtifactViewHolder(@NonNull View artifactView) {
            super(artifactView);
            imageViewArtifact = artifactView.findViewById(R.id.imageViewArtifact);
            textViewName = artifactView.findViewById(R.id.textViewName);
            textViewDescription = artifactView.findViewById(R.id.textViewDescription);
            textViewCategory = artifactView.findViewById(R.id.textViewCategory);
            textViewMaterial = artifactView.findViewById(R.id.textViewMaterial);
            textViewDynasty = artifactView.findViewById(R.id.textViewDynasty);
        }
    }
}
