package com.shurpo.myapplication.app.navigationdrawer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import com.shurpo.myapplication.app.R;

/**
 * Created by Maksim on 08.07.2014.
 */
public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.ViewHolder> {

    private String[] myDataset;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        public void onClick(View view, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(TextView itemView) {
            super(itemView);
            textView = itemView;
        }
    }

    public FeatureAdapter(String[] myDataset, OnItemClickListener onItemClickListener) {
        this.myDataset = myDataset;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.drawer_list_item, viewGroup, false);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int possition) {
        viewHolder.textView.setText(myDataset[possition]);
        viewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClick(view, possition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myDataset.length;
    }
}
