package valres.toolbox.behavior.block.component.type;

public enum MovableType {
	IMMOVABLE("immovable"), POPPED("popped"), PUSH("push"), PUSH_PULL("push_pull");

	private final String value;

	MovableType(String value) {
		this.value = value;
	}

	@Override public String toString() {
		return this.value;
	}
}
