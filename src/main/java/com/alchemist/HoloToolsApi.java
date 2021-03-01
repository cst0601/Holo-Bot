package com.alchemist;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;

import com.alchemist.jsonResponse.HoloToolsLiveJsonResponse;

public class HoloToolsApi extends Api {
	public HoloToolsApi() {
		super();
	}
	
	public HoloToolsLiveJsonResponse request(String uri)
			throws IOException, InterruptedException {
		request = HttpRequest.newBuilder()
				.uri(URI.create(uri))
				.build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		return new HoloToolsLiveJsonResponse(
				response.statusCode(), response.body());
	}
	
	/**
	 * Get all live streams of all members
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public ArrayList<LiveStream> getLiveStreams()
			throws IOException, InterruptedException {
		String apiRequest = "https://api.holotools.app/v1/live?"
				+ "max_upcoming_hours=48&hide_channel_desc=1";
		return request(apiRequest).getLiveStreams();
	}
	
	/**
	 * Get stream by member name and stream type
	 * @param member
	 * @param streamType
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public ArrayList<LiveStream> getStreamOfMember(String member, String streamType)
			throws IOException, InterruptedException {
		int apiMemberId = HoloMemberData.getInstance().getApiIdByName(member);
		
		String apiRequest = String.format("https://api.holotools.app/v1/live?"
				+ "channel_id=%d&max_upcoming_hours=48&hide_channel_desc=1", apiMemberId);
		
		return request(apiRequest).getStream(streamType);
	}
	
	/**
	 * Get "live" stream by member name.
	 * (assumed there is only one stream on live at a time)
	 * @param member name
	 * @return a live stream. If there are no stream on live, return null
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public LiveStream getLiveStreamOfMember(String member) 
			throws IOException, InterruptedException {
		ArrayList<LiveStream> streams = getStreamOfMember(member, "live");
		return (streams.size() > 0)? streams.get(0): null;
	}
}
