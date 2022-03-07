import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.entity.permission.PermissionType;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class PlayCommand extends Command{

    final int ARGUMENT_AMOUNT = 1;

    //Youtube API
    YouTube youtube;
    String youtubeAPIKey = "AIzaSyC9oYcFSAEM-gNxzLZPd4UPl5drLPDjO0A"; //Linked to Google Project ("uhill-youtube-project") - Richard Xie
    private final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private final JsonFactory JSON_FACTORY = new GsonFactory();

    public HashMap<Long, DiscordServer> servers = new HashMap<Long, DiscordServer>();

    public PlayCommand(String name, String usage, String description, PermissionType[] permissions) {
        super(name, usage, description, permissions);
        youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, null).setApplicationName("youtube-fetch-video-discord-bot").build();


        //Connect Listener
        Main.discordApi.addServerVoiceChannelMemberJoinListener(event -> {

            if (!event.getUser().isYourself()) {
                return;
            }
            System.out.println("Reconstructed");
            DiscordServer s = servers.get(event.getServer().getId());
            s.vc = event.getUser().getConnectedVoiceChannel(event.getServer()).get();
            s.vc.connect().thenAccept(audioConnection -> {
                s.audioConnection = audioConnection;
                s.audioConnection.setAudioSource(s.audioSource);
            });
        });
    }



    @Override
    void onCommand() {
        if(!event.getMessageAuthor().isUser()) return;

        if(!event.getMessageAuthor().getConnectedVoiceChannel().isPresent()){
            event.getMessage().reply("You must be in an VC to use this command! (USE. YOUR. BRAIN)");
            return;
        }

        String[] parts = event.getMessageContent().split(" ", ARGUMENT_AMOUNT + 1);
        if (notEnoughArguments(parts, ARGUMENT_AMOUNT)) {
            return;
        }

        if(!servers.containsKey(event.getServer().get().getId())) {
            servers.put(event.getServer().get().getId(), constructServerObject());
        }

        servers.put(event.getServer().get().getId(), reconstructVC(servers.get(event.getServer().get().getId())));

        String videoSource = fetchURL(parts[1]);

        if(videoSource.equals("NO_SONG")){
            event.getMessage().reply("Your taste is so bad that no known song is called that name");
            return;
        }else if(videoSource == null){
            return;
        }


        playMusic(videoSource, servers.get(event.getServer().get().getId()));
    }


    private String getURLFromSearchYoutube(String Content, boolean isPlaylist) throws GoogleJsonResponseException, IOException {

        System.out.println("Fetched song with index: " + Content);

        YouTube.Search.List search = youtube.search().list(Arrays.asList("id,snippet".split(",")));

        search.setType(Arrays.asList("video"));
        search.setKey(youtubeAPIKey);
        search.setQ(Content);
        search.setMaxResults(1L);

        SearchListResponse response = search.execute();
        if(response.getItems().size() < 1) return null;
        return response.getItems().get(0).getId().getVideoId();

    }

    private DiscordServer constructServerObject(){
        DiscordServer s = new DiscordServer();
        s.manager = new DefaultAudioPlayerManager();
        s.manager.registerSourceManager(new YoutubeAudioSourceManager());

        s.player = s.manager.createPlayer();
        s.player.setVolume(50);

        s.audioSource = new LavaPlayerAudioSource(Main.discordApi, s.player);

        s.trackScheduler = new TrackScheduler(s.player);
        s.player.addListener(s.trackScheduler);

        return s;
    }

    private String fetchURL(String q){

        System.out.println("Fetched song with index: " + q);

        String url = null;
        if(q.indexOf("watch?v=") != -1){
            if(q.indexOf("&") != -1){
                url = q.substring(q.indexOf("watch?v=") + 8, q.indexOf("&"));
            }else{
                url = q.substring(q.indexOf("watch?v=") + 8);
            }

        }else{
            try{
                url = getURLFromSearchYoutube(q, false);
                if(url == null){
                    return "NO_SONG";
                }
            } catch (GoogleJsonResponseException e) {
                if(e.getDetails().getCode() == 403){
                    event.getMessage().reply("Youtube song request exceeded");
                    return null;
                }
            } catch(IOException e){
                event.getMessage().reply("An Internal Error Occurred");
                e.printStackTrace();
                return null;
            }
        }

        return url;
    }

    private void playMusic(String URL, DiscordServer s){
        s.manager.loadItem(URL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                s.trackScheduler.queue(track);

                event.getChannel().sendMessage(new StringBuilder("Cooking in queue (NOT METH\u2122): `")
                        .append(track.getInfo().title)
                        .append("` by `")
                        .append(track.getInfo().author)
                        .append('`').append(". Position on stove: `#" + s.trackScheduler.queue.size() + "`").toString());

            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    s.player.playTrack(track);
                }
            }

            @Override
            public void noMatches() {
                event.getMessage().reply("BAD URL: " + "Youtube.com/watch?v=" + URL + " Please Add me Lattee#8548 on Discord");
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                event.getMessage().reply("REEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            }
        });


    }

    private DiscordServer reconstructVC(DiscordServer s){
        if(s.vc != null && s.audioConnection != null && event.getMessageAuthor().getConnectedVoiceChannel().get().getIdAsString().equals(s.vc.getIdAsString())){
            //Same VC
            return s;
        }

        s.vc = event.getMessageAuthor().getConnectedVoiceChannel().get();
        s.vc.connect().thenAccept(audioConnection -> {
            s.audioConnection = audioConnection;
            s.audioConnection.setAudioSource(s.audioSource);
        }).exceptionally(e -> {
            event.getMessage().reply("An Internal Error Occurred");
            e.printStackTrace();
            return null;
        });

        return s;

    }




}
