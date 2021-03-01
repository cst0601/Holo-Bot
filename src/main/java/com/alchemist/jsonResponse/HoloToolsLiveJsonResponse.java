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

import com.alchemist.HoloMemberData;
import com.alchemist.LiveStream;

public class HoloToolsLiveJsonResponse {
	public HoloToolsLiveJsonResponse(int statusCode, String body) {
		logger = LoggerFactory.getLogger(HoloToolsLiveJsonResponse.class);
		members = HoloMemberData.getInstance().generateChannelIdNameMap();
		this.statusCode = statusCode;
		this.body = new JSONObject(body);
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public ArrayList<LiveStream> getLiveStreams() {
		return getStream("live");
	}
	
	public ArrayList<LiveStream> getStream(String streamStatus) {
		ArrayList<LiveStream> streams = new ArrayList<LiveStream>();
		
		if (!STREAM_STATUS.contains(streamStatus)) // if not one of the vaild status of stream
			return new ArrayList<LiveStream>();
		
		JSONArray liveStreams = body.getJSONArray(streamStatus);
		
		for (int i = 0; i < liveStreams.length(); ++i) {
			JSONObject stream = liveStreams.getJSONObject(i);
			String memberName = members.get(stream.getJSONObject("channel").getString("yt_channel_id"));
			if (memberName != null) {
				try {
					streams.add(new LiveStream(
							memberName,
							stream.getString("yt_video_key"),
							stream.getString("title"),
							stream.getString("live_schedule"),
							stream.getJSONObject("channel").getString("name")
						));
				} catch (JSONException | ParseException e) {
					logger.warn("Failed to create live stream.");
					e.printStackTrace();
				}
			}
		}
		
		return streams;
	}
	
	private Logger logger;
	private int statusCode;
	private JSONObject body;
	private Map<String, String> members = new HashMap<String, String>();	// channel_id, name
	private static final Set<String> STREAM_STATUS = Set.of("upcoming", "live", "ended");
}
