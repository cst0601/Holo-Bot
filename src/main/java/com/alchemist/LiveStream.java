package com.alchemist;

import java.text.ParseException;
import java.time.ZonedDateTime;

/**
 * LiveStream.
 * Stores the information of a live steam.
 */
public class LiveStream {
  /**
   * Constructor of the data class.
   *
   * @param memberName hololive member name.
   * @param videoId youtube video Id.
   * @param title title of the live stream.
   * @param liveSchedule stream start time.
   * @param channelTitle title of the Youtube channel.
   * @throws ParseException throws exception if liveSchedule failed to be parsed
   *     into time.
   */
  public LiveStream(String memberName, String videoId, String title, ZonedDateTime liveSchedule,
            String channelTitle) throws ParseException {
    this.memberName = memberName;
    this.videoId = videoId;
    this.title = title;
    this.liveSchedule = liveSchedule;
    this.channelTitle = channelTitle;
    this.mentionedStream = false;
  }

  /** 
   * Constructor that specifies if the stream is from another channel mentioning
   * the member. 
   */
  public LiveStream(
      String memberName, 
      String videoId, 
      String title, 
      ZonedDateTime liveSchedule, 
      String channelTitle, 
      boolean isMentionedStream) throws ParseException {
    this.memberName = memberName;
    this.videoId = videoId;
    this.title = title;
    this.liveSchedule = liveSchedule;
    this.channelTitle = channelTitle;
    this.mentionedStream = isMentionedStream;
  }

  public String toString() {
    return getYtUrl(videoId);
  }

  public String toMarkdownLink() {
    return String.format("[%s](%s)", title, getYtUrl(videoId));
  }

  public String getMemberName() {
    return memberName;
  }

  public String getVideoId() {
    return videoId;
  }

  public String getTitle() {
    return title;
  }

  public String getChannelName() {
    return channelTitle;
  }

  public ZonedDateTime getStreamStartTime() {
    return liveSchedule;
  }

  public boolean isMentionedStream() {
    return mentionedStream;
  }

  // This is only a possibility of the stream being member only or not
  public boolean isPossibleMemberOnly() {
    String keyword = Config.getConfig().speculateName;
    return title.contains(keyword);
  }

  private String getYtUrl(String id) {
    return String.format(URL_PREFIX, id);
  }

  private static final String URL_PREFIX = "https://www.youtube.com/watch?v=%s";
  private String memberName;
  private String videoId;
  private String title;
  private ZonedDateTime liveSchedule;
  private String channelTitle;
  private boolean mentionedStream;
}
