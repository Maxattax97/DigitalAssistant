package ocull.max.assistant.command;

import ocull.max.assistant.Assistant;
import ocull.max.assistant.ClientConnection;
import ocull.max.assistant.StreamDataTypeFlag;

public class DisconnectAssistantCommand extends Command {

	public DisconnectAssistantCommand(ClientConnection conn, Assistant assistant, String message) {
		super(conn, assistant, message);
	}

	@Override
	public double getProbability() {
		double prob = 0;

		prob = checkWord(prob, 0.5, "disconnect", "disconnected", "bye", "goodbye", "good-bye");
		prob = checkPhrase(prob, 0.5, "good bye");
		prob = checkWord(prob, 0.25, "client");
		prob = checkWord(prob, 0.1, "this");
		prob = checkWord(prob, 0.1, "connect", "connects");

		return prob;
	}

	@Override
	public void run() {
		super.assistant.speak(super.conn, "Closing socket. Goodbye, " + conn.userName + ".",
				"Goodbye " + conn.userName + ", you will be disconnected momentarily.",
				"Remember to reconnect. I get lonely " + conn.userName + "... Severing connection.");
		try {
			Thread.sleep(5000);
			super.conn.sendTextPacket(StreamDataTypeFlag.END);
			super.conn.close();
		} catch (InterruptedException e) {
			super.conn.sendTextPacket(StreamDataTypeFlag.END);
			super.conn.close();
			e.printStackTrace();
		}
	}

}
