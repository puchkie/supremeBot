package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Random;

public class cmdSup implements Command {

    static Random rnd = new Random();
    static String[] answers = {"Beep Boop Boop Beep.","OK.","Error 404 'Sup' not found.","Double shift today, You?","Make it rain! :money_with_wings::money_with_wings: ",
            "You are boring..","The Bot goes skrrraaa!","I love you. :kissing_heart: "};

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        event.getChannel().sendMessage(answers[rnd.nextInt(8)]).queue();
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return null;
    }
}
