package valres.toolbox.behavior.item.properties;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.item.customitem.data.CreativeGroup;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.item.ItemPropertyNames;
import valres.toolbox.behavior.item.components.ComponentNbtHelper;

public final class CreativeGroupProperty extends DataDrivenItemProperty {
	private final String groupName;

	public CreativeGroupProperty(@NonNull CreativeGroup group) {
		this(group == CreativeGroup.NONE ? "" : group.getGroupName());
	}

	public CreativeGroupProperty(@NonNull String groupName) {
		this.groupName = groupName.trim();
	}

	@Override public @NonNull String getIdentifier() {
		return ItemPropertyNames.CREATIVE_GROUP;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.tag(this.groupName);
	}
}
