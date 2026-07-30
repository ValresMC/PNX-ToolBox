package valres.toolbox.behavior.item.components.type;

public enum DamageCause {
    ALL("all"),
    ENTITY_ATTACK("entity_attack"),
    ENTITY_EXPLOSION("entity_explosion"),
    BLOCK_EXPLOSION("block_explosion"),
    FIRE("fire"),
    FIRE_TICK("fire_tick"),
    LAVA("lava"),
    MAGMA("magma"),
    FALL("fall"),
    PROJECTILE("projectile"),
    MAGIC("magic"),
    WITHER("wither");

    final private String value;

    DamageCause(String value) {
        this.value = value;
    }

    @Override public String toString() {
        return this.value;
    }
}
