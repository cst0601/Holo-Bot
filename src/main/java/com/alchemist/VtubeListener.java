package com.alchemist;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VtubeListener extends ListenerAdapter {
	private String key;
	public VtubeListener(String key) {
		// TODO move this to somewhere else
		// this is some prototyping
		this.key = key;
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
			
			if (msg.equals("!miko")) {
				try {
					YoutubeApi api = new YoutubeApi(key);
					LiveStream liveStream = new ContentFactory().createLiveStream(api.request().getBody());
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
