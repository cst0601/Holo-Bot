package com.alchemist;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.Scopes;
import twitter4j.Status;
import twitter4j.SymbolEntity;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

public class TestTweetCache {
	
	private ArrayList<Status> statusList;
	private ArrayList<Status> updatedStatusList;
	
	@BeforeEach
	void setUp() {
		statusList = new ArrayList<Status>();
		updatedStatusList = new ArrayList<Status>();
		
		for (int i = 14; i >= 0 ; --i) {
			statusList.add(new TestStatus(i, "Name_" + i));
			updatedStatusList.add(new TestStatus(i + 2, "Name_" + (i + 2)));
		}
	}

	@Test
	void testUpdateTweets() {
		TweetCache cache = new TweetCache("test_hashtag");
		cache.updateTweets(statusList);
		Queue<String> newTweets = cache.updateTweets(updatedStatusList);
		
		assertEquals(2, newTweets.size());
		assertEquals("https://twitter.com/Name_15/status/15", newTweets.poll());
		assertEquals("https://twitter.com/Name_16/status/16", newTweets.poll());
	}
}

@SuppressWarnings("serial")
class TestUser implements User {

	private String name;
	
	public TestUser(String name) {
		this.name = name;
	}
	
	@Override
	public int compareTo(User o) { return 0; }

	@Override
	public RateLimitStatus getRateLimitStatus() { return null; }

	@Override
	public int getAccessLevel() {return 0;}

	@Override
	public long getId() {return 0;}

	@Override
	public String getName() {
		 
		return name;
	}

	@Override
	public String getEmail() {
		 
		return null;
	}

	@Override
	public String getScreenName() {
		return name;
	}

	@Override
	public String getLocation() {
		 
		return null;
	}

	@Override
	public String getDescription() {
		 
		return null;
	}

	@Override
	public boolean isContributorsEnabled() {
		 
		return false;
	}

	@Override
	public String getProfileImageURL() {
		 
		return null;
	}

	@Override
	public String getBiggerProfileImageURL() {
		 
		return null;
	}

	@Override
	public String getMiniProfileImageURL() {
		 
		return null;
	}

	@Override
	public String getOriginalProfileImageURL() {
		 
		return null;
	}

	@Override
	public String get400x400ProfileImageURL() {
		 
		return null;
	}

	@Override
	public String getProfileImageURLHttps() {
		 
		return null;
	}

	@Override
	public String getBiggerProfileImageURLHttps() {
		 
		return null;
	}

	@Override
	public String getMiniProfileImageURLHttps() {
		 
		return null;
	}

	@Override
	public String getOriginalProfileImageURLHttps() {
		 
		return null;
	}

	@Override
	public String get400x400ProfileImageURLHttps() {
		 
		return null;
	}

	@Override
	public boolean isDefaultProfileImage() {
		 
		return false;
	}

	@Override
	public String getURL() {
		 
		return null;
	}

	@Override
	public boolean isProtected() {
		 
		return false;
	}

	@Override
	public int getFollowersCount() {
		 
		return 0;
	}

	@Override
	public Status getStatus() {
		 
		return null;
	}

	@Override
	public String getProfileBackgroundColor() {
		 
		return null;
	}

	@Override
	public String getProfileTextColor() {
		 
		return null;
	}

	@Override
	public String getProfileLinkColor() {
		 
		return null;
	}

	@Override
	public String getProfileSidebarFillColor() {
		 
		return null;
	}

	@Override
	public String getProfileSidebarBorderColor() {
		 
		return null;
	}

	@Override
	public boolean isProfileUseBackgroundImage() {
		 
		return false;
	}

	@Override
	public boolean isDefaultProfile() {
		 
		return false;
	}

	@Override
	public boolean isShowAllInlineMedia() {
		 
		return false;
	}

	@Override
	public int getFriendsCount() {
		 
		return 0;
	}

	@Override
	public Date getCreatedAt() {
		 
		return null;
	}

	@Override
	public int getFavouritesCount() {
		 
		return 0;
	}

	@Override
	public int getUtcOffset() {
		 
		return 0;
	}

	@Override
	public String getTimeZone() {
		 
		return null;
	}

	@Override
	public String getProfileBackgroundImageURL() {
		 
		return null;
	}

