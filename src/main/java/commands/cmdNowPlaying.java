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
import java.util.stream.Collectors;

public class cmdNowPlaying implements Command {

    private static final int PLAYLIST_LIMIT = 1000;
    private static Guild guild;
    private static final AudioPlayerManager MANAGER = new DefaultAudioPlayerManager();
    private static final Map<Guild, Map.Entry<AudioPlayer, trackManager>> PLAYERS = new HashMap<>();


    public cmdNowPlaying(){
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

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return null;
    }

}
