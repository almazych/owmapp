package com.almaz.owmapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.almaz.owmapp.data.Data;
import com.almaz.owmapp.data.List;


import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondActivity extends AppCompatActivity {

    public static final String API_KEY = "02b26bd7923f11abd076c3fd7f0835ac";

    RecyclerView horRecyclerView, verRecyclerView;
    Data data;
    TodayAdapter adapter;
    DaysAdapter verAdapter;
    ProgressBar progressBar;
    Menu menu;


    TextView temp, about, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        temp = (TextView) findViewById(R.id.temp);
        about = (TextView)findViewById(R.id.about);
        title = (TextView) findViewById(R.id.toolbar_title);

        progressBar  = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        horRecyclerView = (RecyclerView) findViewById(R.id.today_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        horRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        horRecyclerView.setLayoutManager(layoutManager);

        verRecyclerView = (RecyclerView) findViewById(R.id.five_day_recycler);
        LinearLayoutManager verLayoutManager = new LinearLayoutManager(this);
        verRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        verRecyclerView.setLayoutManager(verLayoutManager);

        verAdapter = new DaysAdapter(new ArrayList<List>());
        verRecyclerView.setAdapter(verAdapter);


        adapter = new TodayAdapter(new ArrayList<List>());
        horRecyclerView.setAdapter(adapter);

        title.setText(getIntent().getStringExtra("EXTRA_NAME"));

        App.getApi().getData(getIntent().getStringExtra("EXTRA_NAME"),API_KEY, "metric", "ru").enqueue(new Callback<Data>() {
            public void onResponse(Call<Data> call, Response<Data> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Log.d("TAG", "Status Code = " + response.code());
                    data = response.body();
                    temp.setText(String.valueOf(Math.round(data.getList().get(0).getMain().getTemp())) + "\u00B0" );
                    about.setText(data.getList().get(0).getWeather().get(0).getDescription() + "\nскорость ветра: " +
                                    String.valueOf(data.getList().get(0).getWind().getSpeed()) + "м/с \nвлажность: " +
                                    String.valueOf(data.getList().get(0).getClouds().getAll()) + "%" );

                    adapter.changeDataSet(data.getList());
                    verAdapter.changeDataSet(data.getList());
                } else {
                    try {
                        Log.d("TAG", response.errorBody().string());
                    } catch (IOException ioe) {
                        Log.d("TAG", ioe.getLocalizedMessage());
                    }
                }
                horRecyclerView.getAdapter().notifyDataSetChanged();
                verRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                Log.i("TAG", t.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.share){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "In " + data.getCity().getName() + " the temperature is " + temp.getText());

            try
            {
                startActivity(Intent.createChooser(intent, "Поделиться погодой"));
            }
            catch (android.content.ActivityNotFoundException ex)
            {
                Toast.makeText(getApplicationContext(), "Some error", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
