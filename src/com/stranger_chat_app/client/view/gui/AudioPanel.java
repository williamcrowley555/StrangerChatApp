package com.stranger_chat_app.client.view.gui;

import com.stranger_chat_app.client.thread.AudioPlayer;
import com.stranger_chat_app.shared.model.Audio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AudioPanel extends JPanel {
    private AudioPlayer audioPlayer;
    private JProgressBar audioBar;
    private JLabel lblState;
    private JLabel lblTime;

    private String urlPlay = "/com/stranger_chat_app/client/asset/icons8-play-24.png";
    private String urlStop = "/com/stranger_chat_app/client/asset/icons8-pause-24.png";

    private Audio audio;

    public AudioPanel(Audio audio) {
        this.audio = audio;
        initComponents();

        audioPlayer = new AudioPlayer(audio.getBuffer());
        audioPlayer.start();
    }

    public void initComponents() {
        setLayout(new FlowLayout());
        add(new JLabel(""));
        add(lblState);
        add(lblTime);
        add(audioBar);
        setVisible(true);

        event();
    }

    public void event() {
        lblState.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (audioPlayer.isRunning()) {
                    if (audioPlayer.isPlaying()) {
                        ImageIcon playIcon = getImageIcon(urlPlay);
                        lblState.setIcon(playIcon);
                        audioPlayer.pause();
                    } else {
                        ImageIcon stopIcon = getImageIcon(urlStop);
                        lblState.setIcon(stopIcon);
                        audioPlayer.play();
                    }
                } else {
                    audioPlayer = new AudioPlayer(audio.getBuffer());
                    audioPlayer.start();
                }
            }
        });
    }

    public ImageIcon getImageIcon(String url) {
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource(url)).getImage());
        return imageIcon;
    }

    public static void main(String[] args) {
        byte[] buffer2 = new byte[1024];
        int time2 = 200;
        Audio audio = new Audio(buffer2, time2);
        AudioPanel audioPanel = new AudioPanel(audio);
        audioPanel.setVisible(true);
    }

}
