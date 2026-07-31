package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;

final public class RawBlockComponent extends BlockComponent {
    final private String identifier;
    final private Tag value;

    public RawBlockComponent(
        @NonNull String identifier,
        @NonNull Tag value
    ) {
        this.identifier = identifier;
        this.value = value;
    }

    @Override public @NonNull String getIdentifier() {
        return this.identifier;
    }

    @Override public @NonNull Tag toNBT() {
        return this.value;
    }
}
