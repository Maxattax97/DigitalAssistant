package ocull.max.assistant;

import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class AssistantClient {

	private static String host = "127.0.0.1";
	private static int port = 8700;
	
	public static Socket audioSocket;
	public static Socket textSocket;
	
	public static InputStream fromServerAudioStream;
	public static OutputStream toServerAudioStream;
	public static DataInputStream fromServerTextStream;
	public static DataOutputStream toServerTextStream;
	
	public static boolean exiting = false;
	
	public static void main(String[] args) throws InterruptedException {
		System.out.println("Connecting to " + host + ":" + port + "-" + (port + 1) + " ...");
		try {
			audioSocket = new Socket(host, port);
			textSocket = new Socket(host, port + 1);
			System.out.println("Connection established.");

			fromServerAudioStream = audioSocket.getInputStream();
			toServerAudioStream = audioSocket.getOutputStream();
			fromServerTextStream = new DataInputStream(textSocket.getInputStream());
			toServerTextStream = new DataOutputStream(textSocket.getOutputStream());
			
			//AudioFormat format = new AudioFormat(48000, 16, 1, true, false);
			
			(new TextThread(fromServerTextStream, toServerTextStream)).start();
			(new AudioUploadThread(toServerAudioStream, new AudioFormat(16000, 16, 1, true, false))).start();
			(new AudioDownloadThread(fromServerAudioStream, new AudioFormat(48000, 16, 1, true, false))).start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void exit() {
		exiting = true;
		try {
			toServerAudioStream.close();
			fromServerAudioStream.close();
			audioSocket.close();
			// Streams already closed in TextThread.
			textSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
