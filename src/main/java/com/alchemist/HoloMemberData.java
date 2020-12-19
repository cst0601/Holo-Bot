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
	
	/**
	 * Search all hololive divisions for the member by name
	 * @param name
	 * @return
	 */
	public HoloMember getMemberByName(String name) {
		for (Map<String, HoloMember> divisionDict: memberDict.values()) {
			HoloMember member = divisionDict.get(name);
			if (member != null)
				return member;
		}
		return null;
	}
	
	/**
	 * Get holotools API id by name
	 * @param name
	 * @return
	 */
	public int getApiIdByName(String name) {
		for (Map<String, HoloMember> divisionDict: memberDict.values()) {
			HoloMember member = divisionDict.get(name);
			if (member != null)
				return member.getApiId();
		}
		return -1;	// debt :(((((
	}
	
	public Map<String, String> generateChannelIdNameMap() {
		Map<String, String> map = new HashMap<String, String>();
		
		for (Map<String, HoloMember> membersOfDiv: memberDict.values())
			for (Map.Entry<String, HoloMember> entry: membersOfDiv.entrySet()) {
				map.put(entry.getValue().getYoutubeId(),
						entry.getValue().getName());
			}
		
		return map;
	}
	
	public ArrayList<HoloMember> getAvaliableMembers(DIVISION division) {
		return sortedMember.get(division);
	}
	
	private HoloMemberData() {
		logger = LoggerFactory.getLogger(HoloMemberData.class);
		logger.info("Reading member data from config/member.json");
		
		memberDict = new HashMap<DIVISION, Map<String, HoloMember>>();
		sortedMember = new HashMap<DIVISION, ArrayList<HoloMember>>();
		
		loadMemberInfoFromFile(DIVISION.JP, "member.json");
		loadMemberInfoFromFile(DIVISION.EN, "member_en.json");
	}
	
	/**
	 * Read member files from config and save to dict
	 * @param division Division of the group: JP, EN or ID
	 * @param path
	 */
	private void loadMemberInfoFromFile(DIVISION division, String path) {
		
		Map<String, HoloMember> dict = new HashMap<String, HoloMember>();
		ArrayList<HoloMember> sortedList = new ArrayList<HoloMember>();
		
		try {
			Scanner scanner = new Scanner(new File("config/" + path));
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
				
				dict.put(member.getId(), member);
				sortedList.add(member);
			}
			
			memberDict.put(division, dict);
			sortedMember.put(division, sortedList);
			
		} catch (FileNotFoundException e) {
			logger.error("config/member.json is missing.");
		}
	}
	
	public enum DIVISION {
		JP, EN, ID
	}
	
	private static HoloMemberData instance = null;
	private Map<DIVISION, Map<String, HoloMember>> memberDict;
	private Map<DIVISION, ArrayList<HoloMember>> sortedMember;
	private Logger logger;
}
