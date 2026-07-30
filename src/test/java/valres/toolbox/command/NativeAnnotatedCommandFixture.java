package valres.toolbox.command;

import org.powernukkitx.plugin.annotation.CommandDefinition;

/**
 * Compile-time fixture ensuring PNX accepts toolbox commands in its native
 * annotation processor without an explicit constructor.
 */
@CommandDefinition(
    name = "nativefixture",
    description = "Native command definition fixture",
    aliases = {"nf"},
    permission = "test.nativefixture"
)
public final class NativeAnnotatedCommandFixture extends Command {
    @Override protected Object onRun(CommandContext context) {
        return null;
    }
}
