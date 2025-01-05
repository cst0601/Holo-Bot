package com.alchemist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Twitter broadcast service configuration
 * Configuration file is located at "config/twitter_broadcast.json". The file
 * consists of twitter search query, broadcast target discord channel id and
 * blacklisted twitter user id.
 */
public class TwitterBroadcastConfig {

  /** Reads config file and setup. */
  public TwitterBroadcastConfig() throws FileNotFoundException {
    logger = LoggerFactory.getLogger(TwitterBroadcastConfig.class);

    Scanner scanner = new Scanner(new File(CONFIG_FILE_PWD));
    scanner.useDelimiter("\\Z");
    json = new JSONObject(scanner.next());
    scanner.close();

    // parse json file
    // broadcast
    JSONArray broadcast = json.getJSONArray("broadcast");
    for (int i = 0; i < broadcast.length(); ++i) {
      JSONArray targetJson = broadcast.getJSONObject(i).getJSONArray("target");
      ArrayList<Long> targetChannels = new ArrayList<Long>();

      for (int j = 0; j < targetJson.length(); ++j) {
        targetChannels.add(targetJson.getLong(j));
      }

      subscriptions.add(
        new TwitterSubscription(
          broadcast.getJSONObject(i).getString("query"), targetChannels));
    }

    // blacklist
    JSONArray list = json.getJSONArray("blacklist");
    for (int i = 0; i < list.length(); i++) {
      blacklist.add(list.getLong(i));
    }

    JSONArray targets = json.getJSONArray("uncensored_targets");
    for (int i = 0; i < targets.length(); i++) {
      uncensoredTargetId.add(targets.getLong(i));
    }
  }

  public ArrayList<TwitterSubscription> getTwitterSubscriptions() {
    return subscriptions;
  }

  public boolean isUncensoredTargetId(long channelId) {
    return uncensoredTargetId.contains(channelId);
  }

  public boolean isInBlacklist(long twitterId) {
    return blacklist.contains(twitterId);
  }

  /** Add target twitter ID to blacklist. */
  public void addBlacklistUser(long twitterId) {
    if (!blacklist.contains(twitterId)) {
      json.getJSONArray("blacklist").put(twitterId);
      blacklist.add(twitterId);
      updateConfigFile();
    }
  }

  /**
   * Remove the twitter ID from blacklist.
   *
   * @param twitterId target twitter ID.
   * @return false if twitter id not found in config
   */
  public boolean removeBlacklistUser(long twitterId) {
    JSONArray jsonBlacklist = json.getJSONArray("blacklist");
    for (int i = 0; i < jsonBlacklist.length(); i++) {
      if (jsonBlacklist.getLong(i) == twitterId) {
        jsonBlacklist.remove(i);
        break;
      }
    }

    boolean userExist =  blacklist.remove(twitterId);
    updateConfigFile();
    return userExist;
  }

  /** Rewite the twitter config file. */
  public void updateConfigFile() {
    try {
      Writer writer = json.write(
        new FileWriter(CONFIG_FILE_PWD, Charset.forName("UTF-8")), 4, 0);
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Failed to update/write configuration back to twitter_broadcast.json");
    }
  }

  private Logger logger;
  private JSONObject json;
  private ArrayList<TwitterSubscription> subscriptions = new ArrayList<TwitterSubscription>();
  private HashSet<Long> blacklist = new HashSet<Long>();
  private HashSet<Long> uncensoredTargetId = new HashSet<Long>();
  private static final String CONFIG_FILE_PWD = "config/twitter_broadcast.json";
}
