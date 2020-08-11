package io.github.nightlyside.enstaunofficialguide.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.nightlyside.enstaunofficialguide.data_structure.News;
import io.github.nightlyside.enstaunofficialguide.R;

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {

    private List<News> newsList;

    public NewsAdapter(List<News> list) {
        this.newsList = list;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        News newsobj = newsList.get(position);
        holder.bind(newsobj);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
}
