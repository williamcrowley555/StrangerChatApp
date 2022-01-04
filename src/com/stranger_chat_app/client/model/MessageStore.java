package com.stranger_chat_app.client.model;

import com.stranger_chat_app.shared.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageStore {
    private static List<Message> messages = new ArrayList<>();

    public static void saveMessage(Message msg) {
        messages.add(msg);
    }

    public void clear() {
        messages.clear();
    }
}
