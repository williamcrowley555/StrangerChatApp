package com.stranger_chat_app.client.thread;

import com.stranger_chat_app.client.util.TimeUtils;
import com.stranger_chat_app.client.view.gui.AudioPanel;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class AudioPlayer extends Thread {
    private float frequency = 8000.0F;  //8000,11025,16000,22050,44100
    private int samplesize = 16;

    // Thread is running
    private boolean isRunning = false;
    // Audio is playing
    private boolean isPlaying = false;

    private AudioInputStream audioInputStream;
    private Clip clip;
    private long totalSecs;
    private long currentSecs = 0;

    private byte[] data;
    private AudioPanel audioPanel;

    public AudioPlayer(byte[] data, AudioPanel audioPanel) {
        this.data = data;
        this.audioPanel = audioPanel;
    }

    @Override
    public void run() {
        if (data != null) {
            try {
                InputStream byteArrayInputStream = new ByteArrayInputStream(data);
                AudioFormat audioFormat = getAudioFormat();
                audioInputStream = new AudioInputStream(byteArrayInputStream, audioFormat, data.length / audioFormat.getFrameSize());
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);

                totalSecs = TimeUnit.MICROSECONDS.toSeconds(clip.getMicrosecondLength());
                audioPanel.getLblTimestamp().setText(TimeUtils.convertToTimeString(totalSecs));
                audioPanel.getAudioBar().setMaximum((int) totalSecs);
                isRunning = true;

                while (isRunning) {
                    if (clip.isRunning()) {
                        currentSecs = TimeUnit.MICROSECONDS.toSeconds(clip.getMicrosecondPosition());

                        audioPanel.getLblTimestamp().setText(TimeUtils.convertToTimeString(totalSecs - currentSecs));
                        audioPanel.getAudioBar().setValue((int) currentSecs);
                    }

                    if (clip.getMicrosecondPosition() == clip.getMicrosecondLength()) {
                        isRunning = false;
                    }
                }
            } catch (LineUnavailableException | IOException e) {
                e.printStackTrace();
            } finally {
                close();
                currentSecs = 0;
                audioPanel.getLblTimestamp().setText(TimeUtils.convertToTimeString(totalSecs));
                audioPanel.getAudioBar().setValue(0);
                audioPanel.changePlayerState(AudioPanel.PLAYER_STATE_STOP);
            }
        }
    }

    public void play() {
        if (!clip.isRunning()) {
            clip.start();
            isPlaying = true;
        }
    }

    public void pause() {
        if (clip.isRunning()) {
            clip.stop();
            isPlaying = false;
        }
    }

    private void close() {
        clip.drain();
        clip.stop();
        isPlaying = false;
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public long getCurrentSecs() {
        return currentSecs;
    }

    public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
//        Scanner scanner = new Scanner(System.in);
//
//        File file = new File(System.getProperty("user.dir") + "\\src\\com\\stranger_chat_app\\client\\asset\\audio\\ringing.wav");
//        File file = new File(System.getProperty("user.dir") + "\\src\\com\\stranger_chat_app\\client\\asset\\audio\\incoming_call.wav");
//
//        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
//        Clip clip = AudioSystem.getClip();
//        clip.open(audioStream);
//        clip.start();
//
//        String response = "";
//
//        while(!response.equals("Q")) {
//            System.out.println("P = play, S = Stop, R = Reset, Q = Quit");
//            System.out.print("Enter your choice: ");
//
//            response = scanner.next();
//            response = response.toUpperCase();
//
//            switch(response) {
//                case ("P"):
//                    if (clip.getMicrosecondPosition() == clip.getMicrosecondLength())
//                        clip.setMicrosecondPosition(0);
//                    clip.start();
//                    break;
//                case ("S"):
//                    clip.stop();
//                    break;
//                case ("R"): clip.setMicrosecondPosition(0);
//                    break;
//                case ("Q"): clip.close();
//                    break;
//                default: System.out.println("Not a valid response");
//            }
//        }
    }
}
