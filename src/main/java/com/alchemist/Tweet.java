package com.alchemist;

/**
 * Data class representing a tweet.
 */
public class Tweet {

  /** Constructor. */
  public Tweet(long userId, String userScreenName, long tweetId) {
    this.userId = userId;
    this.userScreenName = userScreenName;
    this.tweetId = tweetId;
  }

  public long getUserId() {
    return userId;
  }

  public String toUrl() {
    return String.format(url, userScreenName, tweetId);
  }

  private long userId;
  private long tweetId;
  private String userScreenName;
  private static final String url = "https://twitter.com/%s/status/%s";
}
