package listeners;

import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class voiceListener extends ListenerAdapter{

    //Listens to changes in voice channels activities and reports them in text channel "voicelog"


    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {

        event.getGuild().getTextChannelsByName("voicelog", true).get(0).sendMessage(
                "Member " + event.getVoiceState().getMember().getUser().getName() + " joined voice channel - " + event.getChannelJoined().getName() + "."
        ).queue();

    }

    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {

        event.getGuild().getTextChannelsByName("voicelog", true).get(0).sendMessage(
                "Member " + event.getVoiceState().getMember().getUser().getName() + " moved to voice channel - " + event.getChannelJoined().getName() + "."
        ).queue();

    }

    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {

        event.getGuild().getTextChannelsByName("voicelog", true).get(0).sendMessage(
                "Member " + event.getVoiceState().getMember().getUser().getName() + " left voice channel - " + event.getChannelLeft().getName() + "."
        ).queue();

    }


}
