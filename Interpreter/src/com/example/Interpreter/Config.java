package com.example.Interpreter;

public class Config implements java.io.Serializable{
    private final String filePath = "/storage/sdcard0/interpreter/";
    private final String sendFileName = filePath + "send";
    private final String receiveFileName = filePath + "receive";
    private final String configFileName = filePath + "config.cfg";
    private String selfId = "0";
    private String fromId = "1";

    public String getFilePath() {
        return filePath;
    }

    public String getSendFileName() {
        return sendFileName;
    }

    public String getReceiveFileName() {
        return receiveFileName;
    }

    public String getConfigFileName() {
        return configFileName;
    }

    public String getSelfId() {
        return selfId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setSelfId(String selfId) {
        selfId = selfId;
    }

    public void setFromId(String fromId) {
        fromId = fromId;
    }

    private static final class ConfigLoader {
        private static final Config config = new Config();
    }

    public static Config getConfig() {
        return ConfigLoader.config;
    }
}
