package com.stranger_chat_app.client.view.gui;

import com.stranger_chat_app.client.RunClient;
import com.stranger_chat_app.client.model.MessageStore;
import com.stranger_chat_app.server.controller.MyFile;
import com.stranger_chat_app.shared.model.Message;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
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
import java.lang.reflect.InvocationTargetException;
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
    private JButton sendFileButton;
    private JLabel lblCall;
    private JPanel pnlMessageArea;
    private JLabel lblAttachment;
    private JLabel lblStranger;
    private JLabel lblStatus;

    private StyleSheet styleSheet = new StyleSheet();
    private HTMLEditorKit kit = new HTMLEditorKit();
    private HTMLDocument doc;

    private String you;
    private String stranger;

    private final File[] fileToSend = new File[1];
    private final float fileSizeLimit = 250F;
    private final ArrayList<String> fileExtensionsBlacklist = new ArrayList<>( Arrays
            .asList("bat", "cmd", "exe", "jar", "msi", "msc", "js", "ps1"
                    , "ps1xml", "ps2", "ps2xml", "psc1", "psc2", "reg", "lnk"));
                    
    public static String path;
    public static Icon fileIcon;
    private boolean eventNotAdded = true;

    private boolean isCalling = false;
    MessageHandler messageHandler;
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
        String userType = "";

        if (fileName.length != 0) {
            userType = "sender";
            messageHandler.addFileMessage(message, userType, fileName[0]);
        } else {
            userType = "recipient";
            if(ChatRoomGUI.fileIcon != null)
                System.out.println("recipient icon not null");
            messageHandler.addFileMessage(message, userType, null);
        }
    }

    public void addChatMessage(Message message) {
        MessageStore.add(message);
        messageHandler.addTextMessage(message, "recipient");
    }

    private void sendMessage(String content) {
        if(!content.equals("")) {
            Message message = new Message(you, stranger, content);

            RunClient.socketHandler.sendChatMessage(message);
            txtMessage.setText("");
            messageHandler.addTextMessage(message, "sender");

            MessageStore.add(message);
        }
    }

    //Auto hyperlink urls if there are URLs in message
    private String handleHyperlinkUrls(String message){
        String [] parts = message.split("\\s+");
        String content = "";

        // Attempt to convert each item into an URL.
        for( String item : parts ) {
            try {
                URL url = new URL(item);
                // If there is possible url then replace with anchor...
                content += "<a style='color: #0000EE' href=\"" + url + "\">"+ url + "</a> ";
            } catch (MalformedURLException e) {
                // If there was an normal string
                content += item + " ";
            }
        }
        return content;
    }


    private void initComponents() {
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

        lblAttachment.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                //Choose File
                JFileChooser fileChooser = windowsJFileChooser(new JFileChooser());
                fileChooser.setDialogTitle("Chọn file muốn gửi.");

                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                    fileToSend[0] = fileChooser.getSelectedFile();

                    //Send File
                    try {
                        FileInputStream fileInputStream = null;
                        fileInputStream = new FileInputStream(fileToSend[0].getAbsoluteFile());
                        int sizeInBytes = fileInputStream.available();
                        float sizeInMegabytes =  sizeInBytes * 1F / (1024 * 1024);
                        if (sizeInMegabytes > fileSizeLimit){
                            JOptionPane.showMessageDialog(ChatRoomGUI.this, "Kích thước file phải nhỏ hơn 250mb");
                            return;
                        }
                        String fileName = fileToSend[0].getName();
                        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
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

                        addFileMessage(message, myFile.getName());
                        RunClient.socketHandler.sendFile(message);

                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(ChatRoomGUI.this, "Không tìm thấy file đã chọn, vui lòng chọn lại!");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
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
                emojiTable.setModel(new DefaultTableModel(emojiMatrix, new String[] { "", "", "", "", "", "" }) {
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
    }

    public void setClients(String you, String stranger) {
        this.you = you;
        this.stranger = stranger;
        this.lblStranger.setText(stranger);
    }

    public static JFileChooser windowsJFileChooser(JFileChooser chooser){
        LookAndFeel previousLF = UIManager.getLookAndFeel();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            chooser = new JFileChooser();
            UIManager.setLookAndFeel(previousLF);
        } catch (IllegalAccessException | UnsupportedLookAndFeelException | InstantiationException | ClassNotFoundException e) {}
        return chooser;
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


