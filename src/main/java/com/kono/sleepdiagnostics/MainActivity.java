package com.kono.sleepdiagnostics;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context mainContext = this;

        Button btn_start = findViewById(R.id.btn_main_start);
        btn_start.setOnClickListener(v -> {
            Intent intent = new Intent(mainContext, Recorder.class);
            startActivity(intent);
        });

        Button btn_statistics = findViewById(R.id.btn_main_statistics);
        btn_statistics.setOnClickListener(v ->{
            Intent intent = new Intent(mainContext, Statistics.class);
            startActivity(intent);
        });
    }
}


