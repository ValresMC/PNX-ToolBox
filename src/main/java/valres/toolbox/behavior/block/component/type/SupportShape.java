package valres.toolbox.behavior.block.component.type;

public enum SupportShape {
    FENCE("fence"),
    STAIR("stair");

    final private String value;

    SupportShape(String value) {
        this.value = value;
    }

    @Override public String toString() {
        return this.value;
    }
}
