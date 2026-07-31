package valres.toolbox.behavior.block.component.type;

public enum CardinalDirection {
    NORTH("north"),
    EAST("east"),
    SOUTH("south"),
    WEST("west");

    final private String value;

    CardinalDirection(String value) {
        this.value = value;
    }

    @Override public String toString() {
        return this.value;
    }
}
