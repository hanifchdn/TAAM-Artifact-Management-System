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
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


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
    private static final String ARG_IS_LIKED = "isLiked";
    private static final String ARG_LIKE_COUNT = "likeCount";

    private String lot;
    private boolean isLiked;
    private int likeCount;

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
        args.putInt(ARG_LIKE_COUNT, 0);
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
        TextView likeCountText = view.findViewById(R.id.likeCount);

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

        likeCount = args.getInt(ARG_LIKE_COUNT, 0);
        isLiked = false;

        String artifactLot = args.getString(ARG_LOT);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        LikeDatabaseReader likeDatabaseReader = new LikeDatabaseReader();
        likeDatabaseReader.hasUserLiked(userId, artifactLot, new LikeDatabaseReader.HasUserLikedCallback() {
            @Override
            public void onSuccess(boolean hasLiked) {
                isLiked = hasLiked;
                updateLikeUi(buttonLike, likeCountText);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        likeDatabaseReader.GetLikesOnArtifact(artifactLot, new LikeDatabaseReader.GetLikesCallback() {
            @Override
            public void onSuccess(int amountOfLikes) {
                likeCount = amountOfLikes;
                updateLikeUi(buttonLike, likeCountText);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        buttonLike.setOnClickListener(v -> {
            LikeDatabaseWriter likeDatabaseWriter = new LikeDatabaseWriter();
            if (isLiked) {
                likeDatabaseWriter.removeFromDatabase(userId, artifactLot, new WriteCallback() {
                    // all good on success
                    @Override
                    public void onSuccess() {

                    }

                    // undo unlike on failure
                    @Override
                    public void onFailure(String err) {
                        isLiked = !isLiked;
                        likeCount += isLiked ? 1 : -1;
                        updateLikeUi(buttonLike, likeCountText);
                    }
                });
            } else {
                Like newLike = new Like(userId, artifactLot);
                likeDatabaseWriter.addToDatabase(newLike, new WriteCallback() {
                    // all good on success
                    @Override
                    public void onSuccess() {

                    }

                    // undo like on failure
                    @Override
                    public void onFailure(String err) {
                        isLiked = !isLiked;
                        likeCount += isLiked ? 1 : -1;
                        updateLikeUi(buttonLike, likeCountText);
                        Toast.makeText(requireContext(), "Error liking artifact", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            isLiked = !isLiked;
            likeCount += isLiked ? 1 : -1;
            updateLikeUi(buttonLike, likeCountText);
        });

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
     * Syncs the heart icon and like counter with the current state.
     */
    private void updateLikeUi(ImageButton buttonLike, TextView likeCountText) {
        buttonLike.setImageResource(
                isLiked ? R.drawable.like : R.drawable.unlike
        );
        likeCountText.setText(String.valueOf(likeCount));
    }

    /**
     * Shows a warning before deleting an artifact.
     *
     * @param artifactLot LOT of the artifact to delete
     */
    private void showDeleteConfirmation(String artifactLot) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Artifact")
                .setMessage(
                        "Are you sure you want to delete this artifact? "
                                + "This action cannot be undone."
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> deleteArtifact(artifactLot)
                )
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

                Toast.makeText(
                        requireContext(),
                        "Artifact deleted successfully",
                        Toast.LENGTH_SHORT
                ).show();

                loadFragment(new HomeFragment());
            }

            @Override
            public void onFailure(String err) {
                if (!isAdded()) {
                    return;
                }

                Toast.makeText(
                        requireContext(),
                        "Failed to delete Artifact",
                        Toast.LENGTH_SHORT
                ).show();

                isDeleteQueryRunning = false;
                enableButton();
            }
        });
    }

    public void disableButton() {
        buttonReturn.setEnabled(false);
        buttonSave.setEnabled(false);
        buttonComment.setEnabled(false);
        buttonLike.setEnabled(false);
        buttonEdit.setEnabled(false);
        buttonDelete.setEnabled(false);
    }

    public void enableButton() {
        buttonReturn.setEnabled(true);
        buttonSave.setEnabled(true);
        buttonComment.setEnabled(true);
        buttonLike.setEnabled(true);
        buttonEdit.setEnabled(true);
        buttonDelete.setEnabled(true);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction =
                getParentFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}