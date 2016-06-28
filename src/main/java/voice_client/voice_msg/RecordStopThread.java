package voice_client.voice_msg;

public class RecordStopThread extends Thread {
	
	public void run() { 
        RecordThread.targetDataLine.stop(); 
    } 
}
