package com.alchemist.holoModel;

import java.io.IOException;
import java.net.ConnectException;

import com.alchemist.HoloApi;
import com.alchemist.ScheduleEmbedBuilder;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class HoloScheduleModel {
	private HoloApi holoApi;
	
	public HoloScheduleModel () {
		holoApi = new HoloApi();
	}
	
	public MessageEmbed getHoloSchedule()
			throws IOException, InterruptedException, ConnectException {
		ScheduleEmbedBuilder builder = new ScheduleEmbedBuilder(holoApi.request("hololive"))
				.addDateOfSchedule(holoApi.getDateOfSchedule())
				.addTimeStamp(holoApi.getUpdateTime());
		return builder.build();
	}
	
	public MessageEmbed getHoloEnSchedule()
			throws ConnectException, IOException, InterruptedException {
		ScheduleEmbedBuilder builder = new ScheduleEmbedBuilder(holoApi.request("en"))
				.addDateOfSchedule(holoApi.getDateOfSchedule())
				.addTimeStamp(holoApi.getUpdateTime());
		return builder.build();
	}
}
