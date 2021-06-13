package com.alchemist;

import java.time.Instant;
import java.time.ZonedDateTime;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

/**
 * Data class that stores a LiveStream and its status
 * @author greg8
 *
 */
public class UpcommingStream {
	public UpcommingStream(LiveStream liveStream, Role mentionRole) {
		this.liveStream = liveStream;
		this.mentionRole = mentionRole;
		upcommingNotificationTime = liveStream.getStreamStartTime().minusMinutes(5);
	}
	
	public Message broadcast() {
		if (state == StreamState.INIT) {
			nextState();
			return new MessageBuilder()
					.append("頻道有新動靜！快去看看！\n")
					.append(liveStream.toString())
					.build();
		}
		else if (state == StreamState.NOTIFIED) {
			if (upcommingNotificationTime.toInstant().isBefore(Instant.now())) {
				nextState();
				return new MessageBuilder()
						.append("再過五分鐘配信開始！\n")
						.append(liveStream.toString())
						.build();
			}
		}
		else if (state == StreamState.UPCOMMING) {
			if (liveStream.getStreamStartTime().toInstant().isBefore(Instant.now())) {
				nextState();
				return new MessageBuilder()
						.append(mentionRole)
						.append(" にゃっはろ～！配信開始了！\n")
						.append(liveStream.toString())
						.build();
			}
		}
		return null;
	}
	
	public Message checkStreamStartTime(LiveStream liveStream) {
		ZonedDateTime newNotificationTime = liveStream.getStreamStartTime().minusMinutes(5);
		if (!upcommingNotificationTime.equals(newNotificationTime)) {
			upcommingNotificationTime = newNotificationTime;
			
			state = StreamState.INIT;	// update to the corresponding state
			Message msg = broadcast();
			while (msg != null) { msg = broadcast(); }
		
			return new MessageBuilder()
					.append("直播開始時間更新了！")
					.append(liveStream.toString())
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
	private ZonedDateTime upcommingNotificationTime;
	private StreamState state = StreamState.INIT;
}
