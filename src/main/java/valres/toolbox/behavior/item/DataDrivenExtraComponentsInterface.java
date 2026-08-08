package valres.toolbox.behavior.item;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.customitem.CustomItem;
import org.powernukkitx.item.customitem.CustomItemDefinition;
import org.powernukkitx.registry.ItemRegistry;
import valres.toolbox.behavior.item.builder.DataDrivenItemBuilder;

public interface DataDrivenExtraComponentsInterface extends CustomItem {
	void defineDataDrivenComponent(DataDrivenItemBuilder builder);

	@Override default CustomItemDefinition getDefinition() {
		if (!(this instanceof Item item)) {
			throw new IllegalStateException("A data-driven component owner must extend Item");
		}

		CustomItemDefinition definition = ItemRegistry.getCustomItemDefinitionByIdStatic(item.getId());
		if (definition == null) {
			throw new IllegalStateException("Item '" + item.getId() + "' has not been registered yet");
		}
		return definition;
	}
}
