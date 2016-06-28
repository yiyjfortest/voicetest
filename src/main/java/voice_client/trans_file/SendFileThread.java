package voice_client.trans_file;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFileChooser;
import javax.swing.JTextArea;

import voice_client.Dic;

public class SendFileThread extends Thread implements Dic {
    FileInputStream fis; 
    OutputStream os; 
    DataOutputStream dos; 
    int port; 
    ServerSocket serverSocket; 
    Socket socket; 
    
    private String otherName;
    private String userName;
    private DatagramPacket sendDp;
    private DatagramSocket ds;
    private JTextArea displayArea;
    private InetAddress ia; 

	public SendFileThread(String otherName, String userName, DatagramPacket sendDp, DatagramSocket ds,
			JTextArea displayArea, InetAddress ia) {
		this.otherName = otherName;
		this.userName = userName;
		this.sendDp = sendDp;
		this.ds = ds;
		this.displayArea = displayArea;
		this.ia = ia;
	}

	public void run() { 
        try { 
            JFileChooser jfc = new JFileChooser(""); 
            jfc.showOpenDialog(null); 
            File f = jfc.getSelectedFile(); 
            String fname = jfc.getName(f); 
            String ip = ia.getHostAddress(); 
            serverSocket = new ServerSocket(0); 
            port = serverSocket.getLocalPort(); 
            String msg = SEND_FILE_MSG + otherName + "#" + ip + "@" + port + "@" + userName + "向你发送文件：" + fname; 
            System.out.println(msg); 
            byte[] by = msg.getBytes(); 
            sendDp.setData(by); 
            ds.send(sendDp); 
            socket = serverSocket.accept(); 

            os = socket.getOutputStream(); 
            dos = new DataOutputStream(os); 
            fis = new FileInputStream(f); 

            byte b[] = new byte[8192]; 
            int a; 
            long l = 0L; 
            while ((a = fis.read(b)) != -1) { 
                dos.write(b, 0, a); 
                dos.flush(); 
                l += a; 
                System.out.println("已发送文件大小：" + l); 
            } 
            displayArea.append("文件" + fname + "发送完成\n"); 
            System.out.println("文件发送完成"); 
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
