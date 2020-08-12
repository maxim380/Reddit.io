package bot;

import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
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

    public void processCommand(MessageCreateEvent event) throws MalformedURLException {
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
            default:
                sendMessage("Unknown command", event.getChannel());
        }
    }

    private void getTopPost(String message, TextChannel channel) throws MalformedURLException {
        String subreddit = message.split(" ")[1];

        Submission post = this.reddit.getRandomTopPostFromSub(subreddit);
        this.sendRedditPostToChannel(post, channel);
    }

    private void getHotPost(String message, TextChannel channel) {
        String subreddit = message.split(" ")[1];

        Submission post = this.reddit.getRandomHotPostFromSub(subreddit);
        this.sendRedditPostToChannel(post, channel);
    }

    private void sendRedditPostToChannel(Submission post, TextChannel channel) {
        EmbedBuilder embed = this.generateEmbed(post);

        new MessageBuilder()
                .setEmbed(embed)
                .send(channel);
    }

    private EmbedBuilder generateEmbed(Submission post) {
        //Handle text posts
        if(post.isSelfPost()) {
            EmbedBuilder embed = new EmbedBuilder().setTitle(post.getTitle()).setColor(Color.BLUE);
            if(post.getPreview() != null) {
                embed.setDescription(post.getPreview().toString());
            }
            embed.setDescription(post.getUrl());
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
