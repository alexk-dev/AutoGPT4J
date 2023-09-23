package com.autogpt4j.command;

import com.autogpt4j.config.AppProperties;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Map;

@Component
public class TwitterCommand implements Command {

    private final AppProperties appProperties;

    public TwitterCommand(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public String getName() {
        return "TwitterCommand";
    }

    @Override
    public String getDescription() {
        return "TwitterCommand";
    }

    public String execute(Map<String, Object> params) {
        String tweetText = (String) params.get("tweetText");
        return sendTweet(tweetText);
    }

    private String sendTweet(String tweetText) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(appProperties.getTwitterConsumerKey())
                .setOAuthConsumerSecret(appProperties.getTwitterConsumerSecret())
                .setOAuthAccessToken(appProperties.getTwitterAccessToken())
                .setOAuthAccessTokenSecret(appProperties.getTwitterTokenSecret());

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        try {
            Status status = twitter.updateStatus(tweetText);
            return status.getText();
        } catch (TwitterException e) {
            return "Error sending tweet: " + e.getErrorMessage();
        }
    }
}