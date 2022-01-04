package com.stranger_chat_app.client;

import com.stranger_chat_app.client.controller.SocketHandler;
import com.stranger_chat_app.client.view.enums.GUIName;
import com.stranger_chat_app.client.view.gui.LoginGUI;

public class RunClient {

//    GUIs
    public static LoginGUI loginGUI;

    // controller
    public static SocketHandler socketHandler;

    public RunClient() {
        socketHandler = new SocketHandler();
        initGUIs();
        openGUI(GUIName.LOGIN);
    }

    public void initGUIs() {
        loginGUI = new LoginGUI();
    }

    public static void openGUI(GUIName guiName) {
        if (guiName != null) {
            switch (guiName) {
                case LOGIN:
                    loginGUI = new LoginGUI();
                    loginGUI.setVisible(true);
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

                default:
                    break;
            }
        }
    }

    public static void closeAllGUIs() {
        loginGUI.dispose();
    }

    public static void main(String[] args) {
        new RunClient();
    }
}
