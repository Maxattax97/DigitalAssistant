package ocull.max.assistant.command;

import java.io.IOException;

import ocull.max.assistant.Assistant;
import ocull.max.assistant.AssistantHost;
import ocull.max.assistant.ClientConnection;

public class LockOperatingSystemCommand extends Command {
	
	public final SeverityLevel severity = SeverityLevel.LOW;
	
	public LockOperatingSystemCommand(ClientConnection conn, Assistant assistant, String message) {
		super(conn, assistant, message);
	}

	@Override
	public double getProbability() {
		double prob = 0;

		prob = checkWord(prob, 0.5, "lock", "lockdown", "locke");
		prob = checkPhrase(prob, 0.5, "lock down", "locke down", "walk down");
		prob = checkWord(prob, 0.25, "ubuntu", "windows", "linux");
		prob = checkPhrase(prob, 0.25, "operating system");
		prob = checkWord(prob, 0.25, "desktop");

		return prob;
	}

	@Override
	public void run() {
		if (super.conn.platform.equals("linux")) {
			try {
				Runtime.getRuntime().exec("gnome-screensaver-command -l");
				super.assistant.speak(conn, "Locking down.");
			} catch (IOException e) {
				e.printStackTrace();
				super.assistant.speak(conn, "Unable to lock your desktop. Please read the terminal for more information.");
			}
		} else {
			super.assistant.speak(conn, "I am unable to perform this command on a non Linux operating system.");
		}
	}

}
