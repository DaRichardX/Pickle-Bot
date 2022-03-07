import org.javacord.api.entity.permission.PermissionType;

public final class HelpCommand extends Command {
    /**
     * Constructs the command.
     *
     * @param name        the command name
     * @param usage       the command usage
     * @param description the command description
     * @param permissions the command permissions; can be null if anyone can use the command
     */
    public HelpCommand(String name, String usage, String description, PermissionType[] permissions) {
        super(name, usage, description, permissions);
    }

    /**
     * Called when a command is sent with the sufficient permissions.
     */
    @Override
    void onCommand() {
        StringBuilder helpMessage = new StringBuilder();
        for (Command command : Main.COMMANDS) {
            helpMessage.append(String.format("%s (`%s`): %s\n", command.NAME, command.USAGE, command.description));
        }
        event.getMessage().reply(helpMessage.toString());
    }
}