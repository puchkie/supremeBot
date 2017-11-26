package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class cmdSay implements Command {
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {

        List argsList = Arrays.asList(args);
        StringBuilder sb = new StringBuilder();
        argsList.forEach(s -> sb.append(s + " "));
        send(":loudspeaker: " + sb.toString(), event);

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return null;
    }
    private void send(String msg, MessageReceivedEvent event){

        event.getTextChannel().sendMessage(msg).queue();

    }
}
