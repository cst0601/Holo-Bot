package com.alchemist;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PingListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		JDA jda = event.getJDA();
		
		// Event specific information
		User author = event.getAuthor();
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
	
		String msg = message.getContentDisplay();	// get readable version of the message
		boolean isBot = author.isBot();
		
		System.out.println("Message received from: " + author.getName());
		
		if (event.isFromType(ChannelType.TEXT)) {
			Guild guild = event.getGuild();
			TextChannel textChannel = event.getTextChannel();
			Member member = event.getMember();
			
			if (msg.equals("!ping")) {
				channel.sendMessage("pong!").queue();
			}
			else if (msg.equals("!exit")) {
				if (!member.hasPermission(Permission.ADMINISTRATOR)) {
					channel.sendMessage("Sorry! You don't have the permission to do this!").queue();
					return;
				}
				channel.sendMessage("Exiting...").queue();
				System.exit(0);
			}
		}
	}

}
