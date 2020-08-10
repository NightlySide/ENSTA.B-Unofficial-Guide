package io.github.nightlyside.enstaunofficialguide.recyclerview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import io.github.nightlyside.enstaunofficialguide.data_structure.News;
import io.github.nightlyside.enstaunofficialguide.R;

public class NewsViewHolder extends RecyclerView.ViewHolder {

    private TextView title;
    private TextView date;
    private TextView author;
    private TextView text;
    private ImageView imageView;

    // itemview = 1 card
    public NewsViewHolder(View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.news_card_title);
        date = (TextView) itemView.findViewById(R.id.news_card_date);
        author = (TextView) itemView.findViewById(R.id.news_card_author);
        text = (TextView) itemView.findViewById(R.id.news_card_text);
        imageView = (ImageView) itemView.findViewById(R.id.news_card_image);
    }

    public void bind(News news) {
        title.setText(news.title);
        date.setText(news.date);
        author.setText(news.author);
        text.setText(news.text);
        //Picasso.with(imageView.getContext()).load(myObject.getImageUrl()).centerCrop().fit().into(imageView);
    }
}
