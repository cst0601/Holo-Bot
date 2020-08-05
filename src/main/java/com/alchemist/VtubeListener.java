package com.alchemist;

import java.awt.Color;

import net.dv8tion.jda.api.JDA;
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
public class VtubeListener extends ListenerAdapter {
	private YoutubeApi api;
	
	public VtubeListener(String key) {
		api = new YoutubeApi(key);
	}
	
	public String contentFormat() {
		return "";
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		// JDA jda = event.getJDA();	// core stuff jda
		
		User author = event.getAuthor();
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
		
		String msg = message.getContentDisplay();
		// boolean isBot = author.isBot();
		
		if (event.isFromType(ChannelType.TEXT)) {
			// Guild guild = event.getGuild();	// Not used in this case
			// TextChannel textChannel = event.getTextChannel();
			
			String [] commandVector = msg.split(" ");
			if (commandVector[0].equals(">holo")) {
				try {
					if (commandVector.length < 2) {
						channel.sendMessage(new MessageBuilder("Error: Usage: ")
								.append(">holo <member>", MessageBuilder.Formatting.BLOCK)
								.append(".\nUse ")
								.append(">holo list", MessageBuilder.Formatting.BLOCK)
								.append(" to get a full list of available members.")
								.build()).queue();
					}
					else {
						JsonResponse response = api.request(commandVector[1]);
						if (commandVector[1].equals("list")) {
							
							String members = "";
							for (String name: api.getAvailableMembers())
								members += " - " + name + "\n";
							
							channel.sendMessage(new EmbedBuilder()
								.setTitle(">holo list")
								.setColor(Color.red)
								.addField("List of availble members", members, false)
								.setFooter("35P | Chikuma", "https://i.imgur.com/DOb1GZ1.png")
								.build()).queue();
						}
						
						else if (response != null) {	// if arg member name not avaliable
							LiveStream liveStream = new ContentFactory().createLiveStream(api.request(commandVector[1]).getBody());
						
							if (liveStream == null) 
								channel.sendMessage("目前並沒有直播 :(").queue();
							
							else
								channel.sendMessage("目前的直播：\n" + liveStream.getTitle() + "\n" + liveStream.toString()).queue();
						}
						
						else {
							channel.sendMessage(new MessageBuilder("Error: member not found.\n")
								.append("Use ")
								.append(">holo list", MessageBuilder.Formatting.BLOCK)
								.append(" to get a full list of available members.")
								.build()).queue();
						}
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
	}
}
