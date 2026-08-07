package group17_b07summer2026;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.os.Handler;

/**
 * Displays comments in a RecyclerView and handles comment actions.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> comments;
    private final OnDeleteClickListener listener;
    /**
     * Handler used to refresh the displayed timestamps every minute.
     */
    private final Handler timestampHandler = new Handler(Looper.getMainLooper());
    /**
     * Runnable that refreshes the comment list every minute so that relative
     */

    private final Runnable timestampUpdater = new Runnable() {
        @Override
        public void run() {
            notifyDataSetChanged();
            timestampHandler.postDelayed(this, 60_000L);
        }
    };
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
        timestampHandler.post(timestampUpdater);
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
        holder.textViewTimestamp.setText(getTime(comment.getTimestamp()));

        User currentUser = SessionManager.getInstance().getCurrentUser();
        boolean canDelete = currentUser != null && (currentUser.isAdmin() || currentUser.getUid().equals(comment.getUserId()));
        holder.buttonDeleteComment.setVisibility(canDelete ? View.VISIBLE : View.GONE);

        holder.buttonDeleteComment.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(comment);
            }
        });
    }
    /**
     * Converts a timestamp into a shortened relative time, e.g:"just now", "5m", "2h", "3d", and "1w"
     * @param timestamp the comment creation time in milliseconds
     * @return the shortened relative time
     */
    private String getTime(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        long minute = 60L * 1000L;
        long hour = 60L * minute;
        long day = 24L * hour;
        long week = 7L * day;
        if (diff < minute){
            return "just now";
        }
        if (diff < hour){
            return diff / minute + "m";
        }
        if (diff < day) {
            return diff / hour + "h";
        }
        if (diff < week) {
            return diff / day + "d";
        }
        return diff / week + "w";
    }

    /**
     * Stops the automatic timestamp refreshes which is called
     * when the fragment's view is destroyed to prevent
     * the adapter from continuing to schedule updates.
     */
    public void stopTimestampUpdates() {
        timestampHandler.removeCallbacks(timestampUpdater);
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
        private TextView textViewTimestamp;
        public CommentViewHolder(@NonNull View commentView) {
            super(commentView);
            textViewUsername = commentView.findViewById(R.id.textViewUsername);
            textViewComment = commentView.findViewById(R.id.textViewComment);
            buttonDeleteComment = commentView.findViewById(R.id.buttonDeleteComment);
            textViewTimestamp = commentView.findViewById(R.id.textViewTimestamp);
        }
    }
}
