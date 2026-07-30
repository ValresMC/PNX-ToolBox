package valres.toolbox.command;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.powernukkitx.Server;
import org.powernukkitx.permission.Permission;
import org.powernukkitx.plugin.Plugin;
import org.powernukkitx.plugin.PluginManager;
import valres.toolbox.command.annotation.SubCommandDefinition;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CommandDefinitionTest {
    @Test
    void generatesRootPermissionWhenNativePermissionIsEmpty() {
        Plugin plugin = mock(Plugin.class);
        Server server = mock(Server.class);
        PluginManager pluginManager = mock(PluginManager.class);
        when(plugin.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(pluginManager);

        try (MockedStatic<Server> serverStatic = mockStatic(Server.class)) {
            serverStatic.when(Server::getInstance).thenReturn(server);

            BareCommand command = new BareCommand(plugin);
            command.setName("ping");
            command.initialize();

            assertEquals("ping.command", command.getCommandPermission());
        }
    }

    @Test
    void nativeNoArgumentCommandResolvesItsOwningPlugin() {
        Plugin plugin = mock(Plugin.class);
        Server server = mock(Server.class);
        PluginManager pluginManager = mock(PluginManager.class);
        when(plugin.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(pluginManager);
        when(pluginManager.getPlugins()).thenReturn(Map.of("TestPlugin", plugin));

        try (MockedStatic<Server> serverStatic = mockStatic(Server.class)) {
            serverStatic.when(Server::getInstance).thenReturn(server);

            NativeAnnotatedCommandFixture command =
                new NativeAnnotatedCommandFixture();
            command.setName("nativefixture");
            command.setPermission("test.nativefixture");
            command.enableCommandTree();
            command.initialize();

            assertSame(plugin, command.getPlugin());
            assertFalse(command.hasCommandTree());
        }
    }

    @Test
    void consumesNativeMetadataAndGeneratesSubCommandPermission() {
        Plugin plugin = mock(Plugin.class);
        Server server = mock(Server.class);
        PluginManager pluginManager = mock(PluginManager.class);
        when(plugin.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(pluginManager);

        try (MockedStatic<Server> serverStatic = mockStatic(Server.class)) {
            serverStatic.when(Server::getInstance).thenReturn(server);

            TestCommand command = new TestCommand(plugin);
            command.setName("coins");
            command.setDescription("Manage player coins");
            command.setAliases(new String[] {"money"});
            command.setPermission("example.coins");

            command.initialize();

            assertEquals("coins", command.getName());
            assertEquals("Manage player coins", command.getDescription());
            assertEquals("example.coins", command.getCommandPermission());
            assertEquals("/coins", command.getUsageLines().getFirst());
            assertEquals(
                "example.coins.give",
                command.getSubCommand("give").getPermission()
            );
            verify(pluginManager).addPermission(argThat(permission ->
                permission.getName().equals("example.coins")
                    && permission.getDefault().equals(Permission.DEFAULT_OP)
            ));
            verify(pluginManager).addPermission(argThat(permission ->
                permission.getName().equals("example.coins.give")
                    && permission.getDefault().equals(Permission.DEFAULT_OP)
            ));
        }
    }

    private static final class TestCommand extends Command {
        private TestCommand(Plugin plugin) {
            super(plugin, "placeholder");
        }

        @Override protected void configure() {
            this.addSubCommand(new GiveSubCommand());
        }

        @Override protected Object onRun(CommandContext context) {
            return null;
        }
    }

    private static final class BareCommand extends Command {
        private BareCommand(Plugin plugin) {
            super(plugin, "placeholder");
        }

        @Override protected Object onRun(CommandContext context) {
            return null;
        }
    }

    @SubCommandDefinition(
        name = "give",
        description = "Give coins to a player"
    )
    private static final class GiveSubCommand extends SubCommand {
        @Override protected Object onRun(CommandContext context) {
            return null;
        }
    }
}
