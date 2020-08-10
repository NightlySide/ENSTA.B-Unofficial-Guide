package io.github.nightlyside.enstaunofficialguide.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.data_structure.Association;

public class AssoListAdapter extends RecyclerView.Adapter<AssoListViewHolder> {

    private List<Association> assoList;

    public AssoListAdapter(List<Association> list) {
        this.assoList = list;
    }

    @Override
    public AssoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.asso_list_card, parent, false);
        return new AssoListViewHolder(view);
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
}
