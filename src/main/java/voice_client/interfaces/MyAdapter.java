package voice_client.interfaces;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import voice_client.Dic;

public class MyAdapter extends WindowAdapter implements Dic {
	private String userName;
	private byte[] buf;
	private DatagramPacket sendDp;
	private DatagramSocket ds;
	
	public MyAdapter(String userName, byte[] buf, DatagramPacket sendDp, DatagramSocket ds) {
		this.userName = userName;
		this.buf = buf;
		this.sendDp = sendDp;
		this.ds = ds;
	}

	public void windowClosing(WindowEvent e) {
		try {
			buf = (SYS_MSG + LOGOUT + userName).getBytes();
			sendDp.setData(buf);
			ds.send(sendDp);
			System.exit(0);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}
}
