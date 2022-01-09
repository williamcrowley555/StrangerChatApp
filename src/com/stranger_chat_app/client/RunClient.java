package com.stranger_chat_app.client;

import com.stranger_chat_app.client.controller.SocketHandler;
import com.stranger_chat_app.client.view.enums.GUIName;
import com.stranger_chat_app.client.view.gui.ChatRoomGUI;
import com.stranger_chat_app.client.view.gui.LoginGUI;
import com.stranger_chat_app.client.view.gui.MainMenuGUI;

import javax.swing.*;

public class RunClient {

//    GUIs
    public static LoginGUI loginGUI;
    public static MainMenuGUI mainMenuGUI;
    public static ChatRoomGUI chatRoomGUI;

    // controller
    public static SocketHandler socketHandler;

    public RunClient() {
        socketHandler = new SocketHandler();

        // Customize LookAndFeel UI
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch(Exception e){
            e.printStackTrace();
        }

        initGUIs();
        openGUI(GUIName.LOGIN);
    }

    public void initGUIs() {
        loginGUI = new LoginGUI();
        mainMenuGUI = new MainMenuGUI();
        chatRoomGUI = new ChatRoomGUI();
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
        new RunClient();
    }
}
