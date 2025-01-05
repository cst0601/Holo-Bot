package com.alchemist;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Builder of schedule embedded message.
 */
public class ScheduleEmbedBuilder {
  private EmbedBuilder builder;
  private ArrayList<String> slicedSchedules;
  private MessageMode mode = MessageMode.ALL;
  private String dateOfSchedule = "";
  private int page = 1;

  /** constructor. */
  public ScheduleEmbedBuilder(ArrayList<Schedule> schedules) {
    slicedSchedules = getSlicedScheduleString(schedules);
    builder = new EmbedBuilder()
      .setTitle(">holo schedule")
      .setColor(Color.red);
  }

  public ScheduleEmbedBuilder setMessageMode(MessageMode mode) {
    this.mode = mode;
    return this;
  }

  /**
   * Set the page index, ranges from 1~n.
   * If mode is all, page will be ignored.
   */
  public ScheduleEmbedBuilder setPage(int page) {
    this.page = page;
    return this;
  }

  public ScheduleEmbedBuilder addDateOfSchedule(String date) {
    this.dateOfSchedule = date;
    return this;
  }

  /** Add time stamp to embed. */
  public ScheduleEmbedBuilder addTimeStamp(Instant timeStamp) {
    builder.setFooter("All showed time are in JST")
        .setTimestamp(timeStamp);
    return this;
  }

  /** Build message embed. */
  public MessageEmbed build() {
    if (slicedSchedules.size() == 0) {
      builder.addField("No stream schedules yet :(((", "Check again later.", false);
      return builder.build();
    }

    if (mode == MessageMode.ALL) {
      builder.addField("Schedules of " + dateOfSchedule,
              slicedSchedules.get(0), false);
      slicedSchedules.remove(0);

      // add the rest of the schedules to message
      for (String scheduleSlice : slicedSchedules) {
        builder.addField("", scheduleSlice, false);
      }
    } else if (mode == MessageMode.PAGED) {
      if (page <= slicedSchedules.size() && page > 0) {
        builder.addField(
            String.format("Schedule of %s, page[%d/%d]",
                  dateOfSchedule, page, slicedSchedules.size()),
            slicedSchedules.get(page - 1), false);
      } else {
        builder.addField("Error", "Page out of range "
            + "(Max: " + slicedSchedules.size() + ")", false);
      }
    }

    return builder.build();
  }

  /** Page the message if too long. */
  public enum MessageMode { ALL, PAGED }

  /**
   * Limit of text in EmbedMessage.Field.text is 1024, if exceed, slice it.
   *
   * @return List of sliced schedules with each limited to 1024 characters.
   */
  private ArrayList<String> getSlicedScheduleString(ArrayList<Schedule> schedules) {
    ArrayList<String> slicedSchedule = new ArrayList<String>();
    String slice = "";
    for (Schedule schedule : schedules) {
      if (slice.length() + schedule.toMarkdownLink().length() > 1024) {
        slicedSchedule.add(slice);
        slice = "";
      }
      slice += " - " + schedule.toMarkdownLink() + "\n";
    }
    if (!slice.equals("")) {
      slicedSchedule.add(slice);
    }

    return slicedSchedule;
  }
}
