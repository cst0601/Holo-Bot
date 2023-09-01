package com.alchemist;

import java.time.Instant;
import java.time.ZonedDateTime;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.utils.TimeFormat;
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
			Instant startTimeInstant = liveStream.getStreamStartTime().toInstant();
			MessageCreateBuilder builder = new MessageCreateBuilder()
				.addContent("頻道有新動靜！快去看看！\n")
				.addContent("預定開始時間: " + TimeFormat.DATE_TIME_LONG.atInstant(startTimeInstant))
				.addContent(", " + TimeFormat.RELATIVE.atInstant(startTimeInstant) + "\n")
				.addContent(getStreamUrl() + "\n");
			
			if (liveStream.isPossibleMemberOnly()) builder = appendMemberOnlyMessage(builder);
			
			return builder.build();
		}
		else if (state == StreamState.NOTIFIED) {
			if (upcomingNotificationTime.toInstant().isBefore(Instant.now())) {
				nextState();
				return new MessageCreateBuilder()
						.addContent("再過五分鐘配信開始！\n")
						.addContent(getStreamUrl() + "\n")
						.build();
			}
		}
		else if (state == StreamState.UPCOMMING) {
			if (liveStream.getStreamStartTime().toInstant().isBefore(Instant.now())) {
				nextState();
				return new MessageCreateBuilder()
						.addContent(mentionRole.getAsMention() + "にゃっはろ～！配信開始了！\n")
						.addContent(getStreamUrl() + "\n")
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
		
			Instant startTimeInstant = liveStream.getStreamStartTime().toInstant();
			
			return new MessageCreateBuilder()
					.addContent("直播開始時間更新了！")
					.addContent("預定開始時間: " + TimeFormat.DATE_TIME_LONG.atInstant(startTimeInstant))
					.addContent(", " + TimeFormat.RELATIVE.atInstant(startTimeInstant) + "\n")
					.addContent(getStreamUrl() + "\n")
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
	
	private MessageCreateBuilder appendMemberOnlyMessage(MessageCreateBuilder builder) {
		Config config = Config.getConfig();
		builder
			.addContent("嗶嗶...從直播標題判斷，這個可能是會員限定直播...\n")
			.addContent("如果是的話，請到<#" + config.membershipTargetChannelId + ">頻道同時視聽討論\n");
		return builder;
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
