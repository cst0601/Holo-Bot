package com.alchemist;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Scanner;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alchemist.jsonResponse.youtube.LiveStreamChatMessageList;
import com.alchemist.exceptions.ApiQuotaExceededException;
import com.alchemist.exceptions.HttpException;

public class YoutubeApi extends Api {
	private static final String CREDENTIALS =
		"config/credentials/youtube_api.json";
	private final String requestLiveStreamChat =
		"https://youtube.googleapis.com/youtube/v3/liveChat/messages?liveChatId"
		+ "=%s&part=authorDetails&maxResults=15&key=%s";
	private final String apiKey;
	private String ytLiveChatId;
	private HttpRequest request;

	private Logger logger;


	public YoutubeApi(String liveChatId) throws IOException {
		super();
		logger = LoggerFactory.getLogger(YoutubeApi.class);
		apiKey = readCredentials();
		ytLiveChatId = liveChatId;
	}
	
	public LiveStreamChatMessageList requestLiveStreamChat () 
			throws IOException, InterruptedException, ApiQuotaExceededException, HttpException {
		request = HttpRequest.newBuilder()
				.uri(URI.create(String.format(requestLiveStreamChat, ytLiveChatId, apiKey)))
				.build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		if (response.statusCode() == 200) {
			return new LiveStreamChatMessageList(response.body());
		}
		else if (response.statusCode() == 403) {
			logger.warn("Youtube API quota exceeded.");
			throw new ApiQuotaExceededException("Youtube api quota exceeded.");
		}
		throw new HttpException("Error occured when sending request to youtube api.", response.statusCode());
	}

	private String readCredentials() throws IOException {
		Scanner scanner = new Scanner(new File(CREDENTIALS));
		scanner.useDelimiter("\\Z");
		JSONObject json = new JSONObject(scanner.next());
		scanner.close();

		return json.getString("youtube-api-key");
	}

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
				} 
				else return false;
			}
		}
		
		logger.warn("User with YoutubeId: " + youtubeId + " not found in LiveStreamChat." );
		return false;
	}
}
