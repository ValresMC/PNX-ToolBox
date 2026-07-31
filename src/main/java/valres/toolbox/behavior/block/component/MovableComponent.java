package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;
import valres.toolbox.behavior.block.component.type.MovableType;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Defines how pistons can move the block and whether it is sticky.
 */
final public class MovableComponent extends BlockComponent {
    final private String movementType;
    final private String sticky;

    public MovableComponent() {
        this(MovableType.PUSH_PULL, null);
    }

    public MovableComponent(
        @NonNull MovableType movementType,
        @Nullable String sticky
    ) {
        this(
            Objects.requireNonNull(
                movementType,
                "Movement type cannot be null"
            ).toString(),
            sticky
        );
    }

    public MovableComponent(
        @NonNull String movementType,
        @Nullable String sticky
    ) {
        this.movementType = Objects.requireNonNull(
            movementType,
            "Movement type cannot be null"
        );
        this.sticky = sticky;
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.MOVABLE;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.compound(
            "movement_type", this.movementType,
            "sticky", this.sticky
        );
    }
}
