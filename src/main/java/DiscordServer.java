import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.entity.channel.ServerVoiceChannel;

public class DiscordServer {

    LavaPlayerAudioSource audioSource = null;
    AudioPlayerManager manager = null;
    AudioPlayer player = null;
    ServerVoiceChannel vc = null;
    AudioConnection audioConnection = null;
    TrackScheduler trackScheduler = null;

}
