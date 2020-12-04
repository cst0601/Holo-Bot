package com.alchemist;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONArray;

public class HoloMemberData {
	
	public static HoloMemberData getInstance() {
		if (instance == null) {
			synchronized (HoloMemberData.class) {
				if (instance == null) {
					instance = new HoloMemberData();
				}
			}
		}
		
		return instance;
	}
	
	public HoloMember getMemberByName(String name) {
		return memberDict.get(name);
	}
	
	public int getApiIdByName(String name) {
		return memberDict.get(name).getApiId();
	}
	
	public Map<String, String> generateChannelIdNameMap() {
		Map<String, String> map = new HashMap<String, String>();
		
		for (Map.Entry<String, HoloMember> entry: memberDict.entrySet()) {
			map.put(entry.getValue().getYoutubeId(),
					entry.getValue().getName());
		}
		
		return map;
	}
	
	public ArrayList<HoloMember> getAvaliableMembers() {
		return sortedMember;
	}
	
	private HoloMemberData() {
		logger = LoggerFactory.getLogger(HoloMemberData.class);
		logger.info("Reading member data from config/member.json");
		
		memberDict = new HashMap<String, HoloMember>();
		sortedMember = new ArrayList<HoloMember>();
		
		try {
			Scanner scanner = new Scanner(new File("config/member.json"));
			scanner.useDelimiter("\\Z");
			JSONArray json = new JSONArray(scanner.next());
			scanner.close();
			
			for (int i = 0; i < json.length(); ++i) {
				HoloMember member = new HoloMember(
						json.getJSONObject(i).getString("id"),
						json.getJSONObject(i).getString("name"),
						json.getJSONObject(i).getString("generation"),
						json.getJSONObject(i).getString("channel_id"),
						json.getJSONObject(i).getInt("api_id"));
				
				memberDict.put(member.getId(),
						       member);
				sortedMember.add(member);
			}
		} catch (FileNotFoundException e) {
			logger.error("config/member.json is missing.");
		}
	}
	
	private static HoloMemberData instance = null;
	private Map<String, HoloMember> memberDict;
	private ArrayList<HoloMember> sortedMember;	// such a bad way of maintaining this
	private Logger logger;
}
