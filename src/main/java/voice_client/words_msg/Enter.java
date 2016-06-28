package voice_client.words_msg;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import voice_client.Dic;

public class Enter extends KeyAdapter implements Dic{
	private JTextArea sendArea; 
	private String userName;
	private DatagramPacket sendDp;
    private DatagramSocket ds;
	
	public Enter(JTextArea sendArea, String userName, DatagramPacket sendDp, DatagramSocket ds) {
		this.sendArea = sendArea;
		this.userName = userName;
		this.sendDp = sendDp;
		this.ds = ds;
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			String msg = sendArea.getText();
			if ("".equals(msg)) {
				JOptionPane.showMessageDialog(null, "消息不能为空！");
				return;
			}
			msg = setSendMsg(msg);
			send(msg);
			sendArea.setText("");
		}
	}
	
    public String setSendMsg(String msg) { 
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss"); 
        msg = USER_MSG + PUBLIC_MSG + userName + " " + dateFormat.format(new Date()) + " 说: " + msg; 
        return msg; 
    } 
    
    public void send(String msg) { 
        try { 
            byte[] by = msg.getBytes(); 
            sendDp.setData(by); 
            ds.send(sendDp); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    } 
}
