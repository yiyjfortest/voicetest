package voice_client.voice_chat;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class SendVoiceThread extends Thread {
	private boolean b;
	private byte[] sendBuf;
	private DatagramPacket sendVoice; 
	private DatagramSocket ds;
	
    public SendVoiceThread(boolean b, byte[] sendBuf, DatagramPacket sendVoice, DatagramSocket ds) {
		this.b = b;
		this.sendBuf = sendBuf;
		this.sendVoice = sendVoice;
		this.ds = ds;
	}


	public void run() { 
        AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100F, 16, 1, 2, 44100F,false); 
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat); 
        TargetDataLine targetDataLine = null; 
        try { 
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info); 
            targetDataLine.open(audioFormat); 
            info = new DataLine.Info(SourceDataLine.class, audioFormat); 
            targetDataLine.start(); 
        
            while (b) { 
                targetDataLine.read(sendBuf, 0, sendBuf.length); 
                ds.send(sendVoice); 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
   } 

}
