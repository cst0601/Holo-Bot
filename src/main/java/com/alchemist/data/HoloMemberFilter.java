package com.alchemist.data;

import com.alchemist.HoloMember;
import com.alchemist.data.HoloMemberData.Division;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for storing hololive member filter. 
 */
public class HoloMemberFilter {
    
  /** singleton constructor. */
  public static HoloMemberFilter getInstance() {
    if (instance == null) {
      synchronized (HoloMemberFilter.class) {
        if (instance == null) {
          instance = new HoloMemberFilter();
        }
      }
    }

    return instance;
  }

  /**
   * HoloDex API returns data of not desired members (holostars etc.). This
   * method checks if the member is in hololive member specified in
   * HoloMemberData.
   *
   * @param channelId Youtube Channel ID.
   * @return Is the member in HoloMemberData (desired).
   */
  public boolean isMemberDesired(String channelId) {
    return memberFilter.contains(channelId);
  }

  /** 
   * Initialize a hash set containing hololive members that are specified in
   * HoloMemberData. 
   */
  private HoloMemberFilter() {
    logger = LoggerFactory.getLogger(HoloMemberFilter.class);
    logger.info("Reading holo member filter");

    memberFilter = new HashSet<String>();

    for (HoloMember memberData : HoloMemberData.getInstance().getAvaliableMembers(Division.JP)) {
      memberFilter.add(memberData.getYoutubeId());
    }
    for (HoloMember memberData : HoloMemberData.getInstance().getAvaliableMembers(Division.EN)) {
      memberFilter.add(memberData.getYoutubeId());
    }
    for (HoloMember memberData : HoloMemberData.getInstance().getAvaliableMembers(Division.ID)) {
      memberFilter.add(memberData.getYoutubeId());
    }
  }

  private static HoloMemberFilter instance = null;
  private Logger logger;
  private Set<String> memberFilter = null;
}
