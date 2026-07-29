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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.concurrent.CompletableFuture;

public class HomeFragment extends Fragment {
    private ImageButton searchButton;
    private ImageButton searchUpdateButton;
    private EditText searchInput;
    private LinearLayout searchContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
            }
            else {
                searchContainer.setVisibility(View.GONE);
            }
        });

        /**
         * TODO: Complete Documentation
         */
        searchUpdateButton.setOnClickListener(v -> {
            //TODO: B07-75
        });

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
