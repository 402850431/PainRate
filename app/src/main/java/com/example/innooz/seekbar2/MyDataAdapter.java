package com.example.innooz.seekbar2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Innooz on 2017/10/5.
 */

class MyDataAdapter extends RecyclerView.Adapter<MyDataAdapter.ViewHolder> {

    private Context context;
    private List<MyData> tabData;


    MyDataAdapter(Context context, List<MyData> tabData) {
        this.context = context;
        this.tabData = tabData;
    }

    @Override
    public MyDataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyDataAdapter.ViewHolder holder, int position) {

        holder.tvProgress.setText(String.valueOf(tabData.get(position).getNowProgress()));
        holder.tvTime.setText(tabData.get(position).getNowTime());
    }

    @Override
    public int getItemCount() {
        return tabData.size();
    }

    public void delete(int position){
        tabData.remove(position);
        notifyItemRemoved(position);
    }

    void clear() {
        int size = this.tabData.size();
        this.tabData.clear();
        notifyItemRangeRemoved(0, size);
    }
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvProgress, tvTime;

        ViewHolder(final View itemView) {
            super(itemView);

            tvProgress = (TextView) itemView.findViewById(R.id.text1);
            tvTime = (TextView) itemView.findViewById(R.id.text2);

        }

    }
}