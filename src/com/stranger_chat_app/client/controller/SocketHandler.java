package com.stranger_chat_app.client.controller;

import com.stranger_chat_app.client.RunClient;
import com.stranger_chat_app.client.view.enums.GUIName;
import com.stranger_chat_app.shared.constant.DataType;
import com.stranger_chat_app.shared.model.Data;
import com.stranger_chat_app.shared.security.AESUtil;
import com.stranger_chat_app.shared.security.RSAUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketHandler {
    private Socket socket;
    private ObjectOutput out;
    private ObjectInput in;

    String nickname = null; // lưu nickname hiện tại
    String roomId = null; // lưu room hiện tại

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

            // connect success
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

                        case EXIT:
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

        // alert if connect interup
//        JOptionPane.showMessageDialog(null, "Mất kết nối tới server", "Lỗi", JOptionPane.ERROR_MESSAGE);
//        RunClient.closeAllScene();
//        RunClient.openScene(RunClient.SceneName.CONNECTSERVER);
    }


    private void sendSecretKey() throws IOException, NoSuchAlgorithmException {
//        Generate Key pair to get Public & Private key
        this.secretKey = AESUtil.generateAESKey();
        String strSecretKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        sendData(DataType.SECRET_KEY, strSecretKey);
    }

    private void sendData(DataType dataType, String content) {
        System.out.println("Sent: " + dataType + " - " + content);
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
        // tắt Login GUI khi client nhận được phản hồi "đã nhận được secret key" từ server
//        RunClient.closeGUI(GUIName.LOGIN);
        // mở GUI
//        RunClient.openGUI();
        System.out.println("CLOSE LOGIN");
    }
}
