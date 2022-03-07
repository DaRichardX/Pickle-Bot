import org.javacord.api.entity.permission.PermissionType;

public final class VolumeCommand extends Command {
    /**
     * Constructs the command.
     *
     * @param name        the command name
     * @param usage       the command usage
     * @param description the command description
     * @param permissions the command permissions; can be null if anyone can use the command
     */
    public VolumeCommand(String name, String usage, String description, PermissionType[] permissions) {
        super(name, usage, description, permissions);
    }

    /**
     * Called when a command is sent with the sufficient permissions.
     */
    @Override
    void onCommand() {
        int vol = ((PlayCommand) Main.COMMANDS[Main.COMMANDS.length - 1]).servers.get(event.getServer().get().getId()).player.getVolume();
        if (event.getMessageContent().split(" ").length > 1) {
            try {
                ((PlayCommand) Main.COMMANDS[Main.COMMANDS.length - 1]).servers.get(event.getServer().get().getId()).player.setVolume(Integer.parseInt(event.getMessageContent().split(" ")[1]));
                event.getMessage().reply("Modified volume from " + vol + " to " + event.getMessageContent().split(" ")[1]);
            } catch (NumberFormatException e) {
                event.getMessage().reply("Bruhh you stupid idiot, " + event.getMessageContent().split(" ")[1] + " isn't even a number, stupid.");

            }
        }else {
            event.getMessage().reply("Volume is " + vol + " , you can't even remember that?");
        }
    }
}