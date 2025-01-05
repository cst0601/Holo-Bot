package com.alchemist;

import com.alchemist.exceptions.ApiQuotaExceededException;
import com.alchemist.exceptions.HttpException;
import com.alchemist.jsonresponse.youtube.LiveStreamChatMessageList;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Youtube API.
 */
public class YoutubeApi extends Api {
  private final String requestLiveStreamChat =
      "https://youtube.googleapis.com/youtube/v3/liveChat/messages?liveChatId"
      + "=%s&part=authorDetails&maxResults=15&key=%s";
  private final String apiKey;
  private String ytLiveChatId;
  private HttpRequest request;

  private Logger logger;

  /**
   * Constructor. Reads youtube API key from config.
   *
   * @param liveChatId Live chat ID for member verification service.
   * @throws IOException Throws IOException if could not read config file.
   */
  public YoutubeApi(String liveChatId) throws IOException {
    super();
    logger = LoggerFactory.getLogger(YoutubeApi.class);
    apiKey = Config.getConfig().youtubeKey;
    ytLiveChatId = liveChatId;
  }

  /**
   * Get live stream chat.
   *
   * @return List of live stream chat.
   */
  public LiveStreamChatMessageList requestLiveStreamChat()
      throws IOException, InterruptedException, ApiQuotaExceededException, HttpException {
    request = HttpRequest.newBuilder()
        .uri(URI.create(String.format(requestLiveStreamChat, ytLiveChatId, apiKey)))
        .build();
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    if (response.statusCode() == 200) {
      return new LiveStreamChatMessageList(response.body());
    } else if (response.statusCode() == 403) {
      logger.warn("Youtube API quota exceeded.");
      throw new ApiQuotaExceededException("Youtube api quota exceeded.");
    }
    throw new HttpException(
      "Error occured when sending request to youtube api.",
      response.statusCode());
  }

  /**
   * Search the live stream chat and verify if the youtube Id is a member of the
   * channel.
   *
   * @param youtubeId user youtube Id.
   * @return is the user a member of the channel?
   */
  public boolean verify(String youtubeId) {
    LiveStreamChatMessageList list;
    try {
      list = requestLiveStreamChat();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    for (int i = 0; i < list.getSize(); i++) {
      logger.debug(list.getMessage(i).getDisplayName());
      if (list.getMessage(i).getChannelId().equals(youtubeId)) {
        if (list.getMessage(i).isChatSponsor()) {
          return true;
        } else {
          return false;
        }
      }
    }

    logger.warn("User with YoutubeId: " + youtubeId + " not found in LiveStreamChat.");
    return false;
  }
}
