package listeners;

import net.dv8tion.jda.client.entities.Application;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.QUOTES;
import util.STATIC;

import java.util.Timer;
import java.util.TimerTask;

public class readyListener extends ListenerAdapter {

    Timer timer = new Timer();


    public void onReady(ReadyEvent event) {

        String out = "\nThis bot is running on following servers: \n";

        for (Guild g:        event.getJDA().getGuilds()) {
            out += g.getName() + " (" + g.getId() + ") \n";
        }

        System.out.println(out);

        for (Guild g: event.getJDA().getGuilds()) {
            g.getTextChannels().get(0).sendMessage(
                    "I'm back bitches! :sunglasses:"
            ).queue();
        }}



    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        User user = event.getMember().getUser();

        for (Guild g: event.getJDA().getGuilds()) {
            g.getTextChannels().get(0).sendMessage(
                     user.getAsMention() + ", just Joined!"
            ).queue();
        }

    }


}
