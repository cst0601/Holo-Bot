package com.alchemist.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alchemist.ArgParser;
import com.alchemist.UserDB;
import com.alchemist.YoutubeApi;
import com.alchemist.exceptions.ArgumentParseException;
import com.alchemist.exceptions.EntryExistException;
import com.alchemist.jsonResponse.youtube.LiveStreamChatMessageList;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberVerificationService extends ListenerAdapter implements Service {
	
	private Logger logger;
	private UserDB userDb;
	private YoutubeApi api;
	private ArgParser parser;
	
	public MemberVerificationService () {
		logger = LoggerFactory.getLogger(MemberVerificationService.class);
		userDb = new UserDB();
		
		try {
			api = new YoutubeApi();
		} catch (Exception e) {
			logger.warn("Failed to read credentials and build youtube api.");
			e.printStackTrace();
		}
	}
	
	public void register(String discordId, String youtubeId) throws EntryExistException {
		userDb.register(discordId, youtubeId);
	}
	
	public void verifyMember(MessageChannel channel, User author, Guild guild) {
		String youtubeId = userDb.getYoutubeIdByUserId(author.getId());
		if (youtubeId != null) {
			if (api.verify(youtubeId)) {
				// success
				userDb.verifyUser(author.getId());
				Role role = guild.getRoleById("885266762915004416");
				guild.addRoleToMember(guild.getMember(author), role).queue();
				channel.sendMessage(author.getAsMention() + " 認證成功！").queue();
			}
			else {
				// failed try again five min later
				channel.sendMessage("failed check again later").queue();
			}
		}
		else {
			channel.sendMessage(
					new MessageBuilder()
					.append(author.getAsMention() + " 請先使用")
					.append(">register", MessageBuilder.Formatting.BLOCK)
					.append("綁定Youtube ID以及Discord ID").build()).queue();
		}
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
	
		String msg = message.getContentDisplay();	// get readable version of the message
		
		if (event.isFromType(ChannelType.TEXT)) {
			parser = new ArgParser(msg);
			
			if (parser.getCommand().equals(">register")) {
				
				try {
					parser.parse();
				} catch (ArgumentParseException e) {
					e.printStackTrace();
					return;
				}
				
				if (parser.getCommandSize() < 1) {
					channel.sendMessage(new MessageBuilder()
							.append("Error: Usage: ")
							.append(">register <youtube_id>", MessageBuilder.Formatting.BLOCK)
							.build()).queue();
				}
				else {
					try {
						register(message.getAuthor().getId(), parser.getCommand(1));
						channel.sendMessage(message.getAuthor().getAsMention() + " 完成綁定！\n"
								+ "請到みこ的FreeChat去留言「にゃっはろ～」後使用>verify_member來完成認證").queue();
					} catch (EntryExistException e) {
						channel.sendMessage(message.getAuthor().getAsMention() + " Discord ID或是Youtube ID已經註冊過").queue();
					}
				}
			}
			else if (parser.getCommand().equals(">verify_member")) {
				Role role = event.getGuild().getRoleById("885266762915004416");
				verifyMember(channel, message.getAuthor(), event.getGuild());
			}
		}
	}
	
	@Override
	public String getServiceManualName() {
		return "member_verify";
	}

	@Override
	public String getServiceMan() {
		return
				"# NAME\n"
				+ "    member_verify - About the bot!\n\n";
	}
}
