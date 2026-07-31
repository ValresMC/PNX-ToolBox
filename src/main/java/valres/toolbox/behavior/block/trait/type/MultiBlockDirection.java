package valres.toolbox.behavior.block.trait.type;

public enum MultiBlockDirection {
    UP("up"),
    DOWN("down");

    final private String value;

    MultiBlockDirection(String value) {
        this.value = value;
    }

    @Override public String toString() {
        return this.value;
    }
}
