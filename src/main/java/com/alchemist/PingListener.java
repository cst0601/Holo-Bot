package com.alchemist;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
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
		
		System.out.println("Message received from: " + author.getName());
		
		if (event.isFromType(ChannelType.TEXT)) {
			Guild guild = event.getGuild();
			TextChannel textChannel = event.getTextChannel();
			Member member = event.getMember();
			
			if (msg.equals(">ping")) {
				channel.sendMessage("pong!").queue();
			}
			
			else if (msg.equals(">man mp")) {
				EmbedBuilder embedBuilder;
				
				embedBuilder = new EmbedBuilder();
				embedBuilder.setTitle("Discord MP-NExT Manual", null);
				embedBuilder.setColor(Color.red);
				embedBuilder.setDescription("List of commands and usage of MP-NExT");
				embedBuilder.addField("Title of field", "Test of field (inline=false)", false);
				embedBuilder.addField("Title of field", "Test of field (inline=false)", false);
				embedBuilder.addBlankField(false);
				embedBuilder.addField("Title of field", "Test of field (inline=true)", true);
				embedBuilder.addField("Title of field", "Test of field (inline=true)", true);
				embedBuilder.setAuthor("MP NExT", null, "https://i.imgur.com/GndAbTC.png");
				embedBuilder.setFooter("35P Chikuma", "https://i.imgur.com/DOb1GZ1.png");
				
				channel.sendMessage(embedBuilder.build()).queue();
			}
			
			else if (msg.equals(">sudo exit")) {
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
