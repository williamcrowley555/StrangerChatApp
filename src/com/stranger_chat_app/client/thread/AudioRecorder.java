package com.stranger_chat_app.client.thread;

import javax.sound.sampled.*;

public class AudioRecorder extends Thread {
    private boolean isRecord;
    private TargetDataLine microphone;
    private AudioFormat audioFormat;
    private DataLine.Info info;
    private int recordTime;

    // Hàm chính, khi chạy sẽ tiến hành thu âm cho đến khi dừng lại.

    @Override
    public void run() {
        createMicrophone();
        startRecord();
    }

    // Hàm trả về format âm thanh với tần số phù hợp
    public AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
        return format;
    }

    // Hàm khởi tạo micro để bắt đầu ghi âm
    public void createMicrophone() {
        try {
            audioFormat = getAudioFormat();
            info = new DataLine.Info(TargetDataLine.class, audioFormat);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Hàm thu âm
    public void startRecord() {
        byte[] buffer = new byte[1024];
        recordTime = 0;
        isRecord = true;

        microphone.start();
        System.out.println("run startRecord");
        while (isRecord) {
            recordTime++;
            microphone.read(buffer, 0, buffer.length);
        }

        endRecord();
    }

    // Hàm đóng microphone và gửi bản ghi âm
    public void endRecord() {
        microphone.close();
        microphone.drain();
        System.out.println("close mic");

        //TODO Thêm hàm gửi từ người gửi sang người nhận.
    }

    // Hàm dừng thu âm
    public void terminate() {
        System.out.println("run terminate");
        this.isRecord = false;
    }
}
