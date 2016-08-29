package ocull.max.assistant;

import java.io.*;
import java.net.*;

public class SpeechSynthesisModule {

	private String host;
	private int port;

	public SpeechSynthesisModule(String ttsHost, int ttsPort) {
		this.host = ttsHost;
		this.port = ttsPort;
		//this.synthFormat = new AudioFormat(48000, 16, 1, true, false);
	}

	public void synthesize(ClientConnection conn, Assistant assistant, String message) {
		(new SynthesisThread(conn, assistant, message)).start();
	}

	private class SynthesisThread extends Thread {
		private String cleanMessage;
		private String encodedMessage;
		private ClientConnection conn;
		private Assistant assistant;
		public SynthesisThread(ClientConnection conn, Assistant assistant, String msg) {
			this.conn = conn;
			this.assistant = assistant;
			this.cleanMessage = msg;
		}

		public void processMessage() {
			cleanMessage = cleanMessage.replace('\n', '.');
			if (!cleanMessage.endsWith(".")) {
				cleanMessage = cleanMessage + ".";
			}
			try {
				encodedMessage = URLEncoder.encode(cleanMessage, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				System.err.println("Encoding is not supported: " + e.getMessage());
			}
		}

		@Override
		public void run() {
			processMessage();

			try {
				String hypertextLink = "http://" + host + ":" + port
						+ "/process?INPUT_TYPE=TEXT&OUTPUT_TYPE=AUDIO&AUDIO=WAVE_FILE&LOCALE=en_US&VOICE=" + assistant.voice
						+ "&INPUT_TEXT=" + encodedMessage;
				// System.out.println("Accessing: " + hypertextLink);
				URL url = new URL(hypertextLink);
				URLConnection connection = url.openConnection();

				InputStream resultStream = connection.getInputStream();
				// https://stackoverflow.com/questions/5680259/using-sockets-to-send-and-receive-data

				int bytesRead = -1;
				byte[] buffer = new byte[8 * 1024];
				bytesRead = resultStream.read(buffer, 0, buffer.length);
				for (int i = 0; i < 64; i++) {
					buffer[i] = (byte) (buffer[i] * (i / 64));
				}
				conn.toClientAudioStream.write(buffer, 0, bytesRead);
				while (true) {
					bytesRead = resultStream.read(buffer, 0, buffer.length);
					if (bytesRead == -1)
						break;
					// System.out.println(buffer[0]);
					//conn.toClientStream.write(StreamDataTypeFlag.AUDIO.getValue());
					conn.toClientAudioStream.write(buffer, 0, bytesRead);
				}

			} catch (MalformedURLException e) {
				System.err.println("URL is malformed: " + e.getMessage());
			} catch (IOException e) {
				System.out.println("Potentially unable to connect to MaryTTS server.");
				e.printStackTrace();
			}
		}
	}
}
