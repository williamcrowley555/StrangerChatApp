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
    private JButton btnCancelPairUp;
    private JPanel pnlStrangerFound;
    private JButton btnDecline;
    private JButton btnAccept;
    private JLabel lblFoundStranger;
    private JLabel lblPairUpCountdown;
    private JPanel pnlPairUp;
    private JButton btnPairUp;
    private JProgressBar pgbLoading;
    private JPanel pnlHeader;

    CountdownTimer acceptPairUpTimer;
    CountdownTimer waitingPairUpTimer;
    final int acceptWaitingTime = 15;

    boolean isAcceptingPairUp = false;

    public MainMenuGUI() {
        super();
        setTitle("Màn hình chính - Bạn: " + RunClient.socketHandler.getNickname());
        setContentPane(pnlMain);
        setSize(540, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
                btnPairUp.setEnabled(false);
                btnLogout.setEnabled(false);
                break;

            case WAITING_ACCEPT:
                stopWaitingPairUpTimer();
                startAcceptPairUpTimer();
                isAcceptingPairUp = false;
                pnlWaiting.setVisible(false);
                btnPairUp.setEnabled(false);
                btnLogout.setEnabled(false);
                break;

            case WAITING_STRANGER_ACCEPT:
                isAcceptingPairUp = true;
                pnlWaiting.setVisible(false);
                btnAccept.setEnabled(false);
                btnDecline.setEnabled(false);
                btnPairUp.setEnabled(false);
                btnLogout.setEnabled(false);
                lblWaiting.setText("Đang chờ phản hồi từ đối phương..");
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

    public void stopAcceptPairUpTimer() {
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
                    int waitingTime = waitingPairUpTimer.getTimeLimit() - waitingPairUpTimer.getCurrentTick();
                    int mins = waitingTime / 60;
                    int secs = waitingTime % 60;

                    lblWaiting.setText("<html><body style='text-align:center;'>Đang tìm người để chat...<br>" +
                            (mins < 10 ? "0" + mins : mins) + ":" + (secs < 10 ? "0" + secs : secs) + "</body></html>");
                    return null;
                },
                1
        );
    }

    public void stopWaitingPairUpTimer() {
        if (waitingPairUpTimer != null) {
            waitingPairUpTimer.cancel();
        }
    }

    public void foundStranger(String strangerNickname) {
        setDisplayState(MainMenuState.WAITING_ACCEPT);
        lblFoundStranger.setText("Bắt đầu chat cùng " + strangerNickname + "?");
    }

    private void initComponents() {
        btnLogout.setOpaque(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorder(null);
        btnLogout.setBorderPainted(false);

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
