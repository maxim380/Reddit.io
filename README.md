# Reddit.IO
Reddit.io is a simple Discord bot to post the hot and top posts of a subreddit to a Discord channel.

## Building
For Reddit.io to work you need an additional props.properties file. This file must contain the folowing fields:
```
discordClientId
helpMessage
redditUsername
redditPassword
redditClientId
redditClientSecret
```
After placing this file in the root directory of the project run the following command to build the project:
```bash
mvn assembly:assembly -DdescriptorId=jar-with-dependencies
```
Reddit.io can then be started via the 'RedditDiscordBot-1.0-SNAPSHOT-jar-with-dependencies.jar' file in the target directory using the following command:
```bash
java -jar RedditDiscordBot-1.0-SNAPSHOT-jar-with-dependencies.jar
```
