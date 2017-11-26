package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class cmdCat implements Command {

    String catURL = "http://theoldreader.com/kittens/500/500";


    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {

        event.getChannel().sendMessage("**:cat: | Here is your random cat:**"
                + " "+ catURL).queue();

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return null;
    }
}
