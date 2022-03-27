package com.alchemist.jsonResponse.youtube;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class LiveStreamChatMessageList {
	private List<LiveStreamChatMessage> messages;
	
	public LiveStreamChatMessageList(String body) {
		messages = new ArrayList<LiveStreamChatMessage>();
		JSONArray items = new JSONObject(body).getJSONArray("items");
		for (int i = 0; i < items.length(); i++) {
			messages.add(new LiveStreamChatMessage(items.getJSONObject(i)));
		}
	}
	
	public LiveStreamChatMessage getMessage(int index) {
		return messages.get(index);
	}
}
