package com.alchemist;

import net.dv8tion.jda.api.JDA;
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
		JDA jda = event.getJDA();	// core stuff jda
		
		User author = event.getAuthor();
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
		
		String msg = message.getContentDisplay();
		boolean isBot = author.isBot();
		
		if (event.isFromType(ChannelType.TEXT)) {
			Guild guild = event.getGuild();
			TextChannel textChannel = event.getTextChannel();
			
			String [] commandVector = msg.split(" ");
			if (commandVector[0].equals(">holo")) {
				try {
					System.out.println(commandVector[1]);
					LiveStream liveStream = new ContentFactory().createLiveStream(
							api.request(commandVector[1]).getBody());
					if (liveStream == null) 
						channel.sendMessage("目前並沒有直播 :(").queue();
					else
						channel.sendMessage("目前的直播：\n" + liveStream.getTitle() + "\n" + liveStream.toString()).queue();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
