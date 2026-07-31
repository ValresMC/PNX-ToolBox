package valres.toolbox.behavior.block.component.type;

public enum SupportShape {
	FENCE("fence"), STAIR("stair");

	private final String value;

	SupportShape(String value) {
		this.value = value;
	}

	@Override public String toString() {
		return this.value;
	}
}
