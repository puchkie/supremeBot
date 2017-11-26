package core;

import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;

import java.util.Arrays;

public class permsCore {

    public static int check(MessageReceivedEvent event){

        for (  Role r : event.getGuild().getMember(event.getAuthor()).getRoles() ) {

            if(Arrays.stream(STATIC.FULL_PERMS).parallel().anyMatch(r.getName()::contains)){
                return 2;
            }
            else if (Arrays.stream(STATIC.PERMS).parallel().anyMatch(r.getName()::contains)) {
                return 1;
            } else {
                event.getTextChannel().sendMessage(":warning: Sorry, " + event.getAuthor().getAsMention() + ", You dont have permissions to use this command!").queue();
            }
        }
        return 0;

    }

}
