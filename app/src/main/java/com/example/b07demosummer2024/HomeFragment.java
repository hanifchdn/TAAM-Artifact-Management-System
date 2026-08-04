package com.example.b07demosummer2024;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ImageButton searchButton;
    private ImageButton searchUpdateButton;
    private EditText searchInput;
    private LinearLayout searchContainer;
    private RecyclerView artifactRecyclerView;
    private ArtifactAdapter artifactAdapter;
    private final List<Artifact> artifactList = new ArrayList<>();
    private TextView noArtifacts;
    private LinearLayout artifactLoadingLayout;
    private ImageButton savedArtifactsButton;
    private boolean isSavedArtifactsSelected = false;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.activity_home_fragment,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        // run request to update profile in case a change occured
        super.onViewCreated(view, savedInstanceState);
        SessionModel sessionModel = new SessionModel();
        sessionModel.fetchUserProfile(SessionManager.getInstance().getCurrentUser().getUid(), new SessionContract.Model.ProfileCallback() {
            @Override
            public void onProfileLoaded(User newUser) {
                SessionManager.getInstance().setCurrentUser(newUser);
            }

            // Ignore an error, as user does not need to know if a routine refresh failed
            @Override
            public void onProfileError(String message) {

            }
        });

        /**
         * Configures RecyclerView and its adapter to display two artifacts on each row.
         */
        artifactRecyclerView = view.findViewById(R.id.artifactRecyclerView);
        artifactRecyclerView.setLayoutManager(
                new GridLayoutManager(requireContext(), 2)
        );

        /**
         * Binds variables from xml layout to java
         */
        searchButton = view.findViewById(R.id.searchButton);
        searchContainer = view.findViewById(R.id.searchContainer);
        searchInput = view.findViewById(R.id.searchInput);
        searchUpdateButton = view.findViewById(R.id.searchUpdateButton);
        noArtifacts = view.findViewById(R.id.noArtifacts);
        artifactLoadingLayout = view.findViewById(R.id.artifactLoadingLayout);
        savedArtifactsButton = view.findViewById(R.id.savedArtifactsButton);

        savedArtifactsButton.setOnClickListener(v -> {
            isSavedArtifactsSelected = !isSavedArtifactsSelected;
            savedArtifactsButton.setImageResource(
                    isSavedArtifactsSelected ? R.drawable.bookmark : R.drawable.bookmark_hollow
            );
            refreshArtifactList();
        });

        /**
         * Displays searchContainer when searchButton is clicked
         */
        searchButton.setOnClickListener(v -> {
            if (searchContainer.getVisibility() == View.GONE) {
                searchContainer.setVisibility(View.VISIBLE);
                searchInput.requestFocus();
            } else {
                searchContainer.setVisibility(View.GONE);
            }
        });

        /**
         * Searches for artifacts using the text entered in the search input field.
         *
         * If the input is empty, all artifacts are retrieved from the database.
         * Otherwise, artifacts whose fields contain the entered substring are retrieved.
         *
         * The callback first checks whether this fragment is still attached to its
         * activity before accessing the fragment context or updating the user interface.
         */
        searchUpdateButton.setOnClickListener(v -> {
            searchForArtifacts();
        });

        /**
         * Retrieve artifacts from firebase and displays them
         */
        showLoadingIndicator();

        ArtifactDatabaseReader reader = new ArtifactDatabaseReader();
        reader.getArtifactList(artifacts -> {
            if (!isAdded()) {
                return;
            }

            hideLoadingIndicator();
            if (!reader.handleArtifactListError(artifacts)) {
                Toast.makeText(requireContext(), "No artifacts found", Toast.LENGTH_SHORT).show();
                return;
            }

            artifactList.clear();
            artifactList.addAll(artifacts);
            artifactAdapter.notifyDataSetChanged();
            updateEmptyState();
        });



        ImageButton logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logout());

        /**
         * Navigates to the add-artifact page when the add artifact button is tapped.
         */
        ImageButton addArtifactButton =
                view.findViewById(R.id.addArtifactButton);

        addArtifactButton.setOnClickListener(
                v -> loadFragment(new AddItemFragment())
        );

        /**
         *  Opens Expanded Artifact View Fragment
         */
        artifactAdapter = new ArtifactAdapter(artifactList, artifact -> {
            ExpandedArtifactFragment detailFragment = ExpandedArtifactFragment.newInstance(artifact);
            loadFragment(detailFragment);
        });
        artifactRecyclerView.setAdapter(artifactAdapter);
    }

    /**
     * Shows the loading indicator and hides both the RecyclerView
     * and the empty-state message.
     */
    private void showLoadingIndicator() {
        artifactLoadingLayout.setVisibility(View.VISIBLE);
        artifactRecyclerView.setVisibility(View.GONE);
        noArtifacts.setVisibility(View.GONE);
    }

    /**
     * Refresh the Home Page when it is updated
     */
    private void refreshArtifactList() {
        searchForArtifacts();
    }

    /**
     * Searches for artifacts based on search input and user filter
     */
    private void searchForArtifacts() {
        String input = searchInput.getText().toString().trim();
        showLoadingIndicator();

        ArtifactDatabaseReader reader = new ArtifactDatabaseReader();

        ArtifactDatabaseReader.GetArtifactListReaderCallback callback =
                new ArtifactDatabaseReader.GetArtifactListReaderCallback() {
                    @Override
                    public void getArtifactListCallback(List<Artifact> artifacts) {
                        if (!isAdded()) {
                            return;
                        }

                        hideLoadingIndicator();
                        artifactList.clear();
                        if(artifacts != null){
                            if (isSavedArtifactsSelected) {
                                for (Artifact artifact : artifacts) {
                                    if (SessionManager.getInstance().getCurrentUser().containsSavedArtifact(artifact.getLOT())) {
                                        artifactList.add(artifact);
                                    }
                                }
                            }else {
                                artifactList.addAll(artifacts);
                            }
                        }
                        artifactAdapter.notifyDataSetChanged();
                        updateEmptyState();
                        boolean isValid = reader.handleArtifactListError(artifacts);
                        if(!isValid){
                            Toast.makeText(requireContext(), "No artifacts found", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        if (input.isEmpty()) {
            reader.getArtifactList(callback);
        } else {
            reader.getArtifactListBySubstring(input, callback);
        }
    }

    /**
     * Hides loading indicator.
     */
    private void hideLoadingIndicator() {
        artifactLoadingLayout.setVisibility(View.GONE);
    }

    /**
     * Shows the "No Artifacts Exist" message and hides the RecyclerView when there
     * are no artifacts to display. When artifacts exist, this message is hidden.
     */
    private void updateEmptyState() {
        boolean isEmpty = artifactList.isEmpty();

        noArtifacts.setVisibility(
                isEmpty ? View.VISIBLE : View.GONE
        );

        artifactRecyclerView.setVisibility(
                isEmpty ? View.GONE : View.VISIBLE
        );
    }

    /**
     * Navigate from home page to login page when clicking the logout button.
     */
    private void logout() {
        new SessionModel().logOut();

        getParentFragmentManager().popBackStack(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
        );

        FragmentTransaction transaction =
                getParentFragmentManager().beginTransaction();

        transaction.replace(
                R.id.fragment_container,
                new LoginFragment()
        );

        transaction.commit();
    } // Note (clear later): The firebase is backend

    private void loadFragment(Fragment fragment) {
        FragmentManager temp = getParentFragmentManager();
        FragmentTransaction transaction = temp.beginTransaction();

        transaction.hide(this);
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}