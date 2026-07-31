package valres.toolbox.behavior.block.component.type;

public enum LiquidTouchReaction {
	BLOCKING("blocking"), BROKEN("broken"), NO_REACTION("no_reaction"), POPPED("popped");

	private final String value;

	LiquidTouchReaction(String value) {
		this.value = value;
	}

	@Override public String toString() {
		return this.value;
	}
}
