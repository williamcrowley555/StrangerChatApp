package com.stranger_chat_app.client.thread;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class AudioPlayer extends Thread {
    private float frequency = 8000.0F;  //8000,11025,16000,22050,44100
    private int samplesize = 16;

    // Thread is running
    private boolean isRunning = false;
    // Audio is playing
    private boolean isPlaying = false;

    private AudioInputStream audioInputStream;
    private Clip clip;

    private byte[] data;

    public AudioPlayer(byte[] data) {
        this.data = data;
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

                isRunning = true;

                while (isRunning) {
                    if (clip.getMicrosecondPosition() == clip.getMicrosecondLength()) {
                        isRunning = false;
                    }
                }
            } catch (LineUnavailableException | IOException e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }
    }

    public void play() {
        clip.start();
        isPlaying = true;
    }

    public void pause() {
        clip.stop();
        isPlaying = false;
    }

    private void close() {
        clip.drain();
        clip.stop();
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = frequency;
        //8000,11025,16000,22050,44100
        int sampleSizeInBits = samplesize;
        //8,16
        int channels = 1;
        //1,2
        boolean signed = true;
        //true,false
        boolean bigEndian = false;
        //true,false
        //return new AudioFormat( AudioFormat.Encoding.PCM_SIGNED, 8000.0f, 8, 1, 1,
        //8000.0f, false );

        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isPlaying() {
        return isPlaying;
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
