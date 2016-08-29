package ocull.max.assistant.command;

import ocull.max.assistant.Assistant;
import ocull.max.assistant.ClientConnection;

public class IdentifyAssistantCommand extends Command {

	public IdentifyAssistantCommand(ClientConnection conn, Assistant assistant, String message) {
		super(conn, assistant, message);
	}

	@Override
	public double getProbability() {
		double prob = 0;

		prob = checkWord(prob, 0.25, "who", "identify");
		prob = checkPhrase(prob, 0.5, "your self");
		prob = checkWord(prob, 0.5, "you", "yourself");
		prob = checkWord(prob, 0.1, "hi", "hello");
		
		return prob;
	}

	@Override
	public void run() {
		super.assistant.speak(conn, "My name is " + super.assistant.name + ". Pleasure to meet you, " + conn.userName + ".",
				"I am " + super.assistant.name + ", a digital assistant that will cater to all your everyday needs.",
				"I am " + super.assistant.name + ", of course. Do you not remember, " + conn.userName + "?");
	}

}
