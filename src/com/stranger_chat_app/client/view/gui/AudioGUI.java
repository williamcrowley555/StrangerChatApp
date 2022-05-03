package com.stranger_chat_app.client.view.gui;

import com.stranger_chat_app.client.thread.AudioPlayer;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AudioGUI {
    private AudioPlayer audioPlayer;
    private JProgressBar audioBar;
    private JLabel lblPlay;
    private JLabel lblTime;

    private String urlPlay = "com/stranger_chat_app/client/asset/icons8-play-24.png";
    private String urlStop = "com/stranger_chat_app/client/asset/icons8-stop-24.png";

    private byte[] sound;

    public AudioGUI() {
        initComponents();
        event();
    }

    public void initComponents() {
        audioPlayer = new AudioPlayer();
        audioPlayer.setAudio(sound);
        audioPlayer.setjProgressBar(audioBar);
    }

    public void event() {
        lblPlay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ImageIcon imageIcon = getImageIcon(urlStop);
                lblPlay.setIcon(imageIcon);
                audioPlayer.start();
            }
        });
    }

    public ImageIcon getImageIcon(String url) {
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource(url)).getImage());
        return imageIcon;
    }
}
