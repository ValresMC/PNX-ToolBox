package valres.toolbox.behavior.creative;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockCrops;
import org.powernukkitx.block.BlockOre;
import org.powernukkitx.block.BlockSapling;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.SpawnEggPickable;
import org.powernukkitx.item.customitem.data.CreativeCategory;
import org.powernukkitx.item.customitem.data.CreativeGroup;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.registry.CreativeItemRegistry;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.ItemTags;
import valres.toolbox.behavior.annotation.CreativeInventoryInfo;
import valres.toolbox.behavior.item.builder.DataDrivenItemBuilder;
import valres.toolbox.behavior.item.properties.CreativeCategoryProperty;
import valres.toolbox.behavior.item.properties.CreativeGroupProperty;

import java.util.Locale;

final public class CreativeInventoryManager {
    final private static CreativeInventoryManager INSTANCE = new CreativeInventoryManager();
    final private static String SMITHING_TEMPLATES_GROUP = "itemGroup.name.smithing_templates";

    private CreativeInventoryManager() {
    }

    public static CreativeInventoryManager getInstance() {
        return INSTANCE;
    }

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
        String itemPath = this.identifierPath(item.getId());

        if (item instanceof SpawnEggPickable
            || item.hasTag(ItemTags.SPAWN_EGG)
            || itemPath.endsWith("_spawn_egg")) {
            return this.placement(CreativeCategory.NATURE, CreativeGroup.MOB_EGGS);
        }

        if (item.isArmor()) {
            CreativeGroup armorGroup = switch (item.getWearableType()) {
                case HEAD -> CreativeGroup.HELMET;
                case CHEST -> CreativeGroup.CHESTPLATE;
                case LEGS -> CreativeGroup.LEGGINGS;
                case FEET -> CreativeGroup.BOOTS;
                case NONE -> CreativeGroup.NONE;
            };

            if (armorGroup != CreativeGroup.NONE) {
                return this.placement(CreativeCategory.EQUIPMENT, armorGroup);
            }
        }

        CreativeGroup toolGroup = this.detectToolGroup(item);
        if (toolGroup != CreativeGroup.NONE) {
            return this.placement(CreativeCategory.EQUIPMENT, toolGroup);
        }
        if (this.isUngroupedEquipment(item)) {
            return new CreativePlacement(CreativeCategory.EQUIPMENT, "", false);
        }

        if (itemPath.endsWith("_smithing_template")) {
            return new CreativePlacement(
                CreativeCategory.ITEMS,
                SMITHING_TEMPLATES_GROUP,
                false
            );
        }

        Block block = item.getBlock();
        if (block instanceof BlockOre || itemPath.endsWith("_ore")) {
            return this.placement(CreativeCategory.NATURE, CreativeGroup.ORE);
        }
        if (block instanceof BlockSapling || itemPath.endsWith("_sapling")) {
            return this.placement(CreativeCategory.NATURE, CreativeGroup.SAPLING);
        }
        if (this.isSeed(item, itemPath)) {
            return this.placement(CreativeCategory.NATURE, CreativeGroup.SEED);
        }
        if (block instanceof BlockCrops
            || itemPath.endsWith("_crop")
            || itemPath.endsWith("_crops")) {
            return this.placement(CreativeCategory.NATURE, CreativeGroup.CROP);
        }

        return CreativePlacement.DEFAULT;
    }

    private @NonNull CreativeGroup detectToolGroup(@NonNull Item item) {
        if (item.isSword()) {
            return CreativeGroup.SWORD;
        }
        if (item.isPickaxe()) {
            return CreativeGroup.PICKAXE;
        }
        if (item.isAxe()) {
            return CreativeGroup.AXE;
        }
        if (item.isShovel()) {
            return CreativeGroup.SHOVEL;
        }
        if (item.isHoe()) {
            return CreativeGroup.HOE;
        }

        return CreativeGroup.NONE;
    }

    private boolean isUngroupedEquipment(@NonNull Item item) {
        return item.isTool()
            || item.isShears()
            || item.isBow()
            || item.isCrossbow()
            || item.isSpear()
            || item.isTrident()
            || item.isMace()
            || item.isShield();
    }

    private boolean isSeed(@NonNull Item item, @NonNull String itemPath) {
        String className = item.getClass().getSimpleName().toLowerCase(Locale.ROOT);
        return itemPath.endsWith("_seed")
            || itemPath.endsWith("_seeds")
            || className.endsWith("seed")
            || className.endsWith("seeds");
    }

    private @NonNull CreativePlacement placement(
        @NonNull CreativeCategory category,
        @NonNull CreativeGroup group
    ) {
        return new CreativePlacement(
            category,
            this.groupName(group),
            false
        );
    }

    private @NonNull String identifierPath(@NonNull String identifier) {
        int separator = identifier.indexOf(':');
        String path = separator < 0 ? identifier : identifier.substring(separator + 1);
        return path.toLowerCase(Locale.ROOT);
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
