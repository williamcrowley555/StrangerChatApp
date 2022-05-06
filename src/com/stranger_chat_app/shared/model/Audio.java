package com.stranger_chat_app.shared.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stranger_chat_app.server.controller.MyFile;

import java.io.IOException;

public class Audio {
    private byte[] buffer;
    private int recordTime;

    public Audio() {
    }

    public Audio(byte[] buffer, int recordTime) {
        this.buffer = buffer;
        this.recordTime = recordTime;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public int getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(int recordTime) {
        this.recordTime = recordTime;
    }

    public String toJSONString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Audio parse(String json) {
        try {
            return new ObjectMapper().readValue(json, Audio.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
