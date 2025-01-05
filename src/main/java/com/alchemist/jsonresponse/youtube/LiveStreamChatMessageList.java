package com.alchemist.jsonresponse.youtube;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * List containing livestream chat messages.
 */
public class LiveStreamChatMessageList {
  private List<LiveStreamChatMessage> messages;
  private int size = 0;

  /**
   * Takes response body from youtube API and parse it to messages.

   * @param body JSON string response from youtube API,
   */
  public LiveStreamChatMessageList(String body) {
    messages = new ArrayList<LiveStreamChatMessage>();
    JSONArray items = new JSONObject(body).getJSONArray("items");
    size = items.length();
    for (int i = 0; i < items.length(); i++) {
      messages.add(new LiveStreamChatMessage(items.getJSONObject(i)));
    }
  }

  public LiveStreamChatMessage getMessage(int index) {
    return messages.get(index);
  }

  public int getSize() {
    return size;
  }
}
