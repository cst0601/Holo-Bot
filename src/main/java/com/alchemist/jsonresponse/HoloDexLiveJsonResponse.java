package com.alchemist.jsonresponse;

import com.alchemist.data.HoloMemberData;
import com.alchemist.data.HoloMemberFilter;
import com.alchemist.notification.LiveStream;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data structure for live endpoint JSON response from Holo Dex API.
 */
public class HoloDexLiveJsonResponse {
  /**
   * Construct HoloDexLiveResponse from API response.
   *
   * @param statusCode status code from the API response.
   * @param body JSON String response body.
   */
  public HoloDexLiveJsonResponse(int statusCode, String body) {
    logger = LoggerFactory.getLogger(HoloDexLiveJsonResponse.class);
    members = HoloMemberData.getInstance().getChannelIdNameMap();
    this.statusCode = statusCode;
    this.body = new JSONArray(body);
  }

  public int getStatusCode() {
    return statusCode;
  }

  /**
   * Get a list of formatted LiveStream objects.

   * @param streamStatus One of the STREAM_STATUS.
   * @return list for formatted LiveStream objects.
   */
  public ArrayList<LiveStream> getStream(String streamStatus, boolean isMentionedStream) {
    ArrayList<LiveStream> streams = new ArrayList<LiveStream>();

    if (!STREAM_STATUS.contains(streamStatus)) { // if not one of the vaild status of stream
      return streams;
    }

    for (int i = 0; i < body.length(); ++i) {
      JSONObject stream = body.getJSONObject(i);
      JSONObject channel = stream.getJSONObject("channel");
      String channelId = channel.getString("id");


      if (!HoloMemberFilter.getInstance().isMemberDesired(channelId)) {
        continue;
      } else if (!stream.getString("status").equals(streamStatus)) {
        continue;
      }

      ZonedDateTime scheduledTime = ZonedDateTime.parse(
          stream.getString("start_scheduled"), 
          dateTimeFormatter
      );

      try {
        streams.add(new LiveStream(
            members.get(channelId),
            stream.getString("id"),
            stream.getString("title"),
            scheduledTime,
            channel.getString("name"),
            isMentionedStream
          ));
      } catch (JSONException | ParseException e) {
        logger.warn("Failed to create live stream.");
        e.printStackTrace();
      }
    }

    return streams;
  }

  private Logger logger;
  private int statusCode;
  private JSONArray body;
  private Map<String, String> members = new HashMap<String, String>();
  private static final DateTimeFormatter dateTimeFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
  private static final Set<String> STREAM_STATUS = Set.of("upcoming", "live", "ended");

}
