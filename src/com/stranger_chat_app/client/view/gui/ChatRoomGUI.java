package com.stranger_chat_app.client.view.gui;

import com.stranger_chat_app.client.RunClient;
import com.stranger_chat_app.client.model.MessageStore;
import com.stranger_chat_app.client.thread.AudioRecorder;
import com.stranger_chat_app.server.controller.MyFile;
import com.stranger_chat_app.shared.model.Audio;
import com.stranger_chat_app.shared.model.Message;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
    private JLabel lblEmoji;
    private JPanel sendButtonPanel;
    private JButton chooseFileButton;
    private JButton sendFileButton;
    private JLabel lblCall;
    private JPanel pnlMessageArea;
    private JLabel lblAudio;
    private JLabel lblStranger;
    private JLabel lblStatus;

    private StyleSheet styleSheet = new StyleSheet();
    private HTMLEditorKit kit = new HTMLEditorKit();
    private HTMLDocument doc;

    private String you;
    private String stranger;
    private String urlBlockMicrophone = "/com/stranger_chat_app/client/asset/icons8-block-microphone-24.png";
    private String urlMicrophone = "/com/stranger_chat_app/client/asset/icons8-microphone-24.png";

    private final File[] fileToSend = new File[1];
    private final float fileSizeLimit = 250F;
    private final ArrayList<String> fileExtensionsBlacklist = new ArrayList<>(Arrays
            .asList("bat", "cmd", "exe", "jar", "msi", "msc", "js", "ps1"
                    , "ps1xml", "ps2", "ps2xml", "psc1", "psc2", "reg", "lnk"));

    public static String path;
    private boolean eventNotAdded = true;

    private boolean isCalling = false;
    private boolean isRecord = false;
    MessageHandler messageHandler;
    AudioRecorder audioRecorder;

    public ChatRoomGUI() {
        super();
        setTitle("Phòng chat - Bạn: " + RunClient.socketHandler.getNickname());
        setContentPane(pnlMain);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        initComponents();
        messageHandler = new MessageHandler(pnlMessageArea);

    }

    public void addFileMessage(Message message, String... fileName) {
        MessageStore.add(message);
        String htmlContent = "";
        String userType = "";

        if (fileName.length != 0) {
            userType = "sender";
            htmlContent = "<a style='color: #0000EE' href=\"" + fileName[0] + "\">" + fileName[0] + "</a> ";
        } else {
            userType = "recipient";
            htmlContent = "<a style='color: #0000EE' href=\"" + message.getContent() + "\">" + message.getContent() + "</a> ";
        }

        try {
            Message htmlMessage = (Message) message.clone();
            htmlMessage.setContent(htmlContent);

            kit.insertHTML(doc, doc.getLength(),
                    createHTMLMsg(userType, htmlMessage),
                    0, 0, null);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        /*FileName null: file receive from stranger.
         *         not null: self file download.    */
        addEventHyperlinkMessage(message, fileName.length != 0 ? true : false);
        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    }

    public void addEventHyperlinkMessage(Message message, boolean selfDownload) {
        if (eventNotAdded) {
            messageArea.addHyperlinkListener(e -> {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                    int resutl = JOptionPane.showConfirmDialog(ChatRoomGUI.this, "Bạn có muỗn tải file về không ?");
                    if (resutl == 0) {
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
                            message.setContent(e.getDescription());
                            if (selfDownload)
                                message.setRecipient(message.getSender());
                            System.out.println(message.toJSONString());
                            RunClient.socketHandler.download(message);
                        }
                    } else if (resutl == 1)
                        System.out.println("NO");
                    else
                        System.out.println("CANCEL");
                }
            });
            eventNotAdded = false;
        }
    }

    public void addChatMessage(Message message) {
        MessageStore.add(message);
        messageHandler.addTextMessage(message, "recipient");
//        try {
//            kit.insertHTML(doc, doc.getLength(),
//                    createHTMLMsg("recipient", message),
//                    0, 0, null);
//        } catch (BadLocationException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    }

    public void addAudio(Audio audio, String userType) {
        messageHandler.addAudioMessage(audio, you, stranger, userType);
    }

    private void sendMessage(String content) {
        if (!content.equals("")) {
            Message message = new Message(you, stranger, content);

            RunClient.socketHandler.sendChatMessage(message);
            txtMessage.setText("");
            messageHandler.addTextMessage(message, "sender");
//            try {
//                kit.insertHTML(doc, doc.getLength(),
//                        createHTMLMsg("sender", message),
//                        0, 0, null);
//            } catch (BadLocationException | IOException badLocationException) {
//                badLocationException.printStackTrace();
//            }

            MessageStore.add(message);
            // messageArea.setCaretPosition(messageArea.getDocument().getLength());
        }
    }

    //Auto hyperlink urls if there are URLs in message
    private String handleHyperlinkUrls(String message) {
        String[] parts = message.split("\\s+");
        String content = "";

        // Attempt to convert each item into an URL.
        for (String item : parts) {
            try {
                URL url = new URL(item);
                // If there is possible url then replace with anchor...
                content += "<a style='color: #0000EE' href=\"" + url + "\">" + url + "</a> ";
            } catch (MalformedURLException e) {
                // If there was an normal string
                content += item + " ";
            }
        }

        return content;
    }

    public String createHTMLMsg(String userType, Message message) {
        String bubble = null;

        switch (userType) {
            case "sender":
                bubble = "<div class=\"my-msg\">\n" +
                        "   <div class=\"msg-info-name\">Bạn</div>\n" +
                        "  <div class=\"msg-text\">\n" +
                        handleHyperlinkUrls(message.getContent()) +
                        "  </div>\n" +
                        "</div>";
                break;

            case "recipient":
                bubble = "<div class=\"stranger-msg\">\n" +
                        "   <div class=\"msg-info-name\">" + message.getSender() + "</div>\n" +
                        "  <div class=\"msg-text\">\n" +
                        handleHyperlinkUrls(message.getContent()) +
                        "  </div>\n" +
                        "</div>";
                break;

            default:
                break;
        }

        return bubble + "<br/>";
    }

    private void initComponents() {

        btnSend.setPreferredSize(new Dimension(50, 40));
        sendFileButton.setEnabled(false);
        txtMessage.setMargin(new Insets(3, 3, 3, 3));
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
        topPanel.add(lblCall, BorderLayout.EAST);

        // Add CSS Styles
        styleSheet.addRule(".my-msg {\n" +
                "  padding: 10px;\n" +
                "  color: #fff; \n" +
                "  background: #3498DB;\n" +
                "}");
        styleSheet.addRule(".stranger-msg {\n" +
                "  padding: 10px;\n" +
                "  color: #fff; \n" +
                "  background: #26A65B;\n" +
                "}");
        styleSheet.addRule(".msg-info-name {\n" +
                "  margin-bottom: 10px;\n" +
                "  font-weight: bold;\n" +
                "}");

        kit.setStyleSheet(styleSheet);
//        doc = (HTMLDocument) messageArea.getDocument();
//
//        messageArea.setEditorKit(kit);
//        messageArea.setDocument(doc);
//
//        messageArea.addHyperlinkListener(e -> {
//            if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
//                if (e.getURL() != null) {
//                    Desktop desktop = Desktop.getDesktop();
//                    try {
//                        desktop.browse(e.getURL().toURI());
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        });

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

                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    fileToSend[0] = fileChooser.getSelectedFile();
                }
                sendFileButton.setEnabled(true);
            }
        });

        sendFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileToSend[0] == null) {
                    JOptionPane.showMessageDialog(ChatRoomGUI.this, "Bạn chưa chọn file để gửi.");
                } else {
                    try {
                        FileInputStream fileInputStream = null;
                        fileInputStream = new FileInputStream(fileToSend[0].getAbsoluteFile());
                        int sizeInBytes = fileInputStream.available();
                        float sizeInMegabytes = sizeInBytes * 1F / (1024 * 1024);
                        if (sizeInMegabytes > fileSizeLimit) {
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
                        if (fileExtension.equals("doc") || fileExtension.equals("docx")) {
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
                        addFileMessage(message, myFile.getName());
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(ChatRoomGUI.this, "Không tìm thấy file đã chọn, vui lòng chọn lại!");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                sendFileButton.setEnabled(false);
            }
        });

        lblEmoji.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //Values to position the emojiDialog
                int offsetX = 0,
                        offsetY = -240;

                //Get position of label that trigger the event
                JLabel label = (JLabel) e.getSource();
                Point lablePosition = label.getLocationOnScreen();

                JDialog emojiDialog = new JDialog();

                //Set location
                emojiDialog.setLocation(lablePosition.x + offsetX, lablePosition.y + offsetY + label.getHeight());

                Object[][] emojiMatrix = new Object[6][6];
                int emojiCode = 0x1F601;
                for (int i = 0; i < 6; i++) {
                    for (int j = 0; j < 6; j++)
                        emojiMatrix[i][j] = new String(Character.toChars(emojiCode++));
                }

                JTable emojiTable = new JTable();
                emojiTable.setModel(new DefaultTableModel(emojiMatrix, new String[]{"", "", "", "", "", ""}) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                });
                emojiTable.setFont(new Font("Dialog", Font.PLAIN, 20));
                emojiTable.setShowGrid(false);
                emojiTable.setIntercellSpacing(new Dimension(0, 0));
                emojiTable.setRowHeight(30);
                emojiTable.getTableHeader().setVisible(false);

                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
                for (int i = 0; i < emojiTable.getColumnCount(); i++) {
                    emojiTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
                    emojiTable.getColumnModel().getColumn(i).setMaxWidth(30);
                }
                emojiTable.setCellSelectionEnabled(true);
                emojiTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                emojiTable.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        txtMessage.setText(txtMessage.getText() + emojiTable
                                .getValueAt(emojiTable.rowAtPoint(e.getPoint()), emojiTable.columnAtPoint(e.getPoint())));
                    }
                });

                emojiDialog.setContentPane(emojiTable);
                emojiDialog.setTitle("Chọn emoji");
                emojiDialog.setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
                emojiDialog.pack();
                emojiDialog.setVisible(true);
            }
        });

        lblCall.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isCalling == false) {
                    RunClient.socketHandler.call(stranger);
                }
            }
        });

        lblAudio.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                recordAudio();
            }
        });
    }

    // Hàm thực hiện thu âm hoặc dừng thu âm dựa vào biến isRecord
    public void recordAudio() {
        if (!isRecord) {
            audioRecorder = new AudioRecorder();
            setIsRecordAndLblAudioIcon(urlBlockMicrophone);
            audioRecorder.start();
        } else {
            setIsRecordAndLblAudioIcon(urlMicrophone);
            audioRecorder.terminate();
        }

    }

    // Hàm truyền vào đường dẫn của icon cần sử dụng, trả về biến imageIcon
    public ImageIcon getImageIcon(String url) {
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource(url)).getImage());
        return imageIcon;
    }

    // Hàm thay đổi trạng thái của biến isRecord và icon Micro tuỳ trường hợp.
    public void setIsRecordAndLblAudioIcon(String url) {
        this.isRecord = (isRecord) ? false : true;
        ImageIcon imageIcon = getImageIcon(url);
        lblAudio.setIcon(imageIcon);
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

    public boolean isCalling() {
        return isCalling;
    }

    public void setCalling(boolean calling) {
        isCalling = calling;
    }
}
