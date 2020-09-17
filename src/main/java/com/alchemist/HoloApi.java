package com.alchemist;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;

import com.alchemist.jsonResponse.HoloApiJsonResponse;


public class HoloApi extends Api {
	public HoloApi() {
		super();	
	}
	
	public ArrayList<Schedule> request() throws IOException, InterruptedException {
		request = HttpRequest.newBuilder()
				.uri(URI.create(defaultUrl))
				.build();
		
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		HoloApiJsonResponse jsonResponse = new HoloApiJsonResponse(
				response.statusCode(), response.body());
		updateTime = jsonResponse.getUpdateTime();
		return jsonResponse.getSchedules();
	}
	
	public String getUpdateTime() {
		return updateTime;
	}
		
	private final String defaultUrl = "http://127.0.0.1:5000/schedules/today";
	private HttpRequest request;
	private String updateTime = "";
}
