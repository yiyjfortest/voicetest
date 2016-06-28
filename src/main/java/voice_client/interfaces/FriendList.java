package voice_client.interfaces;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Color;

public class FriendList {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FriendList window = new FriendList();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FriendList() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 364, 703);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel newPanel = new JPanel();
		newPanel.setBackground(Color.WHITE);
		newPanel.setBounds(0, 0, 342, 124);
		frame.getContentPane().add(newPanel);
		
		JTextArea infoArea = new JTextArea();
		infoArea.setBounds(0, 0, 342, 124);
		infoArea.setEditable(false);
		newPanel.add(infoArea);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 124, 342, 522);
		frame.getContentPane().add(scrollPane);
		
		JTextArea friendListArea = new JTextArea();
		scrollPane.setViewportView(friendListArea);
		friendListArea.setEditable(false);
		
		
		
	}
}
