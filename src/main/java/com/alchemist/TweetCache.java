package com.alchemist;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import twitter4j.v1.Status;

/**
 * Cache for tweets, preventing the bot from broadcasting tweets that are
 * already notified.
 */
public class TweetCache {
  public TweetCache(String searchQuery) {
    this.searchQuery = searchQuery;
  }

  public String getSearchQuery() {
    return searchQuery;
  }

  /**
   * Check the new tweets with the cached tweets and get the newly added tweets.
   *
   * @param statusList List of Tweets.
   * @param maxId tweet Id is a incrementing value, larger id is newer tweet.
   * @return new tweets.
   */
  public Queue<Tweet> updateTweets(List<Status> statusList, long maxId) {
    Queue<Tweet> differentTweets = new LinkedList<Tweet>();

    for (int i = statusList.size() - 1; i >= 0; --i) {  // check status from old to new
      if (statusList.get(i).getId() > this.maxId) {
        Tweet tweet = new Tweet(
            statusList.get(i).getUser().getId(),
            statusList.get(i).getUser().getScreenName(),
            statusList.get(i).getId()
        );

        differentTweets.add(tweet);
      }
    }

    this.maxId = maxId;

    return differentTweets;
  }

  private String searchQuery;
  private long maxId = 0;
}
