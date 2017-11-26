package AudioCore;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;


import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class trackManager extends AudioEventAdapter {

    private final AudioPlayer PLAYER;
    private final Queue<AudioInfo> queue;

    public trackManager(AudioPlayer player) {

        this.PLAYER = player;
        this.queue = new LinkedBlockingQueue<>();

    }

    public void queue(AudioTrack track, Member author){

        AudioInfo info = new AudioInfo(track, author);
     queue.add(info);

     if (PLAYER.getPlayingTrack() == null){

         PLAYER.playTrack(track);
     }

    }

    public Set<AudioInfo> getQueue(){

        return new LinkedHashSet<>(queue);

    }

    public AudioInfo getInfo(AudioTrack track){

        return queue.stream()
                .filter(info -> info.getTRACK().equals(track))
                .findFirst().orElse(null);
    }

    public void clearQueue(){
        queue.clear();
    }

    public void shuffleQueue(){

        List<AudioInfo> cQueue = new ArrayList<>(getQueue());
        AudioInfo current = cQueue.get(0);
        cQueue.remove(0);
        Collections.shuffle(cQueue);
        cQueue.add(0, current);
        clearQueue();
        queue.addAll(cQueue);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {

        AudioInfo info = queue.element();
        VoiceChannel vChan = info.getAUTHOR().getVoiceState().getChannel();

        if (vChan == null){
            player.stopTrack();
        }else {
            info.getAUTHOR().getGuild().getAudioManager().openAudioConnection(vChan);
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {

        Guild g = queue.poll().getAUTHOR().getGuild();

        if (queue.isEmpty()){
            g.getAudioManager().closeAudioConnection();
        }else {
            player.playTrack(queue.element().getTRACK());
        }

    }

}
