import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import org.javacord.api.entity.permission.PermissionType;

public class NowPlayingCommand extends Command{

    /**
     * Constructs the command.
     *
     * @param name        the command name
     * @param usage       the command usage
     * @param description the command description
     * @param permissions the command permissions; can be null if anyone can use the command
     */
    public NowPlayingCommand(String name, String usage, String description, PermissionType[] permissions) {
        super(name, usage, description, permissions);
    }

    @Override
    void onCommand() {
        if (((PlayCommand) Main.COMMANDS[Main.COMMANDS.length - 1]).servers.get(event.getServer().get().getId()).player.getPlayingTrack() == null) {
            event.getMessage().reply("ERROR: NO TRACK PLAYING, ERROR: STUPIDITY DETECTED -> " + event.getMessageAuthor().getDiscriminatedName());
            return;
        }

        final AudioTrackInfo info = ((PlayCommand) Main.COMMANDS[Main.COMMANDS.length - 1]).servers.get(event.getServer().get().getId()).player.getPlayingTrack().getInfo();

        event.getMessage().reply("You can't remember that? Failure. Now Cooking: " + info.title + " by " + info.author);
    }
}