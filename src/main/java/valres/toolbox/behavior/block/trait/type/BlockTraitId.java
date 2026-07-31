package valres.toolbox.behavior.block.trait.type;

public enum BlockTraitId {
	CONNECTION("minecraft:connection"), MULTI_BLOCK("minecraft:multi_block"), PLACEMENT_DIRECTION("minecraft:placement_direction"), PLACEMENT_POSITION("minecraft:placement_position");

	private final String value;

	BlockTraitId(String value) {
		this.value = value;
	}

	@Override public String toString() {
		return this.value;
	}
}
