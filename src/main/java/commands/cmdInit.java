package commands;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.QUOTES;
import util.STATIC;

import java.util.Timer;
import java.util.TimerTask;


public class cmdInit implements Command {

    Timer timer = new Timer();
    int time = 1000 * 60 * 5;

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {


        //event.getMember().getVoiceState().getGuild().

        if (event.getGuild().getVoiceChannelById(0).getMembers().isEmpty()) { // not working

            System.out.println("no one is in the vcs");}else {
            System.out.println("this does not work.");

//            for (Guild g : event.getJDA().getGuilds()) {
//                g.getTextChannels().get(0).sendMessage(
//                        "This place is too quiet.."
//                ).queue();
//            }
//
//        } else {
//
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                for (Guild g : event.getJDA().getGuilds()) {
//                    g.getTextChannels().get(0).sendMessage(
//                            QUOTES.AFK_NOTES[STATIC.rnd.nextInt(QUOTES.AFK_NOTES.length)]
//                    ).queue();
//                }
//            }
//        };
//
//        timer.scheduleAtFixedRate(task, time, time);
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
