package io.github.nightlyside.enstaunofficialguide.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import java.util.Comparator;
import java.util.List;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.data_structure.Association;
import io.github.nightlyside.enstaunofficialguide.data_structure.Colloc;
import io.github.nightlyside.enstaunofficialguide.fragments.AssoListFragment;

public class AssoListAdapter extends RecyclerView.Adapter<AssoListViewHolder> {

    private final SortedList.Callback<Association> mCallback = new SortedList.Callback<Association>() {

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
        public int compare(Association a, Association b) {
            final Comparator<Association> ALPHABETICAL_COMPARATOR = new Comparator<Association>() {
                @Override
                public int compare(Association a, Association b) {
                    return a.name.compareTo(b.name);
                }
            };

            return ALPHABETICAL_COMPARATOR.compare(a, b);
        }

        @Override
        public boolean areContentsTheSame(Association oldItem, Association newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Association item1, Association item2) {
            return item1.id == item2.id;
        }
    };
    final SortedList<Association> assoList = new SortedList<>(Association.class, mCallback);

    private AssoListFragment parent;
    private boolean isEditing;

    public AssoListAdapter(AssoListFragment parent, boolean isEditing) {
        this.parent = parent;
        this.isEditing = isEditing;
    }

    @Override
    public AssoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_asso_list, parent, false);
        return new AssoListViewHolder(this.parent, isEditing, view);
    }

    @Override
    public void onBindViewHolder(AssoListViewHolder holder, int position) {
        Association assoobj = assoList.get(position);
        holder.bind(assoobj);
    }

    @Override
    public int getItemCount() {
        return assoList.size();
    }

    public void add(Association model) {
        assoList.add(model);
    }

    public void remove(Association model) {
        assoList.remove(model);
    }

    public void add(List<Association> models) {
        assoList.addAll(models);
    }

    public void remove(List<Association> models) {
        assoList.beginBatchedUpdates();
        for (Association model : models) {
            assoList.remove(model);
        }
        assoList.endBatchedUpdates();
    }

    public void clear() {
        assoList.beginBatchedUpdates();
        assoList.clear();
        assoList.endBatchedUpdates();
    }

    public void replaceAll(List<Association> models) {
        assoList.beginBatchedUpdates();
        for (int i = assoList.size() - 1; i >= 0; i--) {
            final Association model = assoList.get(i);
            if (!models.contains(model)) {
                assoList.remove(model);
            }
        }
        assoList.addAll(models);
        assoList.endBatchedUpdates();
    }
}
