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

    public Submission getRandomPostFromSub(String subreddit, SubredditSort subredditSort, String[] args) {
        TimePeriod timePeriod = TimePeriod.ALL;
        int amountOfPosts = 50;
        for (String arg : args) {
            if (arg.toLowerCase().contains("time=")) {
                timePeriod = handleTimePeriodArgument(arg);
            }
            if (arg.toLowerCase().contains("posts=")) {
                amountOfPosts = handleAmountOfPostsArgument(arg);
            }
        }
        DefaultPaginator<Submission> paginator = buildPaginator(subreddit, subredditSort, amountOfPosts, timePeriod);
        Listing<Submission> firstPage = paginator.next();
        int index = random.nextInt(firstPage.size());
        return firstPage.get(index);
    }

    private DefaultPaginator<Submission> buildPaginator(String subreddit, SubredditSort subredditSort, int amountOfPosts, TimePeriod timePeriod) {

        DefaultPaginator.Builder<Submission, SubredditSort> paginatorBuilder = null;

        switch (subredditSort) {
            case BEST:
                paginatorBuilder = this.client.subreddit(subreddit).posts()
                        .limit(amountOfPosts)
                        .sorting(SubredditSort.BEST)
                        .timePeriod(timePeriod);
                break;
            case NEW:
                paginatorBuilder = this.client.subreddit(subreddit).posts()
                        .limit(amountOfPosts)
                        .sorting(SubredditSort.NEW);
                break;
            case RISING:
                paginatorBuilder = this.client.subreddit(subreddit).posts()
                        .limit(amountOfPosts)
                        .sorting(SubredditSort.RISING);
                break;
            case CONTROVERSIAL:
                paginatorBuilder = this.client.subreddit(subreddit).posts()
                        .limit(amountOfPosts)
                        .sorting(SubredditSort.CONTROVERSIAL)
                        .timePeriod(timePeriod);
                break;
            case HOT:
                paginatorBuilder = this.client.subreddit(subreddit).posts()
                        .limit(amountOfPosts)
                        .sorting(SubredditSort.HOT);
                break;
            case TOP:
                paginatorBuilder = this.client.subreddit(subreddit).posts()
                        .limit(amountOfPosts)
                        .sorting(SubredditSort.TOP)
                        .timePeriod(timePeriod);
                break;
        }

        return paginatorBuilder.build();
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
