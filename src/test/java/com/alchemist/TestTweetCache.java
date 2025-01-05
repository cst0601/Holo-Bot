package com.alchemist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Queue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import twitter4j.v1.Status;
import twitter4j.v1.User;

/**
 * Unit tests for TweetCache.
 */
public class TestTweetCache {

  private ArrayList<Status> statusList;
  private ArrayList<Status> updatedStatusList;

  private Status createMockStatus(long id, String name) {
    Status mockStatus = mock();
    User mockUser = mock();

    when(mockUser.getName()).thenReturn(name);
    when(mockUser.getScreenName()).thenReturn(name);
    when(mockStatus.getId()).thenReturn(id);
    when(mockStatus.getUser()).thenReturn(mockUser);

    return mockStatus;
  }

  @BeforeEach
  void setUp() {
    statusList = new ArrayList<Status>();
    updatedStatusList = new ArrayList<Status>();

    for (int i = 14; i >= 0; --i) {
      statusList.add(createMockStatus(i, "Name_" + i));
      // statusList.add(new TestStatus(i, "Name_" + i));
      updatedStatusList.add(createMockStatus(i + 5, "Name_" + (i + 5)));
    }
  }

  @Test
  void testUpdateTweets() {
    TweetCache cache = new TweetCache("test_query");
    cache.updateTweets(statusList, 14);
    Queue<Tweet> newTweets = cache.updateTweets(updatedStatusList, 19);

    assertEquals(5, newTweets.size());
    assertEquals("https://twitter.com/Name_15/status/15", newTweets.poll().toUrl());
    assertEquals("https://twitter.com/Name_16/status/16", newTweets.poll().toUrl());
    assertEquals("https://twitter.com/Name_17/status/17", newTweets.poll().toUrl());
    assertEquals("https://twitter.com/Name_18/status/18", newTweets.poll().toUrl());
    assertEquals("https://twitter.com/Name_19/status/19", newTweets.poll().toUrl());
  }

  @Test
  void testUpdateManyTweets() {
    TweetCache cache = new TweetCache("test_query");
    for (int i = 0; i < 15; ++i) {
      ArrayList<Status> newTweet = new ArrayList<Status>();
      newTweet.add(createMockStatus(i + 15, "Name_" + (i + 15)));
      Queue<Tweet> temp = cache.updateTweets(newTweet, 0);
      assertEquals("https://twitter.com/Name_" + (i + 15) + "/status/" + (i + 15), temp.poll().toUrl());
    }

    ArrayList<Status> newTweet = new ArrayList<Status>();
    newTweet.add(createMockStatus(1600, "The 16th new tweet"));
    assertEquals(1, cache.updateTweets(newTweet, 0).size());
  }
}