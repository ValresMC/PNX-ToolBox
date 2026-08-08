package valres.toolbox.menu.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.junit.jupiter.api.Test;
import org.powernukkitx.Player;
import org.powernukkitx.event.inventory.ItemStackRequestActionEvent;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.item.Item;
import valres.toolbox.menu.InventoryMenu;

final class MenuTransactionTest {
	@Test void exposesAStableMenuFacingTransaction() {
		InventoryMenu menu = mock(InventoryMenu.class);
		Inventory inventory = mock(Inventory.class);
		Player player = mock(Player.class);
		Item clicked = mock(Item.class);
		Item clickedSnapshot = mock(Item.class);
		Item cursor = mock(Item.class);
		Item cursorSnapshot = mock(Item.class);
		ItemStackRequestAction action = mock(ItemStackRequestAction.class);
		ItemStackRequestActionEvent event = mock(ItemStackRequestActionEvent.class);

		when(menu.getInventory()).thenReturn(inventory);
		when(clicked.clone()).thenReturn(clickedSnapshot);
		when(clickedSnapshot.clone()).thenReturn(clickedSnapshot);
		when(cursor.clone()).thenReturn(cursorSnapshot);
		when(cursorSnapshot.clone()).thenReturn(cursorSnapshot);
		when(event.getAction()).thenReturn(action);
		when(action.getType()).thenReturn(ItemStackRequestActionType.SWAP);
		when(event.isCancelled()).thenReturn(true);

		MenuTransaction transaction = new MenuTransaction(menu, player, 7, clicked, cursor, event);

		assertSame(menu, transaction.getMenu());
		assertSame(inventory, transaction.getInventory());
		assertSame(player, transaction.getPlayer());
		assertEquals(7, transaction.getSlot());
		assertSame(clickedSnapshot, transaction.getItemClicked());
		assertSame(cursorSnapshot, transaction.getItemClickedWith());
		assertSame(action, transaction.getAction());
		assertEquals(ItemStackRequestActionType.SWAP, transaction.getActionType());
		assertSame(event, transaction.getEvent());
		assertTrue(transaction.isAlreadyCancelled());
	}

	@Test void returnsExplicitTransactionDecisions() {
		MenuTransaction transaction = new MenuTransaction(mock(InventoryMenu.class), mock(Player.class), 0, itemSnapshot(), itemSnapshot(), mock(ItemStackRequestActionEvent.class));

		assertFalse(transaction.allow().isCancelled());
		assertTrue(transaction.cancel().isCancelled());
	}

	private static Item itemSnapshot() {
		Item item = mock(Item.class);
		when(item.clone()).thenReturn(item);
		return item;
	}
}
