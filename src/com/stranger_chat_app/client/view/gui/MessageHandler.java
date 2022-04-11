package com.stranger_chat_app.client.view.gui;

import com.stranger_chat_app.shared.model.Message;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    public JPanel createBubble(Color bubbleColor, String userType, int paddingX, int paddingY){
        JPanel bubble;
        if(userType.equals("sender")){
            bubble = new JPanel( new WrapLayout(WrapLayout.RIGHT));
        } else {
            bubble = new JPanel( new WrapLayout(WrapLayout.LEFT));
        }
        bubble.setBackground(bubbleColor);
        bubble.setBorder(BorderFactory.createEmptyBorder(paddingY, paddingX, paddingY, paddingX));
        return  bubble;
    }


    public boolean addTextMessage (Message message, String userType){
        try{
            int contentWidth = messageArea.getWidth() * 90/100;
            int avatarWidthOffset;

            if (vertical.getComponentCount() != 0)
            {
                JPanel spacerBubble = createBubble(Color.white, userType, 5 , 10);
                vertical.add(spacerBubble);
            }



            if(!userType.equals("sender"))
            {
                if (message.getSender().length() < 6)
                    avatarWidthOffset = contentWidth * -2/100;
                else if (message.getSender().length() < 15)
                    avatarWidthOffset = contentWidth * 10/100;
                else
                    avatarWidthOffset = contentWidth * 18/100;
            } else
                avatarWidthOffset = contentWidth * 0/100;

            int textBubbleWidth = contentWidth * 90/100 - avatarWidthOffset;
            int avatarWidth = contentWidth - textBubbleWidth;

            JPanel textBubble = createBubble(
                    Color.white,userType,
                    5,15);

            //Hyperlink message
            String [] parts = message.getContent().split("\\s+");
            System.out.println(parts);
            String content = "";

            for( String item : parts ) {
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

            JPanel chatBubble = new JPanel();
            chatBubble.setLayout(new BorderLayout());


            //textBubble.add(text, userType.equals("sender") ? BorderLayout.LINE_END : BorderLayout.LINE_START);

            ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource(defaultAvatarLocation))
                    .getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
            JLabel nickname = new JLabel(userType.equals("sender") ? "Bạn" : message.getSender());

            JPanel pnlAvatar = createAvatar(imageIcon, Color.white,
                    nickname, avatarWidth,textBubble.getHeight() + 10,0,0);
            chatBubble.setBackground(Color.white);
            if (userType.equals("sender")){
                AbstractBorder brdrRight = new TextBubbleBorder(messengerGreen,2,16,8,false);
                textBubble.setBorder(brdrRight);
                chatBubble.add(textBubble, BorderLayout.CENTER);
                chatBubble.add(pnlAvatar, BorderLayout.EAST);
            } else {
                AbstractBorder brdrLeft = new TextBubbleBorder(messengerBlue,2,16,8);
                textBubble.setBorder(brdrLeft);
                chatBubble.add(textBubble, BorderLayout.CENTER);
                chatBubble.add(pnlAvatar, BorderLayout.WEST);
            }
            vertical.add(chatBubble);
            messageArea.revalidate();
        } catch (Exception e){
            return false;
        }
        return true;
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

    public static JLabel makeHyperLink(final String s, final String link, int x, int y)
    {
        final JLabel l = new JLabel(s);
        l.addMouseListener(new MouseAdapter()
        {

            @Override
            public void mouseExited(MouseEvent arg0)
            {
                l.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                l.setText(s);
            }

            @Override
            public void mouseEntered(MouseEvent arg0)
            {
                l.setCursor(new Cursor(Cursor.HAND_CURSOR));
                l.setText(String.format("<HTML><FONT color = \"#000099\"><U>%s</U></FONT></HTML>", s));
            }

            @Override
            public void mouseClicked(MouseEvent arg0)
            {
                try
                {
                    URI uri = new URI(link);
                    if (Desktop.isDesktopSupported())
                        Desktop.getDesktop().browse(uri);
                } catch (Exception e)
                {
                }
            }
        });

        l.setBounds(x, y, s.length()*5, 20);
        l.setToolTipText(String.format("go to %s", link));
        return l;
    }
}
