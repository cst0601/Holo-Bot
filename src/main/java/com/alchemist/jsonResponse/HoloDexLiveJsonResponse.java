package com.alchemist.jsonResponse;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alchemist.HoloDexApi;
import com.alchemist.HoloMemberData;
import com.alchemist.LiveStream;

public class HoloDexLiveJsonResponse {
	public HoloDexLiveJsonResponse(int statusCode, String body) {
		logger = LoggerFactory.getLogger(HoloDexLiveJsonResponse.class);
		members = HoloMemberData.getInstance().generateChannelIdNameMap();
		this.statusCode = statusCode;
		this.body = new JSONArray(body);
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public ArrayList<LiveStream> getStream(String streamStatus) {
		ArrayList<LiveStream> streams = new ArrayList<LiveStream>();
		
		if (!STREAM_STATUS.contains(streamStatus)) // if not one of the vaild status of stream
			return streams;
		
		for (int i = 0; i < body.length(); ++i) {
			JSONObject stream = body.getJSONObject(i);
			JSONObject channel = stream.getJSONObject("channel");
			String channelId = channel.getString("id");
			
			
			if (!HoloDexApi.isMemberDesired(channelId)) continue;
			else if (!stream.getString("status").equals(streamStatus)) continue;
			
			try {
				streams.add(new LiveStream(
						members.get(channelId),
						stream.getString("id"),
						stream.getString("title"),
						stream.getString("start_scheduled"),
						channel.getString("name")
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
	private static final Set<String> STREAM_STATUS = Set.of("upcoming", "live", "ended");

}
