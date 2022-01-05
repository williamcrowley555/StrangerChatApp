package com.stranger_chat_app.shared.constant;

public enum DataType {
//    Server Response
    PUBLIC_KEY,
    RECEIVED_SECRET_KEY,

    ERROR,

//    Client Request
    CLIENT_INFO,
    SECRET_KEY,
    START_WAITING,
    LOGOUT,

//    Connection state
    EXIT
}
