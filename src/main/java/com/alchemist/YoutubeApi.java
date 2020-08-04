package com.alchemist;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.*;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.*;
import java.util.Dictionary;
import java.util.Hashtable;


public class YoutubeApi {
	public YoutubeApi(String key) {
		apiKey = key;
		client = HttpClient.newBuilder()
					.version(Version.HTTP_2)
					.followRedirects(Redirect.NORMAL)
					.build();
		
		initChannelId();
	}
	
	public JsonResponse request(String vtubeName) throws
		IOException, InterruptedException
	{
		if(channelId.get(vtubeName) == null) {	// vtuber not found
			return new JsonResponse();
		}
	    
		request = HttpRequest.newBuilder()
				.uri(URI.create(String.format(
						defaultUrl, channelId.get(vtubeName), apiKey)))
				.build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		return new JsonResponse(response.statusCode(), response.body());
	}
	
	/**
	 * Crappy implementation, change it if not lazy
	 */
	private void initChannelId() {
		channelId.put("miko", "UC-hM6YJuNYVAmUWxeIr9FeA");
		channelId.put("marine", "UCCzUftO8KOVkV4wQG1vkUvg");
	}
		
	private final String apiKey;
	private final String defaultUrl = "https://www.googleapis.com/youtube/v3/se"
			+ "arch?part=snippet&channelId=%s&eventType=live&type=video&key=%s";
	private Dictionary<String, String> channelId = new Hashtable<String, String>();
	private HttpClient client;
	private HttpRequest request;
}
