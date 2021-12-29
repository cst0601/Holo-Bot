package com.alchemist.service;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alchemist.ArgParser;


public class BonkListener extends ListenerAdapter implements Service {
	
	private Logger logger = LoggerFactory.getLogger(BonkListener.class);
	private final String BONK_EMOTE_ID[] = {
			"756142081864630343",
			"819050266078740480",
			"781204705765752882",
			"781204705572290621",
			"817816581416681503"};	// TODO: remove this literal constant
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
		
		String msg = message.getContentDisplay();
		
		if (event.isFromType(ChannelType.TEXT)) {
			ArgParser parser = new ArgParser(msg);
			
			if (parser.getCommand().equals(">bonk")) {
				
				try {
					List<Emote> emotes = getEmotes(message.getGuild());
					List<Member> mentionedMember = new LinkedList<>(message.getMentionedMembers());
					
					getTargetMessage(channel, message.getId(), mentionedMember, emotes);
					
				} catch (NoSuchElementException e) {
					logger.warn(String.format(
							"Requested from guild: %s, does not contain designated emote.",
							message.getGuild().getId()));
					channel.sendMessage("Error: This sever does not contain the designated bonk emote.").queue();
				}
				
			}
		}
	}
	
	private List<Emote> getEmotes(Guild guild) throws NoSuchElementException {
		List<Emote> emotes = new ArrayList<Emote>();
		for (String id: BONK_EMOTE_ID) {
			Emote emote = guild.getEmoteById(id);
			if (emote == null)
				throw new NoSuchElementException("");
			emotes.add(emote);
		}
		return emotes;
	}
	
	private void getTargetMessage(
			MessageChannel channel,
			String messageId,
			List<Member> targetMember,
			List<Emote> emotes)
	{	
		// this is a rest action that waits for completion, might make this method to thread
		MessageHistory history = MessageHistory.getHistoryBefore(channel, messageId)
				.limit(10).complete();
		for(Message message: history.getRetrievedHistory()) {
			try {
				int index = containsMember(targetMember, message.getAuthor());
				targetMember.remove(index);
				
				for (Emote emote: emotes)
					message.addReaction(emote).queue();
			} catch (NoSuchElementException e) { }
		}
	}
	
	/**
	 * Check if there are member with same ID
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	private int containsMember(List<Member> list, User target) throws NoSuchElementException {
		for (int i = 0; i < list.size(); ++i) {
			if (list.get(i).getId().equals(target.getId()))
					return i;
		}
		throw new NoSuchElementException("Member with ID " + target.getId() + " does not exist.");
	}

	@Override
	public String getServiceManualName() {
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
