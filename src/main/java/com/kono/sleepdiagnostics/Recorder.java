package com.kono.sleepdiagnostics;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class Recorder extends AppCompatActivity {
    SoundMeter sm;
    File outputFile;
    boolean isRecording = false;
    File file;
    private String filepath = "MyFileStorage";
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        Button btn_startRecording =
                findViewById(R.id.btn_recorder_startRecording);
        EditText tw_recorder = findViewById(R.id.tw_recorder_fileName);
        btn_startRecording.setOnClickListener(v -> {
            if(isRecording) {
                btn_startRecording.setText("Start RECORD");
                stopRecording();
                isRecording = false;
            } else if(tw_recorder.getText() != null & tw_recorder.getText().length() != 0) {
                this.fileName = tw_recorder.getText().toString() + ".txt";
                createOutputFile();
                isRecording = true;
                btn_startRecording.setText("STOP RECORD");
                setupMicrophoneChecker(file.getPath());
            }
        });
    }


    private void setupMicrophoneChecker(String fileName) {
        sm = new SoundMeter(fileName);
        sm.start();
    }

    private void stopRecording() {
        sm.stop();
    }

    private void createOutputFile() {
        file = new File(getExternalFilesDir(filepath), fileName);
    }
}