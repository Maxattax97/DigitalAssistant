package ocull.max.assistant;

import java.io.*;
import javax.sound.sampled.*;

public class AudioDownloadThread extends Thread {

	private InputStream audSubIn;
	private SourceDataLine phones;

	public AudioDownloadThread(InputStream input, AudioFormat format) {
		this.audSubIn = input;

		try {
			this.phones = AudioSystem.getSourceDataLine(format);
			phones.open(format);
			phones.start();
		} catch (LineUnavailableException e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void run() {
		try {
			System.out.println("Playing synthesized data ...");

			int bytesRead;
			byte[] buffer = new byte[8 * 1024];
			while (true) {
				bytesRead = audSubIn.read(buffer, 0, buffer.length);
				if (bytesRead == -1)
					break;
				// System.out.println(buffer[0]);
				phones.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			if (!AssistantClient.exiting) {
				e.printStackTrace();
			} else {
				System.out.println("Audio download thread closed.");
			}
		}
	}
}
