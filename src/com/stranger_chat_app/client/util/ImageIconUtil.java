package com.stranger_chat_app.client.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageIconUtil {
    public static final int FLIP_VERTICAL = 1;
    public static final int FLIP_HORIZONTAL = 2;

    public static BufferedImage flip(BufferedImage bufferedImage, int direction) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight ();
        BufferedImage flipped = new BufferedImage (width, height, BufferedImage. TYPE_INT_RGB);

        for(int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                switch (direction) {
                    case FLIP_HORIZONTAL:
                        flipped.setRGB((width - 1) - x, y, bufferedImage.getRGB(x, y));
                        break;
                    case FLIP_VERTICAL:
                        flipped.setRGB(x, (height - 1) - y, bufferedImage.getRGB(x, y));
                        break;
                }
            }
        }

        return flipped;
    }

    public static byte[] getBytes(ImageIcon imageIcon) {
        // Create a buffered image of the size of the original image icon
        BufferedImage image = new BufferedImage(imageIcon.getIconWidth(),
                imageIcon.getIconHeight(), BufferedImage.TYPE_INT_RGB);

        // Create a graphics object to draw the image
        Graphics graphics = image.createGraphics();

        // Paint the icon on to the buffered image
        imageIcon.paintIcon(null, graphics, 0, 0);
        graphics.dispose();

        // Convert the buffered image into a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", baos);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return baos.toByteArray();
    }

    public static ImageIcon parse(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        BufferedImage bufferedImage = null;

        try {
            bufferedImage = ImageIO.read(bis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ImageIcon(bufferedImage);
    }
}
