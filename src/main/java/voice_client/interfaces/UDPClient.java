package voice_client.interfaces;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import voice_client.Dic;
import voice_client.trans_file.GetFileThread;
import voice_client.trans_file.SendFileThread;
import voice_client.voice_chat.GetVoiceThread;
import voice_client.voice_chat.SendVoiceThread;
import voice_client.voice_msg.ListenVoiceThread;
import voice_client.voice_msg.RecordStopThread;
import voice_client.voice_msg.RecordThread;
import voice_client.voice_msg.SendVoiceMsgThread;
import voice_client.words_msg.Enter;

public class UDPClient extends Thread implements Dic, ActionListener {

	private JFrame frmSs;
	private JTextArea displayArea;
	private JTextArea sendArea;
	private JScrollPane displayPane;
	private JScrollPane sendPane;
	private JButton sendButton;
	private JButton callButton;
	private JButton callingButton;
	private JButton voiceButton;
	private JButton sendVoiceButton;
	private JButton fileButton;
	private JButton playLastVoiceButton;

	private static boolean b = true;
	public static int flag = 1;

	BufferedReader in;
	PrintWriter out;
	DataInputStream dis;
	DataOutputStream dos;

	DatagramSocket ds;
	DatagramPacket sendDp;
	DatagramPacket getDp;
	DatagramPacket sendVoice;
	InetAddress ia;
	byte buf[] = new byte[1024];
	byte sendBuf[] = new byte[1024];
	byte getBuf[] = new byte[1024];
	String userName;
	private static String otherName;

