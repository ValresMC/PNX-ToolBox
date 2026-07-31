package valres.toolbox.behavior.item.components.type;

import java.util.Map;
import org.jspecify.annotations.NonNull;

@FunctionalInterface
public interface ItemComponentValue {
	@NonNull Map<String, ?> toMap();
}
