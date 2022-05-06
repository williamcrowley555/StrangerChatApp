package com.stranger_chat_app.client.view.gui;

import com.stranger_chat_app.client.RunClient;
import com.stranger_chat_app.shared.model.Audio;
import com.stranger_chat_app.shared.model.Message;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.Flow;

public class MessageHandler {
    JPanel messageArea;
    Box vertical;
    public static final Color messengerBlue = new Color(6, 149, 255);
    public static final Color messengerGreen = new Color(38, 166, 91);
    private final String defaultAvatarLocation = "/com/stranger_chat_app/client/asset/user-avatar.png";
    private final int defaultSpacerHeight = 30;
    private boolean eventNotAdded = true;
    private int contentWidth;
    private int avatarWidthOffset;
    private int textBubbleWidth;
    private int avatarWidth;

    public MessageHandler(JPanel messageArea) {
        this.messageArea = messageArea;
        this.messageArea.setLayout(new BorderLayout());
        this.messageArea.setBorder(new EmptyBorder(5, 5, 5, 5));
        vertical = Box.createVerticalBox();

        this.messageArea.add(vertical, BorderLayout.PAGE_START);

    }


    public JPanel createAvatar(ImageIcon avatar, Color background, JLabel nickname, int width, int height, int paddingX, int paddingY) {
        JPanel avatarPanel = new JPanel(new BorderLayout());
        avatarPanel.setPreferredSize(new Dimension(width, height));
        JLabel icon = new JLabel();
        icon.setIcon(avatar);
        icon.setText(nickname.getText());
        icon.setHorizontalAlignment(JLabel.CENTER);
        icon.setHorizontalTextPosition(JLabel.CENTER);
        icon.setVerticalTextPosition(JLabel.BOTTOM);

        avatarPanel.add(icon, BorderLayout.NORTH);
        avatarPanel.setBorder(BorderFactory.createEmptyBorder(paddingY, paddingX, paddingY, paddingX));
        avatarPanel.setBackground(background);
        return avatarPanel;
    }

    public JPanel createBubble(Color bubbleColor, String userType, int paddingX, int paddingY) {
        JPanel bubble = new JPanel(new WrapLayout(WrapLayout.LEFT));
        bubble.setBackground(bubbleColor);
        bubble.setBorder(BorderFactory.createEmptyBorder(paddingY, paddingX, paddingY, paddingX));
        return bubble;
    }

    public void calculateChatBubbleSize(Message message, String userType) {
        contentWidth = messageArea.getWidth() * 85 / 100;

        //Add spacer between messages
        if (vertical.getComponentCount() != 0) {
            JPanel spacerBubble = createBubble(Color.white, userType, 5, 10);
            vertical.add(spacerBubble);
        }

        //Calculate avatar width base on nickname
        if (!userType.equals("sender")) {
            if (message.getSender().length() < 6)
                avatarWidthOffset = contentWidth * -2 / 100;
            else if (message.getSender().length() < 15)
                avatarWidthOffset = contentWidth * 10 / 100;
            else
                avatarWidthOffset = contentWidth * 18 / 100;
        } else
            avatarWidthOffset = contentWidth * 0 / 100;

        textBubbleWidth = contentWidth * 90 / 100 - avatarWidthOffset;
        avatarWidth = contentWidth - textBubbleWidth;
    }

