package commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.STATIC;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class cmdVote implements Command {

    private static TextChannel channel;

    private static HashMap<Guild, Poll> polls = new HashMap<Guild, Poll>();

    private static final String[] EMOTI = {":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:", ":nine:", ":keycap_ten:"};


    private class Poll implements Serializable{

        private String creator;
        private String heading;
        private List<String> answers;
        private HashMap<String, Integer> votes;


        private Poll(Member creator, String heading, List<String> answers){

            this.creator = creator.getUser().getId();
            this.heading = heading;
            this.answers = answers;
            this.votes = new HashMap<>();
        }

        private Member getCreator(Guild guild){

            return guild.getMember(guild.getJDA().getUserById(creator));
        }

    }

        private static void message(String content){

    EmbedBuilder eb = new EmbedBuilder().setDescription(content);
    channel.sendMessage(eb.build()).queue();

}

    private static void message(String content, Color color){

            EmbedBuilder eb = new EmbedBuilder().setDescription(content).setColor(color);
            channel.sendMessage(eb.build()).queue();
    }

    private EmbedBuilder getParsedPoll(Poll poll, Guild guild){

        StringBuilder ansSTR = new StringBuilder();
        final AtomicInteger count = new AtomicInteger();

        poll.answers.forEach(s -> {
            int votescount = poll.votes.values().stream().filter(i -> i - 1 == count.get()).findFirst().orElse(0);
            ansSTR.append(EMOTI[count.get()] + " - " + s + " - Votes: '" + votescount + "' \n");
            count.addAndGet(1);
        });
        return new EmbedBuilder()
                .setAuthor(poll.getCreator(guild).getEffectiveName() + "'s poll.", null, poll.getCreator(guild).getUser().getAvatarUrl())
                .setDescription(":pencil: " + poll.heading + "\n\n" + ansSTR.toString())
                .setFooter("Enter '" + STATIC.PREFIX + "vote v <number> to vote!", null)
                .setColor(Color.cyan);
    }

    private void createPoll(String[] args, MessageReceivedEvent event) {

        if (polls.containsKey(event.getGuild())){

            message("There is already a vote running in the sever!", Color.red);
            return;
        }
        String argsSTRG = String.join(" ", new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
        List<String> content = Arrays.asList(argsSTRG.split("\\| "));
        String heading = content.get(0);
        List<String> answers = new ArrayList<>(content.subList(1, content.size()));

        Poll poll = new Poll(event.getMember(), heading, answers);
        polls.put(event.getGuild(), poll);

        channel.sendMessage(getParsedPoll(poll, event.getGuild()).build()).queue();
    }

    private void votePoll(String[] args, MessageReceivedEvent event){

        if (!polls.containsKey(event.getGuild())){

            message("There is no poll running!", Color.red);
            return;
        }
        Poll poll = polls.get(event.getGuild());

        int vote;
        try {
            vote = Integer.parseInt(args[1]);
            if (vote > poll.answers.size()){
                throw new Exception();
            }
        }catch (Exception e){
            message("Please enter a valid number to vote for!", Color.red);
            return;
        }

        if (poll.votes.containsKey(event.getAuthor().getId())){

            message("You can only vote **once** each poll!", Color.RED);
            return;

        }
    poll.votes.put(event.getAuthor().getId(), vote);
        polls.replace(event.getGuild(), poll);
        event.getMessage().delete().queue();
    }

    private void voteStats(MessageReceivedEvent event){

        if (!polls.containsKey(event.getGuild())){

            message("There is no vote running!", Color.red);
            return;
        }
        channel.sendMessage(getParsedPoll(polls.get(event.getGuild()), event.getGuild()).build()).queue();

    }

    private void closeVote(MessageReceivedEvent event){

        if (!polls.containsKey(event.getGuild())){

            message("There is no vote running!", Color.red);
            return;
        }
        Guild g = event.getGuild();
        Poll poll = polls.get(g);

        if (!poll.getCreator(g).equals(event.getMember())){
            message("Only the creator of the poll (" + poll.getCreator(g).getAsMention() + ") can close this poll!", Color.red);
            return;
        }

        polls.remove(g);
        channel.sendMessage(getParsedPoll(poll, g).build()).queue();
        message("Poll closed by " + event.getAuthor().getAsMention() + ".", new Color(0xff7000));
    }

    private void savePoll(Guild guild) throws IOException{

        if (!polls.containsKey(guild)){
            return;}
            String saveFile = "SERVER_SETTINGS/" + guild.getId() + "/vote.dat";
            Poll poll = polls.get(guild);

            FileOutputStream fos = new FileOutputStream(saveFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(poll);
            oos.close();
    }

    private static Poll getPoll(Guild guild) throws IOException, ClassNotFoundException {

        if (polls.containsKey(guild)){
            return null;}

            String saveFile = "SERVER_SETTINGS/" + guild.getId() + "/vote.dat";

            FileInputStream fis = new FileInputStream(saveFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Poll out = (Poll) ois.readObject();
            ois.close();
            return out;
        }

    public static void loadPolls(JDA jda){

        jda.getGuilds().forEach(g -> {

            File f = new File("SERVER_SETTINGS/" + g.getId() + "/vote.dat");
            if (f.exists()){
                try {
                    polls.put(g, getPoll(g));
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        });

    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {

        channel = event.getTextChannel();

        if (args.length < 1){

            message(help(), Color.red);
            return;
        }
        switch (args[0]){

            case "create":
                createPoll(args, event);
                break;

            case "v":
                votePoll(args, event);
                break;

            case "stats":
                voteStats(event);
                break;

            case "close":
                closeVote(event);
                break;

        }

        polls.forEach((g, poll) -> {

            File path = new File("SERVER_SETTINGS/" + g.getId() + "/");
        if (!path.exists()){
                path.mkdirs();
            }
            try {
                savePoll(g);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return null;
    }
}
