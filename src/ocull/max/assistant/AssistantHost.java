package ocull.max.assistant;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

public class AssistantHost {
	
	private static ServerSocket audioServer;
	private static ServerSocket textServer;
	
	public static String ttsHost;
	public static int ttsPort;
	public static int port;
	
	public static SpeechRecognitionModule recog;
	public static SpeechSynthesisModule synth;
	
	public static String platform;
	public static String userName;
	
	public static ArrayList<ClientConnection> connections;
	public static Hashtable<String, Assistant> assistants;
	
	public static Random randGenerator;
	
	// AssistantHost [port] [ttsHost] [ttsPort]
	public static void main(String[] args) {
		if (args.length > 0 && (args[0].indexOf("help") != -1 || args[0].indexOf("?") != -1)) {
			System.out.println("Usage: AssistantHost [port] [ttsHost] [ttsPort]");
		} else {
			port = args.length >= 1 ? Integer.parseInt(args[0]) : 8700;
			ttsHost = args.length >= 2 ? args[1] : "127.0.0.1";
			ttsPort = args.length == 3 ? Integer.parseInt(args[2]) : 59125; // Mary TTS default port.
		}
		
		randGenerator = new Random();
		
		synth = new SpeechSynthesisModule(ttsHost, ttsPort);
		
		assistants = new Hashtable<String, Assistant>();
		connections = new ArrayList<ClientConnection>();
		
		assistants.put("Julia", new Assistant("Julia", new String[] { "Julia", "Jules", "Jewels", "Jewel", "Juliet", "Julie"}, "cmu-slt-hsmm"));
		
		try {
			audioServer = new ServerSocket(AssistantHost.port);
			textServer = new ServerSocket(AssistantHost.port + 1);
			System.out.println("Awaiting connections on ports " + AssistantHost.port + " and " + (AssistantHost.port + 1) + " ...");
			
	        while (true) {
	        	ClientConnection conn = new ClientConnection(audioServer.accept(), textServer.accept());
	        	connections.add(conn);
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
