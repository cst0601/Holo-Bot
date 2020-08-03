package com.alchemist;

/**
 * LiveStream
 * @author greg8
 * Stores the information of a live steam
 */
public class LiveStream extends Content {
	public LiveStream(String videoId, String title, String description,
					  String channelTitle, String time) {
		this.videoId = videoId;
		this.title = title;
		this.description = description;
		this.channelTitle = channelTitle;
		this.time = time;
	}
	
	public String toString() {
		return getYtUrl(videoId);
	}
	
	public String getVideoId() { return videoId; }
	public String getTitle() { return title; }
	public String getDescription() { return description; }
	public String getChannelName () { return channelTitle; }
	public String getTime() { return time; }
	
	private String videoId;
	private String title;
	private String description;
	private String channelTitle;
	private String time;
}
