package group17_b07summer2026;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    /**
     * Creates and returns the HomePage view.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Inflated homepage view
     */
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

    /**
     * Initializes UI components and click listeners.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        // refresh user data
        SessionModel sessionModel = new SessionModel();
        sessionModel.fetchUserProfile(SessionManager.getInstance().getCurrentUser().getUid(), new SessionContract.Model.ProfileCallback() {
                    @Override
                    public void onProfileLoaded(User user) {
                        SessionManager.getInstance().setCurrentUser(user);
                    }

                    @Override
                    public void onProfileError(String message) {

                    }
                });

        User curUser = SessionManager.getInstance().getCurrentUser();
        boolean isAdmin = curUser.isAdmin();

        // Configures RecyclerView and its adapter to display two artifacts on each row.
        artifactRecyclerView = view.findViewById(R.id.artifactRecyclerView);
        artifactRecyclerView.setLayoutManager(
                new GridLayoutManager(requireContext(), 2)
        );

        // Binds variables from XML layout to java.
        searchButton = view.findViewById(R.id.searchButton);
        searchContainer = view.findViewById(R.id.searchContainer);
        searchInput = view.findViewById(R.id.searchInput);
        searchUpdateButton = view.findViewById(R.id.searchUpdateButton);
        noArtifacts = view.findViewById(R.id.noArtifacts);
        artifactLoadingLayout = view.findViewById(R.id.artifactLoadingLayout);
        savedArtifactsButton = view.findViewById(R.id.savedArtifactsButton);

        //Configures savedArtifactsButton
        savedArtifactsButton.setOnClickListener(v -> {
            isSavedArtifactsSelected = !isSavedArtifactsSelected;
            savedArtifactsButton.setImageResource(
                    isSavedArtifactsSelected ? R.drawable.bookmark : R.drawable.bookmark_hollow
            );
            refreshArtifactList();
        });

        // Displays searchContainer when searchButton is clicked
        searchButton.setOnClickListener(v -> {
            if (searchContainer.getVisibility() == View.GONE) {
                searchContainer.setVisibility(View.VISIBLE);
                searchInput.requestFocus();
            } else {
                searchContainer.setVisibility(View.GONE);
            }
        });

        // Search for artifact on search button click
        searchUpdateButton.setOnClickListener(v -> {
            searchForArtifacts();
        });

        // Retrieve artifacts from firebase and displays them
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


        //Configures logoutButton
        ImageButton logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logout());

        // Navigates to the add-artifact page when the add artifact button is tapped.
        ImageButton addArtifactButton =
                view.findViewById(R.id.addArtifactButton);


        addArtifactButton.setVisibility(
                isAdmin ? View.VISIBLE : View.GONE
        );

        addArtifactButton.setOnClickListener(
                v -> loadFragment(new AddArtifactFragment())
        );

        // Opens Expanded Artifact View Fragment
        artifactAdapter = new ArtifactAdapter(artifactList, artifact -> {
            ExpandedArtifactFragment detailFragment = ExpandedArtifactFragment.newInstance(artifact);
            loadFragment(detailFragment);
        });
        artifactRecyclerView.setAdapter(artifactAdapter);
    }

    /**
     * On resume, navigates to home fragment from expanded artifact view
     * refreshes the page in case of update
     */
    @Override
    public void onResume() {
        super.onResume();
        savedArtifactsButton.setImageResource(
                isSavedArtifactsSelected ? R.drawable.bookmark : R.drawable.bookmark_hollow
        );

        // refresh user data
        SessionModel sessionModel = new SessionModel();
        sessionModel.fetchUserProfile(SessionManager.getInstance().getCurrentUser().getUid(), new SessionContract.Model.ProfileCallback() {
            @Override
            public void onProfileLoaded(User user) {
                SessionManager.getInstance().setCurrentUser(user);
            }

            @Override
            public void onProfileError(String message) {

            }
        });

        refreshArtifactList();
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
     * Searches for artifacts using the text entered in the search input field.
     * If the input is empty, all artifacts are retrieved from the database.
     * Otherwise, artifacts whose fields contain the entered substring are retrieved.
     * The callback first checks whether this fragment is still attached to its
     * activity before accessing the fragment context or updating the user interface.
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
    }

    /**
     * Replaces the current fragment and add transaction to the back stack.
     * @param fragment Fragment to display.
     */
    private void loadFragment(Fragment fragment) {
        FragmentManager temp = getParentFragmentManager();
        FragmentTransaction transaction = temp.beginTransaction();

        transaction.hide(this);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}