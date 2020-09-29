package com.alchemist;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

import twitter4j.Status;

public class TweetCache {
	public TweetCache(String hashtag) {
		logger = Logger.getLogger(TweetCache.class.getName());
		this.hashtag = hashtag;
	}
	
	public String getHashtag() {
		return hashtag;
	}
	
	public Queue<String> updateTweets(List<Status> statusList) {
		differentTweets = new LinkedList<String>();
		
		for (Status status: statusList) {
			String url = 
				"https://twitter.com/" + status.getUser().getScreenName() +
				"/status/" + status.getId();
			
			if (!cache.contains(url)) {
				logger.info("New tweet!");
				cache.add(url);
				differentTweets.add(url);
				
				if (cache.size() > 15) {	// cache maximum size 15
					cache.poll();
					logger.info("cache size: " + cache.size());
				}
			}
		}
		
		return differentTweets;
	}
	
	private String hashtag;
	private Queue<String> differentTweets;
	private Queue<String> cache = new LinkedList<String>();
	private Logger logger;
}
