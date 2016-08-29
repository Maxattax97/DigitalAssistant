package ocull.max.assistant;

import java.util.*;

import javax.sound.sampled.*;

import ocull.max.assistant.command.*;

public class Assistant {
	
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_BLUE = "\u001B[34m";
	
	public String name;
	public String[] aliases;
	public String voice;
	// Storage object that serializes and saves data to a file.
	
	public AudioFormat voiceFormat;
	
	public Assistant(String name, String[] aliases, String voice) {
		this.name = name;
		this.aliases = aliases;
		this.voice = voice;
		
		//this.voiceFormat = ;
	}
	
	public void speak(ClientConnection conn, String... messages) {
		// Send synthesized voice to client.
		String message = messages[AssistantHost.randGenerator.nextInt(messages.length)];
		
		AssistantHost.synth.synthesize(conn, this, message);
		
		String coloredMessage = ANSI_BLUE + "[" + name + "]: " + message + ANSI_RESET;
		System.out.println(coloredMessage);
		conn.sendTextPacket(StreamDataTypeFlag.SUBTITLE, coloredMessage);
	}
	
	public void listen(ClientConnection conn, String message) {
		
		String coloredMessage = ANSI_GREEN + "[" + conn.userName + "]: " + message + ANSI_RESET;
		System.out.println(coloredMessage);
		conn.sendTextPacket(StreamDataTypeFlag.SUBTITLE, coloredMessage);
		
		for (String alias : aliases) {
			if (message.equalsIgnoreCase(alias)) {
				speak(conn, "Yes?", "How can I help you?", "Yes " + conn.userName + "?");
			}
		}
		
		// Resolve queries?
		
		// Process message into command, execute, etc...
		List<Command> commands = new ArrayList<Command>();
		List<Command> conflicting = new ArrayList<Command>();
		double highestProb = 0;
		
		commands.add(new LockOperatingSystemCommand(conn, this, message));
		commands.add(new IdentifyAssistantCommand(conn, this, message));
		commands.add(new DisconnectAssistantCommand(conn, this, message));
		commands.add(new HelloCommand(conn, this, message));
		commands.add(new TimeCommand(conn, this, message));
		
		for (Command command : commands) {
			double prob = command.getProbability();
			if (prob > highestProb) {
				highestProb = prob;
				conflicting.clear();
				conflicting.add(command);
			} else if (prob == highestProb) {
				conflicting.add(command);
			}
		}
		
		System.out.println(highestProb + " " + conflicting.toString());
		if (highestProb >= 0.33) {
			if (conflicting.size() > 1) {
				speak(conn, conflicting.size() + " commands are conflicting. Please rephrase your command.");
			} else {
				System.out.println("Selected " + Command.class.getSimpleName() + " with " + highestProb + " probability.");
				conflicting.get(0).run();
			}
		} else {
			//speak(conn, "Command not understood: " + message);
			System.out.println("Command not understood: " + message);
		}
		
		/*
		if (message.length() > 0) {
			if (message.equals("who are you")) {
				speak("My name is " + this.name + ". Pleasure to meet you, Max.");
			} else if (message.equals("good morning")) {
				speak("Good morning Max.");
	        	speak("Today's agenda has a breakfast in Wiley Food Court, according to your Google Calendar.");
	        	speak("It is rainy outside with an average humidity of 87%. Rainboots are recommended.");
			} else {
				speak("I am sorry, but I do not understand your message: " + message);
			}
		}*/
	}
}
