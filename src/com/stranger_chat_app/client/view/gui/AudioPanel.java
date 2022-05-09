package com.stranger_chat_app.client.view.gui;

import com.stranger_chat_app.client.thread.AudioPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AudioPanel extends JPanel {
    public static final int PLAYER_STATE_PLAY = 1;
    public static final int PLAYER_STATE_STOP = 2;

    private AudioPlayer audioPlayer;
    private JProgressBar audioBar;
    private JLabel lblState;
    private JLabel lblTimestamp;

    private String urlPlay = "/com/stranger_chat_app/client/asset/icons8-play-24.png";
    private String urlStop = "/com/stranger_chat_app/client/asset/icons8-pause-24.png";

    private byte[] data;

    public AudioPanel(byte[] data) throws InterruptedException {
        this.data = data;
        initComponents();

        generatePlayer(data);
    }

    private void generatePlayer(byte[] data) throws InterruptedException {
        audioPlayer = new AudioPlayer(data, this);
        audioPlayer.start();
        Thread.sleep(800);
    }

    public void changePlayerState(int state) {
        switch (state) {
            case PLAYER_STATE_PLAY:
                ImageIcon stopIcon = getImageIcon(urlStop);
                lblState.setIcon(stopIcon);
                audioPlayer.play();
                break;

            case PLAYER_STATE_STOP:
                ImageIcon playIcon = getImageIcon(urlPlay);
                lblState.setIcon(playIcon);
                audioPlayer.pause();
                break;

            default:
                break;
        }
    }

    public ImageIcon getImageIcon(String url) {
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource(url)).getImage());
        return imageIcon;
    }

    public void event() {
        lblState.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!audioPlayer.isRunning()) {
                    try {
                        generatePlayer(data);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }

                if (audioPlayer.isPlaying()) {
                    changePlayerState(PLAYER_STATE_STOP);
                } else {
                    changePlayerState(PLAYER_STATE_PLAY);
                }
            }
        });
    }

    public void initComponents() {
        setLayout(new FlowLayout());
        add(new JLabel(""));
        add(lblState);
        add(lblTimestamp);
        add(audioBar);

        audioBar.setValue(0);
        setVisible(true);
        event();
    }

    public JProgressBar getAudioBar() {
        return audioBar;
    }

    public JLabel getLblTimestamp() {
        return lblTimestamp;
    }

}
