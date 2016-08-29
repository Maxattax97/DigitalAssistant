package ocull.max.assistant;

import java.io.*;

public class TextThread extends Thread {

	private DataInputStream input;
	private DataOutputStream output;

	public TextThread(DataInputStream in, DataOutputStream out) {
		this.input = in;
		this.output = out;
	}

	@Override
	public void run() {
		boolean exit = false;
		while (!exit) {
			try {
				byte flag = input.readByte();
				if (flag == StreamDataTypeFlag.SUBTITLE.getValue()) {
					System.out.println(input.readUTF());
				} else if (flag == StreamDataTypeFlag.COMMAND.getValue()) {
					System.out.println("COMMAND> " + input.readUTF());
				} else if (flag == StreamDataTypeFlag.DATA.getValue()) {
					System.out.println("DATA> " + input.readUTF());
				} else if (flag == StreamDataTypeFlag.END.getValue()) {
					System.out.println("Exit packet recieved. Disconnecting from host ...");
					input.close();
					output.close();
					AssistantClient.exit();
					break;
				}
			} catch (IOException e) {
				exit = true;
				e.printStackTrace();
				System.out.println("Abruptly disconnected from host. Exiting ...");
				AssistantClient.exit();
			}
		}
	}

}
