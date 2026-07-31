package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;

public final class ProjectileComponent extends DataDrivenItemComponent {
	private final String projectileEntity;
	private final Float minimumCriticalPower;

	public ProjectileComponent(@NonNull String projectileEntity) {
		this(projectileEntity, null);
	}

	public ProjectileComponent(String projectileEntity, Float minimumCriticalPower) {
		this.projectileEntity = projectileEntity;
		this.minimumCriticalPower = minimumCriticalPower;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.PROJECTILE;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("projectile_entity", this.projectileEntity, "minimum_critical_power", this.minimumCriticalPower);
	}
}
