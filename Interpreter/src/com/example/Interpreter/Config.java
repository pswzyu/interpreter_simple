package com.example.Interpreter;

public class Config implements java.io.Serializable{
    private final String filePath = "/storage/sdcard0/interpreter/";
    private final String sendFileName = filePath + "send";
    private final String receiveFileName = filePath + "receive";
    private final String configFileName = filePath + "config.cfg";
    private String selfId = "0";
    private String targetId = "1";

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

    public String getTargetId() {
        return  targetId;
    }

    public void setSelfId(String selfId) {
        this.selfId = selfId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    private static final class ConfigLoader {
        private static final Config config = new Config();
    }

    public static Config getConfig() {
        return ConfigLoader.config;
    }
}
