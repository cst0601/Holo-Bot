package com.alchemist.service;

import java.awt.Color;

import com.alchemist.ArgParser;
import com.alchemist.HoloDexApi;
import com.alchemist.LiveStream;
import com.alchemist.exceptions.ArgumentParseException;
import com.alchemist.holoModel.HoloMemberListModel;
import com.alchemist.holoModel.HoloScheduleModel;
import com.alchemist.HoloMemberData;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * VtubeListener
 * Message commands related to vtube
 * @author greg8
 *
 */
public class VtubeListener extends ListenerAdapter implements Service {
	private HoloScheduleModel holoScheduleModel;
	private HoloDexApi holoDexApi;
	private Logger logger;
	private ArgParser parser = null;

	
	public VtubeListener() {
		holoScheduleModel = new HoloScheduleModel();
		holoDexApi = new HoloDexApi();
		logger = LoggerFactory.getLogger(VtubeListener.class.getName());
	}
	
	public String contentFormat() {
		return "";
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
		
		String msg = message.getContentDisplay();
		
		if (event.isFromType(ChannelType.TEXT)) {
			parser = new ArgParser(msg);

			if (parser.getCommand().equals(">holo")) {
				
				// actual parse the command if sure this is a command
				try {
					parser.parse();
				} catch (ArgumentParseException e1) {
					channel.sendMessage("Command format error.\n" + e1.getMessage()).queue();
					return;
				}
				

				if (parser.getParamSize() < 2) {
					channel.sendMessage(getMemberNotFoundMessage()).queue();
					return;
				}
				
				try {
					if (parser.containsArgs("list")) {
						getHoloMemberList(channel, parser);
					}
					
					else if (parser.containsArgs("schedules")) {
						getSchedules(channel, parser);
					}
					
					else if (parser.containsArgs("live")) {
						getLive(channel);
					}
					// get stream
					else if (HoloMemberData.getInstance().getMemberByName(parser.getCommand(1)) != null) {	// if arg member name not avaliable
						LiveStream liveStream = holoDexApi.getLiveStreamOfMember(parser.getCommand(1));
					
						if (liveStream == null) 
							channel.sendMessage("目前並沒有直播 :(").queue();
						
						else
							channel.sendMessage("目前的直播：\n" + liveStream.getTitle() + "\n" + liveStream.toString()).queue();
					}
					// no member found
					else {
						channel.sendMessage(getErrorMessage()).queue();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	
	// does not bother to test these methods :)
	private void getSchedules(MessageChannel channel, ArgParser parser) {
		try {
			if (parser.containsArgs("jp") || parser.getCommandSize() == 2)		
				channel.sendMessage(holoScheduleModel.getHoloSchedule()).queue();
			else if (parser.containsArgs("en"))
				channel.sendMessage(holoScheduleModel.getHoloEnSchedule()).queue();
			else if (parser.containsArgs("id"))
				channel.sendMessage(holoScheduleModel.getHoloIdSchedule()).queue();
			else
				channel.sendMessage("Error: Unknown group of Hololive").queue();
		} catch (Exception e) {		// schedule api exceptions
			channel.sendMessage("Looks like schedule api went on vacation.  :((\n"
					+ "Contact admin to get help.").queue();
			logger.warn("Failed to use schedule api");
			e.printStackTrace();
		}
	}
	
	private void getHoloMemberList(MessageChannel channel, ArgParser parser) {
		HoloMemberListModel holoMemberList = new HoloMemberListModel();
		
		if (parser.containsArgs("jp") || parser.getCommandSize() == 2)
			channel.sendMessage(holoMemberList.getHoloMemberList()).queue();
		else if (parser.containsArgs("en"))
			channel.sendMessage(holoMemberList.getHoloEnMemberList()).queue();
		else if (parser.containsArgs("id"))
			channel.sendMessage(holoMemberList.getHoloIdMemberList()).queue();
		else
			channel.sendMessage("Error: Unknown group of Hololive").queue();
	}
	
	private void getLive(MessageChannel channel) {
		EmbedBuilder builder = new EmbedBuilder()
				.setTitle(">holo live")
				.setColor(Color.red)
				.setDescription(":red_circle: Streams now")
				.setFooter("Holo Bot", "https://i.imgur.com/DOb1GZ1.png");
		
		try {
			for (LiveStream stream: holoDexApi.getLiveStreams()) {
				builder.addField(stream.getMemberName(), stream.toMarkdownLink(), false);
			}
			
			channel.sendMessage(builder.build()).queue();
			
		} catch (Exception e) {
			logger.warn("Failed to get request from holoToolsApi.");
			e.printStackTrace();
		}
	}
	
	private Message getErrorMessage() {
		return new MessageBuilder("Error: member not found.\n")
			.append("Use ")
			.append(">holo list", MessageBuilder.Formatting.BLOCK)
			.append(" to get a full list of available members.")
			.build();
	}
	
	private Message getMemberNotFoundMessage() {
		MessageBuilder builder = new MessageBuilder("Error: Usage: ")
			.append(">holo <member>", MessageBuilder.Formatting.BLOCK)
			.append(".\nUse ")
			.append(">holo list", MessageBuilder.Formatting.BLOCK)
			.append(" to get a full list of available members.\n")
			.append(">holo schedules", MessageBuilder.Formatting.BLOCK)
			.append(" to get schedules of today.");
		return builder.build();
	}
	
	@Override
	public String getServiceManualName() {
		return "holo";
	}

	@Override
	public String getServiceMan() {
		return
			"NAME\n"
			+ "        holo - Hololive event tracker\n\n"
			+ "SYNOPSIS\n"
			+ "        holo <command> [args]\n\n"
			+ "COMMANDS\n"
			+ "        list: List all available hololive members.\n"
			+ "        live: Get all streams that are currently live.\n"
			+ "        <member_name>: Fetch streams currently going on.\n"
			+ "        schedules: Get all schedules of today (JST).\n";
	}
}
