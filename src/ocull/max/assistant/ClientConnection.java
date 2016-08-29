package ocull.max.assistant;

import java.io.*;
import java.net.*;

public class ClientConnection {

	private Socket audioClient;
	private Socket textClient;
	public InputStream fromClientAudioStream;
	public OutputStream toClientAudioStream;
	public DataInputStream fromClientTextStream;
	public DataOutputStream toClientTextStream;
	
	public String platform;
	public String userName;

	public SpeechRecognitionModule recog;
	
	public Assistant assistant;
	
	public ClientConnection(Socket audioClient, Socket textClient) {
		try {
			System.out.println("Client connected.");
			this.audioClient = audioClient;
			this.textClient = textClient;
			
			// TODO MOVE THIS TO CLIENT-SIDE, NOT SERVER.
			// Detect the client's platform.
			String os = System.getProperty("os.name").toLowerCase();
			if (os.indexOf("win") != -1) {
				platform = "windows";
			} else if (os.indexOf("nix") != -1 || os.indexOf("nux") != -1 || os.indexOf("aix") != -1) {
				platform = "linux";
			} else if (os.indexOf("mac") != -1) {
				platform = "mac";
			} else {
				platform = "unknown";
				System.out.println("Unable to detect local operating system: " + os);
			}

			userName = "Max";
			assistant = AssistantHost.assistants.get("Julia");

			toClientAudioStream = audioClient.getOutputStream();
			fromClientAudioStream = audioClient.getInputStream();
			toClientTextStream = new DataOutputStream(textClient.getOutputStream());
			fromClientTextStream = new DataInputStream(textClient.getInputStream());
			
			recog = new SpeechRecognitionModule(this);

			//assistant = new Assistant("Julia", new String[] { "Julia", "Jules", "Jewels", "Jewel", "Juliet" },
			//		"cmu-slt-hsmm", recog, synth);

			recog.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			audioClient.shutdownOutput();
			audioClient.shutdownInput();
			audioClient.close();
			textClient.shutdownOutput();
			textClient.shutdownInput();
			textClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendTextPacket(StreamDataTypeFlag flag, String message) {
		try {
			this.toClientTextStream.writeByte(flag.getValue());
			this.toClientTextStream.writeUTF(message);
			this.toClientTextStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendTextPacket(StreamDataTypeFlag flag) {
		try {
			this.toClientTextStream.writeByte(flag.getValue());
			this.toClientTextStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
