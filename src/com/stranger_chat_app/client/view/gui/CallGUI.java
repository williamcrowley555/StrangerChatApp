package com.stranger_chat_app.client.view.gui;

import com.stranger_chat_app.client.RunClient;
import com.stranger_chat_app.client.util.AudioUtils;
import com.stranger_chat_app.client.view.enums.CallState;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class CallGUI extends JFrame{
    private JPanel panel1;
    private JPanel mainPanel;
    private JPanel pnlScreen;
    private JPanel buttonsPanel;
    private JCheckBox cameraCheckBox;
    private JCheckBox voiceCheckBox;
    private JButton btnEndCall;
    private JButton btnAcceptCall;
    private JPanel pnlInfo;
    private JLabel lblNickname;
    private JLabel lblUserAvatar;

    private String audioDir = System.getProperty("user.dir") + "\\src\\com\\stranger_chat_app\\client\\asset\\audio\\";
    private AudioUtils audio;

    private String stranger;

    public CallGUI(){
        super();
        setTitle("Video call");
        setContentPane(mainPanel);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        initComponents();
    }

    public void setDisplayState(CallState state) {
        // hiển thị tất cả components
        showAllComponents();

        // ẩn các components tùy theo state
        switch (state) {
            case RINGING:
                voiceCheckBox.setEnabled(false);
                cameraCheckBox.setEnabled(false);
                btnAcceptCall.setVisible(false);
                btnAcceptCall.setEnabled(false);

                // Play ringing audio
                playAudio("ringing.wav", true);
                break;

            case INCOMING_CALL:
                voiceCheckBox.setEnabled(false);
                cameraCheckBox.setEnabled(false);

                // Play incoming call audio
                playAudio("incoming_call.wav", true);
                break;

            case CALLING:
                btnAcceptCall.setVisible(false);
                btnAcceptCall.setEnabled(false);
                break;

            default:
                break;
        }
    }

    private void showAllComponents() {
        cameraCheckBox.setEnabled(true);
        cameraCheckBox.setEnabled(true);
        btnAcceptCall.setVisible(true);
        btnAcceptCall.setEnabled(true);
        btnEndCall.setVisible(true);
        btnEndCall.setEnabled(true);
    }

    private void playAudio(String file, boolean looped) {
        audio = new AudioUtils(audioDir + file);
        try {
            audio.start(looped);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopAudio() {
        audio.stop();
    }

    public void setStranger(String stranger) {
        this.stranger = stranger;
        lblNickname.setText(stranger);
    }

    private void initComponents() {
        btnEndCall.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("END CALL");
                RunClient.socketHandler.endCall(stranger);
            }
        });
    }
}
