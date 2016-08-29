package ocull.max.assistant.command;

import java.time.*;
import java.time.format.*;

import ocull.max.assistant.Assistant;
import ocull.max.assistant.ClientConnection;

public class TimeCommand extends Command {

	public TimeCommand(ClientConnection conn, Assistant assistant, String message) {
		super(conn, assistant, message);
	}

	@Override
	public double getProbability() {
		double prob = 0;

		prob = checkWord(prob, 0.5, "time", "times");
		prob = checkWord(prob, 0.1, "what");
		prob = checkPhrase(prob, 0.1, "is it");
		prob = checkWord(prob, 0.3, "current", "currently");
				
		return prob;
	}

	@Override
	public void run() {
		
		LocalTime lt = ZonedDateTime.now().toLocalTime();
		String time = (lt.getHour() % 12) + ":" + lt.getMinute();
		
		String timeOfDay = "afternoon";
		if (ZonedDateTime.now().getHour() <= 12) {
			timeOfDay = "morning";
		}
		
		super.assistant.speak(super.conn, "It is currently " + time + " in the " + timeOfDay + ".");
	}

}
