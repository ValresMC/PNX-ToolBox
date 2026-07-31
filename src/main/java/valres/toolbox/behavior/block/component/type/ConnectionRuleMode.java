package valres.toolbox.behavior.block.component.type;

public enum ConnectionRuleMode {
    ALL("all"),
    ONLY_FENCES("only_fences"),
    NONE("none");

    final private String value;

    ConnectionRuleMode(String value) {
        this.value = value;
    }

    @Override public String toString() {
        return this.value;
    }
}
