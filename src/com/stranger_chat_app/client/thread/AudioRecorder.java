package com.stranger_chat_app.client.thread;

import com.stranger_chat_app.client.RunClient;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class AudioRecorder extends Thread {
    private boolean isRecord;
    private TargetDataLine microphone;
    private AudioFormat audioFormat;
    private DataLine.Info info;
    private ByteArrayOutputStream out;
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
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
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
        out = new ByteArrayOutputStream();
        int numBytesRead;
        byte[] data = new byte[1024];
        microphone.start();

        isRecord = true;

//        Maximum recording time is 10 minutes
        while (isRecord && recordTime <= 600) {
            numBytesRead = microphone.read(data, 0, data.length);
            out.write(data, 0, numBytesRead);
        }

        endRecord();
    }

    // Hàm đóng microphone và gửi bản ghi âm
    public void endRecord() {
        microphone.close();
        microphone.drain();

        String strData = Base64.getEncoder().encodeToString(out.toByteArray());
        try {
            int selected = JOptionPane.showConfirmDialog(null, "Bạn có muốn gửi đoạn ghi âm không?");
            if (selected == JOptionPane.YES_OPTION) {
                RunClient.chatRoomGUI.addAudio(out.toByteArray(), "sender");
                RunClient.socketHandler.sendAudio(strData);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Hàm dừng thu âm
    public void terminate() {
        this.isRecord = false;
    }
}
