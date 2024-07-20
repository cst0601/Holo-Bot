package com.alchemist;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.utils.TimeFormat;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/**
 * Data class that stores a LiveStream and its status
 * @author greg8
 *
 */
public class UpcomingStream {
	public UpcomingStream(LiveStream liveStream, JDA jda) {
		this.liveStream = liveStream;
		this.notifications = Config.getConfig().notifications;
		this.jda = jda;
		upcomingNotificationTime = liveStream.getStreamStartTime().minusMinutes(5);
		
		// quick patch for api not updating the state of the stream to ended / started
		if (liveStream.getStreamStartTime().toInstant().isBefore(Instant.now())) {
			state = StreamState.STARTED;
		}
	}
	
	public ArrayList<MessageCreateBuilder> broadcast() {
		ArrayList<MessageCreateBuilder> builders = new ArrayList<MessageCreateBuilder>();
		for (int i = 0; i < notifications.size(); i++) {
			builders.add(new MessageCreateBuilder());
		}
		
		if (state == StreamState.INIT) {
			nextState();
			Instant startTimeInstant = liveStream.getStreamStartTime().toInstant();
			builders.forEach(builder -> {
				builder
					.addContent("頻道有新動靜！快去看看！\n")
					.addContent("預定開始時間: " + TimeFormat.DATE_TIME_LONG.atInstant(startTimeInstant))
					.addContent(", " + TimeFormat.RELATIVE.atInstant(startTimeInstant) + "\n")
					.addContent(getStreamUrl() + "\n");
			});
			return builders;
		}
		else if (state == StreamState.NOTIFIED) {
			if (upcomingNotificationTime.toInstant().isBefore(Instant.now())) {
				nextState();
				builders.forEach(builder -> {
					builder
						.addContent("再過五分鐘配信開始！\n")
						.addContent(getStreamUrl() + "\n");
				});
				return builders;
			}
		}
		else if (state == StreamState.UPCOMMING) {
			if (liveStream.getStreamStartTime().toInstant().isBefore(Instant.now())) {
				nextState();
				for (int i = 0; i < builders.size(); i++) {
					long roleId = notifications.get(i).pingRoleId;
					if (roleId != 0) {
						builders.get(i).addContent(
								jda.getRoleById(roleId).getAsMention());
					}
					builders.get(i)
						.addContent("にゃっはろ～！配信開始了！\n")
						.addContent(getStreamUrl() + "\n");
				}
				return builders;
			}
		}
		return null;
	}
	
	public ArrayList<MessageCreateBuilder> checkStreamStartTime(UpcomingStream stream) {
		ZonedDateTime newNotificationTime = stream.upcomingNotificationTime;
		liveStream = stream.liveStream;
		if (!upcomingNotificationTime.equals(newNotificationTime)) {
			upcomingNotificationTime = newNotificationTime;
			
			state = StreamState.INIT;	// update to the corresponding state
			MessageCreateData msg = broadcast().get(0).build();
			while (msg != null) { 
				msg.close();
				msg = broadcast().get(0).build();
			}
		
			Instant startTimeInstant = liveStream.getStreamStartTime().toInstant();
			ArrayList<MessageCreateBuilder> builders = new ArrayList<MessageCreateBuilder>(notifications.size());
			builders.forEach(builder -> {
				builder
					.addContent("直播開始時間更新了！")
					.addContent("預定開始時間: " + TimeFormat.DATE_TIME_LONG.atInstant(startTimeInstant))
					.addContent(", " + TimeFormat.RELATIVE.atInstant(startTimeInstant) + "\n")
					.addContent(getStreamUrl() + "\n");
			});
			return builders;
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
	
	public ArrayList<MessageCreateBuilder> appendMemberOnlyMessage(ArrayList<MessageCreateBuilder> builders) {
		// StreamNotifierRunner should run this after broadcast()
		// therefore state should be NOTIFIED if we want to append this message when stream first broadcasted
		for (int i = 0; i < builders.size(); i++) {
			long memberShipChannelId = notifications.get(i).membershipTargetChannelId;
			if (memberShipChannelId != 0 &&
				state == StreamState.NOTIFIED &&
				liveStream.isPossibleMemberOnly()) {
				builders
					.get(i)
					.addContent("嗶嗶...從直播標題判斷，這個可能是會員限定直播...\n")
					.addContent("如果是的話，請到<#" + memberShipChannelId + ">頻道同時視聽討論\n");

			}
		}

		return builders;
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
	
	private JDA jda;
	private LiveStream liveStream;
	private ArrayList<ConfigNotification> notifications;
	private ZonedDateTime upcomingNotificationTime;
	private StreamState state = StreamState.INIT;
}
