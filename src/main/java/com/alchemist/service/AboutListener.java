package com.alchemist.service;

import java.awt.Color;

import com.alchemist.ArgParser;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AboutListener extends ListenerAdapter implements Service {
	
	private ArgParser parser = null;
	private final MessageEmbed aboutMessage;
	
	public AboutListener() {
		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setTitle("Holo Bot", "https://github.com/cst0601/Discord-MP-Bot")
				.setColor(Color.red)
				.setDescription("Discord MP-NeXT 1.4.3 \"花月ノ夢\"")
				.addField("About Holo Bot", "A simple discord bot that help "
						+ "tracks events of hololive.\n"
						+ "Type `>man` to get manual of this bot.\n\n"
						+ "Created by Chikuma, 2020", false)
				.addBlankField(false)
				.addField("Changes of v1.4.3",
						" - Update `>miko` to 2022 birthday!\n"
						+ " - Add more emotes when issue `>bonk` command.\n"
						+ " - みこち！三周年おめでとうございます！！！\n", false)
				.addField("Subscribe to さくらみこ and become a 35P!",
						"[Youtube](https://www.youtube.com/channel/UC-hM6YJuNYVAmUWxeIr9FeA)\n"
						+ "[Twitter](https://twitter.com/sakuramiko35)", false)
				.addField("Contribute to Holo Bot", "[Repo @ gitHub](https://github.com/cst0601/Discord-MP-Bot)", false)
				.addBlankField(false)
				.setFooter("35P Chikuma", "https://i.imgur.com/DOb1GZ1.png");
		aboutMessage = embedBuilder.build();
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
	
		String msg = message.getContentDisplay();	// get readable version of the message
		
		if (event.isFromType(ChannelType.TEXT)) {
			parser = new ArgParser(msg);
			
			if (parser.getCommand().equals(">about")) {
				channel.sendMessage(aboutMessage).queue();
			}
		}
	}

	@Override
	public String getServiceName() {
		return "about";
	}

	@Override
	public String getServiceMan() {
		return
				"# NAME\n"
				+ "    about - About the bot!\n\n";
	}

}