	@Override
	public String getProfileBackgroundImageUrlHttps() {
		 
		return null;
	}

	@Override
	public String getProfileBannerURL() {
		 
		return null;
	}

	@Override
	public String getProfileBannerRetinaURL() {
		 
		return null;
	}

	@Override
	public String getProfileBannerIPadURL() {
		 
		return null;
	}

	@Override
	public String getProfileBannerIPadRetinaURL() {
		 
		return null;
	}

	@Override
	public String getProfileBannerMobileURL() {
		 
		return null;
	}

	@Override
	public String getProfileBannerMobileRetinaURL() {
		 
		return null;
	}

	@Override
	public String getProfileBanner300x100URL() {
		 
		return null;
	}

	@Override
	public String getProfileBanner600x200URL() {
		 
		return null;
	}

	@Override
	public String getProfileBanner1500x500URL() {
		 
		return null;
	}

	@Override
	public boolean isProfileBackgroundTiled() {
		 
		return false;
	}

	@Override
	public String getLang() {
		 
		return null;
	}

	@Override
	public int getStatusesCount() {
		 
		return 0;
	}

	@Override
	public boolean isGeoEnabled() {
		 
		return false;
	}

	@Override
	public boolean isVerified() {
		 
		return false;
	}

	@Override
	public boolean isTranslator() {
		 
		return false;
	}

	@Override
	public int getListedCount() {
		 
		return 0;
	}

	@Override
	public boolean isFollowRequestSent() {
		 
		return false;
	}

	@Override
	public URLEntity[] getDescriptionURLEntities() {
		 
		return null;
	}

	@Override
	public URLEntity getURLEntity() {
		 
		return null;
	}

	@Override
	public String[] getWithheldInCountries() {
		 
		return null;
	}
	
}

@SuppressWarnings("serial")
class TestStatus implements Status {
	private long id;
	private String userName;
	
	public TestStatus(long id, String userName) {
		this.id = id;
		this.userName = userName;
	}

	@Override
	public int compareTo(Status o) {return 0;}

	@Override
	public RateLimitStatus getRateLimitStatus() {return null;}

	@Override
	public int getAccessLevel() {return 0;}

	@Override
	public UserMentionEntity[] getUserMentionEntities() {return null;}

	@Override
	public URLEntity[] getURLEntities() {return null;}

	@Override
	public HashtagEntity[] getHashtagEntities() {return null;}

	@Override
	public MediaEntity[] getMediaEntities() {return null;}

	@Override
	public SymbolEntity[] getSymbolEntities() {
		 
		return null;
	}

	@Override
	public Date getCreatedAt() {
		 
		return null;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getText() {
		 
		return null;
	}

	@Override
	public int getDisplayTextRangeStart() {
		 
		return 0;
	}

	@Override
	public int getDisplayTextRangeEnd() {
		 
		return 0;
	}

	@Override
	public String getSource() {
		 
		return null;
	}

	@Override
	public boolean isTruncated() {
		 
		return false;
	}

	@Override
	public long getInReplyToStatusId() {
		 
		return 0;
	}

	@Override
	public long getInReplyToUserId() {
		 
		return 0;
	}

	@Override
	public String getInReplyToScreenName() {
		 
		return null;
	}

	@Override
	public GeoLocation getGeoLocation() {
		 
		return null;
	}

	@Override
	public Place getPlace() {
		 
		return null;
	}

	@Override
	public boolean isFavorited() {
		 
		return false;
	}

	@Override
	public boolean isRetweeted() {
		 
		return false;
	}

	@Override
	public int getFavoriteCount() {
		 
		return 0;
	}

	@Override
	public User getUser() {
		return new TestUser(userName);
	}

	@Override
	public boolean isRetweet() {
		 
		return false;
	}

	@Override
	public Status getRetweetedStatus() {
		 
		return null;
	}

	@Override
	public long[] getContributors() {
		 
		return null;
	}

	@Override
	public int getRetweetCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isRetweetedByMe() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getCurrentUserRetweetId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isPossiblySensitive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getLang() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Scopes getScopes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getWithheldInCountries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getQuotedStatusId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Status getQuotedStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URLEntity getQuotedStatusPermalink() {
		// TODO Auto-generated method stub
		return null;
	}
}
