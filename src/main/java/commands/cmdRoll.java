package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Random;

public class cmdRoll implements Command {
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {

        Random rand = new Random();
        int roll = rand.nextInt(6) + 1;
        event.getChannel().sendMessage("Your roll: " + roll).queue();
        {
            if (roll < 3)
            {
                event.getChannel().sendMessage("The roll for " + event.getAuthor().getAsMention() + " wasn't very good... Must be bad luck!\n").queue();
            }
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
