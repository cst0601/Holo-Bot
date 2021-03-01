package com.alchemist;

import java.time.Instant;
import java.time.ZonedDateTime;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;

/**
 * Data class that stores a LiveStream and its status
 * @author greg8
 *
 */
public class UpcommingStream {
	public UpcommingStream(LiveStream liveStream) {
		this.liveStream = liveStream;
		upcommingNotificationTime = liveStream.getStreamStartTime().minusMinutes(5);
	}
	
	public Message broadcast() {
		if (state == StreamState.INIT) {
			nextState();
			return new MessageBuilder("頻道有新動靜！快去看看！\n")
					.append(liveStream.toString())
					.build();
		}
		else if (state == StreamState.NOTIFIED) {
			if (upcommingNotificationTime.toInstant().isBefore(Instant.now())) {
				nextState();
				return new MessageBuilder("再過五分鐘配信開始！\n")
						.append(liveStream.toString())
						.build();
			}
		}
		else if (state == StreamState.UPCOMMING) {
			if (liveStream.getStreamStartTime().toInstant().isBefore(Instant.now())) {
				nextState();
				return new MessageBuilder("配信開始了！\n")
						.append(liveStream.toString())
						.build();
			}
		}
		return null;
	}
	
	public boolean hasStarted() {
		return state == StreamState.STARTED;
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
	private ZonedDateTime upcommingNotificationTime;
	private StreamState state = StreamState.INIT;
}
