package com.alchemist.notification;

import com.alchemist.Config;
import com.alchemist.ConfigNotification;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/**
 * Data class that stores a LiveStream and its status.
 */
public class UpcomingStream {
  /** Constructor. Setup broadcast timestamp and state. */
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

  /** State update. */
  public ArrayList<MessageCreateBuilder> broadcast() {
    ArrayList<MessageCreateBuilder> builders = new ArrayList<MessageCreateBuilder>();
    for (int i = 0; i < notifications.size(); i++) {
      builders.add(new MessageCreateBuilder());
    }

    if (state == StreamState.INIT) {
      nextState();
      Instant startTimeInstant = liveStream.getStreamStartTime().toInstant();
      builders.forEach(builder -> {
        NotificationMessage.getNewStreamMessage(builder, liveStream, startTimeInstant);
      });
      return builders;
    } else if (state == StreamState.NOTIFIED) {
      if (upcomingNotificationTime.toInstant().isBefore(Instant.now())) {
        nextState();
        builders.forEach(builder -> {
          NotificationMessage.getUpcomingStreamMessage(builder, liveStream);
        });
        return builders;
      }
    } else if (state == StreamState.UPCOMMING) {
      if (liveStream.getStreamStartTime().toInstant().isBefore(Instant.now())) {
        nextState();
        for (int i = 0; i < builders.size(); i++) {
          long roleId = notifications.get(i).pingRoleId;
          NotificationMessage.getStreamStartMessage(
              builders.get(i),
              liveStream,
              // prevents jda call if invalid role id
              (roleId == 0) ? null : jda.getRoleById(roleId)  
          );
        }
        return builders;
      }
    }
    return null;
  }

  /** Check the stream start time, if updated, return message for broadcasting. */
  public ArrayList<MessageCreateBuilder> checkStreamStartTime(UpcomingStream stream) {
    ZonedDateTime newNotificationTime = stream.upcomingNotificationTime;
    liveStream = stream.liveStream;
    if (!upcomingNotificationTime.equals(newNotificationTime)) {
      upcomingNotificationTime = newNotificationTime;

      state = StreamState.INIT;   // update to the corresponding state
      MessageCreateData msg = broadcast().get(0).build();
      while (msg != null) {
        msg.close();
        ArrayList<MessageCreateBuilder> newMsg = broadcast();
        msg = (newMsg == null) ? null : newMsg.get(0).build();
      }

      Instant startTimeInstant = liveStream.getStreamStartTime().toInstant();
      ArrayList<MessageCreateBuilder> builders =
          new ArrayList<MessageCreateBuilder>(notifications.size());
      builders.forEach(builder -> {
        NotificationMessage.getStreamStartTimeUpdateMessage(builder, liveStream, startTimeInstant);
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

  /** Convert stream to string for logging. */
  public String toString() {
    return "* start_time:" + upcomingNotificationTime.toString()
         + ", url: " + liveStream.toString()
         + ", state: " + state
         + ", is_mentioned: " + liveStream.isMentionedStream();
  }

  /** Convert stream to markdown syntax message for displaying on discord. */
  public String toMarkdownString() {
    return "* start_time: " + upcomingNotificationTime.toString()
         + ", [url](" + liveStream.toString() + ")"
         + ", state: " + state 
         + ", is_mentioned: " + liveStream.isMentionedStream() + "\n";
  }

  /** Append membership stream only message. */
  public ArrayList<MessageCreateBuilder> appendMemberOnlyMessage(
      ArrayList<MessageCreateBuilder> builders
  ) {
    // StreamNotifierRunner should run this after broadcast()
    // therefore state should be NOTIFIED if we want to append this message when
    // stream first broadcasted.
    for (int i = 0; i < builders.size(); i++) {
      long memberShipChannelId = notifications.get(i).membershipTargetChannelId;
      if (memberShipChannelId != 0
          && state == StreamState.NOTIFIED
          && liveStream.isPossibleMemberOnly()
      ) {
        NotificationMessage.getMembershipOnlyStreamMessage(builders.get(i), memberShipChannelId);
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
