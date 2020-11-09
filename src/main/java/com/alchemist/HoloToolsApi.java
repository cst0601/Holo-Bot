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
	
	public ArrayList<LiveStream> getLiveStreams()
			throws IOException, InterruptedException {
		String apiRequest = "https://api.holotools.app/v1/live?"
				+ "max_upcoming_hours=48&hide_channel_desc=1";
		return request(apiRequest).getLiveStreams();
	}
	
	public LiveStream getLiveStreamOfMember(String member)
			throws IOException, InterruptedException {
		int apiMemberId = HoloMemberData.getInstance().getApiIdByName(member);
		
		String apiRequest = String.format("https://api.holotools.app/v1/live?"
				+ "channel_id=%d&max_upcoming_hours=48&hide_channel_desc=1", apiMemberId);
		
		ArrayList<LiveStream> streams = request(apiRequest).getLiveStreams();

		return (streams.size() > 0)? streams.get(0): null;
	}
}
