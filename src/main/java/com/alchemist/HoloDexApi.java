package com.alchemist;

import com.alchemist.HoloMemberData.Division;
import com.alchemist.jsonresponse.HoloDexLiveJsonResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
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
    readMemberFilter();
  }

  /**
   * Get all live streams of all members.
   */
  public ArrayList<LiveStream> getLiveStreams()
      throws IOException, InterruptedException {
    String apiRequest = "https://holodex.net/api/v2/live?max_upcoming_hours=48&org=Hololive";
    return request(apiRequest).getStream("live");
  }

  /**
   * Get stream by member name and stream type.
   */
  public ArrayList<LiveStream> getStreamOfMember(String member, String streamType)
      throws IOException, InterruptedException {
    try {
      String channelId = HoloMemberData.getInstance().getMemberByName(member).getYoutubeId();
      String apiRequest = String.format("https://holodex.net/api/v2/live?max_upcoming_hours=48&org=Hololive&channel_id=%s", channelId);

      return request(apiRequest).getStream(streamType);
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
  private HoloDexLiveJsonResponse request(String uri) throws IOException, InterruptedException {
    request = HttpRequest.newBuilder()
        .header("X-APIKEY", Config.getConfig().holoDexApiKey)
        .uri(URI.create(uri))
        .build();
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    return new HoloDexLiveJsonResponse(
        response.statusCode(), response.body());
  }

  /**
   * HoloDex API returns data of not desired members (holostars etc.). This
   * method checks if the member is in hololive member specified in
   * HoloMemberData.
   *
   * @param channelId Youtube Channel ID.
   * @return Is the member in HoloMemberData (desired).
   */
  public static boolean isMemberDesired(String channelId) {
    readMemberFilter();
    return MEMBER_FILTER.contains(channelId);
  }

  /**
   * Initialize a hash set containing hololive members that are specified in
   * HoloMemberData. This method is a singleton, if the file is read and data
   * saved in MEMBER_FILTER hash set, returns early.
   */
  private static void readMemberFilter() {
    if (MEMBER_FILTER != null) {
      return;
    }

    MEMBER_FILTER = new HashSet<String>();

    for (HoloMember memberData : HoloMemberData.getInstance().getAvaliableMembers(Division.JP)) {
      MEMBER_FILTER.add(memberData.getYoutubeId());
    }
    for (HoloMember memberData : HoloMemberData.getInstance().getAvaliableMembers(Division.EN)) {
      MEMBER_FILTER.add(memberData.getYoutubeId());
    }
    for (HoloMember memberData : HoloMemberData.getInstance().getAvaliableMembers(Division.ID)) {
      MEMBER_FILTER.add(memberData.getYoutubeId());
    }

  }

  private Logger logger;
  private static Set<String> MEMBER_FILTER = null;
}
