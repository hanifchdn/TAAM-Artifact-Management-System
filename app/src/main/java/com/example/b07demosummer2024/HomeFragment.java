package com.example.b07demosummer2024;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

//        if (testDisplay) {
//            displayedItems.add(new Item("1", "Dune", "Frank Herbert", "Sci-Fi", "A desert planet, a prophecy, a lot of sand."));
//            displayedItems.add(new Item("2", "1984", "George Orwell", "Dystopian", "Big Brother is watching."));
//            displayedItems.add(new Item("3", "The Hobbit", "J.R.R. Tolkien", "Fantasy", "There and back again."));
//        }

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
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
