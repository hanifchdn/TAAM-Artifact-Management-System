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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import androidx.fragment.app.FragmentManager;

public class HomeFragment extends Fragment {
    private ImageButton searchButton;
    private ImageButton searchUpdateButton;
    private EditText searchInput;
    private LinearLayout searchContainer;
    private RecyclerView artifactRecyclerView;
    private ArtifactAdapter artifactAdapter;
    private final List<Artifact> artifactList = new ArrayList<>();
    private TextView noArtifacts;

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
        super.onViewCreated(view, savedInstanceState);

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
            String input = searchInput.getText().toString().trim();
            ArtifactDatabaseReader reader = new ArtifactDatabaseReader();

            ArtifactDatabaseReader.GetArtifactListReaderCallback callback =
                    new ArtifactDatabaseReader.GetArtifactListReaderCallback() {
                        @Override
                        public void getArtifactListCallback(List<Artifact> artifacts) {
                            if (!isAdded()) {
                                return;
                            }

                            if (!reader.handleArtifactListError(
                                    artifacts,
                                    requireContext()
                            )) {
                                return;
                            }

                            artifactList.clear();
                            artifactList.addAll(artifacts);
                            artifactAdapter.notifyDataSetChanged();
                            updateEmptyState();
                        }
                    };

            if (input.isEmpty()) {
                reader.getArtifactList(callback);
            } else {
                reader.getArtifactListBySubstring(input, callback);
            }
        });

        /**
         * Retrieve artifacts from firebase and displays them
         */
        ArtifactDatabaseReader reader = new ArtifactDatabaseReader();
        reader.getArtifactList(artifacts -> {
            if (!isAdded()) {
                return;
            }

            if (!reader.handleArtifactListError(
                    artifacts,
                    requireContext()
            )) {
                return;
            }

            artifactList.clear();
            artifactList.addAll(artifacts);
            artifactAdapter.notifyDataSetChanged();
            updateEmptyState();
        });

        /**
         * Binds the no-artifacts message and logout button from the layout.
         */
        noArtifacts = view.findViewById(R.id.noArtifacts);

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