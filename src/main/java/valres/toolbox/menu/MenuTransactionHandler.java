package valres.toolbox.menu;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.Player;
import valres.toolbox.menu.transaction.MenuTransaction;
import valres.toolbox.menu.transaction.MenuTransactionDecision;

@FunctionalInterface
public interface MenuTransactionHandler {
	@NonNull MenuTransactionDecision onTransaction(@NonNull Player player, @NonNull MenuTransaction transaction);
}
