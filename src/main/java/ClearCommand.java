import org.javacord.api.entity.permission.PermissionType;

import java.util.concurrent.LinkedBlockingQueue;

public class ClearCommand extends Command{

    /**
     * Constructs the command.
     *
     * @param name        the command name
     * @param usage       the command usage
     * @param description the command description
     * @param permissions the command permissions; can be null if anyone can use the command
     */
    public ClearCommand(String name, String usage, String description, PermissionType[] permissions) {
        super(name, usage, description, permissions);
    }

    @Override
    void onCommand() {
        ((PlayCommand) Main.COMMANDS[Main.COMMANDS.length - 1]).servers.get(event.getServer().get().getId()).trackScheduler.queue = new LinkedBlockingQueue<>();
        event.getChannel().sendMessage("Queue is now cleared, just like your braincells");
    }
}