    public boolean addTextMessage(Message message, String userType) {
        try {
            calculateChatBubbleSize(message, userType);

            //Add spacer between messages
            if (vertical.getComponentCount() != 0) {
                JPanel spacerBubble = createBubble(Color.white, userType, 5, 10);
                vertical.add(spacerBubble);
            }

            JPanel textBubble = createBubble(Color.white, userType, 5, 15);
            addBubbleTextContent(textBubble, message, textBubbleWidth);

            JPanel chatBubble = createChatBubble(textBubble, message, userType);
            vertical.add(chatBubble);
            messageArea.revalidate();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void addAudioMessage(Audio audio, String you, String stranger,String userType) {
        AudioPanel audioPanel = new AudioPanel(audio);
        JPanel audioMessage = createAudioChatBubble(audioPanel, you, stranger, userType);

    }

    public boolean addFileMessage(Message message, String userType, String fileName) {
        try {
            calculateChatBubbleSize(message, userType);

            //Add spacer between messages
            if (vertical.getComponentCount() != 0) {
                JPanel spacerBubble = createBubble(Color.white, userType, 5, 10);
                vertical.add(spacerBubble);
            }

            JPanel fileBubble = createBubble(Color.white, userType, 5, 15);
            addFileBubbleContent(fileBubble, message, fileName, userType, textBubbleWidth);

            JPanel chatBubble = createChatBubble(fileBubble, message, userType);
            vertical.add(chatBubble);
            messageArea.revalidate();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //Create chat bubble that contain content bubble and user avatar
    public JPanel createChatBubble(JPanel contentBubble, Message message, String userType) {
        JPanel chatBubble = new JPanel();
        chatBubble.setLayout(new BorderLayout());

        ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource(defaultAvatarLocation))
                .getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        JLabel nickname = new JLabel(userType.equals("sender") ? "Bạn" : message.getSender());

        JPanel pnlAvatar = createAvatar(imageIcon, Color.white,
                nickname, avatarWidth, contentBubble.getHeight() + 10, 0, 0);
        chatBubble.setBackground(Color.white);
        if (userType.equals("sender")) {
            AbstractBorder brdrRight = new TextBubbleBorder(messengerGreen, 2, 16, 8, false);
            contentBubble.setBorder(brdrRight);
            chatBubble.add(contentBubble, BorderLayout.CENTER);
            chatBubble.add(pnlAvatar, BorderLayout.EAST);
        } else {
            AbstractBorder brdrLeft = new TextBubbleBorder(messengerBlue, 2, 16, 8);
            contentBubble.setBorder(brdrLeft);
            chatBubble.add(contentBubble, BorderLayout.CENTER);
            chatBubble.add(pnlAvatar, BorderLayout.WEST);
        }

        return chatBubble;
    }

    public JPanel createAudioChatBubble(AudioPanel audioPanel, String you, String stranger, String userType) {
        JPanel chatBubble = new JPanel();
        chatBubble.setLayout(new BorderLayout());

        ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource(defaultAvatarLocation))
                .getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        JLabel nickname = new JLabel(userType.equals("sender") ? "Bạn" : stranger);

        JPanel pnlAvatar = createAvatar(imageIcon, Color.white,
                nickname, avatarWidth, audioPanel.getHeight() + 10, 0, 0);
        chatBubble.setBackground(Color.white);
        if (userType.equals("sender")) {
            AbstractBorder brdrRight = new TextBubbleBorder(messengerGreen, 2, 16, 8, false);
            audioPanel.setBorder(brdrRight);
            chatBubble.add(audioPanel, BorderLayout.CENTER);
            chatBubble.add(pnlAvatar, BorderLayout.EAST);
        } else {
            AbstractBorder brdrLeft = new TextBubbleBorder(messengerBlue, 2, 16, 8);
            audioPanel.setBorder(brdrLeft);
            chatBubble.add(audioPanel, BorderLayout.CENTER);
            chatBubble.add(pnlAvatar, BorderLayout.WEST);
        }

        return chatBubble;
    }

    public void addBubbleTextContent(JPanel textBubble, Message message, int textBubbleWidth) {
        if (containURL(message.getContent())) {
            //Hyperlink message
            String[] parts = message.getContent().split("\\s+");
            for (String item : parts) {
                try {
                    URL url = new URL(item);

                    JLabel label = new JLabel(url.toString());
                    label = makeHyperLink(url.toString(), url.toString(), 0, label.getText().length());
                    label.setForeground(Color.BLUE);
                    textBubble.add(label);
                } catch (MalformedURLException e) {
                    // If there was an normal string
                    String labelText = String.format(item);
                    JLabel text = new JLabel(labelText);
                    text.setForeground(Color.BLACK);
                    textBubble.add(text);
                }
            }
        } else {
            //Normal text message
            String labelText;
            labelText = String.format("<html><div WIDTH=%d>%s</div></html>", textBubbleWidth, message.getContent());
            JLabel text = new JLabel(labelText);
            text.setForeground(Color.BLACK);
            textBubble.add(text);
        }
    }

    public void addFileBubbleContent(JPanel fileBubble, Message message, String fileName, String userType, int textBubbleWidth) {
        String labelText;
        String htmlContent;
        String file_name = fileName == null ? message.getContent() : fileName;
        // fileName != null : sender
        // fileName == null : recipient

        htmlContent = "<a style='color: #0000EE' href=\"" + file_name + "\">" + file_name + "</a> ";


        labelText = String.format("<html><div WIDTH=%d>%s</div></html>", textBubbleWidth, htmlContent);
        JLabel text = new JLabel(labelText);
        text.setForeground(Color.BLACK);

        if (userType.equals("sender"))
            text = makeFileHyperLink(message, file_name, file_name, 0, file_name.length(), true);
        else
            text = makeFileHyperLink(message, file_name, file_name, 0, file_name.length(), false);

        fileBubble.add(text);
    }

    boolean containURL(String message) {
        String[] parts = message.split("\\s+");
        for (String item : parts) {
            try {
                URL url = new URL(item);
                return true;
            } catch (MalformedURLException ignored) {
            }
        }
        return false;
    }

    public static JLabel makeHyperLink(final String s, final String link, int x, int y) {
        final JLabel l = new JLabel(s);
        l.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent arg0) {
                l.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                l.setText(s);
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
                l.setCursor(new Cursor(Cursor.HAND_CURSOR));
                l.setText(String.format("<HTML><FONT color = \"#000099\"><U>%s</U></FONT></HTML>", s));
            }

            @Override
            public void mouseClicked(MouseEvent arg0) {
                try {
                    URI uri = new URI(link);
                    if (Desktop.isDesktopSupported())
                        Desktop.getDesktop().browse(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        l.setBounds(x, y, s.length() * 5, 20);
        l.setToolTipText(String.format("Truy cập %s", link));
        return l;
    }

    public static JLabel makeFileHyperLink(Message message, final String s, final String link, int x, int y, final boolean selfDownload) {
        final String file_name = s;
        final JLabel l = new JLabel(s);
        l.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseExited(MouseEvent arg0) {
                l.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                l.setText(s);
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
                l.setCursor(new Cursor(Cursor.HAND_CURSOR));
                l.setText(String.format("<HTML><FONT color = \"#000099\"><U>%s</U></FONT></HTML>", s));
            }

            @Override
            public void mouseClicked(MouseEvent arg0) {
                int resutl = JOptionPane.showConfirmDialog(null, "Bạn có muỗn tải file về không ?");
                if (resutl == 0) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Chọn nơi để lưu file.");
                    fileChooser.setSelectedFile(new File(file_name));

                    int userSelection = fileChooser.showSaveDialog(null);
                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File f = fileChooser.getSelectedFile();
                        ChatRoomGUI.path = f.getAbsolutePath();
                        message.setContent(file_name);
                        System.out.println(selfDownload);
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

        l.setBounds(x, y, s.length() * 5, 20);
        l.setToolTipText(String.format("Tải về %s", link));
        return l;
    }

}