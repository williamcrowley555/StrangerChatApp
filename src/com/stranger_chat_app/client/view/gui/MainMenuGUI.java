package com.stranger_chat_app.client.view.gui;

import com.stranger_chat_app.client.RunClient;
import com.stranger_chat_app.client.view.enums.MainMenuState;
import com.stranger_chat_app.shared.helper.CountdownTimer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;

public class MainMenuGUI extends JFrame {
    private JPanel pnlMain;
    private JPanel pnlLogout;
    private JPanel pnlWaiting;
    private JButton btnLogout;
    private JLabel lblWaiting;
    private JProgressBar progressBar1;
    private JButton btnCancelPairUp;
    private JPanel pnlStrangerFound;
    private JButton btnDecline;
    private JButton btnAccept;
    private JLabel lblFoundStranger;
    private JLabel lblPairUpCountdown;
    private JPanel pnlPairUp;
    private JButton btnPairUp;

    CountdownTimer acceptPairUpTimer;
    CountdownTimer waitingPairUpTimer;
    final int acceptWaitingTime = 15;

    boolean isAcceptingPairUp = false;

    public MainMenuGUI() {
        super();
        setTitle("Màn hình chính - " + RunClient.socketHandler.getNickname());
        setContentPane(pnlMain);
        setSize(540, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        initComponents();

        setDisplayState(MainMenuState.DEFAULT);
    }

    public void setDisplayState(MainMenuState state) {
        // hiển thị tất cả components
        showAllComponents();

        // ẩn các components tùy theo state
        switch (state) {
            case DEFAULT:
                stopWaitingPairUpTimer();
                stopAcceptPairUpTimer();
                pnlWaiting.setVisible(false);
                pnlStrangerFound.setVisible(false);
                break;

            case FINDING_STRANGER:
                startWaitingPairUpTimer();
                stopAcceptPairUpTimer();
                pnlStrangerFound.setVisible(false);
                btnLogout.setEnabled(false);
                break;

            case WAITING_ACCEPT:
                stopWaitingPairUpTimer();
                startAcceptPairUpTimer();
                isAcceptingPairUp = false;
                pnlWaiting.setVisible(false);
                btnLogout.setEnabled(false);
                break;

            case WAITING_STRANGER_ACCEPT:
                isAcceptingPairUp = true;
                pnlWaiting.setVisible(false);
                btnAccept.setEnabled(false);
                btnDecline.setEnabled(false);
                btnLogout.setEnabled(false);
                lblWaiting.setText("Đang chờ đối phương chấp nhận..");
                break;
        }
    }

    private void showAllComponents() {
        btnLogout.setEnabled(true);
        pnlWaiting.setVisible(true);
        pnlStrangerFound.setVisible(true);
        btnAccept.setEnabled(true);
        btnDecline.setEnabled(true);
        btnCancelPairUp.setEnabled(true);
        btnPairUp.setEnabled(true);
    }

    private void startAcceptPairUpTimer() {
        acceptPairUpTimer = new CountdownTimer(acceptWaitingTime);
        acceptPairUpTimer.setTimerCallBack(
                // end callback
                (Callable) () -> {
                    // reset acceptPairMatchTimer
                    acceptPairUpTimer.restart();
                    acceptPairUpTimer.pause();

                    // automatically decline if the time has passed without accepting
                    if (!isAcceptingPairUp) {
                        RunClient.socketHandler.declinePairUp();
                    }
                    return null;
                },
                // tick callback
                (Callable) () -> {
                    lblPairUpCountdown.setText(acceptPairUpTimer.getCurrentTick() + " s");
                    return null;
                },
                // tick interval
                1
        );
    }

    private void stopAcceptPairUpTimer() {
        if (acceptPairUpTimer != null) {
            acceptPairUpTimer.cancel();
        }
    }

    private void startWaitingPairUpTimer() {
        waitingPairUpTimer = new CountdownTimer(5 * 60); // 5 min
        waitingPairUpTimer.setTimerCallBack(
                (Callable) () -> {
                    setDisplayState(MainMenuState.DEFAULT);
                    JOptionPane.showMessageDialog(this, "Rất tiếc! Không tìm thấy ai để chat.");
                    return null;
                },
                (Callable) () -> {
                    lblWaiting.setText("Đang tìm người để chat... " + (5 * 60 - waitingPairUpTimer.getCurrentTick()) + " s");
                    return null;
                },
                1
        );
    }

    private void stopWaitingPairUpTimer() {
        if (waitingPairUpTimer != null) {
            waitingPairUpTimer.cancel();
        }
    }

    public void foundStranger(String strangerNickname) {
        setDisplayState(MainMenuState.WAITING_ACCEPT);
        lblFoundStranger.setText("Bắt đầu chat cùng " + strangerNickname + "?");
    }

    private void initComponents() {
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RunClient.socketHandler.logout();
            }
        });

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                RunClient.socketHandler.logout();
            }
        });

        btnPairUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RunClient.socketHandler.pairUp();
            }
        });

        btnAccept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDisplayState(MainMenuState.WAITING_STRANGER_ACCEPT);
                RunClient.socketHandler.acceptPairUp();
            }
        });

        btnDecline.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDisplayState(MainMenuState.DEFAULT);
                RunClient.socketHandler.declinePairUp();
            }
        });

        btnCancelPairUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RunClient.socketHandler.cancelPairUp();
            }
        });
    }
}
