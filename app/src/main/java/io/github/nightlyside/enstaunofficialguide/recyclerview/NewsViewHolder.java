package io.github.nightlyside.enstaunofficialguide.recyclerview;

import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import io.github.nightlyside.enstaunofficialguide.data_structure.News;
import io.github.nightlyside.enstaunofficialguide.R;

public class NewsViewHolder extends RecyclerView.ViewHolder {

    private TextView title;
    private TextView date;
    private TextView asso;
    private TextView author;
    private TextView text;
    private ImageView imageView;

    // itemview = 1 card
    public NewsViewHolder(View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.news_card_title);
        date = itemView.findViewById(R.id.news_card_date);
        author = itemView.findViewById(R.id.news_card_author);
        asso = itemView.findViewById(R.id.news_card_asso_name);
        text = itemView.findViewById(R.id.news_card_text);
        imageView = itemView.findViewById(R.id.news_card_image);
    }

    public void bind(News news) {
        title.setText(news.title);
        date.setText(news.date);
        author.setText(news.author);
        asso.setText(news.asso);
        SpannableString s_text = new SpannableString(news.text);
        Linkify.addLinks(s_text, Linkify.ALL);
        text.setText(s_text);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
