package valres.toolbox.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.powernukkitx.inventory.fake.FakeInventoryType;

final class MenuTypeTest {
	@Test void exposesPowerNukkitXNativeTypes() {
		assertSame(FakeInventoryType.CHEST, MenuType.CHEST.getNativeType());
		assertEquals(27, MenuType.CHEST.getSize());
		assertSame(FakeInventoryType.DOUBLE_CHEST, MenuType.DOUBLE_CHEST.getNativeType());
		assertEquals(54, MenuType.DOUBLE_CHEST.getSize());
		assertSame(FakeInventoryType.WORKBENCH, MenuType.WORKBENCH.getNativeType());
	}

	@Test void createsVariableSizeEntityTypes() {
		MenuType type = MenuType.entity(18);

		assertSame(FakeInventoryType.ENTITY, type.getNativeType());
		assertEquals(18, type.getSize());
		assertEquals("ENTITY(18)", type.toString());
	}

	@Test void rejectsEmptyEntityInventories() {
		assertThrows(IllegalArgumentException.class, () -> MenuType.entity(0));
	}
}
