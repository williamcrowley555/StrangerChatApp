package com.stranger_chat_app.client.thread;

import com.stranger_chat_app.client.RunClient;

import javax.sound.sampled.*;
import java.util.Base64;

public class VoiceRecorder extends Thread {
    private boolean isCalling = false;
    private TargetDataLine microphone = null;

    public VoiceRecorder() {
    }

    @Override
    public void run() {
        try {
            init();

            isCalling = true;
            byte[] buffer = new byte[1024];

            while(isCalling) {
                microphone.read(buffer, 0, buffer.length);

                String data = Base64.getEncoder().encodeToString(buffer);
                RunClient.socketHandler.sendVoice(data);
            }
        } catch(LineUnavailableException e) {
            e.printStackTrace();
        }

        microphone.close();
        microphone.drain();
    }

    private void init() throws LineUnavailableException {
        AudioFormat format = new AudioFormat(16000, 8, 2, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        // Selecting and starting microphone
        microphone = (TargetDataLine) AudioSystem.getLine(info);
        microphone.open(format);
        microphone.start();
    }

    public void terminate() {
        this.isCalling = false;
    }

    public boolean isCalling() {
        return isCalling;
    }

    public void setCalling(boolean calling) {
        isCalling = calling;
    }
}
