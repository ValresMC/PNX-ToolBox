package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;
import valres.toolbox.behavior.block.component.type.BlockFace;
import valres.toolbox.behavior.block.component.type.LiquidTouchReaction;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Defines how the block contains, blocks and reacts to nearby liquids.
 */
final public class LiquidDetectionComponent extends BlockComponent {
    final private List<Map<String, ?>> detectionRules;

    public LiquidDetectionComponent(
        @NonNull Collection<? extends Map<String, ?>> detectionRules
    ) {
        this.detectionRules = List.copyOf(Objects.requireNonNull(
            detectionRules,
            "Detection rules cannot be null"
        ));
    }

    public static @NonNull Map<String, ?> waterRule(
        boolean canContainLiquid
    ) {
        return waterRule(
            canContainLiquid,
            (LiquidTouchReaction) null,
            List.of(),
            null
        );
    }

    public static @NonNull Map<String, ?> waterRule(
        boolean canContainLiquid,
        @Nullable LiquidTouchReaction onLiquidTouches,
        @NonNull Collection<BlockFace> stopsLiquidFlowingFromDirection,
        @Nullable Boolean useLiquidClipping
    ) {
        return rawWaterRule(
            canContainLiquid,
            onLiquidTouches == null ? null : onLiquidTouches.toString(),
            Objects.requireNonNull(
                stopsLiquidFlowingFromDirection,
                "Stopped liquid-flow directions cannot be null"
            ).stream()
                .map(BlockFace::toString)
                .toList(),
            useLiquidClipping
        );
    }

    public static @NonNull Map<String, ?> rawWaterRule(
        boolean canContainLiquid,
        @Nullable String onLiquidTouches,
        @NonNull Collection<String> stopsLiquidFlowingFromDirection,
        @Nullable Boolean useLiquidClipping
    ) {
        Objects.requireNonNull(
            stopsLiquidFlowingFromDirection,
            "Stopped liquid-flow directions cannot be null"
        );
        Map<String, Object> rule = new LinkedHashMap<>();
        rule.put("liquid_type", "water");
        rule.put("can_contain_liquid", canContainLiquid);
        rule.put("on_liquid_touches", onLiquidTouches);
        rule.put(
            "stops_liquid_flowing_from_direction",
            stopsLiquidFlowingFromDirection.isEmpty()
                ? null
                : List.copyOf(stopsLiquidFlowingFromDirection)
        );
        rule.put("use_liquid_clipping", useLiquidClipping);
        return rule;
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.LIQUID_DETECTION;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.compound(
            "detection_rules", this.detectionRules
        );
    }
}
