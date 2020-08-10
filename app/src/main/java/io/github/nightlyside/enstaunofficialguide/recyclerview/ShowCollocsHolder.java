package io.github.nightlyside.enstaunofficialguide.recyclerview;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.activities.MainActivity;
import io.github.nightlyside.enstaunofficialguide.data_structure.Association;
import io.github.nightlyside.enstaunofficialguide.data_structure.Colloc;

public class ShowCollocsHolder extends RecyclerView.ViewHolder {

    private Colloc col;
    private TextView title;
    private TextView text;
    private ImageButton editbtn;
    private ImageButton deletebtn;

    public ShowCollocsHolder(View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.card_title);
        text = itemView.findViewById(R.id.card_text);
        editbtn = itemView.findViewById(R.id.edit_btn);
        deletebtn = itemView.findViewById(R.id.delete_btn);

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void bind(Colloc col) {
        this.col = col;

        title.setText(col.name);
        text.setText(col.adresse);
    }
}
