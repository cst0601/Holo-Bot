package com.alchemist.jsonresponse;

import com.alchemist.VxTweet;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * JsonResponse for VxTwitterApi, creates VxTweet.
 */
public class VxTwitterApiJsonResponse {
  public VxTwitterApiJsonResponse(int statusCode, String body) {
    this.statusCode = statusCode;
    this.json = new JSONObject(body);
  }

  public int getStatus() {
    return statusCode;
  }

  /**
   * Generate VxTweet object from json response.
   *
   * @return VxTweet object.
   */
  public VxTweet getVxTweet() {
    ArrayList<String> mediaUrls = new ArrayList<String>();

    JSONArray jsonMediaUrls = json.getJSONArray("mediaURLs");
    for (int i = 0; i < jsonMediaUrls.length(); i++) {
      mediaUrls.add((String) jsonMediaUrls.get(i));
    }

    return new VxTweet(
        json.getLong("date_epoch"),
        json.getInt("likes"),
        json.getInt("replies"),
        json.getInt("retweets"),
        mediaUrls,
        json.getString("text"),
        json.getString("tweetID"),
        json.getString("user_name"),
        json.getString("user_screen_name"),
        json.getString("user_profile_image_url"));
  }

  private int statusCode;
  private JSONObject json;
}
