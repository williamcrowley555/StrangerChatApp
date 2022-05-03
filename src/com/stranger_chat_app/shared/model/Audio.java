package com.stranger_chat_app.shared.model;

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
}
