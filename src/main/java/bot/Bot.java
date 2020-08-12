package bot;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import util.PropertiesHelper;

public class Bot {

    public Bot() {
        this.run();
    }

    private void run() {
        String token = PropertiesHelper.getInstance().getString("discordClientId");
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
        api.addMessageCreateListener(event -> MessageProcessor.getInstance().processMessage(event));

        System.out.println(api.createBotInvite());
    }
}
