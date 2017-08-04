package com.almaz.owmapp;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.almaz.owmapp.data.List;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.Date;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.ViewHolder> {

    Context context;

    private java.util.List<List> mList;

    public DaysAdapter(java.util.List<List> mList) {
        this.mList = mList;
    }

    @Override
    public DaysAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vertical_recycler_item, parent, false);
        return new DaysAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DaysAdapter.ViewHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    public void changeDataSet(java.util.List<List> list) {
        mList.clear();
        for(int i=0, a=0;i<37;i=i+8,a++){
            mList.add(a, list.get(i));
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

       TextView day, about, temp;
        ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);

            day = (TextView)itemView.findViewById(R.id.day_rec_v);
            about = (TextView)itemView.findViewById(R.id.about_rec_v);
            temp = (TextView)itemView.findViewById(R.id.temp_rec_v);
            icon = (ImageView)itemView.findViewById(R.id.icon_rec_v);
        }

        public void bind(final com.almaz.owmapp.data.List item) {

            day.setText(new SimpleDateFormat("dd MMM").format(new Date((long)item.getDt()*1000)).toString());

            about.setText(item.getWeather().get(0).getDescription());

            temp.setText(String.valueOf(Math.round(item.getMain().getTemp())) + "\u00B0/" + String.valueOf(item.getClouds().getAll()) + "%");

            Picasso.with(context)
                    .load("http://openweathermap.org/img/w/" + item.getWeather().get(0).getIcon() + ".png")
                    .resize(80,80)
                    .into(icon);

        }
    }
}
