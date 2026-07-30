package valres.toolbox.behavior.item.components.type;

import org.jspecify.annotations.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;

final public class Ammunition implements ItemComponentValue {
    final private String item;
    final private Boolean searchInventory;
    final private Boolean useInCreative;
    final private Boolean useOffhand;

    public Ammunition(@NonNull String item) {
        this(item, null, null, null);
    }

    public Ammunition(
        @NonNull String item,
        Boolean searchInventory,
        Boolean useInCreative,
        Boolean useOffhand
    ) {
        this.item = item;
        this.searchInventory = searchInventory;
        this.useInCreative = useInCreative;
        this.useOffhand = useOffhand;
    }

    @Override public @NonNull Map<String, ?> toMap() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("item", this.item);
        values.put("search_inventory", this.searchInventory);
        values.put("use_in_creative", this.useInCreative);
        values.put("use_offhand", this.useOffhand);
        return values;
    }
}
