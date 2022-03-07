import org.javacord.api.entity.permission.PermissionType;

public final class SkipCommand extends Command {
    /**
     * Constructs the command.
     *
     * @param name        the command name
     * @param usage       the command usage
     * @param description the command description
     * @param permissions the command permissions; can be null if anyone can use the command
     */
    public SkipCommand(String name, String usage, String description, PermissionType[] permissions) {
        super(name, usage, description, permissions);
    }

    /**
     * Called when a command is sent with the sufficient permissions.
     */
    @Override
    void onCommand() {
        if(!((PlayCommand) Main.COMMANDS[Main.COMMANDS.length - 1]).servers.get(event.getServer().get().getId()).vc.getIdAsString().equals(event.getMessageAuthor().getConnectedVoiceChannel().get().getIdAsString())){
            event.getMessage().reply("ARE YOU EVEN IN THE SAME VC? NO, SO GET OUT??");
        }else{
            if ( ((PlayCommand) Main.COMMANDS[Main.COMMANDS.length - 1]).servers.get(event.getServer().get().getId()).player.getPlayingTrack() == null) {
                event.getMessage().reply("No track playing! (USE. SOME. BRAINCELLS)");
                return;
            }

            ((PlayCommand) Main.COMMANDS[Main.COMMANDS.length - 1]).servers.get(event.getServer().get().getId()).trackScheduler.nextTrack();
            event.getMessage().reply("Skipped music (SO PICKY OH MY GOD)");

        }

    }
}