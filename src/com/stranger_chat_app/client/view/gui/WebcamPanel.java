package com.stranger_chat_app.client.view.gui;

import java.awt.*;
import javax.swing.*;
public class WebcamPanel extends JPanel {
    private JLayeredPane layeredPane;
    private JLabel strangerStreamContainer = new JLabel();
    private JLabel selfStreamContainer = new JLabel();

    private int strangerStreamWidth;
    private  int strangerStreamHeight;
    private int selfStreamWidth;
    private int selfStreamHeight;

    static final int defaultStreamRatio = 4; // stranger stream / self stream

    WebcamPanel(){
        super();
        this.layeredPane = new JLayeredPane();
    }

    public void createGUI(int width, int height){
        layeredPane.removeAll();
        layeredPane.setPreferredSize(new Dimension(width, height));

        layeredPane.add(strangerStreamContainer, 1);
        layeredPane.add(selfStreamContainer, 2);
        this.add(layeredPane);

        strangerStreamWidth = width;
        strangerStreamHeight = height;

        strangerStreamContainer.setBounds( 0, 0, strangerStreamWidth, strangerStreamHeight);

        int selfStreamX = strangerStreamContainer.getX() +
                (strangerStreamContainer.getWidth() - strangerStreamContainer.getWidth() / defaultStreamRatio);
        int selfStreamY = strangerStreamContainer.getY() +
                (strangerStreamContainer.getHeight()- strangerStreamContainer.getHeight() / defaultStreamRatio);
        selfStreamWidth = strangerStreamContainer.getWidth()/ defaultStreamRatio;
        selfStreamHeight = strangerStreamContainer.getHeight()/ defaultStreamRatio;

        selfStreamContainer.setBounds(selfStreamX, selfStreamY, selfStreamWidth, selfStreamHeight);

       repaint();
       revalidate();
    }

    public void setStrangerStream(ImageIcon strangerStream) {
        ImageIcon scaledStrangerStream = null;

        if (strangerStream != null) {
            scaledStrangerStream = new ImageIcon(strangerStream.getImage().getScaledInstance(
                    strangerStreamWidth,
                    strangerStreamHeight,
                    Image.SCALE_SMOOTH));
        }

        strangerStreamContainer.setIcon(scaledStrangerStream);
        strangerStreamContainer.revalidate();
    }

    public void setSelfStream(ImageIcon selfStream) {
        ImageIcon scaledSelfStream = null;

        if (selfStream != null) {
            scaledSelfStream = new ImageIcon(selfStream.getImage().getScaledInstance(
                    selfStreamWidth,
                    selfStreamHeight,
                    Image.SCALE_SMOOTH));
        }

        selfStreamContainer.setIcon(scaledSelfStream);
        selfStreamContainer.revalidate();
    }

    public ImageIcon getStrangerStream() {
        return (ImageIcon) strangerStreamContainer.getIcon();
    }

    public ImageIcon getSelfStream() {
        return (ImageIcon) selfStreamContainer.getIcon();
    }
}
