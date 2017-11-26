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
import java.awt.List;
import java.util.*;
import java.util.stream.Collectors;

public class cmdSkip implements Command{

    private static Guild guild;
    private static final AudioPlayerManager MANAGER = new DefaultAudioPlayerManager();
    private static final Map<Guild, Map.Entry<AudioPlayer, trackManager>> PLAYERS = new HashMap<>();


    public cmdSkip(){
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


    private boolean isIdle(Guild g){

        return !hasPlayer(g) || getPlayer(g).getPlayingTrack() == null;

    }



    private void forceSkipTrack(Guild guild) {
        getPlayer(guild).stopTrack();
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

        if (isIdle(guild)){
            return;
        }else{
            forceSkipTrack(guild);
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
