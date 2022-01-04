package com.stranger_chat_app.client.view.gui;

import javax.swing.*;

public class MainMenuGUI extends JFrame {
    private JPanel pnlMain;
    private JPanel pnlLogout;
    private JPanel pnlWaiting;
    private JButton btnLogout;
    private JLabel lblWaiting;
    private JProgressBar progressBar1;
    private JButton btnCancelFinding;
    private JPanel pnlFoundStranger;
    private JButton btnDecline;
    private JButton btnAccept;
    private JLabel lblFoundStranger;
    private JLabel lblPairUpCountdown;
    private JPanel pnlPairUp;
    private JButton btnPairUp;

    public MainMenuGUI() {
        super();
        setTitle("Ghép đôi");
        setContentPane(pnlMain);
        setSize(540, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {

    }
}
