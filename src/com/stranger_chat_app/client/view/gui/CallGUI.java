package com.stranger_chat_app.client.view.gui;

import com.stranger_chat_app.client.RunClient;
import com.stranger_chat_app.client.thread.VideoRecorder;
import com.stranger_chat_app.client.thread.VoiceRecorder;
import com.stranger_chat_app.client.util.AudioUtils;
import com.stranger_chat_app.client.view.enums.CallState;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
    private JPanel pnlVideo;
    private JLabel lblStrangerStream;
    private CardLayout cardLayout;
    private WebcamPanel webcamPanel;
    private String audioDir = System.getProperty("user.dir") + "\\src\\com\\stranger_chat_app\\client\\asset\\audio\\";
    private AudioUtils audio;

    private VoiceRecorder microphone;
    private VideoRecorder webcam;

    private String stranger;

    public CallGUI(){
        super();
        setTitle("Video call");
        setContentPane(mainPanel);
        setSize(800, 600);
        setResizable(false);
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
                stopAudio();
                voiceCheckBox.setSelected(false);
                btnAcceptCall.setVisible(false);
                btnAcceptCall.setEnabled(false);
                break;

            default:
                break;
        }
    }

    private void showAllComponents() {
        voiceCheckBox.setEnabled(true);
        cameraCheckBox.setEnabled(true);
        btnAcceptCall.setVisible(true);
        btnAcceptCall.setEnabled(true);
        btnEndCall.setVisible(true);
        btnEndCall.setEnabled(true);
        cardLayout.show(pnlScreen, "cardUserInfo");
        webcamPanel.createGUI(pnlScreen.getWidth(), pnlScreen.getHeight());
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
        if (audio != null) {
            audio.stop();
        }
    }

    public void stopMicrophone() {
        if (microphone != null && microphone.isCalling()) {
            microphone.terminate();
        }
    }

    public void stopWebcam() {
        if (webcam != null && webcam.isRunning()) {
            webcam.terminate();
        }
    }

    public void showStrangerWebcam(ImageIcon imageIcon) {
        webcamPanel.setStrangerStream(imageIcon);
        cardLayout.show(pnlScreen, "webcam");

        if (imageIcon == null) {
            if (webcamPanel.getSelfStream() == null) {
                cardLayout.show(pnlScreen, "cardUserInfo");
            }
        }
    }

    public void showSelfWebcam(ImageIcon imageIcon) {
        webcamPanel.setSelfStream(imageIcon);
        cardLayout.show(pnlScreen, "webcam");

        if (imageIcon == null) {
            if (webcamPanel.getStrangerStream() == null) {
                cardLayout.show(pnlScreen, "cardUserInfo");
            }
        }
    }

    private void setLabelIcon(JLabel label, ImageIcon icon) {
        if (icon == null) {
            label.setIcon(null);
            // **IMPORTANT** to call revalidate() to cause JLabel to resize and be repainted.
            label.revalidate();
        } else {
            Image image = icon.getImage();
            Image imageScale = image.getScaledInstance(label.getWidth(),
                    label.getHeight(),
                    Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(imageScale));
            label.revalidate();
        }
    }

    public void setStranger(String stranger) {
        this.stranger = stranger;
        lblNickname.setText(stranger);
    }

    private void initComponents() {
        cardLayout = (CardLayout) pnlScreen.getLayout();
        webcamPanel = new WebcamPanel();
        pnlScreen.add(webcamPanel, "webcam");

        btnAcceptCall.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                RunClient.socketHandler.acceptCall(stranger);
            }
        });

        btnEndCall.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                stopMicrophone();
                stopWebcam();
                RunClient.socketHandler.endCall(stranger);
            }
        });

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                RunClient.socketHandler.endCall(stranger);
            }
        });

        voiceCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    microphone = new VoiceRecorder();
                    microphone.start();
                } else {
                    stopMicrophone();
                }
            }
        });

        cameraCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                   cardLayout.show(pnlScreen, "cardVideo");
                   webcam = new VideoRecorder();
                   webcam.start();
                } else {
                    stopWebcam();
                }
            }
        });
    }
}
