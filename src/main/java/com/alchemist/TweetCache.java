package com.alchemist;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import twitter4j.Status;

public class TweetCache {
	public TweetCache(String searchQuery) {
		this.searchQuery = searchQuery;
	}
	
	public String getSearchQuery() {
		return searchQuery;
	}
	
	public Queue<String> updateTweets(List<Status> statusList) {
		Queue<String> differentTweets = new LinkedList<String>();
		Queue<String> newCache = new LinkedList<String>();
		
		for (int i = statusList.size() - 1; i >= 0; --i) {	// check status from old to new
			String url = 
				"https://twitter.com/" + statusList.get(i).getUser().getScreenName() +
				"/status/" + statusList.get(i).getId();
			
			newCache.add(url);
			
			if (!cache.contains(url)) {
				differentTweets.add(url);
			}
		}
		cache = newCache;
		return differentTweets;
	}
	
	private String searchQuery;
	private Queue<String> cache = new LinkedList<String>();
	// TODO: Use another data type to store tweet urls (Queue is from old way of cache updating)
}
