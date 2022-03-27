package com.stranger_chat_app.client.view.gui;

import com.stranger_chat_app.client.RunClient;
import com.stranger_chat_app.client.model.MessageStore;
import com.stranger_chat_app.shared.model.Message;

import javax.swing.*;
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
    private JLabel lblStranger;
    private JLabel lblStatus;

    private StyleSheet styleSheet = new StyleSheet();
    private HTMLEditorKit kit = new HTMLEditorKit();
    private HTMLDocument doc;

    private String you;
    private String stranger;

    public ChatRoomGUI() {
        super();
        setTitle("Phòng chat - Bạn: " + RunClient.socketHandler.getNickname());
        setContentPane(pnlMain);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        initComponents();

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
    }

    public void addChatMessage(Message message) {
        MessageStore.add(message);

        try {
            kit.insertHTML(doc, doc.getLength(),
                    createHTMLMsg("recipient", message),
                    0, 0, null);
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
                kit.insertHTML(doc, doc.getLength(),
                        createHTMLMsg("sender", message),
                        0, 0, null);
            } catch (BadLocationException | IOException badLocationException) {
                badLocationException.printStackTrace();
            }

            MessageStore.add(message);
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        }
    }

    public String createHTMLMsg(String userType, Message message) {
        String bubble = null;

        switch (userType) {
            case "sender":
                bubble = "<div class=\"my-msg\">\n" +
                        "   <div class=\"msg-info-name\">Bạn</div>\n" +
                        "  <div class=\"msg-text\">\n" +
                        message.getContent() +
                        "  </div>\n" +
                        "</div>";
                break;

            case "recipient":
                bubble = "<div class=\"stranger-msg\">\n" +
                        "   <div class=\"msg-info-name\">" + message.getSender() + "</div>\n" +
                        "  <div class=\"msg-text\">\n" +
                        message.getContent() +
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
        doc = (HTMLDocument) messageArea.getDocument();

        messageArea.setEditorKit(kit);
        messageArea.setDocument(doc);

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
