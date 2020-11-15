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
	
	public Queue<String> updateTweets(List<Status> statusList, long maxId) {
		Queue<String> differentTweets = new LinkedList<String>();
		
		for (int i = statusList.size() - 1; i >= 0; --i) {	// check status from old to new
			if (statusList.get(i).getId() > this.maxId) {
				String url = 
						"https://twitter.com/" + statusList.get(i).getUser().getScreenName() +
						"/status/" + statusList.get(i).getId();
				differentTweets.add(url);
			}
		}
		
		this.maxId = maxId;
		
		return differentTweets;
	}
	
	private String searchQuery;
	private long maxId = 0;
}