	public UDPClient(String serverIP, Integer serverPort, Integer localPort, String name) {
		try {
			this.userName = name;
			
			// Enumeration<NetworkInterface> netInterfaces = null;
			// netInterfaces = NetworkInterface.getNetworkInterfaces();
			// while (netInterfaces.hasMoreElements()) {
			// NetworkInterface ni = netInterfaces.nextElement();
			// Enumeration<InetAddress> ips = ni.getInetAddresses();
			// while (ips.hasMoreElements()) {
			// String hostAddress = ips.nextElement().getHostAddress();
			// if (hostAddress.startsWith("192")) {
			// ia = Inet4Address.getByName(hostAddress);
			// }
			// }
			// }
			
			ia = InetAddress.getLocalHost();
			
			ds = new DatagramSocket(new InetSocketAddress(ia, localPort));
			getDp = new DatagramPacket(buf, 0, buf.length, new InetSocketAddress(serverIP, serverPort));
			sendDp = new DatagramPacket(buf, 0, buf.length, new InetSocketAddress(serverIP, serverPort));

			MyAdapter ma = new MyAdapter(userName, buf, sendDp, ds);
			frmSs = new JFrame();
			frmSs.setTitle(userName);
			frmSs.addWindowListener(ma);
			frmSs.setLocation(300, 200);
			frmSs.setBounds(100, 100, 602, 612);
			frmSs.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frmSs.getContentPane().setLayout(null);

			displayPane = new JScrollPane();
			displayPane.setBounds(5, 5, 570, 300);
			frmSs.getContentPane().add(displayPane);

			displayArea = new JTextArea();
			displayArea.setLineWrap(true);
			displayArea.setFont(new Font("宋体", Font.PLAIN, 24));
			displayArea.setEditable(false);
			displayPane.setViewportView(displayArea);
			displayPane.setEnabled(false);

			sendPane = new JScrollPane();
			sendPane.setBounds(5, 362, 570, 149);
			frmSs.getContentPane().add(sendPane);

			sendArea = new JTextArea();
			sendArea.setFont(new Font("宋体", Font.PLAIN, 24));
			sendArea.setLineWrap(true);
			Enter enter = new Enter(sendArea, userName, sendDp, ds);
			sendArea.addKeyListener(enter);
			sendPane.setViewportView(sendArea);

			sendButton = new JButton("\u53D1\u9001");
			sendButton.setFont(new Font("宋体", Font.PLAIN, 20));
			sendButton.setBounds(452, 515, 123, 32);
			frmSs.getContentPane().add(sendButton);
			sendButton.addActionListener(this);
			// 语音通信
			callButton = new JButton("\u8BED\u97F3\u901A\u4FE1");
			callButton.setFont(new Font("宋体", Font.PLAIN, 20));
			callButton.setBounds(5, 311, 123, 44);
			frmSs.getContentPane().add(callButton);
			callButton.addActionListener(this);

			callingButton = new JButton("语音结束");
			callingButton.setFont(new Font("宋体", Font.PLAIN, 20));
			callingButton.setBounds(5, 311, 123, 44);
			frmSs.getContentPane().add(callingButton);
			callingButton.setVisible(false);
			callingButton.addActionListener(this);

			// 语音留言
			voiceButton = new JButton("\u8BED\u97F3\u7559\u8A00");
			voiceButton.setFont(new Font("宋体", Font.PLAIN, 20));
			voiceButton.setBounds(142, 311, 123, 44);
			frmSs.getContentPane().add(voiceButton);
			voiceButton.addActionListener(this);

			sendVoiceButton = new JButton("发送");
			sendVoiceButton.setFont(new Font("宋体", Font.PLAIN, 20));
			sendVoiceButton.setBounds(142, 311, 123, 44);
			frmSs.getContentPane().add(sendVoiceButton);
			sendVoiceButton.setVisible(false);
			sendVoiceButton.addActionListener(this);

			fileButton = new JButton("\u53D1\u9001\u6587\u4EF6");
			fileButton.setFont(new Font("宋体", Font.PLAIN, 20));
			fileButton.setBounds(280, 311, 123, 44);
			frmSs.getContentPane().add(fileButton);
			fileButton.addActionListener(this);

			playLastVoiceButton = new JButton("播放语音留言");
			playLastVoiceButton.setFont(new Font("宋体", Font.PLAIN, 20));
			playLastVoiceButton.setBounds(420, 311, 155, 44);
			frmSs.getContentPane().add(playLastVoiceButton);
			playLastVoiceButton.addActionListener(this);

			frmSs.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			// 发送到服务端
			buf = (SYS_MSG + ADD_USER + userName).getBytes();
			sendDp.setData(buf);
			ds.send(sendDp);
			while (!ds.isClosed()) {
				// 接收服务端消息
				ds.receive(getDp);
				String msg = new String(getDp.getData(), 0, getDp.getLength());
				parserMsg(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ds.close();
		}
	}

	public void parserMsg(String msg) {
		SendVoiceThread svt = new SendVoiceThread(b, sendBuf, sendVoice, ds);
		GetVoiceThread gvt = new GetVoiceThread(getBuf, b);
		if (msg.startsWith(SYS_MSG)) {
			msg = msg.replaceFirst(SYS_MSG, "");
			if (msg.startsWith(ADD_USER)) {
				msg = msg.replaceFirst(ADD_USER, "");
				otherName = msg;
				msg += "进入聊天室\n";
				displayArea.append(msg);
			} else if (msg.startsWith(ONLINE_USER)) {
				msg = msg.replaceFirst(ONLINE_USER, "");
				otherName = msg;
				msg += "正在聊天室\n";
				displayArea.append(msg);
			} else if (msg.startsWith(LOGOUT)) {
				msg = msg.replaceFirst(LOGOUT, "");
				msg += "退出聊天室\n";
				displayArea.append(msg);
			} else if (msg.startsWith(GET_IP)) {
				msg = msg.replaceFirst(GET_IP, "");
				try {
					sendVoice = new DatagramPacket(sendBuf, sendBuf.length, InetAddress.getByName(msg), 9090);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (msg.startsWith(USER_MSG)) {
			msg = msg.replaceFirst(USER_MSG, "");
			if (msg.startsWith(PUBLIC_MSG)) {
				msg = msg.replaceFirst(PUBLIC_MSG, "");
				String[] strs = msg.split(" ", 2);
				if (strs[0].equals(userName)) {
					msg = msg.replaceFirst(userName, "我");
				}
				displayArea.append(msg + "\n");
			}
		} else if (msg.startsWith(SEND_FILE_MSG)) {
			msg = msg.replaceFirst(SEND_FILE_MSG, "");
			String str[] = msg.split("@", 3);
			msg = str[2];
			String fileName = msg.substring(msg.lastIndexOf("：") + 1);
			displayArea.append(msg + "\n");
			int n = JOptionPane.showConfirmDialog(null, "是否接收文件：" + fileName, "文件接收确认", JOptionPane.OK_OPTION);
			if (n == 0) {
				System.out.println(str[0] + ":" + str[1]);
				GetFileThread gft = new GetFileThread(str[0], str[1], fileName, displayArea);
				gft.start();
			} else {
				displayArea.append("拒绝接收文件：" + fileName + "\n");
			}
		} else if (msg.startsWith(SEND_VOICE_MSG)) {
			msg = msg.replaceFirst(SEND_VOICE_MSG, "");
			String str[] = msg.split("@", 3);
			msg = str[2];
			String fileName = ++flag + ".au";
			displayArea.append(msg + "\n");
			int n = JOptionPane.showConfirmDialog(null, "是否收听语音留言：", "收听确认", JOptionPane.OK_OPTION);
			if (n == 0) {
				System.out.println(str[0] + ":" + str[1]);
				ListenVoiceThread lvt = new ListenVoiceThread(str[0], str[1], fileName, displayArea);
				lvt.start();
			} else {
				displayArea.append("拒绝接收文件：" + fileName + "\n");
			}
		} else if (msg.startsWith(SEND_VOICE_REQ_CON)) {
			int n = JOptionPane.showConfirmDialog(null, "是否接听语音：", "语音接听确认", JOptionPane.OK_OPTION);
			if (n == 0) {
				try {
					msg = VOICE_START + otherName + "#" + userName + "语音通信";
					byte[] by = msg.getBytes();
					sendDp.setData(by);
					ds.send(sendDp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				displayArea.append("拒绝接听语音" + "\n");
			}
		} else if (msg.startsWith(VOICE_START)) {
			b = true;
			svt.start();
			gvt.start();
			callingButton.setVisible(true);
			callButton.setVisible(false);
		} else if (msg.startsWith(VOICE_STOP)) {
			b = false;
			svt.interrupt();
			gvt.interrupt();

			callingButton.setVisible(false);
			callButton.setVisible(true);
		}
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

	public String setSendMsg(String msg) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		msg = USER_MSG + PUBLIC_MSG + userName + " " + dateFormat.format(new Date()) + " 说: " + msg;
		return msg;
	}

	public void actionPerformed(ActionEvent e) {
		RecordThread rt = new RecordThread(flag);
		flag++;
		RecordStopThread rst = new RecordStopThread();
		if (e.getSource() == sendButton) {
			String msg = sendArea.getText();
			if ("".equals(msg)) {
				JOptionPane.showMessageDialog(null, "消息不能为空！");
				return;
			}
			msg = setSendMsg(msg);
			send(msg);
			sendArea.setText("");
		} else if (e.getSource() == fileButton) {
			SendFileThread sft = new SendFileThread(otherName, userName, sendDp, ds, displayArea, ia);
			sft.start();
		} else if (e.getSource() == callButton) {
			try {
				String msg = SEND_VOICE_REQ + otherName + "#" + userName + "向你发送语音请求";
				byte[] by = msg.getBytes();
				sendDp.setData(by);
				ds.send(sendDp);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == callingButton) {
			String msg = VOICE_STOP + otherName + "#" + userName + "结束语音";
			byte[] by = msg.getBytes();
			sendDp.setData(by);
			try {
				ds.send(sendDp);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == voiceButton) {
			voiceButton.setVisible(false);
			sendVoiceButton.setVisible(true);
			rt.start();
		} else if (e.getSource() == sendVoiceButton) {
			voiceButton.setVisible(true);
			sendVoiceButton.setVisible(false);
			rst.start();
			rt.interrupt();
			SendVoiceMsgThread svmt = new SendVoiceMsgThread(flag, ia, otherName, userName, getDp, ds, displayArea);
			svmt.start();
		} else if (e.getSource() == playLastVoiceButton) {
			if (flag >= 2) {
				File file = new File(flag + ".au");
				AudioInputStream ais;
				try {
					ais = AudioSystem.getAudioInputStream(file);
					AudioFormat audioFormat = ais.getFormat();
					SourceDataLine sourceDataLine = null;
					DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
					sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
					sourceDataLine.open(audioFormat);
					sourceDataLine.start();
					int nByte = 0;
					final int bufSize = 1024;
					byte[] buffer = new byte[bufSize];
					while (nByte != -1) {
						nByte = ais.read(buffer, 0, bufSize);
						sourceDataLine.write(buffer, 0, nByte);
					}
					sourceDataLine.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				displayArea.append("上一条语音留言收听完毕\n");
			} else {
				JOptionPane.showMessageDialog(null, "沒有语音留言！");
				return;
			}
		}
	}
}