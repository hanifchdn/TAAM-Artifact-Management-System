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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.comment_section, container, false);
    }

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
            Comment comment = new Comment(currentUser.getUid(), currentUser.getUsername(), lot, body);
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

    private void onDeleteComment(Comment comment) {
        CommentDatabaseWriter writer = new CommentDatabaseWriter();
        writer.deleteFromDatabase(comment);
        loadComments();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView = null;
        noCommentsText = null;
        commentInput = null;
        adapter = null;
    }
}