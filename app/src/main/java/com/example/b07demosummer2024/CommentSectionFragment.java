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

import java.util.ArrayList;
import java.util.List;

public class CommentSectionFragment extends Fragment {

    private String lot;
    private RecyclerView recyclerView;
    private TextView noCommentsText;
    private EditText commentInput;
    private CommentAdapter adapter;
    public CommentSectionFragment() {
    }
    public CommentSectionFragment(String lot) {
        this.lot = lot;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.comment_section, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        /* TODO: Retrieve userid and username*/
        String userId = "UserID";
        String username = "Username";
        Comment comment = new Comment(userId, username, lot, body);
        CommentDatabaseWriter writer = new CommentDatabaseWriter();

        writer.addToDatabase(comment, new WriteCallback() {
            @Override
            public void onSuccess() {
                if (!isAdded()) {
                    return;
                }
                commentInput.setText("");
                loadComments();
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded()) {
                    return;
                }
                Toast.makeText(requireContext(), "Failed to post comment", Toast.LENGTH_SHORT).show();
            }
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