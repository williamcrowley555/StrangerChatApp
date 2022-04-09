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
    RINGING,
    INCOMING_CALL,
    START_CALLING,
    ERROR,

//    Client Request
    LOGIN,
    SECRET_KEY,
    PAIR_UP,
    CANCEL_PAIR_UP,
    PAIR_UP_RESPONSE,
    CHAT_MESSAGE,
    SEND_FILE,
    DOWNLOAD,
    LEAVE_CHAT_ROOM,
    CALLING,
    ACCEPT_CALL,
    END_CALL,
    VOICE,
    VIDEO_STREAM,
    STOP_VIDEO_STREAM,
    LOGOUT,
    EXIT,
}
