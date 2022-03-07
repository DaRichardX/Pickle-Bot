import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class Main {

    static String token;

    public static final String PREFIX = "!";

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static final Command[] COMMANDS = {
            new HelpCommand("help", null, "The help command.", null),
            new SkipCommand("skip", null, "skip music", null),
            new QueueCommand("queue", null, "show the current music queue", null),
            new ClearCommand("clear", null, "clear the queue", null),
            new NowPlayingCommand("nowplaying", null, "show the current music playing", null),
            new VolumeCommand("volume", "volume", "check what is volume or set volume if add extra arguments", null),
            null //reserved for play command
    };

    public static DiscordApi discordApi;

    public static void main(String[] args) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("token.txt"));
            token = bufferedReader.readLine();
        } catch(IOException ioException){
            ioException.printStackTrace();
        }

        try{
            FileHandler fh = new FileHandler("test.log");
            SimpleFormatter formatter = new SimpleFormatter();
            LOGGER.addHandler(fh);
            fh.setFormatter(formatter);
            log(Level.INFO, "Starting the program");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        DiscordApi api = new DiscordApiBuilder()
                .setToken(token)
                .setAllIntents()
                .login()
                .join();
        discordApi = api;
        COMMANDS[COMMANDS.length - 1] = new PlayCommand("play", "<input content>", "search youtube.com", null);

        api.addMessageCreateListener(event -> {
            if (event.getMessageAuthor().isYourself()) {
                return;
            }

            if (!event.isServerMessage()) {
                log(Level.INFO, String.format("%s sent a message to the bot (message: %s); sending error to them",
                        event.getMessageAuthor().getDiscriminatedName(), event.getMessageContent()));
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("REEEEEEEEEEEEEEE")
                        .setDescription("Don't DM ME! Im busy >:(")
                        .setColor(Color.RED)
                );
                return;
            }

            if (!event.getMessageContent().startsWith(PREFIX)) {
                return;
            }

            log(Level.INFO, String.format("A possible command was called (%s) by %s/%s; verifying command...",
                    event.getMessageContent(), event.getMessageAuthor().getDisplayName(), event.getMessageAuthor().getDiscriminatedName()));
            for (Command command : COMMANDS) {
                if (event.getMessageContent().startsWith(PREFIX + command.NAME)) {
                    command.checkPermissions(event);
                    return;
                }
            }

            log(Level.INFO, "Not a valid command; sending error");
            event.getMessage().reply("Not a valid command! See `" + PREFIX + "help` for all possible commands.");
        });
        api.updateActivity("Musica");
        log(Level.INFO, "Bot ready");
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, message);
    }
}