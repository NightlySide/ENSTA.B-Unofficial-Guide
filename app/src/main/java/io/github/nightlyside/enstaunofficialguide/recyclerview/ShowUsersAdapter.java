package io.github.nightlyside.enstaunofficialguide.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import java.util.Comparator;
import java.util.List;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.data_structure.User;
import io.github.nightlyside.enstaunofficialguide.fragments.ShowAndEditCollocsFragment;
import io.github.nightlyside.enstaunofficialguide.fragments.ShowAndEditUsersFragment;

public class ShowUsersAdapter extends RecyclerView.Adapter<ShowUsersHolder> {

    private final SortedList.Callback<User> mCallback = new SortedList.Callback<User>() {

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public int compare(User a, User b) {
            final Comparator<User> ALPHABETICAL_COMPARATOR = new Comparator<User>() {
                @Override
                public int compare(User a, User b) {
                    return a.display_name.compareTo(b.display_name);
                }
            };

            return ALPHABETICAL_COMPARATOR.compare(a, b);
        }

        @Override
        public boolean areContentsTheSame(User oldItem, User newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(User item1, User item2) {
            return item1.id == item2.id;
        }
    };
    final SortedList<User> userList = new SortedList<>(User.class, mCallback);

    private ShowAndEditUsersFragment parent;

    public ShowUsersAdapter(ShowAndEditUsersFragment parent) {this.parent = parent;}

    @Override
    public ShowUsersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_editable, parent, false);
        return new ShowUsersHolder(this.parent, view);
    }

    @Override
    public void onBindViewHolder(ShowUsersHolder holder, int position) {
        User userobj = userList.get(position);
        holder.bind(userobj);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void add(User model) {
        userList.add(model);
    }

    public void remove(User model) {
        userList.remove(model);
    }

    public void add(List<User> models) {
        userList.addAll(models);
    }

    public void remove(List<User> models) {
        userList.beginBatchedUpdates();
        for (User model : models) {
            userList.remove(model);
        }
        userList.endBatchedUpdates();
    }

    public void clear() {
        userList.beginBatchedUpdates();
        userList.clear();
        userList.endBatchedUpdates();
    }

    public void replaceAll(List<User> models) {
        userList.beginBatchedUpdates();
        for (int i = userList.size() - 1; i >= 0; i--) {
            final User model = userList.get(i);
            if (!models.contains(model)) {
                userList.remove(model);
            }
        }
        userList.addAll(models);
        userList.endBatchedUpdates();
    }
}