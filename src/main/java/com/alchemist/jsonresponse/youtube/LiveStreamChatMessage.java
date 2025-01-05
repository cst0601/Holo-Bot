package com.alchemist.jsonresponse.youtube;

import org.json.JSONObject;

/**
 * Data structure for Youtube's live stream chat message response.
 */
public class LiveStreamChatMessage {
  private JSONObject json;

  public LiveStreamChatMessage(JSONObject object) {
    json = object;
  }

  public String getDisplayName() {
    return json.getJSONObject("authorDetails").getString("displayName");
  }

  public String getChannelId() {
    return json.getJSONObject("authorDetails").getString("channelId");
  }

  public boolean isChatSponsor() {
    return json.getJSONObject("authorDetails").getBoolean("isChatSponsor");
  }
}
