package com.ricknotes.weatherapp;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>{
    public static String imageUrl = "https://openweathermap.org/img/wn/";
    List<Hourly> mList;
    Context mContext;

    public HourlyAdapter(List<Hourly> list, Context context){
        this.mList = list;
        this.mContext = context;
    }

    @NonNull
    @Override
    public HourlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_list,
                parent, false);
        return new HourlyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyViewHolder holder, int position) {
        Hourly data = mList.get(position);

        String temp = Math.round(data.getTemp()) + "\u00B0";
        String precipitation = Math.round(data.getHumidity()) + "%";
        String iconUrl = imageUrl + data.getWeather().get(0).getIcon() +  "@2x.png";

        holder.mTemp.setText(temp);
        holder.mPrecipitation.setText(precipitation);
        holder.mTime.setText(getDate(data.getDt()));


        Glide.with(mContext).load(iconUrl).into(holder.mIcon);


    }
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("HH", cal).toString();
        return date;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class HourlyViewHolder extends RecyclerView.ViewHolder {
        TextView mTime;
        TextView mPrecipitation;
        TextView mTemp;
        ImageView mIcon;
        public HourlyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTime = itemView.findViewById(R.id.item_list_time_TV);
            mPrecipitation = itemView.findViewById(R.id.item_list_precipitation_TV);
            mTemp = itemView.findViewById(R.id.item_list_temp_TV);
            mIcon = itemView.findViewById(R.id.item_list_icon_imag);
        }
    }
}
