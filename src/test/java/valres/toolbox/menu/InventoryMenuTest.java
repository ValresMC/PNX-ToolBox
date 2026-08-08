package valres.toolbox.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.powernukkitx.Player;
import valres.toolbox.menu.transaction.MenuTransaction;
import valres.toolbox.menu.transaction.MenuTransactionDecision;

final class InventoryMenuTest {
	@Test void createsANativeInventoryAndUpdatesItsName() {
		InventoryMenu menu = InventoryMenu.create(MenuType.CHEST);

		assertSame(MenuType.CHEST, menu.getType());
		assertEquals(27, menu.getInventory().getSize());
		assertEquals("Chest", menu.getName());

		assertSame(menu, menu.setName("My Menu"));
		assertEquals("My Menu", menu.getName());
	}

	@Test void readOnlyHandlerObservesThenCancelsTransactions() {
		Player player = mock(Player.class);
		MenuTransaction transaction = mock(MenuTransaction.class);
		AtomicBoolean observed = new AtomicBoolean();
		when(transaction.cancel()).thenReturn(MenuTransactionDecision.CANCEL);

		MenuTransactionDecision decision = InventoryMenu.readOnly(ignored -> observed.set(true)).onTransaction(player, transaction);

		assertTrue(observed.get());
		assertSame(MenuTransactionDecision.CANCEL, decision);
	}
}
