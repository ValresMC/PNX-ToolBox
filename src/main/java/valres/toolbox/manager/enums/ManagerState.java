package valres.toolbox.manager.enums;

public enum ManagerState {
	UNLOADED(false), LOADED(false), ENABLED(true), DISABLED(false);

	private final boolean ready;

	ManagerState(boolean ready) {
		this.ready = ready;
	}

	public boolean isReady() {
		return this.ready;
	}
}
