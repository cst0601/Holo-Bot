package com.alchemist;

/**
 * LiveStream
 * @author greg8
 * Stores the information of a live steam
 */
public class LiveStream extends Content {
	// deprecated
	public LiveStream(String videoId, String title, String channelTitle) {
		this.videoId = videoId;
		this.title = title;
		this.channelTitle = channelTitle;
	}
	
	public LiveStream(String memberName, String videoId, String title,
					  String channelTitle) {
		this.memberName = memberName;
		this.videoId = videoId;
		this.title = title;
		this.channelTitle = channelTitle;
	}
	
	public String toString() {
		return getYtUrl(videoId);
	}
	
	public String toMarkdownLink() {
		return String.format("[%s](%s)", title, getYtUrl(videoId));
	}
	
	public String getMemberName() { return memberName; }
	public String getVideoId() { return videoId; }
	public String getTitle() { return title; }
	public String getChannelName () { return channelTitle; }
	
	private String memberName;
	private String videoId;
	private String title;
	private String channelTitle;
}
