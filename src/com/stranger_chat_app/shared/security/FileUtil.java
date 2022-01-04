package com.stranger_chat_app.shared.security;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
    public static void writeToFile(File file, byte[] fileContent) {

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(fileContent);
            outputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static byte[] readBytesFromFile(File file) {
        Path path = Paths.get(file.getAbsolutePath());
        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(path);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return data;
    }

    public static byte[] combineBytes(byte[] a, byte[] b){
        byte[] combined = new byte[a.length + b.length];
        System.arraycopy(a, 0, combined, 0, a.length);
        System.arraycopy(b, 0, combined, a.length, b.length);

        return combined;
    }
}
