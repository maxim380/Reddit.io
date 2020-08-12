package reddit;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.*;
import net.dean.jraw.pagination.DefaultPaginator;

import java.util.Random;

public class RedditHelper {

    private RedditClient client;

    private Random random = new Random();

    public RedditHelper() {
        this.client = RedditAuthenticator.getInstance().authenticate();
    }

    public Submission getRandomTopPostFromSub(String subreddit) {
        DefaultPaginator.Builder<Submission, SubredditSort> paginatorBuilder = this.client.subreddit(subreddit).posts()
                .limit(50) // 50 posts per page
                .sorting(SubredditSort.TOP) // top posts
                .timePeriod(TimePeriod.ALL); // of all time
        DefaultPaginator<Submission> paginator = paginatorBuilder.build();

        Listing<Submission> firstPage = paginator.next();
        int index = random.nextInt(firstPage.size());
        return firstPage.get(index);
    }

    public Submission getRandomHotPostFromSub(String subreddit) {
        DefaultPaginator.Builder<Submission, SubredditSort> paginatorBuilder = this.client.subreddit(subreddit).posts()
                .limit(50)
                .sorting(SubredditSort.HOT);
        DefaultPaginator<Submission> paginator = paginatorBuilder.build();

        Listing<Submission> firstPage = paginator.next();
        int index = random.nextInt(firstPage.size());
        return firstPage.get(index);
    }
}
