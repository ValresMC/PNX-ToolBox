# Inventory menus

`InventoryMenu` creates interactive Bedrock inventory windows with the native PowerNukkitX fake-inventory API. It does not install packet listeners or maintain a second player-session registry: PowerNukkitX already owns fake blocks, container packets, acknowledgements, item-stack requests and viewer synchronization.

## Creating a menu

```java
InventoryMenu menu = InventoryMenu.create(MenuType.CHEST)
    .setName("My Menu");

menu.getInventory().setItem(0, item);

menu.setTransactionHandler((player, transaction) -> {
    player.sendMessage("Clicked slot " + transaction.getSlot());

    // Keep this GUI item in place.
    return transaction.cancel();
});

menu.setCloseHandler(player -> {
    player.sendMessage("Menu closed");
});

menu.send(player);
```

With no listener, transactions continue normally. Use the read-only helper when a menu is only used as a clickable GUI:

```java
menu.setTransactionHandler(InventoryMenu.readOnly(transaction -> {
    if (transaction.getSlot() == 0) {
        transaction.getPlayer().sendMessage("Selected");
    }
}));
```

`ALLOW` never uncancels an action already cancelled by another PowerNukkitX listener. `CANCEL` cancels the underlying `ItemStackRequestActionEvent`, so the server remains authoritative and PNX performs its normal inventory resynchronization.

## Menu types

The fixed types map directly to `FakeInventoryType`: `CHEST`, `DOUBLE_CHEST`, `ENDER_CHEST`, `FURNACE`, `BREWING_STAND`, `DISPENSER`, `DROPPER`, `HOPPER`, `SHULKER_BOX` and `WORKBENCH`.

Entity-backed menus can have a custom size:

```java
InventoryMenu menu = InventoryMenu.create(MenuType.entity(18));
```

## Reusable menu classes

`InventoryMenu` is intentionally extensible. A subclass can prepare its contents in the constructor and override the protected transaction and close hooks:

```java
public final class RewardsMenu extends InventoryMenu {
    public RewardsMenu() {
        super(MenuType.CHEST, "Rewards");
        this.getInventory().setItem(13, rewardItem());
    }

    @Override
    protected MenuTransactionDecision onTransaction(MenuTransaction transaction) {
        if (transaction.getSlot() == 13) {
            giveReward(transaction.getPlayer());
        }

        return transaction.cancel();
    }
}
```

Open it with `new RewardsMenu().send(player)`, or keep a shared instance when all viewers should see the same live contents.

## Lifecycle and viewers

No registration call is required. `send(player)` uses `Player.addWindow(...)`; if another non-permanent inventory is open, it closes that window and waits for the native PNX acknowledgement before opening the menu. `close(player)` closes one viewer and `closeAll()` closes every viewer. The close listener is invoked by `FakeInventory.onClose`, including player-initiated closes and programmatic menu replacement.
