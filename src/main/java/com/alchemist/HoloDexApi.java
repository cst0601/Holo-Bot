package com.alchemist;

import com.alchemist.data.HoloMemberData;
import com.alchemist.jsonresponse.HoloDexLiveJsonResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * HoloDex API interface. Replaces old HoloToolsApi
 */
public class HoloDexApi extends Api {
  /** Reads member filter. */
  public HoloDexApi() {
    super();
    logger = LoggerFactory.getLogger(HoloDexApi.class);
  }

  /**
   * Get all live streams of all members.
   */
  public ArrayList<LiveStream> getLiveStreams()
      throws IOException, InterruptedException {
    String apiRequest = "https://holodex.net/api/v2/live?max_upcoming_hours=48&org=Hololive";
    return request(apiRequest).getStream("live", false);
  }

  /**
   * Get stream by member name and stream type.
   */
  public ArrayList<LiveStream> getStreamOfMember(String member, String streamType)
      throws IOException, InterruptedException {
    try {
      String channelId = HoloMemberData.getInstance().getMemberByName(member).getYoutubeId();
      String apiRequest = String.format("https://holodex.net/api/v2/live?max_upcoming_hours=48&org=Hololive&channel_id=%s", channelId);

      return request(apiRequest).getStream(streamType, false);
    } catch (NoSuchElementException e) {
      logger.warn("Member with name " + member + " not found.");
    }

    return new ArrayList<LiveStream>();
  }

  /**
   * Get streams that mentioned member.
   */
  public ArrayList<LiveStream> getStreamMentioningMember(String member, String streamType)
      throws IOException, InterruptedException {
    try {
      String channelId = HoloMemberData.getInstance().getMemberByName(member).getYoutubeId();
      String apiRequest = String.format("https://holodex.net/api/v2/live?max_upcoming_hours=48&mentioned_channel_id=%s", channelId);

      return request(apiRequest).getStream(streamType, true);
    } catch (NoSuchElementException e) {
      logger.warn("Member with name " + member + " not found.");
    }

    return new ArrayList<LiveStream>();
  }

  /**
   * Get "live" stream by member name.
   * (assumed there is only one stream on live at a time)
   *
   * @param member Member name.
   * @return A live stream. If there are no stream on live, return null.
   */
  public LiveStream getLiveStreamOfMember(String member)
      throws IOException, InterruptedException {
    ArrayList<LiveStream> streams = getStreamOfMember(member, "live");
    return (streams.size() > 0) ? streams.get(0) : null;
  }

  /**
   * Send Http request to HoloDex API.
   *
   * @param uri Target uri.
   * @return HoloDexLiveJsonResponse with formatted data.
   */
  public HoloDexLiveJsonResponse request(String uri) throws IOException, InterruptedException {
    request = HttpRequest.newBuilder()
        .header("X-APIKEY", Config.getConfig().holoDexApiKey)
        .uri(URI.create(uri))
        .build();
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    return new HoloDexLiveJsonResponse(
        response.statusCode(), response.body());
  }

  private Logger logger;
}
