package com.alchemist.service;

import java.awt.Color;
import java.util.List;

import com.alchemist.ArgParser;
import com.alchemist.exceptions.ArgumentParseException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ManualListener extends ListenerAdapter implements Service {
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		JDA jda = event.getJDA();
		List<Object> listeners = jda.getRegisteredListeners();
		
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
	
		String msg = message.getContentDisplay();	// get readable version of the message
		
		if (event.isFromType(ChannelType.TEXT) && !event.getAuthor().isBot()) {
			
			ArgParser parser = new ArgParser(msg);
			
			if (parser.getCommand().equals(">man")) {
				
				try {
					parser.parse();
				} catch (ArgumentParseException e1) {
					channel.sendMessage("Command format error.\n" + e1.getMessage()).queue();
					return;
				}
				
				if (parser.getCommandSize() < 2) {
					channel.sendMessage(new EmbedBuilder()
						.setTitle("Manual (help)")
						.setColor(Color.red)
						.setDescription("What manual page do you want?")
						.addField("Usage", ">man <man_page>", false)
						.addField("List of services", getManualList(listeners), false)
						.build()).queue();
					return;
				}
				
				for (int i = 0; i < listeners.size(); ++i) {
					if (listeners.get(i) instanceof Service)
						if (parser.getCommand(1).equals(((Service) listeners.get(i)).getServiceName())) {
							channel.sendMessage(new MessageBuilder()
								.appendCodeBlock(
									((Service) listeners.get(i)).getServiceMan(),
									"md")
								.build()).queue();
						}
				}
			}
		}
	}

	@Override
	public String getServiceName() {
		return "man";
	}

	@Override
	public String getServiceMan() {
		return "# NAME\n"
				+ "    man - Manual, read the doc! :)\n\n"
				+ "# SYNOPSIS\n"
				+ "    * man: Get some informations about manual\n"
				+ "    * man [manual_page]: Get the manual page of a specific service";
	}
	
	/**
	 * Get all the names of services available and form them in to a nice
	 * looking format
	 * @param services
	 * @return String contains list of service names
	 */
	public String getManualList(List<Object> services) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < services.size(); ++i) {
			if (services.get(i) instanceof Service) {	// preclude non service listeners
				buffer.append(" - ");
				buffer.append(((Service)services.get(i)).getServiceName());
				buffer.append("\n");
			}
		}
		return buffer.toString();
	}
}
