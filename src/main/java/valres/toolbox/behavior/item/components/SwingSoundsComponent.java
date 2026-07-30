package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

final public class SwingSoundsComponent extends DataDrivenItemComponent {
    final private String attackMiss;
    final private String attackHit;
    final private String attackCriticalHit;

    public SwingSoundsComponent() {
        this(null, null, null);
    }

    public SwingSoundsComponent(
        String attackMiss,
        String attackHit,
        String attackCriticalHit
    ) {
        this.attackMiss = attackMiss;
        this.attackHit = attackHit;
        this.attackCriticalHit = attackCriticalHit;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemComponentNames.SWING_SOUNDS;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return ComponentNbtHelper.compound(
            "attack_miss", this.attackMiss,
            "attack_hit", this.attackHit,
            "attack_critical_hit", this.attackCriticalHit
        );
    }
}
