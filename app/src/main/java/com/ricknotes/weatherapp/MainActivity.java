package com.ricknotes.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    public static final String API_KEY = "998910b58c534e2336dd25def6b2d564";
    private static final String TAG = "MainActivity";
    private final Double KELVIN_CONSTANT = 273.15;
    private int PLACE_PICKER_REQUEST = 1;
    private TextView mCurrentLocation;
    private TextView mCurrentDate;
    private TextView mCurrentTemp;
    private TextView mMaxNMin;
    private TextView mMainDescription;
    private TextView mYesterday;
    private TextView mPrecipitation;
    private TextView mUVIndex;
    private Results mResults;
    private ImageView mWeatherImg;
    private RecyclerView mHourlyRecyclerView;
    private RecyclerView mDailyRecyclerView;
    private ScrollView mScrollView;
    private Api mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        intiViews();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mApi = retrofit.create(Api.class);
        requestWeatherInfo(6.524379,3.379206, API_KEY);
    }


    private void intiViews() {
        mCurrentLocation = findViewById(R.id.activity_main_current_location_TV);
        mCurrentDate = findViewById(R.id.activity_main_current_date_TV);
        mCurrentTemp = findViewById(R.id.activity_main_current_temp_TV);
        mMaxNMin = findViewById(R.id.activity_main_high_and_low_TV);
        mMainDescription = findViewById(R.id.activity_main_description_TV);
        mYesterday = findViewById(R.id.activity_main_yesterday_temp_TV);
        mPrecipitation = findViewById(R.id.activity_main_precipitation_TV);
        mUVIndex = findViewById(R.id.activity_main_UV_index_TV);
        mWeatherImg = findViewById(R.id.activity_main_weather_img);
        mHourlyRecyclerView = findViewById(R.id.activity_main_hourly_recycler_view);
        mDailyRecyclerView = findViewById(R.id.activity_main_daily_recycler_view);
        mScrollView = findViewById(R.id.activity_main_scrollView);
    }
    private void updateViews(){
        //http://openweathermap.org/img/wn/10d@2x.png
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        Date d = new Date();
        String dayOfWeek = sdf.format(d);

        String date =
                Calendar.DAY_OF_WEEK + " " + Calendar.DAY_OF_MONTH + " " + Calendar.MONTH + " " + Calendar.HOUR + ":" + Calendar.MINUTE;
        String temp = Math.round(mResults.getCurrent().getTemp()) + "\u00B0";
        String UV = uvIndexIntensity(mResults.getCurrent().getUvi());
        String feelsLike = Math.round(mResults.getCurrent().getFeelsLike()) + "\u00B0";
        String highLow =
                Math.round(mResults.getDaily().get(0).getTemp().getMax()) + "\u00B0" + "/" + Math.round(mResults.getDaily().get(0).getTemp().getMin()) + "\u00B0" + " Feels like " + feelsLike ;
        String mainDescription = mResults.getCurrent().getWeather().get(0).getDescription();
        String[] dummy = mResults.getTimezone().split("/");
        String currentLocation = dummy[1];
        String imgUrl =
                "https://openweathermap.org/img/wn/" + mResults.getCurrent().getWeather().get(0).getIcon() + "@2x.png";
        String humidity = mResults.getCurrent().getHumidity() + "";

        Log.d(TAG, "updateViews: " + mResults.getCurrent().getWeather().get(0).getIcon());
        mCurrentDate.setText(dayOfWeek);
        mCurrentTemp.setText(temp);
        if (!UV.equals("")){
            mUVIndex.setText(UV);
        }
        mMaxNMin.setText(highLow);
        mMainDescription.setText(mainDescription);
        mCurrentLocation.setText(currentLocation);
        mPrecipitation.setText(humidity);


        Glide.with(this)
                .load(imgUrl)
                .into(mWeatherImg);

    }
    private void setHourlyData(){
        List<Hourly> list = mResults.getHourly();
        List<Hourly> twentyFourHourList = new ArrayList<>();
        for (int i = 0; i < 24; i++){
            twentyFourHourList.add(list.get(i));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL
                ,false);
        HourlyAdapter adapter = new HourlyAdapter(twentyFourHourList, this);

        mHourlyRecyclerView.setLayoutManager(layoutManager);
        mHourlyRecyclerView.setAdapter(adapter);
    }

    private void setDailyData(){
        List<Daily> list = mResults.getDaily();
        List<Daily> sevenDayList = new ArrayList<>();
        for (int i = 0; i < 7; i++){
            sevenDayList.add(list.get(i));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL
                ,false);
        DailyAdapter adapter = new DailyAdapter(sevenDayList, this);

        mDailyRecyclerView.setLayoutManager(layoutManager);
        mDailyRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.openMap){
            pickPlace();
        }
        return super.onOptionsItemSelected(item);
    }

    private void pickPlace() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    private void requestWeatherInfo(double lat, double lon, String apiKey){
        Call<Results> call = mApi.get(lat, lon,"metric", apiKey);
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                mResults = response.body();
                mScrollView.setVisibility(View.VISIBLE);
                updateViews();
                setHourlyData();
                setDailyData();
            }

            @Override
            public void onFailure(Call<Results> call, Throwable throwable) {
                Toast.makeText(getApplicationContext(),"No internet connection",
                        Toast.LENGTH_LONG).show();
                Log.d(TAG, "!!!!!!!!!!!!onFailure!!!!!!!!!!!: "+ throwable.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                double lat = place.getLatLng().latitude;
                double lon = place.getLatLng().longitude;
                requestWeatherInfo(lat, lon , API_KEY);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String uvIndexIntensity(Double uvIndex){
        if (uvIndex >= 0 && uvIndex <= 2){
            return "Low";
        }else if (uvIndex >= 3 && uvIndex <= 5){
            return "Moderate";
        }else if (uvIndex >= 6 && uvIndex <= 7){
            return "High";
        }else if (uvIndex >= 8){
            return "Very high";
        }else {
            return "";
        }
    }
}