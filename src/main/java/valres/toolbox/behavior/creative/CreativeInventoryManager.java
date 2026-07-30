package valres.toolbox.behavior.creative;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.customitem.data.CreativeCategory;
import org.powernukkitx.item.customitem.data.CreativeGroup;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.registry.CreativeItemRegistry;
import org.powernukkitx.registry.Registries;
import valres.toolbox.behavior.annotation.CreativeInventoryInfo;
import valres.toolbox.behavior.item.builder.DataDrivenItemBuilder;
import valres.toolbox.behavior.item.properties.CreativeCategoryProperty;
import valres.toolbox.behavior.item.properties.CreativeGroupProperty;

final public class CreativeInventoryManager {
    final private static CreativeInventoryManager INSTANCE = new CreativeInventoryManager();

    private CreativeInventoryManager() {
    }

    public static CreativeInventoryManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds an item using its {@link CreativeInventoryInfo} annotation, or an
     * automatically detected placement when the annotation is absent.
     */
    public void addToCreative(@NonNull Item item) {
        CreativePlacement placement = this.resolve(item);
        if (placement.hidden()) {
            return;
        }

        this.add(item, placement.category(), placement.groupName());
    }

    public void add(
        @NonNull Item item,
        @NonNull CreativeCategory category,
        @NonNull CreativeGroup group
    ) {
        this.add(item, category, group == CreativeGroup.NONE ? "" : group.getGroupName());
    }

    public void add(
        @NonNull Item item,
        @NonNull CreativeCategory category,
        @NonNull String groupName
    ) {
        if (category == CreativeCategory.NONE || category == CreativeCategory.ITEM_COMMAND_ONLY) {
            return;
        }
        if (Registries.CREATIVE.isCreativeItem(item)) {
            return;
        }

        int groupIndex = this.resolveGroupIndex(item.getId(), category, groupName);
        Registries.CREATIVE.addCreativeItem(item, groupIndex);
    }

    public void applyDefinition(@NonNull DataDrivenItemBuilder builder) {
        CreativePlacement placement = this.resolve(builder.getItem());
        builder.addProperty(new CreativeCategoryProperty(
            placement.hidden() ? CreativeCategory.NONE : placement.category()
        ));
        builder.addProperty(new CreativeGroupProperty(placement.groupName()));
    }

    CreativePlacement resolve(@NonNull Item item) {
        CreativeInventoryInfo info = item.getClass().getAnnotation(CreativeInventoryInfo.class);
        if (info != null) {
            String customGroup = info.customGroup().trim();
            if (!customGroup.isEmpty() && info.group() != CreativeGroup.NONE) {
                throw new IllegalStateException(
                    "Item '" + item.getId() + "' cannot define both group and customGroup"
                );
            }

            String groupName = customGroup.isEmpty()
                ? this.groupName(info.group())
                : customGroup;
            boolean hidden = info.hidden()
                || info.category() == CreativeCategory.NONE
                || info.category() == CreativeCategory.ITEM_COMMAND_ONLY;

            return new CreativePlacement(info.category(), groupName, hidden);
        }

        return this.detectPlacement(item);
    }

    private CreativePlacement detectPlacement(@NonNull Item item) {
        if (!item.isArmor()) {
            return CreativePlacement.DEFAULT;
        }

        CreativeGroup group = switch (item.getWearableType()) {
            case HEAD -> CreativeGroup.HELMET;
            case CHEST -> CreativeGroup.CHESTPLATE;
            case LEGS -> CreativeGroup.LEGGINGS;
            case FEET -> CreativeGroup.BOOTS;
            case NONE -> CreativeGroup.NONE;
        };

        if (group == CreativeGroup.NONE) {
            return CreativePlacement.DEFAULT;
        }

        return new CreativePlacement(
            CreativeCategory.EQUIPMENT,
            this.groupName(group),
            false
        );
    }

    private int resolveGroupIndex(
        @NonNull String identifier,
        @NonNull CreativeCategory category,
        @NonNull String groupName
    ) {
        if (groupName.isBlank()) {
            return CreativeItemRegistry.getLastGroupIndexFrom(category.name());
        }

        CompoundTag properties = new CompoundTag()
            .putInt("creative_category", category.getId())
            .putString("creative_group", groupName);
        CompoundTag definition = new CompoundTag().putCompound(
            "components",
            new CompoundTag().putCompound("item_properties", properties)
        );

        return Registries.CREATIVE.resolveGroupIndexFromItemDefinition(identifier, definition);
    }

    private @NonNull String groupName(@NonNull CreativeGroup group) {
        return group == CreativeGroup.NONE ? "" : group.getGroupName();
    }

    record CreativePlacement(
        CreativeCategory category,
        String groupName,
        boolean hidden
    ) {
        final private static CreativePlacement DEFAULT = new CreativePlacement(
            CreativeCategory.ITEMS,
            "",
            false
        );
    }
}
