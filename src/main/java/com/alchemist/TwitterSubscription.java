package com.alchemist;

import java.util.ArrayList;

/**
 * Data class of discord channels subscribe to twitter search queries.
 */
public class TwitterSubscription {
  public TwitterSubscription(String searchQuery, ArrayList<Long> targetChannels) {
    this.searchQuery = searchQuery;
    this.targetChannels = targetChannels;
  }

  public String getSearchQuery() {
    return searchQuery;
  }

  public ArrayList<Long> getTargetChannels() {
    return targetChannels;
  }

  private String searchQuery;
  private ArrayList<Long> targetChannels;
}
