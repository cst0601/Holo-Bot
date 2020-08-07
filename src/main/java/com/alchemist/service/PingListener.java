package com.alchemist.service;

import java.awt.Color;
import java.util.Stack;
import java.util.Random;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Listener of some basic commands: ping, sudo exit, about
 * @author greg8
 *
 */
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
		
		if (event.isFromType(ChannelType.TEXT)) {
			Guild guild = event.getGuild();
			TextChannel textChannel = event.getTextChannel();
			Member member = event.getMember();
			
			String [] command = CommandUtil.parseCommand(msg);
			
			if (command[0].equals(">ping")) {
				channel.sendMessage("pong!").queue();
			}
			
			else if (command[0].equals(">sudo") && command[1].equals("exit")) {
				if (!member.hasPermission(Permission.ADMINISTRATOR)) {
					channel.sendMessage("Sorry! You don't have the permission to do this!").queue();
					return;
				}
				channel.sendMessage("Exiting...").queue();
				System.out.println("Received exit command, terminating...");
				System.exit(0);
			}
		}
	}

}
