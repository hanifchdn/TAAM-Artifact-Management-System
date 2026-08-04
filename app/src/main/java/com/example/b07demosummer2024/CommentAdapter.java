package com.example.b07demosummer2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> comments;
    private final OnDeleteClickListener listener;

    /**
     * Callback used when the delete button for a comment is clicked.
     */
    public interface OnDeleteClickListener {
        void onDeleteClick(Comment comment);
    }


    /**
     * Creates an adapter that displays the given list of comments.
     *
     * @param comments comments to display
     */
    public CommentAdapter(List<Comment> comments, OnDeleteClickListener listener) {
        this.comments = comments;
        this.listener = listener;
    }

    /**
     * Replaced the currently displayed comments
     * @param newComments
     */
    public void updateComment(List<Comment> newComments){
        this.comments = newComments;
        notifyDataSetChanged();
    }
    /**
     * Creates a ViewHolder for one comment.
     */
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_layout, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.textViewUsername.setText(comment.getUsername());
        holder.textViewComment.setText(comment.getBody());
        holder.buttonDeleteComment.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(comment);
            }
        });
    }

    /**
     * Returns the number of comments currently displayed.
     */
    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewUsername;
        private TextView textViewComment;
        private ImageButton buttonDeleteComment;
        public CommentViewHolder(@NonNull View commentView) {
            super(commentView);
            textViewUsername = commentView.findViewById(R.id.textViewUsername);
            textViewComment = commentView.findViewById(R.id.textViewComment);
            buttonDeleteComment = commentView.findViewById(R.id.buttonDeleteComment);
        }
    }
}
