package com.stranger_chat_app.client.thread;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.stranger_chat_app.client.RunClient;
import com.stranger_chat_app.client.util.ImageIconUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Base64;

public class VideoRecorder extends Thread {
    private boolean isRunning;
    private Webcam webcam = null;
    private BufferedImage bufferedImage = null;
    private ImageIcon imageIcon = null;
    private Dimension[] nonStandardResolutions = new Dimension[] {
            WebcamResolution.PAL.getSize(),
            WebcamResolution.HD.getSize(),
            new Dimension(2000, 1000),
            new Dimension(1000, 500),
    };

    public VideoRecorder() {
    }

    @Override
    public void run() {
        isRunning = true;
        webcam = Webcam.getDefault();
        webcam.setCustomViewSizes(nonStandardResolutions);
        webcam.setViewSize(WebcamResolution.HD.getSize());
        webcam.open();

        while(isRunning) {
            bufferedImage = ImageIconUtil.flip(webcam.getImage(), ImageIconUtil.FLIP_HORIZONTAL);
            imageIcon = new ImageIcon(bufferedImage);
            byte[] imageContent = ImageIconUtil.getBytes(imageIcon);
            String data = Base64.getEncoder().encodeToString(imageContent);

            RunClient.socketHandler.sendVideoStream(data);
        }

        RunClient.socketHandler.stopVideoStream();
        webcam.close();
    }

    public void terminate() {
        this.isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
