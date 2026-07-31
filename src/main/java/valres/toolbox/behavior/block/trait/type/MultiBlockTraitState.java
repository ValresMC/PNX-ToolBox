package valres.toolbox.behavior.block.trait.type;

public enum MultiBlockTraitState {
	MULTI_BLOCK_PART("minecraft:multi_block_part");

	private final String value;

	MultiBlockTraitState(String value) {
		this.value = value;
	}

	@Override public String toString() {
		return this.value;
	}
}
