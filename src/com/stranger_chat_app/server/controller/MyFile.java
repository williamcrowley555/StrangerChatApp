package com.stranger_chat_app.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stranger_chat_app.shared.model.Message;

import java.io.IOException;
import java.util.Arrays;

public class MyFile {
    private int id;
    private String name;
    private byte[] data;

    public MyFile(int id, String name, byte[] data) {
        this.id = id;
        this.name = name;
        this.data = data;
    }

    public MyFile() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MyFile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }

    public String toJSONString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static MyFile parse(String json) {
        try {
            return new ObjectMapper().readValue(json, MyFile.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
