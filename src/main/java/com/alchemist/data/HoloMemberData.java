package com.alchemist.data;

import com.alchemist.HoloMember;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for storing hololive member information.
 */
public class HoloMemberData {

  /** Singleton constructor. */
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
   * Search all hololive divisions for the member by name.
   */
  public HoloMember getMemberByName(String name) throws NoSuchElementException {
    for (Map<String, HoloMember> divisionDict : memberDict.values()) {
      HoloMember member = divisionDict.get(name);
      if (member != null) {
        return member;
      }
    }
    throw new NoSuchElementException("Member with name: " + name + " not found.");
  }

  /** Get mapping between channdl ID and name. */
  public Map<String, String> getChannelIdNameMap() {
    if (channelIdNameMap == null) {
      channelIdNameMap = new HashMap<String, String>();
      for (Map<String, HoloMember> membersOfDiv : memberDict.values()) {
        for (Map.Entry<String, HoloMember> entry : membersOfDiv.entrySet()) {
          channelIdNameMap.put(entry.getValue().getYoutubeId(),
              entry.getValue().getName());
        }
      }
    }

    return channelIdNameMap;
  }

  public ArrayList<HoloMember> getAvaliableMembers(Division division) {
    return sortedMember.get(division);
  }

  private HoloMemberData() {
    logger = LoggerFactory.getLogger(HoloMemberData.class);
    logger.info("Reading member data from config/member.json");

    memberDict = new HashMap<Division, Map<String, HoloMember>>();
    sortedMember = new HashMap<Division, ArrayList<HoloMember>>();

    loadMemberInfoFromFile(Division.JP, "member.json");
    loadMemberInfoFromFile(Division.EN, "member_en.json");
    loadMemberInfoFromFile(Division.ID, "member_id.json");
  }

  /**
   * Read member files from config and save to dict.
   *
   * @param division Division of the group: JP, EN or ID.
   * @param path File path.
   */
  private void loadMemberInfoFromFile(Division division, String path) {

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
            json.getJSONObject(i).getString("channel_id"));

        dict.put(member.getId(), member);
        sortedList.add(member);
      }

      memberDict.put(division, dict);
      sortedMember.put(division, sortedList);

    } catch (FileNotFoundException e) {
      logger.error("config/" + path + " is missing.");
    }
  }

  /** Enum of Hololive branches. */
  public enum Division {
    JP, EN, ID
  }

  private static HoloMemberData instance = null;
  private Map<Division, Map<String, HoloMember>> memberDict = null;
  private Map<Division, ArrayList<HoloMember>> sortedMember = null;
  private Map<String, String> channelIdNameMap = null;
  private Logger logger;
}
