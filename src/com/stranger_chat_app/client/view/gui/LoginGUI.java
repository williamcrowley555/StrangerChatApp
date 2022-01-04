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

    private void initComponents() {
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
                        String hostname = "localhost";
                        int port = 5004;
                        connectToServer(hostname, port);
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
                    String hostname = "localhost";
                    int port = 5004;
                    connectToServer(hostname, port);
                }
            }
        });
    }

    public void connectToServer(String hostname, int port) {
        new Thread(() -> {
            // establish connection
            boolean isConnected = RunClient.socketHandler.connect(hostname, port);

            // check result
            if (isConnected) {
                onSuccess();
            } else {
                String failedMsg = "Kết nối thất bại!";
                onFailed(failedMsg);
            }
        }).start();
    }

    private void onSuccess() {
        // Kết nối thành công nhưng vẫn chờ server gửi thông báo đã nhận secret key
        // Chuyển qua GUI khi client nhận được phản hồi từ server
        // => code mở GUI được thực hiện ở socket handler, lúc listen nhận được secret key từ server

        setLoading(true, "Đang xử lý...");
    }

    private void onFailed(String failedMsg) {
        setLoading(false, null);
        JOptionPane.showMessageDialog(this, failedMsg, "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
    }

    public void setLoading(boolean isLoading, String btnText) {
        pgbLoading.setVisible(isLoading);
        btnLogin.setEnabled(!isLoading);
        btnLogin.setText(isLoading ? btnText : "Tham gia");
    }
}
