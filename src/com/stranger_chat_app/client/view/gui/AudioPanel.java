package com.stranger_chat_app.client.view.gui;

import com.stranger_chat_app.client.thread.AudioPlayer;
import com.stranger_chat_app.shared.model.Audio;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AudioPanel extends JPanel {
    private AudioPlayer audioPlayer;
    private JProgressBar audioBar;
    private JLabel lblState;
    private JLabel lblTime;

    private String urlPlay = "com/stranger_chat_app/client/asset/icons8-play-24.png";
    private String urlStop = "com/stranger_chat_app/client/asset/icons8-stop-24.png";

    private Audio audio;

    private boolean isPlaying = false;

    public AudioPanel(Audio audio) {
        this.audio = audio;
        initComponents();
        event();
    }

    public void initComponents() {
        audioPlayer = new AudioPlayer();
        audioPlayer.setAudio(audio.getBuffer());
        audioPlayer.setjProgressBar(audioBar);

    }

    public void event() {
        lblState.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(isPlaying) {
                    ImageIcon playIcon = getImageIcon(urlPlay);
                    lblState.setIcon(playIcon);
                    audioPlayer.stopPlay();
                    isPlaying = false;
                } else {
                    ImageIcon stopIcon = getImageIcon(urlStop);
                    lblState.setIcon(stopIcon);
                    audioPlayer.start();
                    isPlaying = true;
                }
            }
        });
    }

    public ImageIcon getImageIcon(String url) {
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource(url)).getImage());
        return imageIcon;
    }

}
