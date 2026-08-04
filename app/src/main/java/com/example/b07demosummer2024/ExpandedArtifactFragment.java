package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.bumptech.glide.Glide;

public class ExpandedArtifactFragment extends Fragment {
    private static final String ARG_LOT = "lot";
    private static final String ARG_NAME = "name";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_CATEGORY = "category";
    private static final String ARG_MATERIAL = "material";
    private static final String ARG_DYNASTY = "dynasty";
    private static final String ARG_CULTURAL_ORIGIN = "culturalOrigin";
    private static final String ARG_HEIGHT = "height";
    private static final String ARG_WIDTH = "width";
    private static final String ARG_DEPTH = "depth";
    private static final String ARG_CONDITION = "condition";
    private static final String ARG_CURRENT_LOCATION = "currentLocation";
    private static final String ARG_ACQUISITION_METHOD = "acquisitionMethod";
    private static final String ARG_PROVENANCE = "provenance";
    private static final String ARG_ACCESSION_NUMBER = "accessionNumber";
    private static final String ARG_NOTES = "notes";
    private static final String ARG_IMAGE_URL = "imageUrl";
    private ImageButton buttonReturn;
    private ImageButton buttonSave;
    private ImageButton buttonComment;
    private ImageButton buttonLike;
    private ImageButton buttonEdit;
    private ImageButton buttonDelete;
    private boolean isDeleteQueryRunning = false;

    /**
     * Creates a new instance of this fragment containing a given artifact's information.
     */
    public static ExpandedArtifactFragment newInstance(Artifact artifact) {
        ExpandedArtifactFragment fragment = new ExpandedArtifactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LOT, artifact.getLOT());
        args.putString(ARG_NAME, artifact.getName());
        args.putString(ARG_DESCRIPTION, artifact.getDescription());
        args.putString(ARG_CATEGORY, artifact.getCategory());
        args.putString(ARG_MATERIAL, artifact.getMaterial());
        args.putString(ARG_DYNASTY, artifact.getDynasty());
        args.putString(ARG_CULTURAL_ORIGIN, artifact.getCulturalOrigin());
        args.putString(ARG_HEIGHT, artifact.getHeight());
        args.putString(ARG_WIDTH, artifact.getWidth());
        args.putString(ARG_DEPTH, artifact.getDepth());
        args.putString(ARG_CONDITION, artifact.getCondition());
        args.putString(ARG_CURRENT_LOCATION, artifact.getCurrentLocation());
        args.putString(ARG_ACQUISITION_METHOD, artifact.getAcquisitionMethod());
        args.putString(ARG_PROVENANCE, artifact.getProvenance());
        args.putString(ARG_ACCESSION_NUMBER, artifact.getAccessionNumber());
        args.putString(ARG_NOTES, artifact.getNotes());
        args.putString(ARG_IMAGE_URL, artifact.getImageUrl());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expanded_artifact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView expandedImage = view.findViewById(R.id.expandedImage);
        TextView expandedName = view.findViewById(R.id.expandedName);
        TextView expandedDescription = view.findViewById(R.id.expandedDescription);
        TextView expandedLot = view.findViewById(R.id.expandedLot);
        TextView expandedCategory = view.findViewById(R.id.expandedCategory);
        TextView expandedMaterial = view.findViewById(R.id.expandedMaterial);
        TextView expandedDynasty = view.findViewById(R.id.expandedDynasty);
        TextView expandedCulturalOrigin = view.findViewById(R.id.expandedCulturalOrigin);
        TextView expandedDimensions = view.findViewById(R.id.expandedDimensions);
        TextView expandedCondition = view.findViewById(R.id.expandedCondition);
        TextView expandedCurrentLocation = view.findViewById(R.id.expandedCurrentLocation);
        TextView expandedAcquisitionMethod = view.findViewById(R.id.expandedAcquisitionMethod);
        TextView expandedProvenance = view.findViewById(R.id.expandedProvenance);
        TextView expandedAccessionNumber = view.findViewById(R.id.expandedAccessionNumber);
        TextView expandedNotes = view.findViewById(R.id.expandedNotes);

