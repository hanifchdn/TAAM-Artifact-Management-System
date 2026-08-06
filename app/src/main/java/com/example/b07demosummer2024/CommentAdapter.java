package com.example.b07demosummer2024;

import android.text.format.DateUtils;
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
     * Create an adapter that displays the given list of comments
     * @param comments comments to display
     * @param listener the callback used when the delete button is clicked
     */
    public CommentAdapter(List<Comment> comments, OnDeleteClickListener listener) {
        this.comments = comments;
        this.listener = listener;
    }

    /**
     * Replaced the currently displayed comments
     * @param newComments the new list of comments to display
     */
    public void updateComment(List<Comment> newComments){
        this.comments = newComments;
        notifyDataSetChanged();
    }
    /**
     * Creates a ViewHolder for one comment
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return a ViewHolder for one comment
     */
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_layout, parent, false);
        return new CommentViewHolder(view);
    }

    /**
     * Binds the comment at the specified position to the provided ViewHolder.
     * Updates the displayed username, comment body, timestamp, and delete button
     * click listener.
     *
     * @param holder the ViewHolder that displays the comment data
     * @param position the position of the comment in the adapter's data set
     */
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

    /**
     * ViewHolder that stores and manage the views used to display one comment
     */
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
