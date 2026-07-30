package valres.toolbox.behavior.item.components.type;

import org.jspecify.annotations.NonNull;

import java.util.Map;

@FunctionalInterface
public interface ItemComponentValue {
    @NonNull Map<String, ?> toMap();
}
