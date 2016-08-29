package ocull.max.assistant.command;

import java.util.*;

import ocull.max.assistant.Assistant;
import ocull.max.assistant.ClientConnection;

public abstract class Command implements Runnable {

	public enum SeverityLevel {
		AUTHORIZE, // Always ask for authorization before proceeding.
		HIGH, // Always ask for confirmation.
		MEDIUM, // If less than 50% confident, confirm.
		LOW, // Only execute if greater than 33% confident.
		NONE // Always execute if greater than 0% confident.
	}

	public String message;
	public List<String> words;
	public List<String> keywords;
	public List<String> arguments;
	protected ClientConnection conn;
	protected Assistant assistant;
	public final SeverityLevel severity = SeverityLevel.NONE;

	public Command(ClientConnection conn, Assistant assistant, String message) {
		this.conn = conn;
		this.assistant = assistant;
		this.message = message;
		this.words = Arrays.asList(message.split(" "));
		this.keywords = new ArrayList<String>();
		keywords.add(words.get(0));
		this.arguments = words.subList(1, words.size());
	}

	public abstract double getProbability();

	// If message contains one of the words, add weight and return potentially
	// changed probability
	protected double checkWord(double prob, double weight, String... words) {
		for (String word : words) {
			if (this.words.contains(word)) {
				prob += weight;
				return prob;
			}
		}
		return prob;
	}
	
	protected double checkPhrase(double prob, double weight, String... phrases) {
		for (String phrase : phrases) {
			if (this.message.indexOf(phrase) != -1) {
				prob += weight;
				return prob;
			}
		}
		return prob;
	}

	@Override
	public abstract void run();

}
