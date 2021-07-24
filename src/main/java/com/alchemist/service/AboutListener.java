package com.alchemist.service;

import java.awt.Color;

import com.alchemist.ArgParser;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AboutListener extends ListenerAdapter implements Service {
	
	private ArgParser parser = null;
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
	
		String msg = message.getContentDisplay();	// get readable version of the message
		
		if (event.isFromType(ChannelType.TEXT)) {
			parser = new ArgParser(msg);
			
			if (parser.getCommand().equals(">about")) {
				EmbedBuilder embedBuilder = new EmbedBuilder()
						.setTitle("Holo Bot", "https://github.com/cst0601/Discord-MP-Bot")
						.setColor(Color.red)
						.setDescription("Discord MP-NeXT 1.4.2b \"しけ村の魔王！\"")
						.addField("About Holo Bot", "A simple discord bot that help "
								+ "tracks events of hololive.\n"
								+ "Type `>man` to get manual of this bot.\n\n"
								+ "Created by Chikuma, 2020", false)
						.addField("Changes of v1.4.2b",
								" - Probably fixed false stream notification when scheduled time changed.\n"
								+ " - Added debug commands for su\n"
								+ " - Some issues have been identified but not fixed.\n"
								+ " - にゃ...助けて！！！！！Benさん怖い！", false)
						.addBlankField(false)
						.addField("Subscribe to さくらみこ and become a 35P!",
								"[Youtube](https://www.youtube.com/channel/UC-hM6YJuNYVAmUWxeIr9FeA)\n"
								+ "[Twitter](https://twitter.com/sakuramiko35)", false)
						.addField("Contribute to Holo Bot", "[Repo @ gitHub](https://github.com/cst0601/Discord-MP-Bot)", false)
						.addBlankField(false)
						.setFooter("35P Chikuma", "https://i.imgur.com/DOb1GZ1.png");
					channel.sendMessage(embedBuilder.build()).queue();
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
