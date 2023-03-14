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
				.setDescription("Discord MP-NeXT 1.5.2 alpha 1 \"Our Bright Parade\"")
				.addField("About Holo Bot", "A simple discord bot that help "
						+ "tracks events of hololive.\n"
						+ "Type `>man` to get manual of this bot.\n\n"
						+ "Created by Chikuma, 2020", false)
				.addBlankField(false)
				.addField("Changes of v1.5.0",
						" - Added みこメンバーシップ verification.\n"
						+ " - Removed some system command.\n"
						+ " - Updates for issuing command in discord threads be will postpond\n"
						+ "   until JDA v5 beta released.\n"
						+ " - みっこより！ hope to see more collabs in the near future :D\n"
						, false)
				.addField("1.5.0 alpha 2 patch notes",
						" - Fixed repeat stream notification issues caused by"
						+ " HoloToolsApi not updating the state of streams.", false)
				.addField("1.5.1 patch notes",
						 " - Added tweet filter function.\n"
						 + " - Fixed member verification function.\n"
						 + " - みこめっと！ ( ⸝⸝•ᴗ•⸝⸝ )", false)
				.addField("1.5.2 patch notes",
						" - Upgrade Twitter API to v2.\n"
						+ " - HoloToolsApi deprecated, switch to other service.", false)
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
