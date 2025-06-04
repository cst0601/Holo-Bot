package com.alchemist.notification;

import java.time.Instant;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.utils.TimeFormat;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

/** Methods for creating notification messages. */
public class NotificationMessage {

  /** Append new stream notification message. */
  public static void getNewStreamMessage(
      MessageCreateBuilder builder, 
      LiveStream liveStream,
      Instant startTimeInstant) {
    if (liveStream.isMentionedStream()) {
      builder.addContent("在其他頻道被提及了！快去看看！");
    } else {
      builder.addContent("頻道有新動靜！快去看看！\n");
    }
    builder
        .addContent("預定開始時間: " + TimeFormat.DATE_TIME_LONG.atInstant(startTimeInstant))
        .addContent(", " + TimeFormat.RELATIVE.atInstant(startTimeInstant) + "\n")
        .addContent(liveStream.toString() + "\n");
  }

  /** Append upcoming stream notification. */
  public static void getUpcomingStreamMessage(
      MessageCreateBuilder builder, 
      LiveStream liveStream) {
    builder
        .addContent("再過五分鐘配信開始！\n")
        .addContent(liveStream.toString() + "\n");
  }

  /** Append stream started notification. */
  public static void getStreamStartMessage(
      MessageCreateBuilder builder,
      LiveStream liveStream,
      Role role
  ) {
    if (!liveStream.isMentionedStream() && role != null) {
      builder.addContent(role.getAsMention());
    } 
    builder
        .addContent("にゃっはろ～！配信開始了！\n")
        .addContent(liveStream.toString() + "\n");
  }

  /** Append stream start time update notification message. */
  public static void getStreamStartTimeUpdateMessage(
      MessageCreateBuilder builder,
      LiveStream liveStream,
      Instant startTimeInstant
  ) {
    builder
        .addContent("直播開始時間更新了！")
        .addContent("預定開始時間: " + TimeFormat.DATE_TIME_LONG.atInstant(startTimeInstant))
        .addContent(", " + TimeFormat.RELATIVE.atInstant(startTimeInstant) + "\n")
        .addContent(liveStream.toString() + "\n");
  }

  /** Append membership only stream notification message. */
  public static void getMembershipOnlyStreamMessage(
      MessageCreateBuilder builder,
      long memberShipChannelId
  ) {
    builder
        .addContent("嗶嗶...從直播標題判斷，這個可能是會員限定直播...\n")
        .addContent("如果是的話，請到<#" + memberShipChannelId + ">頻道同時視聽討論\n");
  }
}
