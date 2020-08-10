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

public class ShowCollocsAdapter extends RecyclerView.Adapter<ShowCollocsHolder> {

    private final SortedList.Callback<Colloc> mCallback = new SortedList.Callback<Colloc>() {

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
        public int compare(Colloc a, Colloc b) {
            final Comparator<Colloc> ALPHABETICAL_COMPARATOR = new Comparator<Colloc>() {
                @Override
                public int compare(Colloc a, Colloc b) {
                    return a.name.compareTo(b.name);
                }
            };

            return ALPHABETICAL_COMPARATOR.compare(a, b);
        }

        @Override
        public boolean areContentsTheSame(Colloc oldItem, Colloc newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Colloc item1, Colloc item2) {
            return item1.id == item2.id;
        }
    };
    final SortedList<Colloc> collocList = new SortedList<>(Colloc.class, mCallback);

    public ShowCollocsAdapter() { }

    @Override
    public ShowCollocsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.editable_card, parent, false);
        return new ShowCollocsHolder(view);
    }

    @Override
    public void onBindViewHolder(ShowCollocsHolder holder, int position) {
        Colloc colobj = collocList.get(position);
        holder.bind(colobj);
    }

    @Override
    public int getItemCount() {
        return collocList.size();
    }

    public void add(Colloc model) {
        collocList.add(model);
    }

    public void remove(Colloc model) {
        collocList.remove(model);
    }

    public void add(List<Colloc> models) {
        collocList.addAll(models);
    }

    public void remove(List<Colloc> models) {
        collocList.beginBatchedUpdates();
        for (Colloc model : models) {
            collocList.remove(model);
        }
        collocList.endBatchedUpdates();
    }

    public void replaceAll(List<Colloc> models) {
        collocList.beginBatchedUpdates();
        for (int i = collocList.size() - 1; i >= 0; i--) {
            final Colloc model = collocList.get(i);
            if (!models.contains(model)) {
                collocList.remove(model);
            }
        }
        collocList.addAll(models);
        collocList.endBatchedUpdates();
    }
}