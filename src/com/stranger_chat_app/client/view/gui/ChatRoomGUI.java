package com.stranger_chat_app.client.view.gui;

import com.stranger_chat_app.client.RunClient;
import com.stranger_chat_app.client.model.MessageStore;
import com.stranger_chat_app.server.controller.MyFile;
import com.stranger_chat_app.shared.model.Message;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class ChatRoomGUI extends JFrame {
    private JScrollPane scrollPanelMsg;
    private JTextPane messageArea;
    private JPanel inputPanel;
    private JTextArea txtMessage;
    private JButton btnSend;
    private JPanel topPanel;
    private JPanel pnlMain;
    private JPanel pnlHeader;
    private JPanel pnlChat;
    private JButton chooseFileButton;
    private JButton sendFileButton;
    private JLabel lblStranger;
    private JLabel lblStatus;
    private HTMLDocument doc;

    private String you;
    private String stranger;

    private final File[] fileToSend = new File[1];
    private final float fileSizeLimit = 250F;
    private final ArrayList<String> fileExtensionsBlacklist = new ArrayList<>( Arrays
            .asList("bat", "cmd", "exe", "jar", "msi", "msc", "js", "ps1"
                    , "ps1xml", "ps2", "ps2xml", "psc1", "psc2", "reg", "lnk"));

    private String cssLocalMessage = "position:relative;\n" +
            "max-width: 40%;\n" +
            "padding:5px 10px;\n" +
            "margin: 1em 2em;\n" +
            "color: white; \n" +
            "background: #3498DB;\n" +
            "border-radius:25px;\n" +
            "float: right;\n" +
            "clear: both;";
    private String cssRemoteMessage = "position:relative;\n" +
            "max-width: 40%;\n" +
            "padding:5px 10px;\n" +
            "margin: 0.3em 2em;\n" +
            "color:white; \n" +
            "background: #26A65B;\n" +
            "border-radius:25px;\n" +
            "float: left;\n" +
            "clear: both;";
    public static String path;

    public ChatRoomGUI() {
        super();
        setTitle("Phòng chat - Bạn: " + RunClient.socketHandler.getNickname());
        setContentPane(pnlMain);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        initComponents();
    }
    public void addFileMessage(Message message) {
        MessageStore.add(message);
        //System.out.println(message);
        String content = "<a style='color: #0000EE' href=\"" + message.getContent() + "\">"+ message.getContent() + "</a> ";
        try {
            doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                    "<div style='background-color: #ebebeb; margin: 0 0 10px 0;'><pre style='color: #000;'>"
                            + "<span style='color: red;'>" + message.getSender() + ": </span>" + content + "</pre></div><br/>");
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        messageArea.addHyperlinkListener(e -> {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                int resutl = JOptionPane.showConfirmDialog(ChatRoomGUI.this, "Bạn có muỗn tải file về không ?");
                if (resutl == 0){
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Chọn nơi để lưu file.");
                    fileChooser.setSelectedFile(new File(e.getDescription()));

                    int userSelection = fileChooser.showSaveDialog(ChatRoomGUI.this);
                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File f = fileChooser.getSelectedFile();
//                        if (f.exists()) {
//                            int click = JOptionPane.showConfirmDialog(ChatRoomGUI.this, "Tên tệp này đã tồn tại, bạn có muốn thay thế không ?", "Lưu tệp", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
//                            if (click != JOptionPane.YES_OPTION) {
//                                return;
//                            }
//                        }
                        path = f.getAbsolutePath();
                        RunClient.socketHandler.download(message);
                    }
                }
                else if (resutl == 1)
                    System.out.println("NO");
                else
                    System.out.println("CANCEL");
            }
        });

        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    }

    public void addSelfFileMessage(Message message, String fileName) {
        MessageStore.add(message);
        try {
            doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                    "<div style='background-color: #ebebeb; margin: 0 0 10px 0;'><pre style='color: #000;'>"
                            + "<span style='color: red;'>" + message.getSender() + ": </span>" + "Bạn đã gửi file: '" + fileName + "'" +"</pre></div><br/>");
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    }

    public void addChatMessage(Message message) {
        MessageStore.add(message);
        try {
            doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                    "<div style='background-color: #ebebeb; margin: 0 0 10px 0;'><pre style='color: #000;'>"
                            + "<span style='color: red;'>" + message.getSender() + ": </span>" + message.getContent() + "</pre></div><br/>");
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    }

    private void sendMessage(String content) {
        if(!content.equals("")) {
            Message message = new Message(you, stranger, content);

            RunClient.socketHandler.sendChatMessage(message);
            txtMessage.setText("");
            try {
                doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                        "<div style='background-color: #05728F; margin: 0 0 10px 0;'><pre style='color: #fff'>"
                                + "<span style='color: yellow;'>Bạn: </span>" + content + "</pre></div><br/>");
            } catch (BadLocationException | IOException badLocationException) {
                badLocationException.printStackTrace();
            }

            MessageStore.add(message);
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        }
    }

    private void initComponents() {
        btnSend.setPreferredSize(new Dimension(50, 40));
        txtMessage.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
        txtMessage.setMargin(new Insets(10, 10, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 3));

        topPanel.setLayout(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        JPanel userPanel = new JPanel(new GridLayout(0, 1));

        lblStranger = new JLabel();
        lblStranger.setOpaque(true);
        lblStatus = new JLabel();
        lblStatus.setText("Online");
        lblStatus.setForeground(Color.GREEN);
        lblStatus.setOpaque(true);
        JLabel icon = new JLabel();
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource("/com/stranger_chat_app/client/asset/icons8-anonymous-24.png"))
                .getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT));
        icon.setIcon(imageIcon);
        icon.setOpaque(true);

        userPanel.add(lblStranger);
        userPanel.add(lblStatus);
        topPanel.add(userPanel, BorderLayout.CENTER);
        topPanel.add(icon, BorderLayout.WEST);

        messageArea.setContentType("text/html");
        doc = (HTMLDocument) messageArea.getStyledDocument();
        messageArea.setText("<br/>");

        // Generate new line of txtMessage on CTRL + ENTER
        InputMap input = txtMessage.getInputMap();
        KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
        KeyStroke controlEnter = KeyStroke.getKeyStroke("control ENTER");
        input.put(controlEnter, "insert-break");
        input.put(enter, "text-submit");

        ActionMap actions = txtMessage.getActionMap();
        actions.put("text-submit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = txtMessage.getText();
                sendMessage(content);
            }
        });

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(ChatRoomGUI.this,
                        "Bạn có chắc muốn thoát phòng?", "Thoát phòng?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    RunClient.socketHandler.leaveChatRoom();
                }
            }
        });

        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = txtMessage.getText();
                sendMessage(content);
            }
        });

        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Chọn file muốn gửi.");

                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                    fileToSend[0] = fileChooser.getSelectedFile();
                }
            }
        });

        sendFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileToSend[0] == null){
                    JOptionPane.showMessageDialog(ChatRoomGUI.this,"Bạn chưa chọn file để gửi.");
                } else {
                    try {
                        FileInputStream fileInputStream = null;
                        fileInputStream = new FileInputStream(fileToSend[0].getAbsoluteFile());
                        int sizeInBytes = fileInputStream.available();
                        float sizeInMegabytes =  sizeInBytes * 1F / (1024 * 1024);
//                        System.out.println("Fize size: " + sizeInMegabytes + "mb");
//                        System.out.println("Fize limit size : " + fileSizeLimit + "mb");
                        if (sizeInMegabytes > fileSizeLimit){
                            JOptionPane.showMessageDialog(ChatRoomGUI.this, "Kích thước file phải nhỏ hơn 250mb");
                            return;
                        }
                        String fileName = fileToSend[0].getName();
                        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
//                        System.out.println("File extension: " + fileExtension);
                        if (fileExtensionsBlacklist.contains(fileExtension)) {
                            JOptionPane.showMessageDialog(ChatRoomGUI.this, "Loại file không được hỗ trợ!");
                            return;
                        }
                        if (fileExtension.equals("doc") || fileExtension.equals("docx")){
                            fileInputStream.close();
                            File f = new File(fileToSend[0].getAbsolutePath());
                            Path source = Paths.get(f.getAbsolutePath());

                            String fileFullName = f.getName();
                            String fileOldName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
                            String newFilePath = Files.move(source, source.resolveSibling(fileOldName + ".rtf")).toString();
                            fileInputStream = new FileInputStream(newFilePath);
                            fileName = fileOldName + ".rtf";
                        }
                        byte[] fileContentBytes = fileInputStream.readAllBytes();

                        MyFile myFile = new MyFile();
                        myFile.setName(fileName);
                        myFile.setData(fileContentBytes);

                        Message message = new Message(you, stranger, myFile.toJSONString());

                        RunClient.socketHandler.sendFile(message);
                        addSelfFileMessage(message, myFile.getName());
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
    }

    public void setClients(String you, String stranger) {
        this.you = you;
        this.stranger = stranger;
        this.lblStranger.setText(stranger);
    }

    public String getYou() {
        return you;
    }

    public void setYou(String you) {
        this.you = you;
    }

    public String getStranger() {
        return stranger;
    }

    public void setStranger(String stranger) {
        this.stranger = stranger;
    }

}
