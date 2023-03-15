package com.alchemist;

import java.time.Instant;
import java.time.ZonedDateTime;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/**
 * Data class that stores a LiveStream and its status
 * @author greg8
 *
 */
public class UpcomingStream {
	public UpcomingStream(LiveStream liveStream, Role mentionRole) {
		this.liveStream = liveStream;
		this.mentionRole = mentionRole;
		upcomingNotificationTime = liveStream.getStreamStartTime().minusMinutes(5);
		
		// quick patch for api not updating the state of the stream to ended / started
		if (liveStream.getStreamStartTime().toInstant().isBefore(Instant.now())) {
			state = StreamState.STARTED;
		}
	}
	
	public MessageCreateData broadcast() {
		if (state == StreamState.INIT) {
			nextState();
			return new MessageCreateBuilder()
					.addContent("頻道有新動靜！快去看看！\n")
					//.append("Start time: " + TimeFormat.atInstant())
					.addContent(getStreamUrl())
					.build();
		}
		else if (state == StreamState.NOTIFIED) {
			if (upcomingNotificationTime.toInstant().isBefore(Instant.now())) {
				nextState();
				return new MessageCreateBuilder()
						.addContent("再過五分鐘配信開始！\n")
						.addContent(getStreamUrl())
						.build();
			}
		}
		else if (state == StreamState.UPCOMMING) {
			if (liveStream.getStreamStartTime().toInstant().isBefore(Instant.now())) {
				nextState();
				return new MessageCreateBuilder()
						.addContent(mentionRole + "にゃっはろ～！配信開始了！\n")
						.addContent(getStreamUrl())
						.build();
			}
		}
		return null;
	}
	
	public MessageCreateData checkStreamStartTime(UpcomingStream stream) {
		ZonedDateTime newNotificationTime = stream.upcomingNotificationTime;
		liveStream = stream.liveStream;
		if (!upcomingNotificationTime.equals(newNotificationTime)) {
			upcomingNotificationTime = newNotificationTime;
			
			state = StreamState.INIT;	// update to the corresponding state
			MessageCreateData msg = broadcast();
			while (msg != null) { 
				msg.close();
				msg = broadcast();
			}
		
			return new MessageCreateBuilder()
					.addContent("直播開始時間更新了！")
					.addContent(getStreamUrl())
					.build();
		}
		return null;
	}
	
	public boolean hasStarted() {
		return state == StreamState.STARTED;
	}
	
	public String getStreamUrl() {
		return liveStream.toString();
	}
	
	public String toString() {
		return "* state: " + state + 
			   ",\n start_time:" + upcomingNotificationTime.toString() +
			   ",\n url: " + liveStream.toString();
	}
	
	private void nextState() {
		switch (state) {
		case INIT:
			state = StreamState.NOTIFIED;
			break;
		case NOTIFIED: 
			state = StreamState.UPCOMMING;
			break;
		case UPCOMMING:
			state = StreamState.STARTED;
			break;
		default:
			break;
		}
	}
	
	/**
	 * INIT: Havn't been announced.
	 * NOTIFIED: Announced, before 5 min mark notification.
	 * UPCOMMING: After 5 min mark notification.
	 * STARTED: Stream has started.
	 */
	enum StreamState { INIT, NOTIFIED, UPCOMMING, STARTED }
	
	private LiveStream liveStream;
	private Role mentionRole;
	private ZonedDateTime upcomingNotificationTime;
	private StreamState state = StreamState.INIT;
}
