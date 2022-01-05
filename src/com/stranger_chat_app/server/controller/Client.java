package com.stranger_chat_app.server.controller;

import com.stranger_chat_app.server.RunServer;
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
import java.util.*;

public class Client implements Runnable {
    private Socket clientSocket;

    private ObjectOutput out;
    private ObjectInput in;

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private SecretKey secretKey;

    String nickname;
    Client stranger;

    boolean isWaiting = false;
    String acceptPairingStatus = "";

    public Client(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;

        this.out = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
        this.out.flush();
        this.in = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        boolean isRunning = true;
        Data receivedData;
        String receivedContent = null;

        try {
            sendPublicKey();

            while (isRunning) {
                receivedData = (Data) in.readObject();

                if (receivedData != null) {

                    if (secretKey != null && receivedData.getContent() != null)
                        receivedContent = AESUtil.decrypt(secretKey, receivedData.getContent());

                    switch (receivedData.getDataType()) {
                        case SECRET_KEY:
                            receivedContent = RSAUtil.decrypt(privateKey, receivedData.getContent());
                            secretKey = AESUtil.getAESKey(Base64.getDecoder().decode(receivedContent));
                            sendData(DataType.RECEIVED_SECRET_KEY, null);
                            break;

                        case CLIENT_INFO:
                            onReceiveClientInfo(receivedData.getContent());
                            break;

                        case START_WAITING:
                            System.out.println("find a client...");
                            break;

                        case LOGOUT:
                            System.out.println(nickname + " has exited");
                            onReceiveLogout(receivedContent);
                            isRunning = false;
                            break;
                    }
                }
            }
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
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
                clientSocket.close();

                // remove from clientManager
                RunServer.clientManager.remove(this);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void sendPublicKey() throws IOException, NoSuchAlgorithmException {
//        Generate Key pair to get Public & Private key
        KeyPair keyPair = RSAUtil.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
        String strPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());

        sendData(DataType.PUBLIC_KEY, strPublicKey);
    }

    public void sendData(DataType dataType, String content) {
        Data data;
        String encryptedContent = null;

        if (secretKey != null && content != null) {
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

    private void onReceiveClientInfo(String received) {
        this.nickname = received;
        System.out.println("Received nickname: " + received);
    }

    private void onReceiveLogout(String received) {
        // log out now
        this.nickname = null;
        this.isWaiting = false;

        // TODO leave room
        // TODO broadcast to all clients
        sendData(DataType.LOGOUT, null);
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Client getStranger() {
        return stranger;
    }

    public void setStranger(Client stranger) {
        this.stranger = stranger;
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
    }

    public String getAcceptPairingStatus() {
        return acceptPairingStatus;
    }

    public void setAcceptPairingStatus(String acceptPairingStatus) {
        this.acceptPairingStatus = acceptPairingStatus;
    }
}
