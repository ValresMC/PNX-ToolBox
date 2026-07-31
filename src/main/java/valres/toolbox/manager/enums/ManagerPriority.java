package valres.toolbox.manager.enums;

import java.util.Comparator;
import valres.toolbox.manager.Manager;

public enum ManagerPriority {
	CRITICAL(0), HIGH(1), MEDIUM(2), LOW(3), COMMANDS(4), FINALIZATION(5);

	private final int priority;

	ManagerPriority(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return this.priority;
	}

	public static Comparator<Manager> comparator() {
		return Comparator.comparingInt((Manager manager) -> manager.getPriority().getPriority()).thenComparing(Manager::getName, String.CASE_INSENSITIVE_ORDER);
	}
}
