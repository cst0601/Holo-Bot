package com.alchemist.holomodel;

import com.alchemist.HoloApi;
import com.alchemist.ScheduleEmbedBuilder;
import java.io.IOException;
import java.net.ConnectException;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Getter of HoloSchedule.
 */
public class HoloScheduleModel {
  private HoloApi holoApi;

  public HoloScheduleModel() {
    holoApi = new HoloApi();
  }

  /** JP members. */
  public MessageEmbed getHoloSchedule()
      throws IOException, InterruptedException, ConnectException {
    ScheduleEmbedBuilder builder = new ScheduleEmbedBuilder(holoApi.request("hololive"))
        .addDateOfSchedule(holoApi.getDateOfSchedule())
        .addTimeStamp(holoApi.getUpdateTime());
    return builder.build();
  }

  /** EN members. */
  public MessageEmbed getHoloEnSchedule()
      throws ConnectException, IOException, InterruptedException {
    ScheduleEmbedBuilder builder = new ScheduleEmbedBuilder(holoApi.request("en"))
        .addDateOfSchedule(holoApi.getDateOfSchedule())
        .addTimeStamp(holoApi.getUpdateTime());
    return builder.build();
  }

  /** ID members. */
  public MessageEmbed getHoloIdSchedule()
      throws ConnectException, IOException, InterruptedException {
    ScheduleEmbedBuilder builder = new ScheduleEmbedBuilder(holoApi.request("id"))
        .addDateOfSchedule(holoApi.getDateOfSchedule())
        .addTimeStamp(holoApi.getUpdateTime());
    return builder.build();
  }
}
