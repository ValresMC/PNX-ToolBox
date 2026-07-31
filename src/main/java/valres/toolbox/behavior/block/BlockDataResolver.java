package valres.toolbox.behavior.block;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockCrops;
import org.powernukkitx.block.BlockNetherWartBlock;
import org.powernukkitx.item.customitem.data.CreativeCategory;
import org.powernukkitx.item.customitem.data.CreativeGroup;
import org.powernukkitx.utils.BlockColor;
import valres.toolbox.behavior.annotation.CreativeInventoryInfo;
import valres.toolbox.behavior.block.builder.BlockBuilder;
import valres.toolbox.behavior.block.component.*;
import valres.toolbox.behavior.block.component.type.ConnectionRuleMode;
import valres.toolbox.behavior.block.component.type.MaterialInstance;
import valres.toolbox.behavior.block.component.type.RenderMethod;
import valres.toolbox.behavior.block.permutation.PermutationsResolver;

final public class BlockDataResolver {
    private BlockDataResolver() {
    }

    public static void applyDefault(@NonNull BlockBuilder builder) {
        Block block = builder.getBlock();
        RenderMethod renderMethod = block.isTransparent()
            ? RenderMethod.ALPHA_TEST_SINGLE_SIDED
            : RenderMethod.OPAQUE;

        builder.addComponent(new GeometryComponent());
        builder.addComponent(MaterialInstancesComponent.all(
            new MaterialInstance(builder.getName(), renderMethod)
        ));
        builder.addComponent(new DisplayNameBlockComponent(
            "tile." + builder.getIdentifier() + ".name"
        ));
        builder.addComponent(new CollisionBoxComponent(
            block.getCollisionBoxes().length > 0
        ));
        builder.addComponent(new SelectionBoxComponent(true));

        double resistance = block.getResistance();
        builder.addComponent(
            resistance < 0
                ? new DestructibleByExplosionComponent(false)
                : new DestructibleByExplosionComponent(resistance)
        );

        double hardness = block.getHardness();
        builder.addComponent(
            hardness < 0
                ? new DestructibleByMiningComponent(false)
                : new DestructibleByMiningComponent(hardness * 3.33334)
        );

        builder.addComponent(new FrictionBlockComponent(
            block.getFrictionFactor()
        ));
        builder.addComponent(new LightDampeningComponent(
            block.getLightFilter()
        ));
        builder.addComponent(new LightEmissionComponent(
            block.getLightLevel()
        ));
        builder.addComponent(new MapColorComponent(
            toRgbHex(resolveColor(block))
        ));

        if (block instanceof BlockCrops || block instanceof BlockNetherWartBlock) {
            builder.addComponent(new ConnectionRuleComponent(ConnectionRuleMode.NONE));
        }

        for (String tag : block.getTags()) {
            builder.addTag(tag);
        }

        applyCreativePlacement(builder);

        if (block instanceof ExtraBlockComponentsInterface extra) {
            extra.defineBlockComponents(builder);
        }

        PermutationsResolver.resolveAll(builder);
    }

    private static void applyCreativePlacement(
        @NonNull BlockBuilder builder
    ) {
        CreativeInventoryInfo info = builder.getBlock()
            .getClass()
            .getAnnotation(CreativeInventoryInfo.class);
        if (info == null) {
            builder.setCreativePlacement(CreativeCategory.CONSTRUCTION, "");
            return;
        }

        String customGroup = info.customGroup().trim();
        if (!customGroup.isEmpty() && info.group() != CreativeGroup.NONE) {
            throw new IllegalStateException(
                "Block '" + builder.getIdentifier()
                    + "' cannot define both group and customGroup"
            );
        }

        CreativeCategory category = info.hidden()
            ? CreativeCategory.NONE
            : info.category();
        String group = customGroup.isEmpty()
            ? groupName(info.group())
            : customGroup;

        builder
            .setCreativePlacement(category, group)
            .setHiddenInCommands(info.hidden());
    }

    private static @NonNull String groupName(@NonNull CreativeGroup group) {
        return group == CreativeGroup.NONE ? "" : group.getGroupName();
    }

    private static @NonNull String toRgbHex(@NonNull BlockColor color) {
        return "#%02X%02X%02X".formatted(
            color.getRed(),
            color.getGreen(),
            color.getBlue()
        );
    }

    private static @NonNull BlockColor resolveColor(@NonNull Block block) {
        try {
            boolean usesBaseColor = block.getClass()
                .getMethod("getColor")
                .getDeclaringClass() == Block.class;
            if (usesBaseColor && block.getLevel() == null) {
                return BlockColor.WHITE_BLOCK_COLOR;
            }

            return block.getColor();
        } catch (ReflectiveOperationException | RuntimeException exception) {
            return BlockColor.WHITE_BLOCK_COLOR;
        }
    }
}
