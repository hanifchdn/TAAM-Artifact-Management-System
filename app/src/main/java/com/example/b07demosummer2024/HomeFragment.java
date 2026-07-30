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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.artifactRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item("1", "Dune", "Frank Herbert", "Sci-Fi", "A desert planet, a prophecy, a lot of sand."));
        itemList.add(new Item("2", "1984", "George Orwell", "Dystopian", "Big Brother is watching."));
        itemList.add(new Item("3", "The Hobbit", "J.R.R. Tolkien", "Fantasy", "There and back again."));

        ItemAdapter adapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(adapter);

//        Button buttonRecyclerView = view.findViewById(R.id.buttonRecyclerView);
//        Button buttonScroller = view.findViewById(R.id.buttonScroller);
//        Button buttonSpinner = view.findViewById(R.id.buttonSpinner);
//        Button buttonManageItems = view.findViewById(R.id.buttonManageItems);
//
//        buttonRecyclerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadFragment(new RecyclerViewFragment());
//            }
//        });
//
//        buttonScroller.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadFragment(new ScrollerFragment());
//            }
//        });
//
//        buttonSpinner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadFragment(new SpinnerFragment());
//            }
//        });
//
//        buttonManageItems.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) { loadFragment(new ManageItemsFragment());}
//        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
