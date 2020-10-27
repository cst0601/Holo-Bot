package com.alchemist.service;

import java.awt.Color;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Logger;

import com.alchemist.ArgParser;
import com.alchemist.ContentFactory;
import com.alchemist.LiveStream;
import com.alchemist.ScheduleEmbedBuilder;
import com.alchemist.YoutubeApi;
import com.alchemist.exceptions.ArgumentParseException;
import com.alchemist.HoloApi;
import com.alchemist.HoloMember;
import com.alchemist.jsonResponse.JsonResponse;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * VtubeListener
 * Message commands related to vtube
 * @author greg8
 *
 */
public class VtubeListener extends ListenerAdapter implements Service {
	private YoutubeApi api;
	private HoloApi holoApi;
	private Logger logger;
	
	public VtubeListener(String key) {
		api = new YoutubeApi(key);
		holoApi = new HoloApi();
		logger = Logger.getLogger(VtubeListener.class.getName());
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
			ArgParser parser = new ArgParser(msg);

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
					JsonResponse response = api.request(parser.getCommand(1));
					if (parser.containsArgs("list")) {
						getHoloMemberList(channel);
					}
					
					else if (parser.containsArgs("schedules")) {
						getSchedules(channel, parser);
					}
					// get stream
					else if (response != null) {	// if arg member name not avaliable
						LiveStream liveStream = new ContentFactory().createLiveStream(api.request(parser.getCommand(1)).getBody());
					
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
			if (parser.containsParam("page")) {
				try {
					int page = parser.getInt("page");
					ScheduleEmbedBuilder builder = new ScheduleEmbedBuilder(holoApi.request())
							.addDateOfSchedule(holoApi.getDateOfSchedule())
							.addTimeStamp(holoApi.getUpdateTime())
							.setMessageMode(ScheduleEmbedBuilder.MessageMode.PAGED)
							.setPage(page);
					channel.sendMessage(builder.build()).queue();
				} catch (ArgumentParseException e1) {
					channel.sendMessage(e1.getMessage()).queue();
				}
			}
			else {
				ScheduleEmbedBuilder builder = new ScheduleEmbedBuilder(holoApi.request())
						.addDateOfSchedule(holoApi.getDateOfSchedule())
						.addTimeStamp(holoApi.getUpdateTime());
				channel.sendMessage(builder.build()).queue();
				
			}
		} catch (Exception e) {		// schedule api exceptions
			channel.sendMessage("Looks like schedule api went on vacation.  :((\n"
					+ "Contact admin to get help.").queue();
			logger.warning("Failed to use schedule api");
			e.printStackTrace();
		}
		
	}
	
	private void getHoloMemberList(MessageChannel channel) {
		String memberInfo = " - %s: [%s](https://www.youtube.com/channel/%s)\n";
		
		Dictionary<String, String> memberByGeneration = new Hashtable<String, String>();
		for (HoloMember member: api.getAvailableMembers()) {
			if (memberByGeneration.get(member.getGeneration()) == null) {
				memberByGeneration.put(
						member.getGeneration(), "");	// first init to empty string
			}
			memberByGeneration.put(						// add stuffs
					member.getGeneration(),
					memberByGeneration.get(member.getGeneration()) +
					String.format(memberInfo,
							member.getId(),
							member.getName(),
							member.getYoutubeId())
					);
		}
		
		/* TODO: think of a better data structure to sort member by generation */
		EmbedBuilder builder = new EmbedBuilder()
				.setTitle(">holo list")
				.setColor(Color.red)
				.addField("List of availble members", "- <id>: <name>", false)
				.setFooter("35P | Chikuma", "https://i.imgur.com/DOb1GZ1.png");
		builder.addField("Generation 0", memberByGeneration.get("0"), false);
		builder.addField("Generation 1", memberByGeneration.get("1"), false);
		builder.addField("Generation 2", memberByGeneration.get("2"), false);
		builder.addField("Generation 3", memberByGeneration.get("3"), false);
		builder.addField("Generation 4", memberByGeneration.get("4"), false);
		builder.addField("Generation 5", memberByGeneration.get("5"), false);
		builder.addField("Hololive Gamers", memberByGeneration.get("gamers"), false);
		builder.addField("INNK Music", memberByGeneration.get("INNK Music"), false);
		
		channel.sendMessage(builder.build()).queue();
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
	public String getServiceName() {
		return "holo";
	}

	@Override
	public String getServiceMan() {
		return
			"# NAME\n"
			+ "    holo - Hololive event tracker\n\n"
			+ "# SYNOPSIS\n"
			+ "    holo <command> [args]\n\n"
			+ "# COMMANDS\n"
			+ "    * list: List all available hololive members.\n"
			+ "    * <member_name>: Fetch streams currently going on.\n"
			+ "    * schedules: Get all schedules of today (JST).\n";
	}
}
