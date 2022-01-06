package com.stranger_chat_app.shared.constant;

public enum DataType {
//    Server Response
    PUBLIC_KEY,
    RECEIVED_SECRET_KEY,
    PAIR_UP_WAITING,
    REQUEST_PAIR_UP,
    RESULT_PAIR_UP,
    JOIN_CHAT_ROOM,
    CLOSE_CHAT_ROOM,
    ERROR,

//    Client Request
    CLIENT_INFO,
    SECRET_KEY,
    PAIR_UP,
    CANCEL_PAIR_UP,
    PAIR_UP_RESPONSE,
    LEAVE_CHAT_ROOM,
    LOGOUT,

//    Connection state
    EXIT
}
