package com.eirture.customrvlayout;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by eirture on 16-10-17.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomHolder> {

    private static final int[] COLORS = {0xff00FFFF, 0xffDEB887, 0xff5F9EA0,
            0xff7FFF00, 0xff6495ED, 0xffDC143C,
            0xff008B8B, 0xff006400, 0xff2F4F4F,
            0xffFF69B4, 0xffFF00FF, 0xffCD5C5C,
            0xff90EE90, 0xff87CEFA, 0xff800000};

    @Override
    public CustomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new CustomHolder(item);
    }

    @Override
    public void onBindViewHolder(final CustomHolder holder, int position) {
        holder.item.setCardColor(randomColor());
        holder.text.setText("菜单" + position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), holder.text.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int randomColor() {
        return COLORS[new Random().nextInt(COLORS.length)];
    }


    @Override
    public int getItemCount() {
        return 50;
    }

    class CustomHolder extends RecyclerView.ViewHolder {
        CardItemView item;
        TextView text;

        public CustomHolder(View itemView) {
            super(itemView);
            item = (CardItemView) itemView.findViewById(R.id.item);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
