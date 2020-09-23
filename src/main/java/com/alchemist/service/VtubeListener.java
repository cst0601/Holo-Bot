package com.alchemist.service;

import java.awt.Color;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Queue;
import java.util.logging.Logger;

import com.alchemist.ContentFactory;
import com.alchemist.LiveStream;
import com.alchemist.YoutubeApi;
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
	
	/**
	 * Parse the command by splitting it by space or endings
	 * @param command
	 * @return parsed arguments
	 */
	public String[] parseArgv (String command) {
		return command.split("\\s+");
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		// JDA jda = event.getJDA();	// core stuff jda
		
		// User author = event.getAuthor();
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
		
		String msg = message.getContentDisplay();
		// boolean isBot = author.isBot();
		
		if (event.isFromType(ChannelType.TEXT)) {
			// Guild guild = event.getGuild();	// Not used in this case
			// TextChannel textChannel = event.getTextChannel();
			
			String [] commandVector = parseArgv(msg);
			if (commandVector[0].equals(">holo")) {
				if (commandVector.length < 2) {
					channel.sendMessage(new MessageBuilder("Error: Usage: ")
							.append(">holo <member>", MessageBuilder.Formatting.BLOCK)
							.append(".\nUse ")
							.append(">holo list", MessageBuilder.Formatting.BLOCK)
							.append(" to get a full list of available members.\n")
							.append(">holo schedules", MessageBuilder.Formatting.BLOCK)
							.append(" to get schedules of today.")
							.build()).queue();
					return;
				}
				
				try {
					JsonResponse response = api.request(commandVector[1]);
					if (commandVector[1].equals("list")) {
						channel.sendMessage(getHoloMemberList()).queue();
					}
					
					else if (commandVector[1].equals("schedules")) {
						try {
							channel.sendMessage(getSchedules()).queue();
						} catch (Exception e) {
							channel.sendMessage("Looks like schedule api went on vacation.  :((\n"
									+ "Contact admin to get help.").queue();
							logger.warning("Failed to use schedule api");
							e.printStackTrace();
						}
					}
					// get stream
					else if (response != null) {	// if arg member name not avaliable
						LiveStream liveStream = new ContentFactory().createLiveStream(api.request(commandVector[1]).getBody());
					
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
	
	private MessageEmbed getSchedules() throws IOException, InterruptedException {
		Queue<String> scheduleSlices = holoApi.getSlicedScheduleString();
									
		EmbedBuilder builder = new EmbedBuilder()
				.setTitle(">holo schedule")
				.setColor(Color.red)
				.addField("Schedules of " + holoApi.getDateOfSchedule(),
						scheduleSlices.poll() ,false)
				.setFooter("updated@" + holoApi.getUpdateTime() +
						" | All showed time are in JST");
		
		while (scheduleSlices.peek() != null)	// add the rest of the schedules to message
			builder.addField("", scheduleSlices.poll(), false);
		
		return builder.build();
	}
	
	private MessageEmbed getHoloMemberList() {
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
		
		return builder.build();
	}
	
	private Message getErrorMessage () {
		return new MessageBuilder("Error: member not found.\n")
			.append("Use ")
			.append(">holo list", MessageBuilder.Formatting.BLOCK)
			.append(" to get a full list of available members.")
			.build();
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
