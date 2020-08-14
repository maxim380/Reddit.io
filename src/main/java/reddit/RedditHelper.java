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

    public Submission getRandomTopPostFromSub(String subreddit, String[] args) {
        TimePeriod timePeriod = TimePeriod.ALL;
        int amountOfPosts = 50;
        for (String arg : args) {
            if (arg.toLowerCase().contains("time=")) {
                timePeriod = handleTimePeriodArgument(arg);
            }
            if(arg.toLowerCase().contains("posts=")) {
                amountOfPosts = handleAmountOfPostsArgument(arg);
            }
        }

        DefaultPaginator.Builder<Submission, SubredditSort> paginatorBuilder = this.client.subreddit(subreddit).posts()
                .limit(amountOfPosts) // 50 posts per page
                .sorting(SubredditSort.TOP) // top posts
                .timePeriod(timePeriod); // of all time
        DefaultPaginator<Submission> paginator = paginatorBuilder.build();

        Listing<Submission> firstPage = paginator.next();
        int index = random.nextInt(firstPage.size());
        return firstPage.get(index);
    }

    public Submission getRandomHotPostFromSub(String subreddit, String args[]) {
        int amountOfPosts = 50;
        for (String arg : args) {
            if(arg.toLowerCase().contains("posts=")) {
                amountOfPosts = handleAmountOfPostsArgument(arg);
            }
        }

        DefaultPaginator.Builder<Submission, SubredditSort> paginatorBuilder = this.client.subreddit(subreddit).posts()
                .limit(amountOfPosts)
                .sorting(SubredditSort.HOT);
        DefaultPaginator<Submission> paginator = paginatorBuilder.build();

        Listing<Submission> firstPage = paginator.next();
        int index = random.nextInt(firstPage.size());
        return firstPage.get(index);
    }

    private TimePeriod handleTimePeriodArgument(String arg) {
        arg = arg.toLowerCase().replace("time=", "");
        switch (arg) {
            case "hour":
                return TimePeriod.HOUR;
            case "day":
                return TimePeriod.DAY;
            case "week":
                return TimePeriod.WEEK;
            case "month":
                return TimePeriod.MONTH;
            case "year":
                return TimePeriod.YEAR;
            default:
                return TimePeriod.ALL;
        }
    }


    private int handleAmountOfPostsArgument(String arg) {
        arg = arg.toLowerCase().replace("posts=", "");
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 50;
        }
    }
}
