package util;

import net.dv8tion.jda.core.entities.Game;

import java.util.Random;

public class STATIC {

    public static final String VERSION = "0.4";
    public static final String PREFIX = "!";

    public static Random rnd = new Random();

    public static final String[] PERMS = {"Owner", "Moderator", "Member", "Bots"};

    public static final String[] FULL_PERMS = {"Owner", "Moderator"};

    public static Game GAME = Game.of("with tits" + " | -help | v." + VERSION);



}
