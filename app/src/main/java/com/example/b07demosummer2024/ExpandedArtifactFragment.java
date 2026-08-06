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
import androidx.fragment.app.FragmentTransaction;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

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
    private View likeContainer;
    private ImageButton buttonEdit;
    private ImageButton buttonDelete;
    private Artifact artifact;
    private User currentUser;
    private boolean isLikeQueryRunning = false;
    private boolean isSaveQueryRunning = false;
    private boolean isDeleteQueryRunning = false;
    private TextView likeCountText;

    /**
     * Creates a new instance of this fragment containing a given artifact's information.
     */
    public static ExpandedArtifactFragment newInstance(Artifact artifact) {
        ExpandedArtifactFragment fragment = new ExpandedArtifactFragment();
        Bundle args = new Bundle();
        fragment.artifact = artifact;
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

        User curUser = SessionManager.getInstance().getCurrentUser();
        boolean isAdmin = curUser.isAdmin();

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
        likeCountText = view.findViewById(R.id.likeCount);

        buttonReturn = view.findViewById(R.id.buttonReturn);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonComment = view.findViewById(R.id.buttonComment);
        buttonLike = view.findViewById(R.id.buttonLike);
        likeContainer = view.findViewById(R.id.likeContainer);
        buttonEdit = view.findViewById(R.id.buttonEdit);
        buttonDelete = view.findViewById(R.id.buttonDelete);

        buttonEdit.setVisibility(
                isAdmin ? View.VISIBLE : View.GONE
        );

        buttonDelete.setVisibility(
                isAdmin ? View.VISIBLE : View.GONE
        );

        // if, for whatever reason, the current user session hasn't been set, set it
        if(SessionManager.getInstance().getCurrentUser() == null) {
            SessionModel sessionModel = new SessionModel();
            sessionModel.fetchUserProfile(FirebaseAuth.getInstance().getUid(), new SessionContract.Model.ProfileCallback() {
                @Override
                public void onProfileLoaded(User user) {
                    SessionManager.getInstance().setCurrentUser(user);
                }

                @Override
                public void onProfileError(String message) {
                    Toast.makeText(requireContext(), "Error logging in", Toast.LENGTH_LONG).show();
                }
            });
        }
        currentUser = SessionManager.getInstance().getCurrentUser();

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
        lot = artifactLot;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        LikeDatabaseReader likeDatabaseReader = new LikeDatabaseReader();
        likeDatabaseReader.hasUserLiked(userId, artifactLot, new LikeDatabaseReader.HasUserLikedCallback() {
            @Override
            public void onSuccess(boolean hasLiked) {
                if (!isAdded()) return;
                isLiked = hasLiked;
                updateLikeUi(buttonLike, likeCountText);
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        likeDatabaseReader.GetLikesOnArtifact(artifactLot, new LikeDatabaseReader.GetLikesCallback() {
            @Override
            public void onSuccess(int amountOfLikes) {
                if (!isAdded()) return;
                likeCount = amountOfLikes;
                updateLikeUi(buttonLike, likeCountText);
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        likeContainer.setOnClickListener(v -> {
            if (isLikeQueryRunning) {
                return;
            }
            isLikeQueryRunning = true;
            disableButton();

            LikeDatabaseReader newReader = new LikeDatabaseReader();
            newReader.hasUserLiked(userId, artifactLot, new LikeDatabaseReader.HasUserLikedCallback() {
                @Override
                public void onSuccess(boolean currentlyLiked) {
                    if (!isAdded()) return;

                    LikeDatabaseWriter likeDatabaseWriter = new LikeDatabaseWriter();

                    if (currentlyLiked) {
                        likeDatabaseWriter.removeFromDatabase(userId, artifactLot, new WriteCallback() {
                            @Override
                            public void onSuccess() {
                                if (!isAdded()) return;
                                isLiked = false;
                                refreshLikeCount();
                            }

                            @Override
                            public void onFailure(String err) {
                                if (!isAdded()) return;
                                Toast.makeText(requireContext(), "Error unliking artifact", Toast.LENGTH_SHORT).show();
                                isLikeQueryRunning = false;
                                enableButton();
                            }
                        });
                    } else {
                        Like newLike = new Like(userId, artifactLot);
                        likeDatabaseWriter.addToDatabase(newLike, new WriteCallback() {
                            @Override
                            public void onSuccess() {
                                if (!isAdded()) return;
                                isLiked = true;
                                refreshLikeCount();
                            }

                            @Override
                            public void onFailure(String err) {
                                if (!isAdded()) return;
                                Toast.makeText(requireContext(), "Error liking artifact", Toast.LENGTH_SHORT).show();
                                isLikeQueryRunning = false;
                                enableButton();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    isLikeQueryRunning = false;
                    enableButton();
                }
            });
        });

        Glide.with(requireContext())
                .load(args.getString(ARG_IMAGE_URL))
                .placeholder(R.drawable.artifact_placeholder)
                .error(R.drawable.artifact_placeholder)
                .fallback(R.drawable.artifact_placeholder)
                .into(expandedImage);

        boolean alreadySaved = currentUser.containsSavedArtifact(artifactLot);

        buttonSave.setImageResource(
                alreadySaved ? R.drawable.bookmark : R.drawable.bookmark_hollow
        );

        buttonReturn.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        buttonEdit.setOnClickListener(
                v -> loadFragment(new EditArtifactFragment(artifact), true)
        );

        buttonComment.setOnClickListener(v -> {
            CommentSectionFragment commentFragment = CommentSectionFragment.newInstance(artifactLot);
            loadFragment(commentFragment, true);
        });

        buttonSave.setOnClickListener(v -> toggleSavedArtifact(args.getString(ARG_LOT)));

        buttonDelete.setOnClickListener(v -> showDeleteConfirmation(args.getString(ARG_LOT)));

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    /**
                     * Ignore the back button while a
                     * database query is running.
                     */
                    @Override
                    public void handleOnBackPressed() {
                        if (isQueryRunning()) {
                            return;
                        }
                        setEnabled(false);
                        getParentFragmentManager().popBackStack();
                    }
                }
        );
    }
    /**
     * Re-fetches the like count from the database, updates the heart icon and
     * counter, then re-enables the action buttons and clears the in-flight flag.
     */
    private void refreshLikeCount() {
        LikeDatabaseReader likeDatabaseReader = new LikeDatabaseReader();
        likeDatabaseReader.GetLikesOnArtifact(lot, new LikeDatabaseReader.GetLikesCallback() {
            @Override
            public void onSuccess(int amountOfLikes) {
                if (!isAdded()) return;
                likeCount = amountOfLikes;
                updateLikeUi(buttonLike, likeCountText);
                isLikeQueryRunning = false;
                enableButton();
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded()) return;
                updateLikeUi(buttonLike, likeCountText);
                isLikeQueryRunning = false;
                enableButton();
            }
        });
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
     * Returns true if a query is running
     */
    private boolean isQueryRunning() {
        return (isDeleteQueryRunning || isSaveQueryRunning || isLikeQueryRunning);
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
     * Saves or unsaves the currently displayed artifact.
     *
     * @param artifactLot LOT of the artifact to save
     */
    private void toggleSavedArtifact(String artifactLot) {
        isSaveQueryRunning = true;
        disableButton();
        boolean wasSaved = currentUser.containsSavedArtifact(artifactLot);
        if (wasSaved) {
            currentUser.removeSavedArtifact(artifactLot);
        }
        else {
            currentUser.addSavedArtifact(artifactLot);
        }


        UserDatabaseWriter writer = new UserDatabaseWriter();
        writer.updateSavedArtifacts(currentUser, new WriteCallback() {
                    @Override
                    public void onSuccess() {
                        if (!isAdded()) {
                            return;
                        }
                        Toast.makeText(requireContext(), wasSaved ? "Artifact unsaved" : "Artifact saved", Toast.LENGTH_SHORT).show();
                        isSaveQueryRunning = false;
                        enableButton();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        if (wasSaved) {
                            currentUser.addSavedArtifact(artifactLot);
                        }
                        else {
                            currentUser.removeSavedArtifact(artifactLot);
                        }

                        if (!isAdded()) {
                            return;
                        }
                        Toast.makeText(requireContext(), "Failed to update saved artifact", Toast.LENGTH_SHORT).show();
                        isSaveQueryRunning = false;
                        enableButton();
                    }
                }

        );
        buttonSave.setImageResource(
                currentUser.containsSavedArtifact(artifactLot) ? R.drawable.bookmark : R.drawable.bookmark_hollow
        );
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
                loadFragment(new HomeFragment(), false);
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

    /**
     * Loads a new fragment
     *
     * @param fragment fragment to be loaded
     * @param addToBackStack set to true if fragment is added to backstack
     */
    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }
}