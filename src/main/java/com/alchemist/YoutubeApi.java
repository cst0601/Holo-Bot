package com.alchemist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.*;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.logging.Logger;

import org.json.JSONArray;

import com.alchemist.jsonResponse.JsonResponse;

// DEPRECATED
public class YoutubeApi extends Api{
	public YoutubeApi(String key) {
		super();
		
		apiKey = key;
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
		if(members.get(vtubeName) == null) {	// vtuber not found
			return null;
		}
	    
		request = HttpRequest.newBuilder()
				.uri(URI.create(String.format(
						defaultUrl, members.get(vtubeName).getYoutubeId(), apiKey)))
				.build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		return new JsonResponse(response.statusCode(), response.body());
	}
	
	/**
	 * Get available member names that is linked with a channel id.
	 * @return list of member names
	 */
	public ArrayList<HoloMember> getAvailableMembers() {
		ArrayList<HoloMember> memberNames = new ArrayList<HoloMember>();
		for (Enumeration<String> e = members.keys(); e.hasMoreElements();)
			memberNames.add(members.get((String) e.nextElement()));
		return memberNames;
	}
	
	private void initChannelId() {
		Logger logger = Logger.getLogger(YoutubeApi.class.getName());
		try {
			Scanner scanner = new Scanner(new File("config/member.json"));
			scanner.useDelimiter("\\Z");
			JSONArray json = new JSONArray(scanner.next());
			scanner.close();
			
			for (int i = 0; i < json.length(); ++i) {
				HoloMember member = new HoloMember(
						json.getJSONObject(i).getString("id"),
						json.getJSONObject(i).getString("name"),
						json.getJSONObject(i).getString("generation"),
						json.getJSONObject(i).getString("channel_id"),
						json.getJSONObject(i).getInt("api_id"));
				members.put(member.getId(), member);
			}
			
		} catch (FileNotFoundException e) {
			logger.severe("Cannot read member file.");
		}
	}
		
	private final String apiKey;
	private final String defaultUrl = "https://www.googleapis.com/youtube/v3/se"
			+ "arch?part=snippet&channelId=%s&eventType=live&type=video&key=%s";
	private Dictionary<String, HoloMember> members = new Hashtable<String, HoloMember>();
	private HttpRequest request;
}
