package valres.toolbox.behavior.block.component.type;

public enum BlockFace {
	ALL("all"), SIDE("side"), DOWN("down"), UP("up"), NORTH("north"), SOUTH("south"), WEST("west"), EAST("east");

	private final String value;

	BlockFace(String value) {
		this.value = value;
	}

	@Override public String toString() {
		return this.value;
	}
}
