package com.alchemist;

import java.util.ArrayList;

public class TwitterSubscription {
	public TwitterSubscription(String hashtag, ArrayList<Long> targetChannels) {
		this.hashtag = hashtag;
		this.targetChannels = targetChannels;
	}
	
	public String getHashtag() {
		return hashtag;
	}
	
	public ArrayList<Long> getTargetChannels() {
		return targetChannels;
	}
	
	private String hashtag;
	private ArrayList<Long> targetChannels;
}
