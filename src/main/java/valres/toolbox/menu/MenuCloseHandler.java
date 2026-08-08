package valres.toolbox.menu;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.Player;

@FunctionalInterface
public interface MenuCloseHandler {
	void onClose(@NonNull Player player);
}
