package com.stranger_chat_app.client.util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class AudioUtils {
    private Scanner scanner;
    private String path;
    private File audioFile;
    private Clip audio;

    public AudioUtils() {
    }

    public AudioUtils(String path) {
        this.path = path;
        this.audioFile = new File(path);
//        this.scanner = new Scanner(System.in);
    }

    public void ready() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        audio = AudioSystem.getClip();
        audio.open(audioInputStream);
        FloatControl volume = (FloatControl) audio.getControl(FloatControl.Type.MASTER_GAIN);
        volume.setValue(1.0f); // Reduce volume by 10 decibels.
    }

    public void start(boolean looped) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if (audio == null) {
            ready();
        }

        audio.start();
        audio.drain();

        if (looped) {
            audio.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stop() {
        if (audio != null) {
            audio.stop();
            audio.close();
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public File getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(File audioFile) {
        this.audioFile = audioFile;
    }

    public Clip getAudio() {
        return audio;
    }

    public void setAudio(Clip audio) {
        this.audio = audio;
    }
}
