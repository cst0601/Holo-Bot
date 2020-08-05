package com.alchemist;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.*;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.*;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
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
	
	/**
	 * Make a request to Youtube API by name of vtuber.
	 * If the name is not found avaulable, return null (debt)
	 * @param vtubeName
	 * @return JsonResponse of youtube api
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public JsonResponse request(String vtubeName) throws IOException, InterruptedException
	{
		if(channelId.get(vtubeName) == null) {	// vtuber not found
			return null;
		}
	    
		request = HttpRequest.newBuilder()
				.uri(URI.create(String.format(
						defaultUrl, channelId.get(vtubeName), apiKey)))
				.build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		return new JsonResponse(response.statusCode(), response.body());
	}
	
	/**
	 * Get available member names that is linked with a channel id.
	 * @return list of member names
	 */
	public ArrayList<String> getAvailableMembers() {
		ArrayList<String> memberNames = new ArrayList<String>();
		for (Enumeration e = channelId.keys(); e.hasMoreElements();)
			memberNames.add((String) e.nextElement());
		return memberNames;
	}
	
	/**
	 * Crappy implementation, change it if not lazy
	 */
	private void initChannelId() {
		channelId.put("miko", "UC-hM6YJuNYVAmUWxeIr9FeA");
		channelId.put("marine", "UCCzUftO8KOVkV4wQG1vkUvg");
		channelId.put("korone", "UChAnqc_AY5_I3Px5dig3X1Q");
		channelId.put("watame", "UCqm3BQLlJfvkTsX_hvm0UmA");
		channelId.put("sora", "UCp6993wxpyDPHUpavwDFqgg");
	}
		
	private final String apiKey;
	private final String defaultUrl = "https://www.googleapis.com/youtube/v3/se"
			+ "arch?part=snippet&channelId=%s&eventType=live&type=video&key=%s";
	private Dictionary<String, String> channelId = new Hashtable<String, String>();
	private HttpClient client;
	private HttpRequest request;
}
