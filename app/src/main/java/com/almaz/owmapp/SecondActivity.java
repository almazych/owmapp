package com.almaz.owmapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.almaz.owmapp.data.Data;
import com.almaz.owmapp.data.List;

import java.io.File;
import java.io.FileOutputStream;
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
    ImageView imageShare;
    TextView title;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        imageShare = (ImageView)findViewById(R.id.imageShare);

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



        getData().enqueue(new Callback<Data>() {
            public void onResponse(Call<Data> call, Response<Data> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Log.d("TAG", "Status Code = " + response.code());
                    data = response.body();

                    bitmap = drawTextToBitmap(String.valueOf(Math.round(data.getList().get(0).getMain().getTemp())) + "\u00B0",
                                    data.getList().get(0).getWeather().get(0).getDescription() + "\nскорость ветра: " +
                                    String.valueOf(data.getList().get(0).getWind().getSpeed()) + "м/с \nвлажность: " +
                                    String.valueOf(data.getList().get(0).getClouds().getAll()) + "%" );

                    imageShare.setImageBitmap(bitmap);

                    title.setText(data.getCity().getName());

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
            intent.putExtra(Intent.EXTRA_TEXT,"Current weather in  " + title.getText());


            File cache = getApplicationContext().getExternalCacheDir();
            File sharefile = new File(cache, "toshare.png");
            Log.d("share file type is", sharefile.getAbsolutePath());
            try {
                FileOutputStream out = new FileOutputStream(sharefile);
                drawBmpToBmp(bitmap).compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                Log.e("ERROR", String.valueOf(e.getMessage()));

            }
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + sharefile));

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

    public Bitmap drawTextToBitmap(String gText, String sText){

        Bitmap  bitmap = Bitmap.createBitmap(widthScreen(), 350, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.TRANSPARENT);

        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setTextSize(140);

        canvas.drawText(gText, bitmap.getWidth()/64, bitmap.getHeight()/2, paint);

        Paint secPaint = new Paint();
        secPaint.setAntiAlias(true);
        secPaint.setColor(Color.WHITE);
        secPaint.setTextSize(40);

        int y = bitmap.getHeight()/4;
        for (String line: sText.split("\n")) {
            canvas.drawText(line, bitmap.getWidth()/3, y, secPaint);
            y += secPaint.descent() - secPaint.ascent();
        }
        return bitmap;
    }


    public Bitmap drawBmpToBmp(Bitmap bitmap){

        Bitmap bitmapOne = BitmapFactory.decodeResource(getResources(), R.drawable.sharing);
        android.graphics.Bitmap.Config bitmapConfig = bitmapOne.getConfig();
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmapOne = bitmapOne.copy(bitmapConfig, true);

        Bitmap  bitmapTwo = Bitmap.createBitmap(bitmapOne,0,0,widthScreen(), 350);
        android.graphics.Bitmap.Config bitmapConfi = bitmapTwo.getConfig();
        if(bitmapConfi == null) {
            bitmapConfi = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmapTwo = bitmapTwo.copy(bitmapConfi, true);

        Canvas canvas = new Canvas(bitmapTwo);
        canvas.drawBitmap(bitmap,0,0,null);

        return bitmapTwo;
    }

    public int widthScreen(){
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        if(width<height){
            return width;
        } else {
            return height;
        }
    }

    public Call<Data> getData() {
        if(getIntent().getStringExtra("EXTRA_NAME")!=null) {
            return App.getApi().getData(getIntent().getStringExtra("EXTRA_NAME"), API_KEY, "metric", "ru");
        }else if(getIntent().getStringExtra("EXTRA_LAT")!=null&&getIntent().getStringExtra("EXTRA_LON")!=null){
            return App.getApi().getDataFromGeo(getIntent().getStringExtra("EXTRA_LAT"), getIntent().getStringExtra("EXTRA_LON"), API_KEY, "metric", "ru");
        } else {
            return null;
        }
    }


}