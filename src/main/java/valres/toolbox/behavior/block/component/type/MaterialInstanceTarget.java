package valres.toolbox.behavior.block.component.type;

public enum MaterialInstanceTarget {
    ALL("*"),
    UP("up"),
    DOWN("down"),
    NORTH("north"),
    SOUTH("south"),
    EAST("east"),
    WEST("west");

    final private String value;

    MaterialInstanceTarget(String value) {
        this.value = value;
    }

    @Override public String toString() {
        return this.value;
    }
}
