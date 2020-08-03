package com.alchemist;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.*;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.*;

public class YoutubeApi {
	public YoutubeApi(String key) {
		apiKey = key;
		client = HttpClient.newBuilder()
					.version(Version.HTTP_2)
					.followRedirects(Redirect.NORMAL)
					.build();
		request = HttpRequest.newBuilder()
					.uri(URI.create(String.format(defaultUrl, mikoChannelId, apiKey)))
					.build();
	}
	
	public JsonResponse request() throws IOException, InterruptedException {
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		return new JsonResponse(response.statusCode(), response.body());
	}
	
	private final String apiKey;
	private final String mikoChannelId = "UC-hM6YJuNYVAmUWxeIr9FeA";
	private final String defaultUrl = "https://www.googleapis.com/youtube/v3/se"
			+ "arch?part=snippet&channelId=%s&eventType=live&type=video&key=%s";
	private HttpClient client;
	private HttpRequest request;
}
