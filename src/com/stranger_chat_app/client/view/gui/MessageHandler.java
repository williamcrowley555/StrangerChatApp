package com.stranger_chat_app.client.view.gui;

import com.stranger_chat_app.shared.model.Message;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MessageHandler {
    JPanel messageArea;
    Box vertical;
    public static final Color messengerBlue = new Color(6, 149, 255);
    public static final Color messengerGreen = new Color(38, 166, 91);
    private final String defaultAvatarLocation = "/com/stranger_chat_app/client/asset/user-avatar.png";
    private final int defaultSpacerHeight = 30;
    public MessageHandler(JPanel messageArea) {
        this.messageArea = messageArea;
        this.messageArea.setLayout(new BorderLayout());
        this.messageArea.setBorder(new EmptyBorder(5,5,5,5));
        vertical = Box.createVerticalBox();
        this.messageArea.add(vertical, BorderLayout.PAGE_START);
    }


    public JPanel createAvatar(ImageIcon avatar, Color background,JLabel nickname, int width, int height, int paddingX, int paddingY){
        JPanel avatarPanel = new JPanel( new BorderLayout());
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
    public JPanel createBubble(Color bubbleColor,int paddingX, int paddingY){
        JPanel bubble = new JPanel( new BorderLayout());
        bubble.setBackground(bubbleColor);
        bubble.setBorder(BorderFactory.createEmptyBorder(paddingY, paddingX, paddingY, paddingX));

        return  bubble;
    }


    public boolean addTextMessage (Message message, String userType){
        try{
            if (vertical.getComponentCount() != 0)
            {
                JPanel spacerBubble = createBubble(Color.white, 5,10);
                vertical.add(spacerBubble);
            }

            int contentWidth = messageArea.getWidth() * 90/100;
            int avatarWidthOffset;

            if(!userType.equals("sender"))
            {
                if (message.getSender().length() < 6)
                    avatarWidthOffset = contentWidth * -5/100;
                else if (message.getSender().length() < 15)
                    avatarWidthOffset = contentWidth * 10/100;
                else
                    avatarWidthOffset = contentWidth * 18/100;
            } else
                avatarWidthOffset = contentWidth * 0/100;

            int textBubbleWidth = contentWidth * 90/100 - avatarWidthOffset;
            int avatarWidth = contentWidth - textBubbleWidth;
            String labelText = String.format("<html><div WIDTH=%d>%s</div></html>", textBubbleWidth, message.getContent());
            JLabel text = new JLabel(labelText);
            text.setForeground(Color.BLACK);
            JPanel chatBubble = new JPanel();
            chatBubble.setLayout(new BorderLayout());

            JPanel textBubble = createBubble(
                    //userType.equals("sender") ? messengerGreen : messengerBlue,
                    Color.white,
                    5,15);
            textBubble.add(text, userType.equals("sender") ? BorderLayout.LINE_END : BorderLayout.LINE_START);

            ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource(defaultAvatarLocation))
                    .getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
            JLabel nickname = new JLabel(userType.equals("sender") ? "Bạn" : message.getSender());

            JPanel pnlAvatar = createAvatar(imageIcon, Color.white,
                    nickname, avatarWidth,textBubble.getHeight() + 10,0,0);
            chatBubble.setBackground(Color.white);
            if (userType.equals("sender")){
                AbstractBorder brdrRight = new TextBubbleBorder(messengerGreen,2,16,8,false);
                textBubble.setBorder(brdrRight);
                chatBubble.add(textBubble, BorderLayout.WEST);
                chatBubble.add(pnlAvatar, BorderLayout.EAST);
            } else {
                AbstractBorder brdrLeft = new TextBubbleBorder(messengerBlue,2,16,8);
                textBubble.setBorder(brdrLeft);
                chatBubble.add(textBubble, BorderLayout.EAST);
                chatBubble.add(pnlAvatar, BorderLayout.WEST);
            }

            vertical.add(chatBubble);
            messageArea.revalidate();
        } catch (Exception e){
            return false;
        }
        return true;
    }

}
