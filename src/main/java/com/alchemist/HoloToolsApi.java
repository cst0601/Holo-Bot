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
	
	public ArrayList<LiveStream> request() throws IOException, InterruptedException {
		request = HttpRequest.newBuilder()
				.uri(URI.create(defaultUrl))
				.build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		HoloToolsLiveJsonResponse json = new HoloToolsLiveJsonResponse(
				response.statusCode(), response.body());
		
		return json.getLiveStreams();
	}
	
	private final String defaultUrl = "https://api.holotools.app/v1/live?max_upcoming_hours=48&hide_channel_desc=1";
}
