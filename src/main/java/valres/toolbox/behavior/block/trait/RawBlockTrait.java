package valres.toolbox.behavior.block.trait;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.List;
import java.util.Objects;

final public class RawBlockTrait extends BlockTrait {
    final public static String RAW_IDENTIFIER = "toolbox:raw_trait";

    final private String identifier;
    final private CompoundTag nbt;

    public RawBlockTrait(
        @NonNull String identifier,
        @NonNull CompoundTag nbt
    ) {
        this.identifier = Objects.requireNonNull(
            identifier,
            "Trait identifier cannot be null"
        );
        this.nbt = Objects.requireNonNull(
            nbt,
            "Trait NBT cannot be null"
        ).copy();
    }

    @Override public @NonNull String getIdentifier() {
        return this.identifier;
    }

    @Override protected @NonNull List<?> enabledStates() {
        return List.of();
    }

    @Override public @NonNull CompoundTag toNBT() {
        return this.nbt.copy();
    }
}
