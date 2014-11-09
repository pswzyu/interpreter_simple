package com.example.Interpreter;

import android.os.Environment;

import java.io.Serializable;

public class Config implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String filePath = Environment.getExternalStorageDirectory().getPath()+"/interpreter/";
    private static final String sendFileName = filePath + "send";
    private static final String receiveFileName = filePath + "receive";
    private static final String configFileName = filePath + "config.cfg";
    private static final String targetPhotoFileName = filePath + "targetPhoto.jpg";
    private static final String targetPhotoFileName_scaled = filePath + "targetPhoto_scaled.jpg";

    private String selfId = "";
    private String selfName = "";
    private String selfLanguage = "";
    private String targetId = "";
    private String targetName = "";
    private String targetRealName = "";
//	private String sendUrl = "http://home.cnzy.me:8001/interpreter/upload.php";
//	private String receiveUrl = "http://home.cnzy.me:8001/interpreter/receive.php";
//    private String sendPicUrl = "http://home.cnzy.me:8001/interpreter/face.php";
//    private String loginUrl = "http://home.cnzy.me:8001/interpreter/login.php";
//	private String sendUrl = "http://home.cnzy.me:8001/interpreter/upload_s.php";
//	private String receiveUrl = "http://home.cnzy.me:8001/interpreter/receive_s.php";
//    private String sendPicUrl = "http://home.cnzy.me:8001/interpreter/face.php";
//    private String loginUrl = "http://home.cnzy.me:8001/interpreter/login_s.php";
    
    private String sendUrl = "http://198.199.97.166/interpreter/server/upload_s.php";
    private String receiveUrl = "http://198.199.97.166/interpreter/server/receive_s.php";
    private String sendPicUrl = "http://198.199.97.166/interpreter/server/face.php";
    private String loginUrl = "http://198.199.97.166/interpreter/server/login_s.php";

    static double target_photo_scale_factor = 0.25;

    private static final Config config = new Config();

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

    public String getTargetPhotoFileName() {
        return targetPhotoFileName;
    }

    public String getScaledTargetPhotoFileName() {
        return targetPhotoFileName_scaled;
    }

    public String getSendPicUrl() {
        return sendPicUrl;
    }

    public void setSelfId(String selfId) {
        this.selfId = selfId;
    }

    public String getSelfName() {
        return selfName;
    }

    public void setSelfName(String selfName) {
        this.selfName = selfName;
    }
    
    public String getSelfLanguage() {
        return selfLanguage;
    }

    public void setSelfLanguage(String selfLanguage) {
        this.selfLanguage = selfLanguage;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTargetRealName() {
        return targetRealName;
    }

    public void setTargetRealName(String targetRealName) {
        this.targetRealName = targetRealName;
    }

    public static Config getConfig() {
        return config;
    }
    
    public String getSendUrl(){
    	return sendUrl;
    }
    public String getReceiveUrl(){
    	return receiveUrl;
    }

    public String getloginUrl(){
    	return loginUrl;
    }
}
