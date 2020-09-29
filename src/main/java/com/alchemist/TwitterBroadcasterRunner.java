package com.alchemist;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Workhorse of searching new tweets and send messages
 * @author chikuma
 *
 */
public class TwitterBroadcasterRunner extends Thread {
	public TwitterBroadcasterRunner(
			JDA jda, Twitter twitter,
			ArrayList<TwitterSubscription> subscriptions) {
		logger = Logger.getLogger(TwitterBroadcasterRunner.class.getName());
		this.jda = jda;
		this.twitter = twitter;
		this.subscriptions = subscriptions;
		
		for (TwitterSubscription sub: subscriptions) {
			caches.put(sub.getHashtag(), new TweetCache(sub.getHashtag()));
			// update cache for init to avoid sending old message
			search(sub.getHashtag());
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
			
			try {
				sleep(300000);	// 5 min
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			logger.info("Update twitter info...");
			
			// send messages
			for (TwitterSubscription subscription: subscriptions) {
				
				// search and construct message to send
				String newTweetMessage = "";
				for (String newTweet: search(subscription.getHashtag()))
					newTweetMessage += newTweet + "\n";
				
				for (Long channelId: subscription.getTargetChannels()) {
					MessageChannel channel = jda.getTextChannelById(channelId);
					if (!newTweetMessage.equals(""))
						channel.sendMessage(newTweetMessage).queue();
				}
			}
		}
	}
	
	public void sendMessage(String message) throws InterruptedException {
		messageBox.put(message);
	}
	
	public Queue<String> search(String target) {
		Query query = new Query("#" + target + " -filter:retweets");
	    QueryResult result;
	    Queue<String> newTweets = null;
	    try {
			result = twitter.search(query);
			newTweets = caches.get(target).updateTweets(result.getTweets());
		} catch (TwitterException e) {
			logger.warning("Failed searching");
			e.printStackTrace();
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
