package com.alchemist.service;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

import com.alchemist.ArgParser;

public class BonkListener extends ListenerAdapter implements Service {
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
		
		String msg = message.getContentDisplay();
		
		if (event.isFromType(ChannelType.TEXT)) {
			ArgParser parser = new ArgParser(msg);
			
			if (parser.getCommand().equals(">bonk")) {
				List<Member> mentionedMember = message.getMentionedMembers();
				if (mentionedMember.size() > 0) {
					
					EmbedBuilder embedBuilder = new EmbedBuilder()
							.setImage("https://i.imgur.com/lmrPJjL.png");
					
					MessageBuilder messageBuilder = new MessageBuilder(embedBuilder);
					
					messageBuilder.append(":white_check_mark: ");
					for (Member member: mentionedMember)
						messageBuilder.append(member.getAsMention() + " ");
					messageBuilder.append("is bonked");
					
					channel.sendMessage(messageBuilder.build()).queue();
				}
			}
		}
	}

	@Override
	public String getServiceName() {
		return "bonk";
	}

	@Override
	public String getServiceMan() {
		return 
			"# NAME\n"
			+ "    bonk - bonk a member!\n\n"
			+ "# SYNOPSIS\n"
			+ "    bonk @member\n";
	}

}
