import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.locks.Lock;

class client_simple{
	static String serverUrl = "http://home.cnzy.me:8001/interpreter/test.php";
	String  targetId;
	Lock recordLock;

	static String sendFileUrl = "voice/";
	static String sendFileName = "record.wav";
	String recieveFileUrl;

	public static void main(String[] str){
		send(sendFileUrl,sendFileName,"user2");
	}
	
	private static void send(String sendFileUrl,String sendFileName, String targetId){
		try {
            URL url=new URL(serverUrl);
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.addRequestProperty("userfile", sendFileName);
            connection.setRequestProperty("content-type", "text/html");
            BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());
            
            //读取文件上传到服务器
            File file=new File(sendFileUrl+sendFileName);
            FileInputStream fileInputStream=new FileInputStream(file);
            byte[]bytes=new byte[1024];
            int numReadByte=0;
            while((numReadByte=fileInputStream.read(bytes,0,1024))>0)
            {
                out.write(bytes, 0, numReadByte);
            }
            out.flush();
            fileInputStream.close();
            BufferedInputStream in=new BufferedInputStream(connection.getInputStream());
            byte[] output = new byte[1000];
            System.out.print(output.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	private void recieve(){
		//always run
		//ask for voiceFile from server
		//store in recieveFile
		//play(String recieveFileUrl);
	}

}