package voice_client.voice_msg;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JTextArea;

import voice_client.Dic;

public class SendVoiceMsgThread extends Thread implements Dic{
	FileInputStream fis; 
    OutputStream os; 
    DataOutputStream dos; 
    int port; 
    ServerSocket serverSocket; 
    Socket socket; 
    
    private int flag;
    private InetAddress ia;
    private String otherName;
    private String userName;
    private DatagramPacket sendDp;
    private DatagramSocket ds;
    private JTextArea displayArea; 

    public SendVoiceMsgThread(int flag, InetAddress ia, String otherName, String userName, DatagramPacket sendDp,
			DatagramSocket ds, JTextArea displayArea) {
		this.flag = flag;
		this.ia = ia;
		this.otherName = otherName;
		this.userName = userName;
		this.sendDp = sendDp;
		this.ds = ds;
		this.displayArea = displayArea;
	}

	public void run() { 
        try { 
            File file = new File(flag+".au"); 
            String ip = ia.getHostAddress(); 
            serverSocket = new ServerSocket(0); 
            port = serverSocket.getLocalPort(); 
            String msg = SEND_VOICE_MSG + otherName + "#" + ip + "@" + port + "@" + userName + "向你发送语音留言"; 
            byte[] by = msg.getBytes(); 
            sendDp.setData(by); 
            ds.send(sendDp); 
            socket = serverSocket.accept(); 

            os = socket.getOutputStream(); 
            dos = new DataOutputStream(os); 
            fis = new FileInputStream(file); 

            byte b[] = new byte[8192]; 
            int a; 
            while ((a = fis.read(b)) != -1) { 
                dos.write(b, 0, a); 
                dos.flush(); 
            } 
            displayArea.append("语音留言" + "发送完成\n"); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            try { 
                if (serverSocket != null) { 
                    serverSocket.close(); 
                } 
                if (fis != null) { 
                    fis.close(); 
                } 
                if (dos != null) { 
                    dos.close(); 
                } 
                if (socket != null) { 
                    socket.close(); 
                } 
            } catch (Exception e) { 
                e.printStackTrace(); 
            } 
        } 
    } 
}
