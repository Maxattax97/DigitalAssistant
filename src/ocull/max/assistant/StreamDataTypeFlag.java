package ocull.max.assistant;

public enum StreamDataTypeFlag {
	
	END(0),
	SUBTITLE(1),
	COMMAND(2),
	DATA(3);
	
	private int value;
	
	private StreamDataTypeFlag(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
}
