package com.stranger_chat_app.server.controller;

import com.stranger_chat_app.server.RunServer;
import com.stranger_chat_app.shared.constant.Code;
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

    private String nickname;
    private Client stranger;
    private volatile Set<String> rejectedClients = new HashSet<>();

    private boolean isWaiting = false;
    private String acceptPairUpStatus = "";     // value: "yes", "no", ""

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

                        case LOGIN:
                            onReceiveLogin(receivedContent);
                            break;

                        case PAIR_UP:
                            onReceivePairUp(receivedContent);
                            break;

                        case CANCEL_PAIR_UP:
                            onReceiveCancelPairUp(receivedContent);
                            break;

                        case PAIR_UP_RESPONSE:
                            onReceivePairUpResponse(receivedContent);
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

                        case CALLING:
                            onReceiveCalling(receivedContent);
                            break;

                        case ACCEPT_CALL:
                            onReceiveAcceptCall(receivedContent);
                            break;

                        case END_CALL:
                            onReceiveEndCall(receivedContent);
                            break;

                        case VOICE:
                            onReceiveVoice(receivedContent);
                            break;

                        case VIDEO_STREAM:
                            onReceiveVideoStream(receivedContent);
                            break;

                        case STOP_VIDEO_STREAM:
                            onReceiveStopVideoStream(receivedContent);
                            break;
                            
                        case AUDIO:
                            onReceiveAudio(receivedContent);
                            break;

                        case LOGOUT:
                            System.out.println(nickname + " logged out");
                            onReceiveLogout(receivedContent);
                            break;

                        case EXIT:
                            onReceiveExit(receivedContent);
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

    private void onReceiveLogin(String received) {
        String status = "failed;";
        Client existedClient = RunServer.clientManager.find(received);

        if (existedClient == null) {
            status = "success;";
            this.nickname = received;

            sendData(DataType.LOGIN, status + nickname);
        } else {
            sendData(DataType.LOGIN, status + Code.NICKNAME_HAS_BEEN_USED);
        }
    }

    private void onReceivePairUp(String received) {
        // Kiếm có ai khác đang đợi ghép cặp không
        Client stranger = RunServer.clientManager.findWaitingClient(this, rejectedClients);

        if (stranger == null) {
            // đặt cờ là đang đợi ghép cặp
            this.isWaiting = true;

            // client hiển thị trạng thái đợi ghép cặp
            sendData(DataType.PAIR_UP_WAITING, null);

        } else {
            // nếu có người cũng đang đợi ghép đôi thì bắt đầu hỏi yêu cầu ghép cặp
            // trong lúc hỏi thì phải tắt trạng thái đợi của 2 bên (để nếu client khác ghép đôi thì sẽ tránh việc bị ghép đè)
            this.isWaiting = false;
            stranger.isWaiting = false;

            // lưu email đối thủ để dùng khi server nhận được result-pair-match
            this.stranger = stranger;
            stranger.stranger = this;

            // trả thông tin đối phương về cho 2 clients
            this.sendData(DataType.REQUEST_PAIR_UP, stranger.nickname);
            stranger.sendData(DataType.REQUEST_PAIR_UP, this.nickname);
        }
    }

    private void onReceiveCancelPairUp(String received) {
        // gỡ cờ đang đợi ghép cặp
        this.isWaiting = false;

        // báo cho client để tắt giao diện đang đợi ghép cặp
        sendData(DataType.CANCEL_PAIR_UP, null);
    }

    private void onReceivePairUpResponse(String received) {
        // save accept pair status
        this.acceptPairUpStatus = received;

        // if stranger has left
        if (stranger == null) {
            sendData(DataType.RESULT_PAIR_UP, "failed;" + Code.STRANGER_LEAVE);
            return;
        }

        // if one decline
        if (received.equals("no")) {
            // if both have no response (both will decline)
            // check the rejected list of the stranger to avoid sending the rejection response twice
            // if this client is on the stranger's rejected list, it means that the stranger refused first
            if (!this.stranger.getRejectedClients().contains(this.nickname)) {
                // add rejected client to list
                this.rejectedClients.add(stranger.getNickname());

                // reset acceptPairUpStatus
                this.acceptPairUpStatus = "";
                stranger.acceptPairUpStatus = "";

                // send data
                this.sendData(DataType.RESULT_PAIR_UP, "failed;" + Code.YOU_CHOOSE_NO);
                stranger.sendData(DataType.RESULT_PAIR_UP, "failed;" + Code.STRANGER_CHOOSE_NO);
            }
        }

        // if both accept
        if (received.equals("yes") && stranger.acceptPairUpStatus.equals("yes")) {
            // send success pair match
            this.sendData(DataType.RESULT_PAIR_UP, "success");
            stranger.sendData(DataType.RESULT_PAIR_UP, "success");

            // send join chat room status to client
            sendData(DataType.JOIN_CHAT_ROOM, stranger.nickname);
            stranger.sendData(DataType.JOIN_CHAT_ROOM, this.nickname);

            // reset acceptPairMatchStatus
            this.acceptPairUpStatus = "";
            stranger.acceptPairUpStatus = "";
        }
    }

    private void onReceiveChatMessage(String received) {
        Message message = Message.parse(received);
        Client stranger = RunServer.clientManager.find(message.getRecipient());

        if (stranger != null) {
            // send message to stranger
            stranger.sendData(DataType.CHAT_MESSAGE, received);
        }
    }

    private void onReceiveFile(String received) {
        Message message = Message.parse(received);
        Client stranger = RunServer.clientManager.find(message.getRecipient());
        MyFile myFile = MyFile.parse(message.getContent());

        File filesFolder = new File(System.getProperty("user.dir")
                + "\\src\\com\\stranger_chat_app\\server\\client-files");
        if (!filesFolder.exists()) {
            filesFolder.mkdir();
        }

        File clientFolder = new File(filesFolder.getAbsolutePath() + "\\" + message.getSender());
        if (!clientFolder.exists()) {
            clientFolder.mkdir();
        }

        try {
            FileOutputStream output = new FileOutputStream(
                    new File(clientFolder.getAbsolutePath() + "\\" + myFile.getName()));
            output.write(myFile.getData());
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        if (stranger != null) {
            // send message to stranger
            stranger.sendData(DataType.SEND_FILE, received);
        }
    }

    private void onDownload(String received) {
        Message message = Message.parse(received);
        System.out.println(message.getContent());
        File filesFolder = new File(System.getProperty("user.dir")
                + "\\src\\com\\stranger_chat_app\\server\\client-files");
        File clientFolder = new File(filesFolder.getAbsolutePath() + "\\" + message.getSender());
        if (!clientFolder.exists()) {
            clientFolder.mkdir();
        }

        File fileToDowload = new File(filesFolder.getAbsolutePath() + "\\" + message.getSender() +
                "\\" + message.getContent());
        if (!fileToDowload.exists()) {
            System.out.println("Not exist");
        } else {
            System.out.println("Found");
            Client stranger = RunServer.clientManager.find(message.getRecipient());

            if (stranger != null) {
                // send message to stranger
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(fileToDowload.getAbsoluteFile());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                String fileName = fileToDowload.getName();
                byte[] fileContentBytes = new byte[0];
                try {
                    fileContentBytes = fileInputStream.readAllBytes();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                MyFile myFile = new MyFile();
                myFile.setName(fileName);
                myFile.setData(fileContentBytes);
                message.setContent(myFile.toJSONString());

                stranger.sendData(DataType.DOWNLOAD, message.toJSONString());

                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void onReceiveLeaveChatRoom(String received) {
        // reset rejected clients of both
        this.rejectedClients.clear();
        this.stranger.rejectedClients.clear();

        // notify the stranger that you have exited
        this.stranger.sendData(DataType.CLOSE_CHAT_ROOM, this.nickname + " đã thoát phòng");

        // delete folder when client exit
        File filesFolder = new File(System.getProperty("user.dir")
                + "\\src\\com\\stranger_chat_app\\server\\client-files");
        File clientFolder1 = new File(filesFolder.getAbsolutePath() + "\\" + this.stranger.nickname);
        File clientFolder2 = new File(filesFolder.getAbsolutePath() + "\\" + this.nickname);

        if (clientFolder1.exists()) {
            String[] list = clientFolder1.list();

            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    File entry = new File(clientFolder1, list[i]);
                    if (entry.exists())
                        entry.delete();
                }
            }
            System.out.println(clientFolder1.delete());
        }

        if (clientFolder2.exists()) {
            String[] list = clientFolder2.list();

            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    File entry = new File(clientFolder2, list[i]);
                    if (entry.exists())
                        entry.delete();
                }
            }
            System.out.println(clientFolder2.delete());
        }

        // TODO leave chat room
        sendData(DataType.LEAVE_CHAT_ROOM, null);
    }

    private void onReceiveCalling(String received) {
        Client stranger = RunServer.clientManager.find(received);

        if (stranger != null) {
            sendData(DataType.RINGING, received);
            // call stranger
            stranger.sendData(DataType.INCOMING_CALL, this.nickname);
        }
    }

    private void onReceiveAcceptCall(String received) {
        Client stranger = RunServer.clientManager.find(received);

        if (stranger != null) {
            sendData(DataType.ACCEPT_CALL, received);
            // call stranger
            stranger.sendData(DataType.ACCEPT_CALL, this.nickname);
        }
    }

    private void onReceiveEndCall(String received) {
        sendData(DataType.END_CALL, null);

        Client stranger = RunServer.clientManager.find(received);

        if (stranger != null) {
            // call stranger
            stranger.sendData(DataType.END_CALL, null);
        }
    }

    private void onReceiveVoice(String received) {
        Client stranger = RunServer.clientManager.find(this.stranger.getNickname());

        if (stranger != null) {
            // call stranger
            stranger.sendData(DataType.VOICE, received);
        }
    }

    private void onReceiveVideoStream(String received) {
        Client stranger = RunServer.clientManager.find(this.stranger.getNickname());

        if (stranger != null) {
            // call stranger
            stranger.sendData(DataType.VIDEO_STREAM, received);
        }
    }

    private void onReceiveStopVideoStream(String received) {
        Client stranger = RunServer.clientManager.find(this.stranger.getNickname());

        if (stranger != null) {
            // call stranger
            stranger.sendData(DataType.STOP_VIDEO_STREAM, received);
        }
    }

    private void onReceiveAudio(String received) {
        Client stranger = RunServer.clientManager.find(this.stranger.getNickname());
        if (stranger != null) {
            // call stranger
            stranger.sendData(DataType.AUDIO, received);
        }
    }

    private void onReceiveLogout(String received) {
        // remove this client nickname from the rejected list of all clients
        RunServer.clientManager.removeRejectedClient(this.nickname);

        // reset all infos
        this.nickname = null;
        this.isWaiting = false;
        this.rejectedClients.clear();

        sendData(DataType.LOGOUT, null);
    }

    private void onReceiveExit(String received) {
        // reset nickname and waiting status
        this.nickname = null;
        this.isWaiting = false;

        sendData(DataType.EXIT, null);
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

    public Set<String> getRejectedClients() {
        return rejectedClients;
    }

    public void setRejectedClients(Set<String> rejectedClients) {
        this.rejectedClients = rejectedClients;
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
    }

    public String getAcceptPairUpStatus() {
        return acceptPairUpStatus;
    }

    public void setAcceptPairUpStatus(String acceptPairUpStatus) {
        this.acceptPairUpStatus = acceptPairUpStatus;
    }
}