        buttonReturn = view.findViewById(R.id.buttonReturn);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonComment = view.findViewById(R.id.buttonComment);
        buttonLike = view.findViewById(R.id.buttonLike);
        buttonEdit = view.findViewById(R.id.buttonEdit);
        buttonDelete = view.findViewById(R.id.buttonDelete);

        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        expandedName.setText(Availability(args.getString(ARG_NAME)));
        expandedDescription.setText(Availability(args.getString(ARG_DESCRIPTION)));
        expandedLot.setText("LOT: " + Availability(args.getString(ARG_LOT)));
        expandedCategory.setText("Category: " + Availability(args.getString(ARG_CATEGORY)));
        expandedMaterial.setText("Material: " + Availability(args.getString(ARG_MATERIAL)));
        expandedDynasty.setText("Dynasty/Period: " + Availability(args.getString(ARG_DYNASTY)));
        expandedCulturalOrigin.setText("Cultural Origin: " + Availability(args.getString(ARG_CULTURAL_ORIGIN)));
        expandedDimensions.setText("Dimensions: " + Availability(args.getString(ARG_HEIGHT))
                + " x " + Availability(args.getString(ARG_WIDTH))
                + " x " + Availability(args.getString(ARG_DEPTH)));
        expandedCondition.setText("Condition: " + Availability(args.getString(ARG_CONDITION)));
        expandedCurrentLocation.setText("Current Location: " + Availability(args.getString(ARG_CURRENT_LOCATION)));
        expandedAcquisitionMethod.setText("Acquisition Method: " + Availability(args.getString(ARG_ACQUISITION_METHOD)));
        expandedProvenance.setText("Provenance: " + Availability(args.getString(ARG_PROVENANCE)));
        expandedAccessionNumber.setText("Accession Number: " + Availability(args.getString(ARG_ACCESSION_NUMBER)));
        expandedNotes.setText("Notes: " + Availability(args.getString(ARG_NOTES)));

        Glide.with(requireContext())
                .load(args.getString(ARG_IMAGE_URL))
                .placeholder(R.drawable.artifact_placeholder)
                .error(R.drawable.artifact_placeholder)
                .fallback(R.drawable.artifact_placeholder)
                .into(expandedImage);

        buttonReturn.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        buttonDelete.setOnClickListener(v -> {
            showDeleteConfirmation(args.getString(ARG_LOT));
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(
                        getViewLifecycleOwner(),
                        new OnBackPressedCallback(true) {
                            /**
                             * Ignore the back button while a delete
                             * request is running.
                             */
                            @Override
                            public void handleOnBackPressed() {
                                if (isDeleteQueryRunning) {
                                    return;
                                }
                                setEnabled(false);
                                getParentFragmentManager().popBackStack();
                            }
                        }
                );
    }

    /**
     * Returns the given value, or "N/A" if the value is null or empty.
     */
    private String Availability(String value) {
        return (value == null || value.trim().isEmpty()) ? "N/A" : value;
    }

    /**
     * Shows a warning before deleting an artifact.
     *
     * @param artifactLot LOT of the artifact to delete
     */
    private void showDeleteConfirmation(String artifactLot) {
        new AlertDialog.Builder(requireContext()).setTitle("Delete Artifact")
                .setMessage("Are you sure you want to delete this artifact? "
                                + "This action cannot be undone.")
                .setPositiveButton("Delete",
                        (dialog, which) -> deleteArtifact(artifactLot))
                .setNegativeButton(
                        "Cancel",
                        (dialog, which) -> dialog.dismiss()
                )
                .show();
    }

    /**
     * Deletes the artifact identified by its LOT.
     *
     * @param artifactLot LOT of the artifact to delete
     */
    private void deleteArtifact(String artifactLot) {
        isDeleteQueryRunning = true;
        disableButton();
        ArtifactDatabaseWriter writer = new ArtifactDatabaseWriter();
        writer.deleteFromDatabase(artifactLot, new WriteCallback() {
            @Override
            public void onSuccess() {
                if (!isAdded()) {
                    return;
                }
                Toast.makeText(requireContext(), "Artifact deleted successfully", Toast.LENGTH_SHORT).show();
                loadFragment(new HomeFragment());
            }

            @Override
            public void onFailure(String err) {
                if (!isAdded()) {
                    return;
                }
                Toast.makeText(requireContext(), "Failed to delete Artifact", Toast.LENGTH_SHORT).show();
                isDeleteQueryRunning = false;
                enableButton();
            }
        });
    }

    /**
     * Disables the buttons functionality
     */
    public void disableButton() {
        buttonReturn.setEnabled(false);
        buttonSave.setEnabled(false);
        buttonComment.setEnabled(false);
        buttonLike.setEnabled(false);
        buttonEdit.setEnabled(false);
        buttonDelete.setEnabled(false);
    }

    /**
     * Enables the buttons functionality
     */
    public void enableButton() {
        buttonReturn.setEnabled(true);
        buttonSave.setEnabled(true);
        buttonComment.setEnabled(true);
        buttonLike.setEnabled(true);
        buttonEdit.setEnabled(true);
        buttonDelete.setEnabled(true);
    }

    /**
     * Loads a new fragment without adding to back stack
     *
     * @param fragment fragment to be loaded
     */
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}