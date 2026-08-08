package valres.toolbox.menu.transaction;

public enum MenuTransactionDecision {
	ALLOW(false), CANCEL(true);

	private final boolean cancelled;

	MenuTransactionDecision(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}
}
