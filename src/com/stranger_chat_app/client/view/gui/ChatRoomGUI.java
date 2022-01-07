package com.stranger_chat_app.client.view.gui;

import com.stranger_chat_app.client.RunClient;
import com.stranger_chat_app.client.model.MessageStore;
import com.stranger_chat_app.shared.model.Message;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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
    private JLabel lblStranger;
    private JLabel lblStatus;
    private HTMLDocument doc;

    private String you;
    private String stranger;

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

    public ChatRoomGUI() {
        super();
        setTitle("Trò chuyện - Bạn: " + RunClient.socketHandler.getNickname());
        setContentPane(pnlMain);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        initComponents();
    }

    public void addChatMessage(Message message) {
        MessageStore.add(message);

        try {
            doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                    "<div style='" + cssRemoteMessage + "'><p>" + message.getContent() + "</p></div><br/>");
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        messageArea.setCaretPosition(messageArea.getDocument().getLength());
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

        doc = (HTMLDocument) messageArea.getStyledDocument();

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

                if(!content.equals("")) {
                    Message message = new Message(you, stranger, content);

                    RunClient.socketHandler.sendChatMessage(message);
                    txtMessage.setText("");

                    try {
                        doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                "<div style='background-color: #ebebeb; margin-top: 10px;'><pre style='color: #000;'>"
                                        + "<span style='color: red;'>" + message.getSender() + ": </span>" + message.getContent() + "</pre></div><br/>");
                    } catch (BadLocationException | IOException badLocationException) {
                        badLocationException.printStackTrace();
                    }

                    MessageStore.add(message);
                    messageArea.setCaretPosition(messageArea.getDocument().getLength());
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
