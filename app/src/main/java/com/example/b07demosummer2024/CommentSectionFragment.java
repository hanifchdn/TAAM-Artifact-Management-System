package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CommentSectionFragment extends Fragment {
    private static final String ARG_LOT = "lot";
    private String lot;
    private RecyclerView recyclerView;
    private TextView noCommentsText;
    private EditText commentInput;
    private CommentAdapter adapter;

    /**
     * Creates a comment section for one artifact.
     *
     * @param artifactLot LOT number of the artifact
     * @return configured CommentSectionFragment
     */
    public static CommentSectionFragment newInstance(String artifactLot) {
        CommentSectionFragment fragment = new CommentSectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LOT, artifactLot);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates and returns the view for the comment section fragment
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the inflated comment section
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.comment_section, container, false);
    }

    /**
     * Initialize views, set up listener, and loads initial comments
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        if (args == null) {
            return;
        }

        lot = args.getString(ARG_LOT);
        if (lot == null || lot.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Artifact LOT is missing.", Toast.LENGTH_SHORT).show();
            return;
        }
        ImageButton buttonReturn = view.findViewById(R.id.buttonCommentsReturn);
        recyclerView = view.findViewById(R.id.commentsRecyclerView);
        noCommentsText = view.findViewById(R.id.noComments);
        commentInput = view.findViewById(R.id.commentInput);
        ImageButton buttonPost = view.findViewById(R.id.buttonPostComment);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CommentAdapter(new ArrayList<>(), this::onDeleteComment);
        recyclerView.setAdapter(adapter);
        buttonReturn.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        buttonPost.setOnClickListener(v -> {
            String body = commentInput.getText().toString().trim();
            if (body.isEmpty()) {
                Toast.makeText(requireContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            postComment(body);
        });
        loadComments();
    }

    /**
     * Fetches the comments for current firebase and updates the ui
     */
    private void loadComments() {
        CommentDatabaseReader reader = new CommentDatabaseReader();
        reader.getCommentsForArtifact(lot, new CommentDatabaseReader.GetCommentsForArtifactCallback() {
            @Override
            public void onSuccess(List<Comment> comments) {
                if (!isAdded()) {
                    return;
                }
                if (comments.isEmpty()) {
                    noCommentsText.setVisibility(View.VISIBLE);
                }
                else {
                    noCommentsText.setVisibility(View.GONE);
                }
                adapter.updateComment(comments);
            }

            @Override
            public void onFailure(String error) {
                if (!isAdded()) {
                    return;
                }
                Toast.makeText(requireContext(), "Failed to load comments" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Adds a comment to the firebase database, and reloads the page to show it
     * @param body of the comment to add
     */
    private void postComment(String body) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            Toast.makeText(requireContext(),"You must be logged in to comment", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = firebaseUser.getUid();
        DatabaseReference userReference = FirebaseDatabase.getInstance("https://taam-artifact-storage-system-default-rtdb.firebaseio.com/").getReference("users").child(uid);
        userReference.get().addOnSuccessListener(snapshot -> {
            User currentUser = snapshot.getValue(User.class);
            if(currentUser == null){
                Toast.makeText(requireContext(), "User profile could not be found", Toast.LENGTH_SHORT).show();
                return;
            }
            long timestamp = System.currentTimeMillis();
            Comment comment = new Comment(currentUser.getUid(), currentUser.getUsername(), lot, body, timestamp);
            CommentDatabaseWriter writer = new CommentDatabaseWriter();
            writer.addToDatabase(comment, new WriteCallback(){
                @Override
                public void onSuccess(){
                    if(!isAdded()){
                        return;
                    }
                    commentInput.setText("");
                    loadComments();
                }
                @Override
                public void onFailure(String errorMessage){
                    if(!isAdded()){
                        return;
                    }
                    Toast.makeText(requireContext(), "Failed to post comment", Toast.LENGTH_SHORT).show();
                    return;
                }
            });
        });
    }

    /**
     * Deletes a comment, and reloads the page on successful firebase call
     * @param comment to delete
     */
    private void onDeleteComment(Comment comment) {
        CommentDatabaseWriter writer = new CommentDatabaseWriter();
        writer.deleteFromDatabase(comment, new WriteCallback() {
            @Override
            public void onSuccess() {
                loadComments();
            }

            @Override
            public void onFailure(String err) {
                Toast.makeText(requireContext(), "Error deleting comment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Cleans up view reference when the fragment's view is destroyed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) {
            adapter.stopTimestampUpdates();
        }
        recyclerView = null;
        noCommentsText = null;
        commentInput = null;
        adapter = null;
    }
}