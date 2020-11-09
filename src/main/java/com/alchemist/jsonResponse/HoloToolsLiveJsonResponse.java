package com.alchemist.jsonResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.alchemist.HoloMemberData;
import com.alchemist.LiveStream;

public class HoloToolsLiveJsonResponse {
	public HoloToolsLiveJsonResponse(int statusCode, String body) {
		members = HoloMemberData.getInstance().generateChannelIdNameMap();
		this.statusCode = statusCode;
		this.body = new JSONObject(body);
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public ArrayList<LiveStream> getLiveStreams() {
		ArrayList<LiveStream> streams = new ArrayList<LiveStream>();
		JSONArray liveStreams = body.getJSONArray("live");
		
		for (int i = 0; i < liveStreams.length(); ++i) {
			JSONObject stream = liveStreams.getJSONObject(i);
			String memberName = members.get(stream.getJSONObject("channel").getString("yt_channel_id"));
			if (memberName != null) {
				streams.add(new LiveStream(
						memberName,
						stream.getString("yt_video_key"),
						stream.getString("title"),
						stream.getJSONObject("channel").getString("name")
					));
			}
		}
		
		return streams;
	}
	
	private int statusCode;
	private JSONObject body;
	private Map<String, String> members = new HashMap<String, String>();	// channel_id, name
}
