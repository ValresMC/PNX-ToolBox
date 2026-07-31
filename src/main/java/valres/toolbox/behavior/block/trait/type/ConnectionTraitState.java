package valres.toolbox.behavior.block.trait.type;

public enum ConnectionTraitState {
	CARDINAL_CONNECTIONS("minecraft:cardinal_connections");

	private final String value;

	ConnectionTraitState(String value) {
		this.value = value;
	}

	@Override public String toString() {
		return this.value;
	}
}
