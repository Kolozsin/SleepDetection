package com.kono.sleepdiagnostics;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Statistics extends AppCompatActivity {

    String data;
    String[] strings;
    String filename;
    float avgSleep = 0;
    private String filepath = "/storage/emulated/0/Android/data/com.kono" +
                                      ".sleepdiagnostics/files/MyFileStorage/";
    private boolean startsWithBreath = false;
    private String lengthOfSleep = "";
    private String avgBreath = "Your avg breath was: ";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Context StatisticsContext = this;
        TextView tw_length = findViewById(R.id.tw_statistics_Length);

        TextView tw_stats = findViewById(R.id.tw_statistics_values);

        EditText et = findViewById(R.id.input_statistics_filename);

        Button button = findViewById(R.id.btn_statistics_read);
        button.setOnClickListener(v -> {
            filename = et.getText().toString();
            if(filename != null & filename.length() != 0) {
                readFiles(filename);
                calculateAvg();
                tw_length.setText(lengthOfSleep);
                tw_stats.setText(avgBreath + "\n" + data);
            } else {
                Toast.makeText(StatisticsContext,
                               "You need to add a FILE " + "name",
                               Toast.LENGTH_SHORT);
            }
        });
    }

    //LégzésKezdete starts with 1
    //LégzésVége starts with 0
    //14 character 1 the start and the others for the milisec
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void calculateAvg() {
        startsWithBreath = data.startsWith("1");

        strings = new String[data.length() / 14];
        for(int i = 0; i < data.length() / 14; i++) {
            for(int j = 1; j < 14; j++) {
                strings[i] = strings[i] == null ?
                                     String.valueOf(data.charAt(14 * i + j))
                                     : strings[i] + data.charAt(14 * i + j);
            }
        }
        double difference =
                Long.valueOf(strings[strings.length - 1]) - Long.valueOf(strings[0]);
        int hours = (int) difference / 3600000;
        int minutes = (int) (difference - hours) / 60000;
        int seconds = (int) (difference - hours - minutes) / 1000;

        long[] ins = new long[strings.length / 2];
        long[] outs = new long[strings.length / 2];
        int indexi = 0;
        int indexo = 0;

        if(strings.length % 2 != 0) {
            if(startsWithBreath) {
                strings[0] = null;
            } else {
                strings[strings.length / 14 - 1] = null;
            }
        }

        for(int i = 0; i < strings.length; i++) {
            if(startsWithBreath & strings[i] != null) {
                if(i % 2 == 0) {
                    ins[indexi++] = Long.valueOf(strings[i]);
                } else {
                    outs[indexo++] = Long.valueOf(strings[i]);
                }
            } else if(strings[i] != null) {
                if(i % 2 != 0) {
                    ins[indexi++] = Long.valueOf(strings[i]);
                } else {
                    outs[indexo++] = Long.valueOf(strings[i]);
                }
            }
        }

        long[] breaths = new long[strings.length / 2];
        float avg = 0;
        for(int i = 0; i < breaths.length; i++) {
            breaths[i] = outs[i] - ins[i];
            avg += breaths[i];
        }

        avgSleep += (avg / breaths.length);
        avgBreath += avgSleep + " miliseconds";

        lengthOfSleep =
                hours + " hours and " + minutes + " minutes " + seconds + " " + "seconds has passed";
    }

    private void readFiles(String filename) {
        try {
            File myObj = new File(filepath + filename + ".txt");
            Scanner myReader = new Scanner(myObj);
            while(myReader.hasNextLine()) {
                data = myReader.nextLine();
            }
            myReader.close();
        } catch(FileNotFoundException e) {
            Toast.makeText(this, "An error occurred.", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
    }
}