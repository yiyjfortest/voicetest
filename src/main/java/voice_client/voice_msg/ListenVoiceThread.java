package voice_client.voice_msg;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JTextArea;

public class ListenVoiceThread extends Thread {
	FileOutputStream fos;
	InputStream is;
	DataInputStream dis;
	String fileName;
	Socket socket;
	String ip;
	int port;
	private JTextArea displayArea; 

	public ListenVoiceThread(String ip, String port, String fileName, JTextArea displayArea) {
		this.fileName = fileName;
		this.ip = ip;
		this.port = new Integer(port);
		this.displayArea = displayArea;
	}

	public void run() {
		File file = new File(fileName);
		try {
			fos = new FileOutputStream(file);
			socket = new Socket(ip, port);
			is = socket.getInputStream();
			dis = new DataInputStream(is);

			byte b[] = new byte[8192];
			int a = 0;
			while ((a = dis.read(b)) != -1) {
				fos.write(b, 0, a);
				fos.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (dis != null) {
					dis.close();
				}
				if (socket != null) {
					socket.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		displayArea.append("语音留言收听完毕\n");
	}
}
