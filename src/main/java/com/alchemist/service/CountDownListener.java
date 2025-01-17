package com.alchemist.service;

import com.alchemist.ArgParser;
import com.alchemist.exceptions.ArgumentParseException;
import java.awt.Color;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Service for >miko command. Respond with the time to the event time set.
 */
public class CountDownListener extends ListenerAdapter implements Service {
  private static final String targetTime = "2023-08-01 00:00:00 +09:00";

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    Message message = event.getMessage();
    MessageChannelUnion channel = event.getChannel();

    String msg = message.getContentDisplay();

    ArgParser parser = new ArgParser(msg);
    Mode countDownMode = Mode.NORM;

    if (parser.getCommand().equals(">miko")) {
      try {
        parser.parse();
        if (parser.containsParam("mode")) { // debt
          switch (parser.getString("mode")) {
            case "hr":
              countDownMode = Mode.HOUR;
              break;
            case "min":
              countDownMode = Mode.MINUTE;
              break;
            case "sec":
              countDownMode = Mode.SECOND;
              break;
            default:
              break;
          }
        }
      } catch (ArgumentParseException e1) {
        channel.sendMessage("Command format error.\n" + e1.getMessage()).queue();
        return;
      }

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
      ZonedDateTime eventTime = ZonedDateTime.parse(targetTime, formatter);
      ZonedDateTime timeNow = ZonedDateTime.now(ZoneId.of("UTC+9"));
      Duration difference = Duration.between(eventTime, timeNow);

      String differenceString = setCountDownFormat(difference, countDownMode);

      EmbedBuilder builder = new EmbedBuilder()
          .setTitle(">miko 距離周年倒數")
          .setColor(Color.red)
          .addField("現在時刻",
              DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(timeNow), false)
          .addField("預定時刻",
              DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(eventTime), false)
          .addField("距離生日還有", differenceString, false)
          .addBlankField(false)
          .setFooter("All time shown are in JST | Might have latency in seconds");

      channel.sendMessageEmbeds(builder.build()).queue();
    }
  }

  /**
   * Format the time difference to string.
   *
   * @param duration time difference.
   * @param mode the format of the time.
   * @return Formatted string.
   */
  public String setCountDownFormat(Duration duration, Mode mode) {
    String format = "%2d 天  %02d 小時 %02d 分鐘 %02d 秒";
    Boolean isAfterTargetTime = !duration.isNegative();
    duration = duration.abs();

    switch (mode) {
      case HOUR:
        format = String.format("%d 小時 %02d 分鐘 %02d 秒",
            duration.toHours(),
            duration.toMinutesPart(),
            duration.toSecondsPart());
        break;
      case MINUTE:
        format = String.format("%02d 分鐘 %02d 秒",
            duration.toMinutes(),
            duration.toSecondsPart());
        break;
      case SECOND:
        format = String.format("%d 秒",
            duration.toSeconds());
      // fallthrough
      case NORM:
      default:
        format = String.format(format,
            duration.toDaysPart(),
            duration.toHoursPart(),
            duration.toMinutesPart(),
            duration.toSecondsPart());
        break;
    }

    if (isAfterTargetTime) { // before or after target time
      format = "+ " + format;
    } else {
      format = "- " + format;
    }

    return format;
  }

  /**
   * Mode of formatting time to string.
   */
  public enum Mode {
    NORM, HOUR, MINUTE, SECOND
  }

  @Override
  public String getServiceManualName() {
    return "miko";
  }

  @Override
  public String getServiceMan() {
    return "NAME\n"
        + "        miko - Miko return timer\n\n"
        + "SYNOPSIS\n"
        + "        miko";
  }

}
