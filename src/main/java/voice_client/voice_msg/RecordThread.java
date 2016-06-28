package voice_client.voice_msg;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class RecordThread extends Thread {
	static TargetDataLine targetDataLine; 
	private int flag;
	
    public RecordThread(int flag) {
		this.flag = flag;
	}

	public void run() { 
        try { 
            AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100F, 16, 2, 4, 44100F, true); 
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat); 
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info); 
            targetDataLine.open(audioFormat); 
            info = new DataLine.Info(SourceDataLine.class, audioFormat); 
            targetDataLine.start(); 
            AudioInputStream ais = new AudioInputStream(targetDataLine); 
            File file = new File(flag +".au"); 
            if (!file.exists()) { 
                file.createNewFile(); 
            } 
            AudioSystem.write(ais, AudioFileFormat.Type.AU, file); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    } 
}
