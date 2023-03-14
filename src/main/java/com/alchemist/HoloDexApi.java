package com.alchemist;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alchemist.HoloMemberData.DIVISION;
import com.alchemist.jsonResponse.HoloDexLiveJsonResponse;

public class HoloDexApi extends Api {
	public HoloDexApi () {
		super();
		logger = LoggerFactory.getLogger(HoloDexApi.class);
		readMemberFilter();
	}
	
	/**
	 * Get all live streams of all members
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public ArrayList<LiveStream> getLiveStreams()
			throws IOException, InterruptedException {
		String apiRequest = "https://holodex.net/api/v2/live?max_upcoming_hours=48&org=Hololive";
		return request(apiRequest).getStream("live");
	}
	
	/**
	 * Get stream by member name and stream type
	 * @param member
	 * @param streamType
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public ArrayList<LiveStream> getStreamOfMember(String member, String streamType) 
			throws IOException, InterruptedException {
		try {
			String channelId = HoloMemberData.getInstance().getMemberByName(member).getYoutubeId();
			String apiRequest = String.format("https://holodex.net/api/v2/live?max_upcoming_hours=48&org=Hololive&channel_id=%s", channelId);
			
			return request(apiRequest).getStream(streamType);
		} catch (NoSuchElementException e) {
			logger.warn("Member with name " + member + " not found.");
		}
		
		return new ArrayList<LiveStream>();
	}
	
	/**
	 * Get "live" stream by member name.
	 * (assumed there is only one stream on live at a time)
	 * @param member name
	 * @return a live stream. If there are no stream on live, return null
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public LiveStream getLiveStreamOfMember(String member) 
			throws IOException, InterruptedException {
		ArrayList<LiveStream> streams = getStreamOfMember(member, "live");
		return (streams.size() > 0)? streams.get(0): null;
	}
	
	private HoloDexLiveJsonResponse request(String uri) throws IOException, InterruptedException {
		request = HttpRequest.newBuilder()
				.uri(URI.create(uri))
				.build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		return new HoloDexLiveJsonResponse (
				response.statusCode(), response.body());
	}
	
	// singleton memberFilter, read if data not exist
	public static boolean isMemberDesired(String channelId) {
		readMemberFilter();
		return MEMBER_FILTER.contains(channelId);
	}
	
	private static void readMemberFilter() {
		if (MEMBER_FILTER != null) return;

		MEMBER_FILTER = new HashSet<String>();

		for (HoloMember memberData: HoloMemberData.getInstance().getAvaliableMembers(DIVISION.JP)) {
			MEMBER_FILTER.add(memberData.getYoutubeId());
		}
		for (HoloMember memberData: HoloMemberData.getInstance().getAvaliableMembers(DIVISION.EN)) {
			MEMBER_FILTER.add(memberData.getYoutubeId());
		}
		for (HoloMember memberData: HoloMemberData.getInstance().getAvaliableMembers(DIVISION.ID)) {
			MEMBER_FILTER.add(memberData.getYoutubeId());
		}
		
	}
	
	private Logger logger;
	private static Set<String> MEMBER_FILTER = null;
}
