package com.stranger_chat_app.shared.model;

import com.stranger_chat_app.shared.constant.DataType;

import java.io.Serial;
import java.io.Serializable;

public class Data implements Serializable {
    private DataType dataType;
    private String content;

    public Data() {
    }

    public Data(DataType dataType, String content) {
        this.dataType = dataType;
        this.content = content;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Data{" +
                "dataType=" + dataType +
                ", content='" + content + '\'' +
                '}';
    }
}
