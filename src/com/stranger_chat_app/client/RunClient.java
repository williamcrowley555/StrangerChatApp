package com.stranger_chat_app.client;

import com.stranger_chat_app.client.controller.SocketHandler;
import com.stranger_chat_app.client.view.enums.GUIName;
import com.stranger_chat_app.client.view.gui.CallGUI;
import com.stranger_chat_app.client.view.gui.ChatRoomGUI;
import com.stranger_chat_app.client.view.gui.LoginGUI;
import com.stranger_chat_app.client.view.gui.MainMenuGUI;

import javax.swing.*;

public class RunClient {
    private String hostname = "localhost";
    private int port = 5004;

//    GUIs
    public static LoginGUI loginGUI;
    public static MainMenuGUI mainMenuGUI;
    public static ChatRoomGUI chatRoomGUI;
    public static CallGUI callGUI;

//    Controller
    public static SocketHandler socketHandler;

    public RunClient() {
        socketHandler = new SocketHandler();

        // Customize LookAndFeel UI
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch(Exception ignored){}

        initGUIs();
    }

    private void run() {
        openGUI(GUIName.LOGIN);
        // first time running will wait for connection
        loginGUI.setLoading(true, "Đang chờ kết nối...");

        connectToServer(hostname, port);
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
        // Cho phép client đăng nhập khi nhận được phản hồi từ server

        loginGUI.setLoading(true, "Đang chờ bảo mật...");;
    }

    private void onFailed(String failedMsg) {
        loginGUI.setLoading(false, null);
        JOptionPane.showMessageDialog(loginGUI, failedMsg, "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    public void initGUIs() {
        loginGUI = new LoginGUI();
        mainMenuGUI = new MainMenuGUI();
        chatRoomGUI = new ChatRoomGUI();
        callGUI = new CallGUI();
    }

    public static void openGUI(GUIName guiName) {
        if (guiName != null) {
            switch (guiName) {
                case LOGIN:
                    loginGUI = new LoginGUI();
                    loginGUI.setVisible(true);
                    break;

                case MAIN_MENU:
                    mainMenuGUI = new MainMenuGUI();
                    mainMenuGUI.setVisible(true);
                    break;

                case CHAT_ROOM:
                    chatRoomGUI = new ChatRoomGUI();
                    chatRoomGUI.setVisible(true);
                    break;

                case CALL:
                    callGUI = new CallGUI();
                    callGUI.setVisible(true);
                    break;

                default:
                    break;
            }
        }
    }

    public static void closeGUI(GUIName guiName) {
        if (guiName != null) {
            switch (guiName) {
                case LOGIN:
                    loginGUI.dispose();
                    break;

                case MAIN_MENU:
                    mainMenuGUI.dispose();
                    break;

                case CHAT_ROOM:
                    chatRoomGUI.dispose();
                    break;

                case CALL:
                    callGUI.stopAudio();
                    callGUI.stopMicrophone();
                    callGUI.dispose();
                    break;

                default:
                    break;
            }
        }
    }

    public static void closeAllGUIs() {
        loginGUI.dispose();
        mainMenuGUI.dispose();
    }

    public static void main(String[] args) {
        new RunClient().run();
    }
}
