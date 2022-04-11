package com.stranger_chat_app.client.view.gui;

import com.stranger_chat_app.client.RunClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LoginGUI extends JFrame {
    private JTextField txtNickname;
    private JPanel pnlMain;
    private JLabel lblNickname;
    private JButton btnLogin;
    private JProgressBar pgbLoading;
    private JPanel pnlHeader;

    public LoginGUI() {
        super();
        setTitle("Đăng nhập");
        setContentPane(pnlMain);
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initComponents();

        // default pgbLoading is hidden
        pgbLoading.setVisible(false);
    }

    public void onFailed(String failedMsg) {
        setLoading(false, null);
        JOptionPane.showMessageDialog(this, failedMsg, "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
    }

    public void setLoading(boolean isLoading, String btnText) {
        pgbLoading.setVisible(isLoading);
        btnLogin.setEnabled(!isLoading);
        btnLogin.setText(isLoading ? btnText : "Tham gia");
    }

    private void initComponents() {
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                RunClient.socketHandler.exit();
            }
        });

        txtNickname.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    String nickname = txtNickname.getText();
                    if (nickname.isEmpty()) {
                        JOptionPane.showMessageDialog(pnlMain, "Vui lòng nhập nickname của bạn", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    } else if (nickname.length() > 15) {
                        JOptionPane.showMessageDialog(pnlMain, "Nickname không được đặt quá 15 ký tự", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // Chờ server kiểm tra đăng nhập
                        setLoading(true, "Đang xử lý...");

                        RunClient.socketHandler.login(nickname);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nickname = txtNickname.getText();

                if (nickname.isEmpty()) {
                    JOptionPane.showMessageDialog(pnlMain, "Vui lòng nhập nickname của bạn", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else if (nickname.length() > 15) {
                    JOptionPane.showMessageDialog(pnlMain, "Nickname không được đặt quá 15 ký tự", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Chờ server kiểm tra đăng nhập
                    setLoading(true, "Đang xử lý...");

                    RunClient.socketHandler.login(nickname);
                }
            }
        });
    }
}
