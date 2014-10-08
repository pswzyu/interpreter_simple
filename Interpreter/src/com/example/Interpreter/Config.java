package com.example.Interpreter;

public class Config implements java.io.Serializable{
    private static String sendFileName = "a";
    private static String receiveFileName = "a";
    private static String selfId = "a";
    private static String fromId = "a";

    public static String getSendFileName() {
        return sendFileName;
    }

    public static String getReceiveFileName() {
        return receiveFileName;
    }

    public static String getSelfId() {
        return selfId;
    }

    public static String getFromId() {
        return fromId;
    }
}
