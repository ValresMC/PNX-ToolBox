package valres.toolbox.behavior.block.trait.type;

import org.jspecify.annotations.NonNull;
import valres.toolbox.behavior.block.component.type.BlockComponentValue;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final public class BlockDescriptor implements BlockComponentValue {
    final private String name;
    final private Map<String, ?> states;
    final private String tags;

    public BlockDescriptor() {
        this(null, null, null);
    }

    public BlockDescriptor(
        @Nullable String name,
        @Nullable Map<String, ?> states,
        @Nullable String tags
    ) {
        this.name = name;
        this.states = states == null
            ? null
            : new LinkedHashMap<>(states);
        this.tags = tags;
    }

    public static @NonNull BlockDescriptor named(@NonNull String name) {
        return named(name, null);
    }

    public static @NonNull BlockDescriptor named(
        @NonNull String name,
        @Nullable Map<String, ?> states
    ) {
        return new BlockDescriptor(
            Objects.requireNonNull(name, "Block name cannot be null"),
            states,
            null
        );
    }

    public static @NonNull BlockDescriptor tagged(@NonNull String query) {
        return new BlockDescriptor(
            null,
            null,
            Objects.requireNonNull(query, "Block tag query cannot be null")
        );
    }

    @Override public @NonNull Map<String, ?> toMap() {
        Map<String, Object> descriptor = new LinkedHashMap<>();
        if (this.name != null) {
            descriptor.put("name", this.name);
        }
        if (this.states != null) {
            descriptor.put("states", this.states);
        }
        if (this.tags != null) {
            descriptor.put("tags", this.tags);
        }
        return descriptor;
    }

    public static @NonNull List<Object> listToValues(
        @NonNull Collection<?> descriptors
    ) {
        Objects.requireNonNull(descriptors, "Block descriptors cannot be null");
        List<Object> values = new ArrayList<>(descriptors.size());

        for (Object descriptor : descriptors) {
            switch (descriptor) {
                case BlockDescriptor typed -> values.add(typed.toMap());
                case String name -> values.add(name);
                case Map<?, ?> raw -> values.add(new LinkedHashMap<>(raw));
                case null -> throw new IllegalArgumentException(
                    "A block descriptor cannot be null"
                );
                default -> throw new IllegalArgumentException(
                    "Unsupported block descriptor type: "
                        + descriptor.getClass().getName()
                );
            }
        }

        return List.copyOf(values);
    }
}
