package ocull.max.assistant;

import java.io.*;
import javax.sound.sampled.*;

public class AudioUploadThread extends Thread {

	private OutputStream micOut;
	private TargetDataLine microphone;

	public AudioUploadThread(OutputStream output, AudioFormat format) {
		this.micOut = output;
		
		try {
			this.microphone = AudioSystem.getTargetDataLine(format);
			microphone.open(format);
			microphone.start();
		} catch (LineUnavailableException e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void run() {
		try {
			System.out.println("Sending microphone data ...");
			
			int bytesRead;
			byte[] buffer = new byte[microphone.getBufferSize() / 5];
			while (true) {
				bytesRead = microphone.read(buffer, 0, buffer.length);
				if (bytesRead == -1)
					break;
				micOut.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			if (!AssistantClient.exiting) {
				e.printStackTrace();
			} else {
				System.out.println("Audio upload thread closed.");
			}
		}
	}
}
