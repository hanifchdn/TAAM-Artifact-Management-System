package com.example.b07demosummer2024;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.CompletableFuture;

public class HomeFragment extends Fragment {
    private RecyclerView artifactRecyclerView;
    private ArtifactAdapter artifactAdapter;
    private final List<Artifact> artifactList = new ArrayList<>();
    private TextView noArtifacts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_home_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * Configures RecyclerView and its adapter to display two artifacts on each row.
         */
        artifactRecyclerView = view.findViewById(R.id.artifactRecyclerView);
        artifactRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        artifactAdapter = new ArtifactAdapter(artifactList);
        artifactRecyclerView.setAdapter(artifactAdapter);


        /**
         * Retrieve artifacts from firebase and displays them
         */
        ArtifactDatabaseReader reader = new ArtifactDatabaseReader();
        reader.getArtifactList(artifacts -> {
            if (!reader.handleArtifactListError(artifacts, requireContext())) {
                return;
            }
            artifactList.clear();
            artifactList.addAll(artifacts);
            artifactAdapter.notifyDataSetChanged();
        });

        /**
         * Binds the no-artifacts message and logout button from the layout.
         */
        noArtifacts = view.findViewById(R.id.noArtifacts);
        ImageButton logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logout());
    }

    /**
     * Shows the "No Artifacts Exist" message and hides the RecyclerView when there
     * are no artifacts to display. When artifacts exist, this message is hidden.
     */
    private void updateEmptyState() {
        boolean isEmpty = artifactList.isEmpty();
        noArtifacts.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        artifactRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    /**
     * Navigate from home page to login page when clicking the logout button.
     */
    private void logout() {
        getParentFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new LoginFragment());
        transaction.commit();
    } // Note (clear later): The firebase is backend

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
