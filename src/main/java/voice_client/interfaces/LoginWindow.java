package voice_client.interfaces;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginWindow {

	private String LOGIN_MSG = "登陆信息";
	private String serverIP = "192.168.102.190";
	private Integer serverPort = 8989;
	private Integer localPort = 8090;

	private JFrame frame;
	private JTextField loginName;
	private JPasswordField loginPassword;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginWindow window = new LoginWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public LoginWindow() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setTitle("\u767B\u9646");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel nameLabel = new JLabel("\u6635\u79F0\uFF1A");
		nameLabel.setFont(new Font("宋体", Font.PLAIN, 27));
		nameLabel.setBounds(116, 57, 81, 32);
		frame.getContentPane().add(nameLabel);

		JLabel passswordLabel = new JLabel("密码：");
		passswordLabel.setFont(new Font("宋体", Font.PLAIN, 27));
		passswordLabel.setBounds(116, 110, 81, 32);
		frame.getContentPane().add(passswordLabel);

		loginName = new JTextField();
		loginName.setFont(new Font("宋体", Font.PLAIN, 27));
		loginName.setBounds(212, 57, 130, 32);
		frame.getContentPane().add(loginName);
		loginName.setColumns(10);

		loginPassword = new JPasswordField();
		loginPassword.setFont(new Font("宋体", Font.PLAIN, 27));
		loginPassword.setBounds(212, 110, 130, 32);
		frame.getContentPane().add(loginPassword);
		loginPassword.setColumns(10);

		JButton loginButton = new JButton("\u767B\u9646");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (loginName.getText() != null) {
					new Thread(new Runnable() {
						public void run() {
							try {
								String userName = loginName.getText();
								String userPassword = new String(loginPassword.getPassword());

								byte[] buf = (LOGIN_MSG + userName + "#" + userPassword).getBytes();

								DatagramPacket getDp = new DatagramPacket(buf, 0, buf.length,
										new InetSocketAddress(serverIP, serverPort));
								DatagramPacket sendDp = new DatagramPacket(buf, 0, buf.length,
										new InetSocketAddress(serverIP, serverPort));
								InetAddress ia = InetAddress.getLocalHost();
								DatagramSocket ds = new DatagramSocket(new InetSocketAddress(ia, localPort));

								sendDp.setData(buf);
								ds.send(sendDp);
								while (!ds.isClosed()) {
									// 接收服务端消息
									ds.receive(getDp);
									String msg = new String(getDp.getData(), 0, getDp.getLength());
									if ("true".equals(msg)) {
										ds.close();
										UDPClient udpc = new UDPClient("192.168.102.190", serverPort, localPort,
												userName);
										System.out.println("192.168.102.190" + ":" + serverPort + ":" + localPort + ":"
												+ userName);
										frame.setVisible(false);
										frame = null;
										System.gc();
										udpc.start();
										return;
									} else {
										ds.close();
										JOptionPane.showMessageDialog(frame, "用户名或密码不正确","标题",JOptionPane.WARNING_MESSAGE);
										return;
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();
				} else {
					JOptionPane.showMessageDialog(frame, "用户名为空，请输入用户名！","标题",JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		loginButton.setFont(new Font("宋体", Font.PLAIN, 27));
		loginButton.setBounds(148, 165, 120, 45);
		frame.getContentPane().add(loginButton);
	}
}
