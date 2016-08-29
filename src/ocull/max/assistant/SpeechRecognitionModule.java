package ocull.max.assistant;

import java.io.*;
import edu.cmu.sphinx.api.*;
import java.util.logging.Logger;

public class SpeechRecognitionModule extends Thread {

	private Configuration config;
	private ClientConnection conn;
	private StreamSpeechRecognizer recognizer;

	public SpeechRecognitionModule(ClientConnection conn) {
		this.conn = conn;
		
		// Disable Sphinx logging.
		Logger cmuLogger = Logger.getLogger("default.config");
		cmuLogger.setLevel(java.util.logging.Level.WARNING);
		String configFile = System.getProperty("java.util.logging.config.file");
		if (configFile == null) {
			System.setProperty("java.util.logging.config.file", "ignoreAllSphinx4LoggingOutput");
		}

		config = new Configuration();
		config.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		config.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
		config.setLanguageModelPath(System.getProperty("user.dir") + "/en-70k-0.2.lm.bin");

		try {
			recognizer = new StreamSpeechRecognizer(config);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		//format = new AudioFormat(16000, 16, 1, true, false);

	}

	@Override
	public void run() {
		recognizer.startRecognition(conn.fromClientAudioStream);

		conn.assistant.speak(conn, "Recognition started. Listening to live microphone audio ...");

		SpeechResult result;
		while ((result = recognizer.getResult()) != null) {
			String hyp = result.getHypothesis();
			if (!hyp.equals("")) {
				conn.assistant.listen(conn, hyp);
			}
		}

		recognizer.stopRecognition();
	}
}
