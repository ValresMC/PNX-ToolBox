package valres.toolbox.behavior.block.component.type;

public enum ChestObstructionRule {
    ALWAYS("always"),
    NEVER("never"),
    SHAPE("shape");

    final private String value;

    ChestObstructionRule(String value) {
        this.value = value;
    }

    @Override public String toString() {
        return this.value;
    }
}
