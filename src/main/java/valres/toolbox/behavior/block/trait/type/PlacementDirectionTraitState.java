package valres.toolbox.behavior.block.trait.type;

public enum PlacementDirectionTraitState {
    CARDINAL_DIRECTION("minecraft:cardinal_direction"),
    FACING_DIRECTION("minecraft:facing_direction"),
    CORNER_AND_CARDINAL_DIRECTION(
        "minecraft:corner_and_cardinal_direction"
    );

    final private String value;

    PlacementDirectionTraitState(String value) {
        this.value = value;
    }

    @Override public String toString() {
        return this.value;
    }
}
