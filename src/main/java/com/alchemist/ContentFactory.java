package com.alchemist;

import org.json.JSONArray;
import org.json.JSONObject;

import com.alchemist.jsonResponse.JsonResponse;

/**
 * ContentFactory
 * @author greg8
 * Creates all kinds of objects of youtube content (livesteam, video)
 */
public class ContentFactory {
	public Content createContent(String contentType, JsonResponse json) {		
		if (contentType.equals("live")) {
			return createLiveStream(json.getBody());
		}
		return null;
	}
	
	/**
	 * create live stream from json
	 * @param body
	 * @return LiveStream
	 */
	public LiveStream createLiveStream(String body) {
		JSONObject json = new JSONObject(body);
		// if can't find stream
		if (json.getJSONArray("items").length() == 0)
			return null;
		
		JSONObject content = json.getJSONArray("items").getJSONObject(0);
		return new LiveStream(
					content.getJSONObject("id").getString("videoId"),
					content.getJSONObject("snippet").getString("title"),
					content.getJSONObject("snippet").getString("description"),
					content.getJSONObject("snippet").getString("channelTitle"),
					content.getJSONObject("snippet").getString("publishedAt")
				);
	}
}
