package reddit;

import bot.MessageProcessor;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import util.PropertiesHelper;

public class RedditAuthenticator {

    //region Singleton code
    private static RedditAuthenticator instance;

    public static RedditAuthenticator getInstance() {
        if(instance == null) {
            instance = new RedditAuthenticator();
        }
        return instance;
    }
    //endregion

    public RedditClient authenticate() {
        UserAgent userAgent = new UserAgent("bot", "com.maximdoolaard.shitpostbot", "v0.1", "maxim380");

        String redditUsername = PropertiesHelper.getInstance().getString("redditUsername");
        String redditPassword = PropertiesHelper.getInstance().getString("redditPassword");
        String redditClientId = PropertiesHelper.getInstance().getString("redditClientId");
        String redditClientSecret = PropertiesHelper.getInstance().getString("redditClientSecret");

        Credentials credentials = Credentials.script(redditUsername, redditPassword,
                redditClientId, redditClientSecret);

        NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);
        RedditClient client = OAuthHelper.automatic(adapter, credentials);
        System.out.println(client.toString());
        return client;
    }
}
