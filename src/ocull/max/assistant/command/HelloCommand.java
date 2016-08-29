package ocull.max.assistant.command;

import java.time.ZonedDateTime;

import ocull.max.assistant.Assistant;
import ocull.max.assistant.ClientConnection;

public class HelloCommand extends Command {

	public HelloCommand(ClientConnection conn, Assistant assistant, String message) {
		super(conn, assistant, message);
	}

	@Override
	public double getProbability() {
		double prob = 0;

		prob = checkWord(prob, 0.5, "hello", "hi", "hey");
		prob = checkPhrase(prob, 0.5, "good morning", "good afternoon");

		return prob;
	}

	@Override
	public void run() {
		String timeOfDay = "afternoon";
		if (ZonedDateTime.now().getHour() <= 12) {
			timeOfDay = "morning";
		}
		
		super.assistant.speak(conn, "Hello, " + conn.userName + ".",
				"Hi there.",
				"Good " + timeOfDay + ", " + conn.userName + ".");
	}

}
