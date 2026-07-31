package valres.toolbox.behavior.block.trait.type;

public enum PlacementPositionTraitState {
	BLOCK_FACE("minecraft:block_face"), VERTICAL_HALF("minecraft:vertical_half");

	private final String value;

	PlacementPositionTraitState(String value) {
		this.value = value;
	}

	@Override public String toString() {
		return this.value;
	}
}
