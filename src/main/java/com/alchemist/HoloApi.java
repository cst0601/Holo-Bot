package com.alchemist;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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
		date = jsonResponse.getSchedules().get(0).getDate();
		return jsonResponse.getSchedules();
	}
	
	/**
	 * Limit of text in EmbedMessage.Field.text is 1024, if exceed, slice it.
	 * @return Queue of sliced schedules with each limited to 1024 characters
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public Queue<String> getSlicedScheduleString() throws IOException, InterruptedException {
		Queue<String> slicedSchedule = new LinkedList<String>();
		String slice = "";
		for (Schedule schedule: request()) {
			if (slice.length() + schedule.toMarkdownLink().length() > 1024) {
				slicedSchedule.offer(slice);
				slice = "";
			}
			slice += " - " + schedule.toMarkdownLink() + "\n";
		}
		if (!slice.equals(""))
			slicedSchedule.offer(slice);

		return slicedSchedule;
	}
	
	public String getUpdateTime() {
		return updateTime;
	}
	
	public String getDateOfSchedule() {
		return date;
	}
		
	private final String defaultUrl = "http://127.0.0.1:5000/schedules/today";
	private HttpRequest request;
	private String updateTime = "";
	private String date = "";
}
