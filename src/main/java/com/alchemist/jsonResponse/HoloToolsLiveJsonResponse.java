package com.alchemist.jsonResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.alchemist.LiveStream;
import com.alchemist.YoutubeApi;

public class HoloToolsLiveJsonResponse {
	public HoloToolsLiveJsonResponse(int statusCode, String body) {
		loadHoloMemberFromFile();
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
						"",	// description
						stream.getJSONObject("channel").getString("name"),
						stream.getString("live_start")
					));
			}
		}
		
		return streams;
	}
	
	private void loadHoloMemberFromFile() {
		Logger logger = Logger.getLogger(YoutubeApi.class.getName());
		try {
			Scanner scanner = new Scanner(new File("config/member.json"));
			scanner.useDelimiter("\\Z");
			JSONArray json = new JSONArray(scanner.next());
			scanner.close();
			
			for (int i = 0; i < json.length(); ++i) {
				members.put(json.getJSONObject(i).getString("channel_id"),
						    json.getJSONObject(i).getString("name"));
			}
			
		} catch (FileNotFoundException e) {
			logger.severe("Cannot read member file.");
		}
	}
	
	private int statusCode;
	private JSONObject body;
	private Dictionary<String, String> members = new Hashtable<String, String>();
}
