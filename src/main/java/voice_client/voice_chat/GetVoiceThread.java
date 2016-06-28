package voice_client.voice_chat;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class GetVoiceThread extends Thread {
    private SourceDataLine sourceDataLine = null; 
    private AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100F, 16, 1, 2, 44100F, false); 
    private DataLine.Info info = null; 
    
    private byte[] getBuf;
    private boolean b;
    
    public GetVoiceThread(byte[] getBuf, boolean b) {
		this.getBuf = getBuf;
		this.b = b;
	}

	/* 
     * 1、创建接受数据包的套接字 2、创建数据包 3、储存数据 4、解析数据 5、关闭资源 
     */ 
    public void play() throws Exception { 

        info = new DataLine.Info(SourceDataLine.class, audioFormat); 
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(info); 
        sourceDataLine.open(audioFormat); 
        sourceDataLine.start(); 
        DatagramSocket dsp = new DatagramSocket(9090); 
        DatagramPacket dp = new DatagramPacket(getBuf, getBuf.length); 
        while (b) { 
            dsp.receive(dp); 
            sourceDataLine.write(dp.getData(), 0, dp.getLength()); 
        } 
        dsp.close();
    } 

    public void run() { 
        try { 
            play(); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    } 
}
