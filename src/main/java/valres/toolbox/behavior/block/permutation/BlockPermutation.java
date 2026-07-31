package valres.toolbox.behavior.block.permutation;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.block.component.BlockComponent;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final public class BlockPermutation {
    final private Map<String, BlockComponent> components = new LinkedHashMap<>();

    private String condition;

    public BlockPermutation(@NonNull String condition) {
        if (condition.isBlank()) {
            throw new IllegalArgumentException(
                "Permutation condition cannot be empty"
            );
        }
        this.condition = condition;
    }

    public @NonNull String getCondition() {
        return this.condition;
    }

    public @NonNull BlockPermutation setCondition(
        @NonNull String condition
    ) {
        if (condition.isBlank()) {
            throw new IllegalArgumentException(
                "Permutation condition cannot be empty"
            );
        }
        this.condition = condition;
        return this;
    }

    public @NonNull Map<String, BlockComponent> getComponents() {
        return Collections.unmodifiableMap(this.components);
    }

    public @NonNull BlockPermutation addComponent(
        @NonNull BlockComponent component
    ) {
        this.components.put(component.getIdentifier(), component);
        return this;
    }

    public @NonNull CompoundTag toNBT() {
        CompoundTag componentData = new CompoundTag();
        for (Map.Entry<String, BlockComponent> entry : this.components.entrySet()) {
            componentData.put(entry.getKey(), entry.getValue().toNBT());
        }

        return new CompoundTag()
            .putString("condition", this.condition)
            .putCompound("components", componentData);
    }
}
