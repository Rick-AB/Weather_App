package com.ricknotes.weatherapp;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.DailyViewHolder>{
    private static final String TAG = "dailyadapter";
    List<Daily> mList;
    Context mContext;

    public DailyAdapter(List<Daily> list, Context context){
        this.mList = list;
        this.mContext = context;
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_daily_list, parent, false);

        return new DailyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyViewHolder holder, int position) {
        Daily data = mList.get(position);

        Log.d(TAG, "##############onBindViewHolder: ##########" + data.getDt());

        String day = getDayOfWeek(data.getDt());
        String humidity = data.getHumidity() + "%";
        String max = Math.round(data.getTemp().getMax()) + "\u00B0";
        String min = Math.round(data.getTemp().getMin()) + "\u00B0";
        String maxIconUrl = HourlyAdapter.imageUrl + data.getWeather().get(0).getIcon() + "@2x.png";



        holder.mDay.setText(day);
        holder.mHumidity.setText(humidity);
        holder.mMax.setText(max);
        holder.mMin.setText(min);

        Glide.with(mContext).load(maxIconUrl).into(holder.mMaxIcon);



    }

    private String getDayOfWeek(int timestamp){
//        SimpleDateFormat formatter = new SimpleDateFormat("EEEE",Locale.ENGLISH);
//        Date date = new Date(timestamp);
//
//        Log.d(TAG, "getDayOfWeek:*************** " + date);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp * 1000);
        String date = DateFormat.format("dd", cal).toString();
        Log.d(TAG, "getDayOfWeek:*************** " + date);
        return date;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class DailyViewHolder extends RecyclerView.ViewHolder{
        TextView mDay;
        TextView mMax;
        TextView mMin;
        TextView mHumidity;
        ImageView mMaxIcon;
        //ImageView mMinIcon;
        public DailyViewHolder(@NonNull View itemView) {
            super(itemView);
            mDay = itemView.findViewById(R.id.item_daily_day_TV);
            mMax = itemView.findViewById(R.id.item_daily_max_TV);
            mMin = itemView.findViewById(R.id.item_daily_min_TV);
            mHumidity = itemView.findViewById(R.id.item_daily_humidity_TV);
            mMaxIcon = itemView.findViewById(R.id.item_daily_max_icon_img);
           // mMinIcon = itemView.findViewById(R.id.item_daily_min_img);
        }
    }
}
