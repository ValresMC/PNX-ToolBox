package valres.toolbox.behavior.item.components.type;

public enum CooldownType {
    USE("use"),
    ATTACK("attack");

    final private String value;

    CooldownType(String value) {
        this.value = value;
    }

    @Override public String toString() {
        return this.value;
    }
}
