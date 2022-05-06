package com.stranger_chat_app.client.thread;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioPlayer extends Thread {
    private InputStream inputStream;
    private AudioFormat audioFormat;
    private AudioInputStream audioInputStream;
    private DataLine.Info info;
    private SourceDataLine speaker;
    private JProgressBar jProgressBar;

    private boolean isPlaying;
    private byte[] audio;
    private byte[] buffer;
    private int bufferSize;
    private int count;

    @Override
    public void run() {
        createAudioLine();
        openSpeaker();
        playAudio();
    }


    // Hàm khởi tạo audio chuẩn bị phát
    public void createAudioLine() {
        AudioRecorder audioRecorder = new AudioRecorder();
        inputStream = new ByteArrayInputStream(audio);
        audioFormat = audioRecorder.getAudioFormat();
        audioInputStream = new AudioInputStream(inputStream, audioFormat, audio.length / audioFormat.getFrameSize());
        info = new DataLine.Info(SourceDataLine.class, audioFormat);

    }

    private void openSpeaker() {
        try {
            speaker = (SourceDataLine) AudioSystem.getLine(info);
            speaker.open();
            speaker.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playAudio() {
        try {
            bufferSize = (int) (audioFormat.getSampleRate() * audioFormat.getFrameSize());
            buffer = new byte[bufferSize];

            isPlaying = true;

            while (isPlaying && (count = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
                jProgressBar.setValue(jProgressBar.getValue() + 1);
                if (count > 0) {
                    speaker.write(buffer, 0, count);
                }
            }
            jProgressBar.setValue(jProgressBar.getMaximum());

            closeSpeaker();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSpeaker() {
        speaker.close();
        speaker.drain();

    }

    public void stopPlay() {
        this.isPlaying = false;
    }

    public void setAudio(byte[] audio) {
        this.audio = audio;
    }

    public void setjProgressBar(JProgressBar jProgressBar) {
        this.jProgressBar = jProgressBar;
    }
}
