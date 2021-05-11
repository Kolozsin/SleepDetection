package com.kono.sleepdiagnostics;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SoundMeter {

    public double min;
    boolean isSilent = true;
    Timer timer;
    long delay = 10;
    String buffer_1 = "";
    String buffer_2 = "";
    String fileName;
    private AudioRecord ar = null;
    private int minSize;

    public SoundMeter(String fileName) {
        this.fileName = fileName;
    }

    public void start() {
        if(ar == null) {
            minSize = AudioRecord.getMinBufferSize(44100,
                                                   AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            ar = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                                 AudioFormat.CHANNEL_IN_MONO,
                                 AudioFormat.ENCODING_PCM_16BIT, minSize);
        }
        ar.startRecording();
        min = getAmplitude();
        timer = timer == null ? new Timer() : timer;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                logBreath();
            }
        };
        timer.schedule(timerTask, delay, delay);
    }

    public void stop() {
        if(ar != null) {
            timer.cancel();
            timer = null;
            ar.stop();
        }
        writeEverything();
    }

    public double getAmplitude() {
        short[] buffer = new short[minSize];
        ar.read(buffer, 0, minSize);
        int max = 0;
        for(short s : buffer) {
            if(Math.abs(s) > max) {
                max = Math.abs(s);
            }
        }
        return max;
    }

    public double getDecibel() {
        return Math.log10(20 * getAmplitude() / min);
    }

    public double getDifferenceInPercentage() {
        return getAmplitude() / min * 100;
    }


    private void logBreath() {
        //14 character-ből épül fel egy érték.
        if(getDifferenceInPercentage() > 180) {
            if(isSilent) {
                buffer_1 += ("1" + String.valueOf(new Date().getTime()));
                addBuffer();
                System.out.println("Légzés kezdete: " + String.valueOf(new Date().getTime()));
            }
            isSilent = false;
            return;
        } else {
            if(isSilent) {
                return;
            } else {
                //Légzés vége
                buffer_1 += ("0" + String.valueOf(new Date().getTime()));
                addBuffer();
                isSilent = true;
                System.out.println("Légzés vége: " + String.valueOf(new Date().getTime()));
            }
        }
    }

    private void addBuffer() {
        if(buffer_1.length() > 1500) {
            buffer_2 = buffer_1;
            buffer_1 = "";
            try {
                FileWriter fw = new FileWriter(fileName, true);

                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(buffer_2);
                bw.close();
            } catch(Exception e) {
                System.out.println("Error has happened: " + e);
            }
        }
    }

    private void writeEverything() {
        try {
            FileWriter fw = new FileWriter(fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(buffer_1);
            bw.close();
        } catch(Exception e) {
            System.out.println("Error has happened: " + e);
        }
    }
}