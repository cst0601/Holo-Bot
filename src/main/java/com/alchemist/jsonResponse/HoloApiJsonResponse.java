package com.alchemist.jsonResponse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.alchemist.Schedule;

/**
 * JsonResponse for holo schedule api response (schedules/today)
 * @author chikuma
 *
 */
public class HoloApiJsonResponse {
	public HoloApiJsonResponse(int statusCode, String body) {
		this.statusCode = statusCode;
		this.json = new JSONObject(body);
	}
	
	public int getStatus() {
		return statusCode;
	}
	
	public String getUpdateTime() {
		return json.getString("update_time");
	}
	
	/**
	 * TODO: yet another debt, move this to HoloApi
	 * @return
	 */
	public ArrayList<Schedule> getSchedules() {
		if (json.isNull("schedule"))	// if no schedules yet
			return new ArrayList<Schedule>();

		JSONArray jsonSchedules = json.getJSONObject("schedule").getJSONArray("schedules");
		ArrayList<Schedule> schedules = new ArrayList<Schedule>();
		for (int i = 0; i < jsonSchedules.length(); ++i) {
			schedules.add(new Schedule(
					jsonSchedules.getJSONObject(i).getString("member"),
					jsonSchedules.getJSONObject(i).getString("youtube_url"),
					json.getJSONObject("schedule").getString("date"),
					jsonSchedules.getJSONObject(i).getString("time")
			));	
		}
		
		return schedules;
	}
	
	private int statusCode;
	private JSONObject json;
}
