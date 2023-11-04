package com.alchemist.service;

import java.awt.Color;

import com.alchemist.ArgParser;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * >about command listener. Sends information about this bot to discord.
 * @author chikuma
 *
 */
public class AboutListener extends ListenerAdapter implements Service {
	
	private ArgParser parser = null;
	private final MessageEmbed aboutMessage;
	
	public AboutListener() {		
		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setTitle("Holo Bot", "https://github.com/cst0601/Discord-MP-Bot")
				.setColor(Color.red)
				.setDescription("Discord MP-NeXT 1.5.5 \"Our Bright Parade\"")
				.addField("About Holo Bot", "A simple discord bot that help "
						+ "tracks events of hololive.\n"
						+ "Type `>man` to get manual of this bot.\n\n"
						+ "Created by Chikuma, 2020", false)
				.addBlankField(false)
				.addField("Changes of v1.5",
						"- Added みこメンバーシップ verification.\n"
						+ "- Bumped to JDA v5. Bot commands in threads now supported.\n"
						+ "- Removed some system command.\n"
						+ "- Upgraded Twitter API to v2.\n"
						+ "- HoloToolsApi deprecated, switch to other service.\n"
						+ "- Now stream notification displays start time!\n"
						+ "- Member only stream now shows additional info in notification.\n"
						+ "- Added auto twitter url translation.", false)
				.addField("Subscribe to さくらみこ and become a 35P!",
						"- [Youtube](https://www.youtube.com/channel/UC-hM6YJuNYVAmUWxeIr9FeA)\n"
						+ "- [Twitter](https://twitter.com/sakuramiko35)", false)
				.addField("Contribute to Holo Bot", "[Repo @ gitHub](https://github.com/cst0601/Discord-MP-Bot)", false)
				.addBlankField(false)
				.setFooter("35P Chikuma", "https://i.imgur.com/DOb1GZ1.png");
		aboutMessage = embedBuilder.build();
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message message = event.getMessage();
		MessageChannelUnion channel = event.getChannel();
	
		String msg = message.getContentDisplay();	// get readable version of the message
				
		parser = new ArgParser(msg);
		
		if (parser.getCommand().equals(">about")) {
			channel.sendMessageEmbeds(aboutMessage).queue();
		}
	}

	@Override
	public String getServiceManualName() {
		return "about";
	}

	@Override
	public String getServiceMan() {
		return
				"# NAME\n"
				+ "        about - About the bot!\n\n"
				+ "SYNOPSIS\n"
				+ "        about: Show messages about the bot.";
	}

}
