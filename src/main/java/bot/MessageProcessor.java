package bot;

import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import reddit.RedditHelper;
import util.PropertiesHelper;

import java.awt.*;
import java.net.MalformedURLException;

public class MessageProcessor {

    private boolean developmentMode = false;

    private RedditHelper reddit;

    //region Singleton code
    private static MessageProcessor instance;

    public static MessageProcessor getInstance() {
        if(instance == null) {
            instance = new MessageProcessor();
        }
        return instance;
    }
    //endregion

    private MessageProcessor() {
        this.reddit = new RedditHelper();
    }

    public void processMessage(MessageCreateEvent event) {
        //Check if the message came from a bot
        if(!event.getMessageAuthor().isUser()) {
            return;
        }

        //Only do something if the message starts with r!
        if(!event.getMessageContent().toLowerCase().startsWith("r!")) {
            return;
        }

        if(developmentMode && event.getMessageAuthor().getId() != 229879981830963200L) {
            sendMessage("Bot is in development mode, will be back later :)", event.getChannel());
            return;
        }

        try {
            processCommand(event);
        } catch (Exception e) {
            sendMessage(e.getMessage(), event.getChannel());
        }
    }

    public void processCommand(MessageCreateEvent event) {
        String command = event.getMessageContent().split(" ")[0].replace("r!","");
        System.out.println(command);
        switch (command) {
            case "help":
                this.printHelp(event.getChannel());
                break;
            case "hot":
                this.getHotPost(event.getMessageContent(), event.getChannel());
                break;
            case "top":
                this.getTopPost(event.getMessageContent(), event.getChannel());
                break;
            case "new":
                this.getNewPost(event.getMessageContent(), event.getChannel());
                break;
            case "controversial":
                this.getControversialPost(event.getMessageContent(), event.getChannel());
                break;
            case "best":
                this.getBestPost(event.getMessageContent(), event.getChannel());
                break;
            case "rising":
                this.getRisingPost(event.getMessageContent(), event.getChannel());
                break;
            case "search":
                this.searchPost(event.getMessageContent(), event.getChannel());
                break;
            default:
                sendMessage("Unknown command", event.getChannel());
        }
    }

    //TODO refactor this
    //region get post based on sorting

    private void searchPost(String message, TextChannel channel) {
        String[] splitMessage = message.split(" ");
        String subreddit = splitMessage[1];
        StringBuilder searchTermBuilder = new StringBuilder();
        for(int i = 2; i < splitMessage.length; i++) {
            searchTermBuilder.append(splitMessage[i]);
        }
        Submission post = this.reddit.searchPostOnSub(subreddit, searchTermBuilder.toString());
        this.sendRedditPostToChannel(post, channel);
    }

    private void getTopPost(String message, TextChannel channel) {
        String subreddit = message.split(" ")[1];
        String[] args = message.split("--");

        Submission post = this.reddit.getRandomPostFromSub(subreddit, SubredditSort.TOP,args);
        this.sendRedditPostToChannel(post, channel);
    }

    private void getHotPost(String message, TextChannel channel) {
        String subreddit = message.split(" ")[1];
        String[] args = message.split("--");

        Submission post = this.reddit.getRandomPostFromSub(subreddit, SubredditSort.HOT, args);
        this.sendRedditPostToChannel(post, channel);
    }

    private void getNewPost(String message, TextChannel channel) {
        String subreddit = message.split(" ")[1];
        String[] args = message.split("--");

        Submission post = this.reddit.getRandomPostFromSub(subreddit, SubredditSort.NEW, args);
        this.sendRedditPostToChannel(post, channel);
    }

    private void getControversialPost(String message, TextChannel channel) {
        String subreddit = message.split(" ")[1];
        String[] args = message.split("--");

        Submission post = this.reddit.getRandomPostFromSub(subreddit, SubredditSort.CONTROVERSIAL, args);
        this.sendRedditPostToChannel(post, channel);
    }

    private void getRisingPost(String message, TextChannel channel) {
        String subreddit = message.split(" ")[1];
        String[] args = message.split("--");

        Submission post = this.reddit.getRandomPostFromSub(subreddit, SubredditSort.RISING, args);
        this.sendRedditPostToChannel(post, channel);
    }

    private void getBestPost(String message, TextChannel channel) {
        String subreddit = message.split(" ")[1];
        String[] args = message.split("--");

        Submission post = this.reddit.getRandomPostFromSub(subreddit, SubredditSort.BEST, args);
        this.sendRedditPostToChannel(post, channel);
    }
    //endregion

    private void sendRedditPostToChannel(Submission post, TextChannel channel) {
        if(post.isNsfw() && !channel.asServerTextChannel().get().isNsfw()) {
            sendIsNsfwMessage(channel);
            return;
        }

        if(!post.getUrl().contains("youtu")) {
            EmbedBuilder embed = this.generateEmbed(post);
            new MessageBuilder()
                    .setEmbed(embed)
                    .send(channel);
        } else {
            //TODO place this in it's own method
            new MessageBuilder()
//                    .setEmbed(new EmbedBuilder()
//                        .setTitle(post.getTitle())
//                        .setDescription(":thumbsup: " + post.getScore()))
                    .append(post.getUrl())
                    .send(channel);
        }
    }

    private void sendIsNsfwMessage(TextChannel channel) {
        String message = "This content is NSFW, but this channel is not";
        channel.sendMessage(message);
    }

    private EmbedBuilder generateEmbed(Submission post) {
        //Handle text posts
        if(post.isSelfPost()) {
            EmbedBuilder embed = new EmbedBuilder().setTitle(post.getTitle()).setColor(Color.BLUE);
            if(post.getPreview() != null) {
                embed.setDescription(post.getPreview().toString());
            }
            embed.setDescription(post.getUrl());
            embed.setDescription(":thumbsup:" + post.getScore());
            return embed;
        }

        //Handle image posts
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(post.getTitle()).setImage(post.getUrl());
        if(post.getUrl().contains("v.redd.it")) {
            embed.setColor(Color.RED).setDescription("This is a v.redd.it link which are not supported yet.\nPlease open the link in your browser to view this content");
        } else {
            embed.setColor(Color.GREEN);
        }
        embed.setDescription(":thumbsup: " + post.getScore());

        return embed;
    }

    private void printHelp(TextChannel channel) {
        String helpMessage = PropertiesHelper.getInstance().getString("helpMessage");
        channel.sendMessage(helpMessage);
    }

    private void sendMessage(String content, TextChannel channel) {
        channel.sendMessage(content);
    }
}
