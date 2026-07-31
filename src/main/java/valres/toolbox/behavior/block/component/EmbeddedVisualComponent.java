package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;
import valres.toolbox.behavior.block.component.type.BlockVisual;

import java.util.Objects;

/**
 * Defines an embedded geometry and material visual for the placed block.
 */
final public class EmbeddedVisualComponent extends BlockComponent {
    final private BlockVisual visual;

    public EmbeddedVisualComponent(@NonNull BlockVisual visual) {
        this.visual = Objects.requireNonNull(
            visual,
            "Block visual cannot be null"
        );
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.EMBEDDED_VISUAL;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.tag(this.visual);
    }
}
