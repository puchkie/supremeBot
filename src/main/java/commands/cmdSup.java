package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.QUOTES;
import util.STATIC;

public class cmdSup implements Command {



    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        event.getChannel().sendMessage(QUOTES.ANSWERS[STATIC.rnd.nextInt(QUOTES.ANSWERS.length)]).queue();
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return null;
    }
}
