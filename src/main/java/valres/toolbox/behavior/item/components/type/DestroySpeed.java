package valres.toolbox.behavior.item.components.type;

import org.jspecify.annotations.NonNull;

import java.util.Map;

final public class DestroySpeed implements ItemComponentValue {
    final private Object block;
    final private int speed;

    public DestroySpeed(@NonNull BlockDescriptor block, int speed) {
        this((Object) block, speed);
    }

    public DestroySpeed(@NonNull String block, int speed) {
        this((Object) block, speed);
    }

    public DestroySpeed(@NonNull Map<String, ?> block, int speed) {
        this((Object) Map.copyOf(block), speed);
    }

    private DestroySpeed(Object block, int speed) {
        this.block = block;
        this.speed = speed;
    }

    @Override public @NonNull Map<String, ?> toMap() {
        return Map.of(
            "block", this.block,
            "speed", this.speed
        );
    }
}
