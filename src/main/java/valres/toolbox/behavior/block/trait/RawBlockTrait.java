package valres.toolbox.behavior.block.trait;

import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;

public final class RawBlockTrait extends BlockTrait {
	public static final String RAW_IDENTIFIER = "toolbox:raw_trait";

	private final String identifier;
	private final CompoundTag nbt;

	public RawBlockTrait(@NonNull String identifier, @NonNull CompoundTag nbt) {
		this.identifier = Objects.requireNonNull(identifier, "Trait identifier cannot be null");
		this.nbt = Objects.requireNonNull(nbt, "Trait NBT cannot be null").copy();
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
