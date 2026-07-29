package valres.toolbox.rcon;

@FunctionalInterface
public interface RconCommandExecutor {
    String execute(String command) throws Exception;
}
