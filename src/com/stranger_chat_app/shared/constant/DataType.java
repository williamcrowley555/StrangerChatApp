package com.stranger_chat_app.shared.constant;

public enum DataType {
//    Server Response
    PUBLIC_KEY,
    RECEIVED_SECRET_KEY,
    PAIR_UP_WAITING,
    REQUEST_PAIR_UP,
    RESULT_PAIR_UP,
    ERROR,

//    Client Request
    CLIENT_INFO,
    SECRET_KEY,
    PAIR_UP,
    CANCEL_PAIR_UP,
    PAIR_UP_RESPONSE,
    LOGOUT,

//    Connection state
    EXIT
}
