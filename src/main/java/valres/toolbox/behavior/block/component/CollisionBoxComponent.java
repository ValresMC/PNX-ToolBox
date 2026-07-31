package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;
import valres.toolbox.behavior.block.component.type.BlockBox;

import java.util.List;

final public class CollisionBoxComponent extends BlockComponent {
    final private boolean enabled;
    final private List<BlockBox> boxes;

    public CollisionBoxComponent(boolean enabled) {
        this(enabled, enabled ? List.of(BlockBox.cube()) : List.of());
    }

    public CollisionBoxComponent(@NonNull BlockBox box) {
        this(true, List.of(box));
    }

    public CollisionBoxComponent(@NonNull List<BlockBox> boxes) {
        this(true, boxes);
    }

    private CollisionBoxComponent(
        boolean enabled,
        @NonNull List<BlockBox> boxes
    ) {
        this.enabled = enabled;
        this.boxes = List.copyOf(boxes);
    }

    public static @NonNull CollisionBoxComponent box(
        @NonNull List<? extends Number> origin,
        @NonNull List<? extends Number> size
    ) {
        return new CollisionBoxComponent(new BlockBox(origin, size));
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.COLLISION_BOX;
    }

    @Override public @NonNull Tag toNBT() {
        if (!this.enabled) {
            return ComponentNbtHelper.compound("enabled", false);
        }

        return ComponentNbtHelper.compound(
            "enabled", true,
            "boxes",
            this.boxes.stream().map(BlockBox::toCollisionMap).toList()
        );
    }
}
