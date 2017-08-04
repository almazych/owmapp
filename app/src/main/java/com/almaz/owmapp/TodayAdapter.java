package com.almaz.owmapp;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TodayAdapter extends RecyclerView.Adapter<TodayAdapter.ViewHolder> {


    Context context;

    private List<com.almaz.owmapp.data.List> mList;

    public TodayAdapter(List<com.almaz.owmapp.data.List> mList) {
        this.mList = mList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_recycler_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    //метод для изменения списка
    public void changeDataSet(List<com.almaz.owmapp.data.List> list) {
        mList.clear();
        for(int i=0;i<9;i++){
                mList.add(i, list.get(i));
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView temp, time;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView)itemView.findViewById(R.id.icon_rec_h);
            temp = (TextView)itemView.findViewById(R.id.temp_rec_h);
            time = (TextView)itemView.findViewById(R.id.time_rec_h);
        }

        public void bind(final com.almaz.owmapp.data.List item) {
            temp.setText(String.valueOf(Math.round(item.getMain().getTemp())) +"\u00B0");
            time.setText(new SimpleDateFormat("HH:mm").format(new Date((long)item.getDt()*1000)).toString());

            Picasso.with(context)
                    .load("http://openweathermap.org/img/w/" + item.getWeather().get(0).getIcon() + ".png")
                    .resize(120,120)
                    .into(icon);
        }
    }
}
