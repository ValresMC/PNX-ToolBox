package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

final public class ThrowableComponent extends DataDrivenItemComponent {
    final private Boolean doSwingAnimation;
    final private Float minDrawDuration;
    final private Float maxDrawDuration;
    final private Float launchPowerScale;
    final private Float maxLaunchPower;
    final private Boolean scalePowerByDrawDuration;

    public ThrowableComponent() {
        this(null, null, null, null, null, null);
    }

    public ThrowableComponent(
        Boolean doSwingAnimation,
        Float minDrawDuration,
        Float maxDrawDuration,
        Float launchPowerScale,
        Float maxLaunchPower,
        Boolean scalePowerByDrawDuration
    ) {
        this.doSwingAnimation = doSwingAnimation;
        this.minDrawDuration = minDrawDuration;
        this.maxDrawDuration = maxDrawDuration;
        this.launchPowerScale = launchPowerScale;
        this.maxLaunchPower = maxLaunchPower;
        this.scalePowerByDrawDuration = scalePowerByDrawDuration;
    }

    @Override public @NonNull String getIdentifier() {
        return ItemComponentNames.THROWABLE;
    }

    @Override public @NonNull CompoundTag toNBT() {
        return ComponentNbtHelper.compound(
            "do_swing_animation", this.doSwingAnimation,
            "min_draw_duration", this.minDrawDuration,
            "max_draw_duration", this.maxDrawDuration,
            "launch_power_scale", this.launchPowerScale,
            "max_launch_power", this.maxLaunchPower,
            "scale_power_by_draw_duration", this.scalePowerByDrawDuration
        );
    }
}
