package com.stranger_chat_app.client.controller;

import com.stranger_chat_app.client.RunClient;
import com.stranger_chat_app.client.view.enums.GUIName;
import com.stranger_chat_app.client.view.enums.MainMenuState;
import com.stranger_chat_app.client.view.gui.ChatRoomGUI;
import com.stranger_chat_app.server.controller.MyFile;
import com.stranger_chat_app.shared.constant.DataType;
import com.stranger_chat_app.shared.model.Data;
import com.stranger_chat_app.shared.model.Message;
import com.stranger_chat_app.shared.security.AESUtil;
import com.stranger_chat_app.shared.security.BytesUtil;
import com.stranger_chat_app.shared.security.RSAUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketHandler {
    private Socket socket;
    private ObjectOutput out;
    private ObjectInput in;

    String nickname = null; // lưu nickname hiện tại

    Thread listener = null;

    PublicKey publicKey;
    SecretKey secretKey;

    public boolean connect(String hostname, int port) {
        try {
            // establish the connection with server port
            socket = new Socket(hostname, port);
            System.out.println("Connected to " + hostname + ":" + port + ", localport: " + socket.getLocalPort());

            // obtaining input and output streams
            this.out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            this.out.flush();
            this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

            // close old listener
            if (listener != null && listener.isAlive()) {
                listener.interrupt();
            }

            // listen to server
            listener = new Thread(this::listen);
            listener.start();

            // connect successfully
            return true;

        } catch (IOException e) {
            // connect failed
            return false;
        }
    }

    private void listen() {
        boolean isRunning = true;
        Data receivedData;
        String receivedContent = null;

        try {
            while (isRunning) {
                receivedData = (Data) in.readObject();

                if (receivedData != null) {
                    if (secretKey != null && receivedData.getContent() != null)
                        receivedContent = AESUtil.decrypt(secretKey, receivedData.getContent());

                    switch (receivedData.getDataType()) {
                        case PUBLIC_KEY:
                            String strPublicKey = receivedData.getContent();
                            publicKey = RSAUtil.getPublicKey(Base64.getDecoder().decode(strPublicKey));
                            sendSecretKey();
                            break;

                        case RECEIVED_SECRET_KEY:
                            onReceivedSecretKey(receivedContent);
                            break;

                        case LOGIN:
                            onReceiveLogin(receivedContent);
                            break;

                        case PAIR_UP_WAITING:
                            onReceivePairUpWaiting(receivedContent);
                            break;

                        case CANCEL_PAIR_UP:
                            onReceiveCancelPairUp(receivedContent);
                            break;

                        case REQUEST_PAIR_UP:
                            onReceiveRequestPairUp(receivedContent);
                            break;

                        case RESULT_PAIR_UP:
                            onReceiveResultPairUp(receivedContent);
                            break;

                        case JOIN_CHAT_ROOM:
                            onReceiveJoinChatRoom(receivedContent);
                            break;

                        case CHAT_MESSAGE:
                            onReceiveChatMessage(receivedContent);
                            break;

                        case SEND_FILE:
                            onReceiveFile(receivedContent);
                            break;

                        case DOWNLOAD:
                            onDownload(receivedContent);
                            break;

                        case LEAVE_CHAT_ROOM:
                            onReceiveLeaveChatRoom(receivedContent);
                            break;

                        case CLOSE_CHAT_ROOM:
                            onReceiveCloseChatRoom(receivedContent);
                            break;

                        case LOGOUT:
                            onReceiveLogout(receivedContent);
                            break;

                        case EXIT:
                            onReceiveExit(receivedContent);
                            isRunning = false;
                            break;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            isRunning = false;
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // closing resources
                in.close();
                out.close();
                socket.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }


    private void sendSecretKey() throws IOException, NoSuchAlgorithmException {
//        Generate Key pair to get Public & Private key
        this.secretKey = AESUtil.generateAESKey();
        String strSecretKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        sendData(DataType.SECRET_KEY, strSecretKey);
    }

    private void sendData(DataType dataType, String content) {
        Data data;
        String encryptedContent = null;

        if (dataType.equals(DataType.SECRET_KEY)) {
            try {
                encryptedContent = RSAUtil.encrypt(publicKey, content);
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }

            data = new Data(dataType, encryptedContent);
        } else if (secretKey != null && content != null) {
            try {
                encryptedContent = AESUtil.encrypt(secretKey, content);
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }

            data = new Data(dataType, encryptedContent);
        } else {
            data = new Data(dataType, content);
        }

        try {
            out.writeObject(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onReceivedSecretKey(String received) {
        // tắt loading và cho phép client bắt đầu đăng nhập
        RunClient.loginGUI.setLoading(false, null);
    }

    private void onReceiveLogin(String received) {
        String[] splitted = received.split(";");
        String status = splitted[0];

        if (status.equals("failed")) {
            String failedMsg = splitted[1];
            RunClient.loginGUI.onFailed(failedMsg);

        } else if (status.equals("success")) {
            this.nickname = splitted[1];

            // tắt Login GUI khi client đăng nhập thành công
            RunClient.closeGUI(GUIName.LOGIN);
            // mở Main Menu GUI
            RunClient.openGUI(GUIName.MAIN_MENU);
        }
    }

    private void onReceiveResultPairUp(String received) {
        String[] splitted = received.split(";");
        String status = splitted[0];

        if (status.equals("failed")) {
            String failedMsg = splitted[1];
            int option = JOptionPane.showConfirmDialog(RunClient.mainMenuGUI, failedMsg + ". Tiếp tục ghép đôi?", "Ghép đôi thất bại",
                                        JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

            // stop pairing
            // reset display state of main menu
            RunClient.mainMenuGUI.setDisplayState(MainMenuState.DEFAULT);

            if(option == JOptionPane.YES_OPTION) {
                // continue pairing
                pairUp();
                return;
            }
        } else if (status.equals("success")) {
            // reset display state of main menu
            RunClient.mainMenuGUI.setDisplayState(MainMenuState.DEFAULT);

            System.out.println("Ghép đôi thành công");
        }
    }

    private void onReceivePairUpWaiting(String received) {
        RunClient.mainMenuGUI.setDisplayState(MainMenuState.FINDING_STRANGER);
    }

    private void onReceiveCancelPairUp(String received) {
        RunClient.mainMenuGUI.setDisplayState(MainMenuState.DEFAULT);
    }

    private void onReceiveRequestPairUp(String received) {
        // show stranger found state
        RunClient.mainMenuGUI.foundStranger(received);
    }

    private void onReceiveJoinChatRoom(String received) {
        // change GUI
        RunClient.closeGUI(GUIName.MAIN_MENU);
        RunClient.openGUI(GUIName.CHAT_ROOM);
        RunClient.chatRoomGUI.setClients(this.nickname, received);
    }

    private void onReceiveChatMessage(String received) {
        // convert received JSON message to Message
        Message message = Message.parse(received);
        RunClient.chatRoomGUI.addChatMessage(message);
    }

    private void onReceiveFile(String received) {
        // convert received JSON message to Message
        Message message = Message.parse(received);
        MyFile myFile = MyFile.parse(message.getContent());
        message.setContent(myFile.getName());
        RunClient.chatRoomGUI.addFileMessage(message);
    }

    private void onDownload(String received) {
        // convert received JSON message to Message
        Message message = Message.parse(received);
        MyFile myFile = MyFile.parse(message.getContent());

            if (ChatRoomGUI.path != null)
            {
                try {
                    String fileName = ChatRoomGUI.path.substring(ChatRoomGUI.path.lastIndexOf("\\"));
                    String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                    String originalFileExtension = myFile.getName().substring(myFile.getName().lastIndexOf("."));

                    if (fileExtension.equals(originalFileExtension)){
                        FileOutputStream output = new FileOutputStream(
                                new File(ChatRoomGUI.path));
                        output.write(myFile.getData());
                        output.close();
                    }
                    else
                        JOptionPane.showMessageDialog(null,"Định dạng file lưu khác file gốc, lưu file thất bại!");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } else {
                System.out.println("Chưa chọn đường dẫn");
            }
    }

    private void onReceiveLeaveChatRoom(String received) {
        // change GUI
        RunClient.closeGUI(GUIName.CHAT_ROOM);
        RunClient.openGUI(GUIName.MAIN_MENU);
    }

    private void onReceiveCloseChatRoom(String received) {
        // change GUI
        RunClient.closeGUI(GUIName.CHAT_ROOM);
        RunClient.openGUI(GUIName.MAIN_MENU);

        // show notification
        JOptionPane.showMessageDialog(
                RunClient.mainMenuGUI,
                "Kết thúc trò chuyện do " + received, "Đóng",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void onReceiveLogout(String received) {
        // xóa nickname
        this.nickname = null;

        // chuyển sang login GUI
        RunClient.closeGUI(GUIName.MAIN_MENU);
        RunClient.openGUI(GUIName.LOGIN);
    }

    private void onReceiveExit(String received) {
        // đóng tất cả GUIs
        RunClient.closeAllGUIs();
    }

    public void login(String nickname) {
        sendData(DataType.LOGIN, nickname);
    }

    public void pairUp() {
        sendData(DataType.PAIR_UP, null);
    }

    public void acceptPairUp() {
        sendData(DataType.PAIR_UP_RESPONSE, "yes");
    }

    public void declinePairUp() {
        sendData(DataType.PAIR_UP_RESPONSE, "no");
    }

    public void cancelPairUp() {
        sendData(DataType.CANCEL_PAIR_UP, null);
    }

    public void sendChatMessage(Message message) {
        sendData(DataType.CHAT_MESSAGE, message.toJSONString());
    }

    public void sendFile(Message message) {
        sendData(DataType.SEND_FILE, message.toJSONString());
    }

    public void download(Message message) {
        sendData(DataType.DOWNLOAD, message.toJSONString());
    }

    public void leaveChatRoom() {
        sendData(DataType.LEAVE_CHAT_ROOM, null);
    }

    public void logout() {
        sendData(DataType.LOGOUT, null);
    }

    public void exit() {
        sendData(DataType.EXIT, null);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
