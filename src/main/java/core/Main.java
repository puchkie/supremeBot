package core;

import commands.*;
import listeners.commandListener;
import listeners.readyListener;
import listeners.voiceListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import util.SECRETS;
import util.STATIC;

import javax.security.auth.login.LoginException;

public class Main {

    public static JDABuilder builder;

    public static void main(String[] Args){

        builder = new JDABuilder(AccountType.BOT);

        builder.setToken(SECRETS.TOKEN);
        builder.setAutoReconnect(true);

        builder.setStatus(OnlineStatus.ONLINE);//Bot status


        builder.setGame(STATIC.GAME);


        addListeners();
        addCommands();

        try {
            JDA jda = builder.buildBlocking();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RateLimitedException e) {
            e.printStackTrace();
        }

    }

    public static void addCommands(){

        commandHandler.commands.put("ping", new cmdPing());
        commandHandler.commands.put("say", new cmdSay());
        commandHandler.commands.put("clear", new cmdClear());
        commandHandler.commands.put("play", new cmdPlay());
        commandHandler.commands.put("p", new cmdPlay());
        commandHandler.commands.put("skip", new cmdSkip());
        commandHandler.commands.put("s", new cmdSkip());
        commandHandler.commands.put("np", new cmdNowPlaying());
        commandHandler.commands.put("now", new cmdNowPlaying());
        commandHandler.commands.put("nowplaying", new cmdNowPlaying());
        commandHandler.commands.put("current", new cmdNowPlaying());

    }

    public static void addListeners(){

        builder.addEventListener(new readyListener());
        builder.addEventListener(new voiceListener());
        builder.addEventListener(new commandListener());

    }

}
