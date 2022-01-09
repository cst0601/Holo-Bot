package com.alchemist;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Workhorse of searching new tweets and send messages
 * @author chikuma
 *
 */
public class TwitterBroadcastRunner extends Thread {
	public TwitterBroadcastRunner(
			JDA jda, Twitter twitter,
			ArrayList<TwitterSubscription> subscriptions) {
		Thread.currentThread().setName("TwitterBroadcastRunner");
		logger = LoggerFactory.getLogger(TwitterBroadcastRunner.class);
		this.jda = jda;
		this.twitter = twitter;
		this.subscriptions = subscriptions;
		
		for (TwitterSubscription sub: subscriptions) {
			caches.put(sub.getSearchQuery(), new TweetCache(sub.getSearchQuery()));
			logger.info("Create cache for: " + sub.getSearchQuery());
			// update cache for init to avoid sending old message
			search(sub.getSearchQuery());
		}
	}
	
	public void run() {
		while (true) {
			String message;		// stop if received stop message
			if ((message = messageBox.poll()) != null) {
				if (message.equals("stop")) {
					logger.info("Terminating twitter broascater runner...");
					break;
				}
			}
			else {					// if no message to handle
				try {
					sleep(30000);	// 30 sec
				} catch (InterruptedException e) {
					logger.info("Runner sleep interrupted");
				}
				sendTwitterUpdate();
			}
		}
		
		logger.info("TwitterBroadcastRunner exit run.");
	}
	
	public void sendTwitterUpdate() {
		// send messages
		for (TwitterSubscription subscription: subscriptions) {
			
			// search and construct message to send
			Queue<String> searchResult = search(subscription.getSearchQuery());
			
			for (Long channelId: subscription.getTargetChannels()) {
				MessageChannel channel = jda.getTextChannelById(channelId);
				try {
					for (String newTweet: searchResult) {
						channel.sendMessage(newTweet).queue();
					}
				} catch (InsufficientPermissionException e) {
					logger.warn("Lacks permission to send message to "
							  + "target text channel: " + channel.getId());
				}
			}
		}
	}
	
	/**
	 * Thread synchronization
	 * @param message
	 * @throws InterruptedException
	 */
	public void sendMessage(String message) throws InterruptedException {
		messageBox.put(message);
	}
	
	public Queue<String> search(String search) {
		Query query = new Query(search);
		query.setResultType(Query.RECENT);
		
	    QueryResult result;
	    Queue<String> newTweets = null;
	    try {
			result = twitter.search(query);
			newTweets = caches.get(search).updateTweets(result.getTweets(), result.getMaxId());

			if (newTweets.size() != 0)
				logger.info(newTweets.size() + " new tweets from " + search);
			
		} catch (TwitterException e) {				// connection error, rate limit exceeded...
			newTweets = new LinkedList<String>();	// no new tweets if exception occurred
			
			logger.warn("Failed searching");
			if (e.exceededRateLimitation())
				logger.warn("Exceeded rate limitation");
			else
				logger.warn("Failed with reason other that rate limitation\n" + e.getMessage());
		}
	    return newTweets;
	}
	
	private JDA jda;
	private Twitter twitter;
	private Logger logger;
	private ArrayList<TwitterSubscription> subscriptions;
	private Dictionary<String, TweetCache> caches = new Hashtable<String, TweetCache>();
	private BlockingQueue<String> messageBox = new LinkedBlockingQueue<String>();
}
