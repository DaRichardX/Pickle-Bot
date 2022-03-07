
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.PermissionType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueCommand extends Command{

    /**
     * Constructs the command.
     *
     * @param name        the command name
     * @param usage       the command usage
     * @param description the command description
     * @param permissions the command permissions; can be null if anyone can use the command
     */
    public QueueCommand(String name, String usage, String description, PermissionType[] permissions) {
        super(name, usage, description, permissions);
    }

    @Override
    void onCommand() {
        final PlayCommand cmd = ((PlayCommand) Main.COMMANDS[Main.COMMANDS.length - 1]);
        final BlockingQueue<AudioTrack> queue = cmd.servers.get(event.getServer().get().getId()).trackScheduler.queue;


        if (queue.isEmpty()) {
            event.getChannel().sendMessage("The queue is currently empty");
            return;
        }

        final int trackCount = Math.min(queue.size(), 20);
        final List<AudioTrack> trackList = new ArrayList<>(queue);
        final StringBuilder b = new StringBuilder("**queue for people who can't remember stuff:**\n");

        for (int i = 0; i <  trackCount; i++) {
            final AudioTrack track = trackList.get(i);
            final AudioTrackInfo info = track.getInfo();

            b.append('#')
                    .append(String.valueOf(i + 1))
                    .append(" `")
                    .append(String.valueOf(info.title))
                    .append(" by ")
                    .append(info.author)
                    .append("` [`")
                    .append(formatTime(track.getDuration()))
                    .append("`]\n");
        }

        if (trackList.size() > trackCount) {
            b.append("And `")
                    .append(String.valueOf(trackList.size() - trackCount))
                    .append("` more...");
        }

        event.getMessage().reply(b.toString());
    }

    private String formatTime(long timeInMillis) {
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}