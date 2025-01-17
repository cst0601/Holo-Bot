package com.alchemist;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.v1.Query;
import twitter4j.v1.QueryResult;
import twitter4j.v1.SearchResource;

/**
 * Workhorse of searching new tweets and send messages.
 */
public class TwitterBroadcastRunner extends Thread {
  /**
   * Thread for broadcasting new tweets to Discord channels.
   */
  public TwitterBroadcastRunner(
      JDA jda, Twitter twitter,
      TwitterBroadcastConfig config) {
    Thread.currentThread().setName("TwitterBroadcastRunner");
    logger = LoggerFactory.getLogger(TwitterBroadcastRunner.class);
    this.jda = jda;
    this.twitter = twitter.v1().search();
    this.config = config;

    for (TwitterSubscription sub : config.getTwitterSubscriptions()) {
      caches.put(sub.getSearchQuery(), new TweetCache(sub.getSearchQuery()));
      logger.info("Create cache for: " + sub.getSearchQuery());
      // update cache for init to avoid sending old message
      search(sub.getSearchQuery());
    }
  }

  /** Thread main loop. */
  public void run() {
    while (true) {
      SyncMessage message;    // stop if received stop message
      if ((message = messageBox.poll()) != null) {
        if (message.getMessageHead().equals("stop")) {
          logger.info("Terminating twitter broascater runner...");
          break;
        } else if (message.getMessageHead().equals("add")) {

          config.addBlacklistUser(Long.parseLong(message.getMessageBody()));
          logger.info("Added twitter user " + message.getMessageBody()
              + " to blacklist.");
        } else if (message.getMessageHead().equals("remove")) {
          if (!config.removeBlacklistUser(Long.parseLong(message.getMessageBody()))) {
            logger.warn("Attempted to remove twitter user "
                + message.getMessageBody() + " from blacklist which"
                + " does not exist."
            );
          } else {
            logger.info("Removed twitter user "
                + message.getMessageBody() + " from blacklist.");
          }
        }
      } else {            // if no message to handle
        try {
          sleep(300000);  // 5 min, twitter API free tier limits to 1500 tweet pulls
        } catch (InterruptedException e) {
          logger.info("Runner sleep interrupted");
        }
        sendTwitterUpdate();
      }
    }

    logger.info("TwitterBroadcastRunner exit run.");
  }

  /** Send messasges. */
  public void sendTwitterUpdate() {
    for (TwitterSubscription subscription : config.getTwitterSubscriptions()) {

      // search and construct message to send
      Queue<Tweet> searchResult = search(subscription.getSearchQuery());

      for (Long channelId : subscription.getTargetChannels()) {
        TextChannel channel = jda.getTextChannelById(channelId);
        try {
          for (Tweet newTweet : searchResult) {
            if (config.isInBlacklist(newTweet.getUserId())) {
              if (config.isUncensoredTargetId(channelId)) {
                channel.sendMessage("**TWEET FROM BLACKLIST USER**: " + newTweet.toUrl()).queue();
              }
            } else {
              channel.sendMessage(newTweet.toUrl()).queue();
            }
          }
        } catch (InsufficientPermissionException e) {
          logger.warn("Lacks permission to send message to "
                + "target text channel: " + channel.getId());
        }
      }
    }
  }

  /**
   * Thread synchronization.
   */
  public void sendMessage(SyncMessage message) throws InterruptedException {
    messageBox.put(message);
  }

  /** Request new tweets from Twitter API. */
  public Queue<Tweet> search(String search) {
    Query query = Query.of(search);
    query.resultType(Query.RECENT);

    QueryResult result;
    Queue<Tweet> newTweets = null;
    try {
      result = twitter.search(query);
      newTweets = caches.get(search).updateTweets(result.getTweets(), result.getMaxId());

      if (newTweets.size() != 0) {
        logger.info(newTweets.size() + " new tweets from " + search);
      }

    } catch (TwitterException e) {          // connection error, rate limit exceeded...
      newTweets = new LinkedList<Tweet>();  // no new tweets if exception occurred

      logger.warn("Failed searching");
      if (e.exceededRateLimitation()) {
        logger.warn("Exceeded rate limitation");
      } else {
        logger.warn("Failed with reason other that rate limitation\n" + e.getMessage());
      }
    }
    return newTweets;
  }

  private JDA jda;
  private SearchResource twitter;
  private Logger logger;
  private TwitterBroadcastConfig config;
  private Dictionary<String, TweetCache> caches = new Hashtable<String, TweetCache>();
  private BlockingQueue<SyncMessage> messageBox = new LinkedBlockingQueue<SyncMessage>();
}
