package com.stranger_chat_app.client.view.gui;

import com.stranger_chat_app.client.RunClient;

import javax.swing.*;

public class CallGUI extends JFrame{
    private JPanel panel1;
    private JPanel mainPanel;
    private JPanel videoPanel;
    private JPanel buttonsPanel;
    private JCheckBox cameraCheckBox;
    private JCheckBox voiceCheckBox;
    private JButton endCallButton;

    public CallGUI(){
        super();
        setTitle("Video call");
        setContentPane(mainPanel);
        setSize(600, 600);
        setLocationRelativeTo(null);
    }
}
