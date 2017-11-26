package commands;

import AudioCore.AudioInfo;
import AudioCore.PlayerSendHandler;
import AudioCore.trackManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Music implements Command {

    private static final int PLAYLIST_LIMIT = 1000;
    private static Guild guild;
    private static final AudioPlayerManager MANAGER = new DefaultAudioPlayerManager();
    private static final Map<Guild, Map.Entry<AudioPlayer, trackManager>> PLAYERS = new HashMap<>();


    public Music(){
        AudioSourceManagers.registerRemoteSources(MANAGER);
    }

    private AudioPlayer createPlayer(Guild g){
        AudioPlayer p = MANAGER.createPlayer();
        trackManager m = new trackManager(p);
        p.addListener(m);

        guild.getAudioManager().setSendingHandler(new PlayerSendHandler(p));

        PLAYERS.put(g, new AbstractMap.SimpleEntry<>(p, m));

        return p;
    }

    private boolean hasPlayer(Guild g){

        return PLAYERS.containsKey(g);

    }

    private AudioPlayer getPlayer(Guild g){

        if (hasPlayer(g)){

            return PLAYERS.get(g).getKey();

        }else {
            return createPlayer(g);
        }
    }

    private trackManager getManager(Guild g){

        return PLAYERS.get(g).getValue();
    }

    private boolean isIdle(Guild g){

        return !hasPlayer(g) || getPlayer(g).getPlayingTrack() == null;

    }

    private void loadTrack(String identifier, Member author, Message msg){

        Guild guild = author.getGuild();
        getPlayer(guild);
        MANAGER.setFrameBufferDuration(5000);
        MANAGER.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                getManager(guild).queue(track, author);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (int i = 0; i < (playlist.getTracks().size() > PLAYLIST_LIMIT ? PLAYLIST_LIMIT : playlist.getTracks().size()); i++){
                    getManager(guild).queue(playlist.getTracks().get(i), author);
                }
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });


    }

    private void skip(Guild g){
        getPlayer(g).stopTrack();
    }

    private String getTimeStamp(long milis){

        long seconds = milis / 1000;
        long hours = Math.floorDiv(seconds, 3600);
        seconds = seconds - (hours * 3600);
        long mins = Math.floorDiv(seconds, 60);
        seconds = seconds - (mins * 60);
        return (hours == 0 ? "" : hours + "") + String.format("%02d", mins) + ":" + String.format("%02d", seconds);
    }

    private String buildQueueMessage(AudioInfo info){

        AudioTrackInfo trackInfo = info.getTRACK().getInfo();
        String title = trackInfo.title;
        long length = trackInfo.length;
        return "'[ " + getTimeStamp(length) + " ]' " + title + "\n";

    }

    private void sendErrorMsg(MessageReceivedEvent event, String content){

        event.getChannel().sendMessage(
                new EmbedBuilder()
                .setColor(Color.red)
                .setDescription(content)
                .build()
        ).queue();

    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {

        guild = event.getGuild();

        if (args.length < 1){
            sendErrorMsg(event, help());
            return;
        }

        switch (args[0].toLowerCase()){

            case "play":
            case "p":

                if (args.length < 2){
                    sendErrorMsg(event, "please enter a valid source!");
                    return;
                }

                String input = Arrays.stream(args).skip(1).map(s -> " " + s).collect(Collectors.joining()).substring(1);

                if (!(input.startsWith("http://") || input.startsWith("https://"))){
                    input = "ytsearch: " + input;
                }

                loadTrack(input, event.getMember(), event.getMessage());

                        break;


            case "skip":
            case "s":

                if (isIdle(guild)) return;
                for (int i = (args.length > 1 ? Integer.parseInt(args[1]) : 1); i == 1; i--){
                    skip(guild);
                }
                break;

            case "stop":

                if (isIdle(guild)) return;

                getManager(guild).clearQueue();
                skip(guild);
                guild.getAudioManager().closeAudioConnection();

                break;

            case "shuffle":

                if (isIdle(guild)) return;
                getManager(guild).shuffleQueue();

                break;

            case "now":
            case "info":
            case "np":
            case "nowplaying":

                if (isIdle(guild)) return;

                AudioTrack track = getPlayer(guild).getPlayingTrack();
                AudioTrackInfo info = track.getInfo();

                event.getTextChannel().sendMessage(
                        new EmbedBuilder()
                        .setDescription("**CURRENT TRACK INFO:**")
                        .addField("Title", info.title, false)
                        .addField("Duration", "'[ " + getTimeStamp(track.getPosition()) + "/ " + getTimeStamp(track.getDuration()) + " ]'", false)
                        .addField("Author", info.author, false)
                        .build()
                ).queue();

                break;

            case "queue":

                if(isIdle(guild)) return;
                int sideNumb = args.length > 1 ? Integer.parseInt(args[1]) : 1;
                List<String> tracks = new ArrayList<>();
                List<String> trackSublist;

                getManager(guild).getQueue().forEach(AudioInfo -> tracks.add(buildQueueMessage(AudioInfo)));

                if (tracks.size() > 20){
                    trackSublist = tracks.subList((sideNumb-1)*20, (sideNumb-1)*20+20);
                }else {
                    trackSublist = tracks;
                }
                String out = trackSublist.stream().collect(Collectors.joining("\n"));
                int sideNumbAll = tracks.size() >= 20 ? tracks.size() / 20 : 1;

                event.getTextChannel().sendMessage(
                        new EmbedBuilder()
                        .setDescription("**CURRENT QUEUE:**\n\n" +
                        "*[ " + getManager(guild).getQueue().stream() + " Tracks | Side " + sideNumb + " / " + sideNumbAll + "]*" +
                        out)
                        .build()
                ).queue();

        }


    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return null;
    }
}
