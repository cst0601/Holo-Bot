package com.alchemist;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import twitter4j.Status;

public class TweetCache {
	public TweetCache(String hashtag) {
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
				cache.add(url);
				differentTweets.add(url);
				
				if (cache.size() > 15) {	// cache maximum size 15
					cache.poll();
				}
			}
		}
		
		return differentTweets;
	}
	
	private String hashtag;
	private Queue<String> differentTweets;
	private Queue<String> cache = new LinkedList<String>();
}
