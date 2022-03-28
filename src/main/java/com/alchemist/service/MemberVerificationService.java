package com.alchemist.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alchemist.ArgParser;
import com.alchemist.UserDB;
import com.alchemist.YoutubeApi;
import com.alchemist.exceptions.ArgumentParseException;
import com.alchemist.exceptions.EntryExistException;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
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
	private String discordRoleId;

	public MemberVerificationService() {
		logger = LoggerFactory.getLogger(MemberVerificationService.class);

		JSONObject config;
		try {
			config = readConfig();
		} catch (FileNotFoundException e1) {
			logger.error("Failed to read config file for memberVerificaitonService.");
			e1.printStackTrace();
			return;
		}

		discordRoleId = config.getString("discord_role_id");
		userDb = new UserDB(config.getString("db_connection"), config.getString("db_name"));

		try {
			api = new YoutubeApi(config.getString("yt_live_stream_chat_id"));
		} catch (Exception e) {
			logger.warn("Failed to read credentials and build youtube api.");
			e.printStackTrace();
		}
	}

	public void register(String discordId, String youtubeId) throws EntryExistException {
		userDb.register(discordId, youtubeId);
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
		Member member = event.getMember();

		String msg = message.getContentDisplay();

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
					channel.sendMessage(new MessageBuilder().append("Error: Usage: ")
							.append(">register <youtube_id>", MessageBuilder.Formatting.BLOCK).build()).queue();
				} else {
					try {
						register(message.getAuthor().getId(), parser.getCommand(1));
						channel.sendMessage(message.getAuthor().getAsMention() + " 完成綁定！\n"
								+ "請到みこ的FreeChat去留言「にゃっはろ～」後使用>verify_member來完成認證").queue();
					} catch (EntryExistException e) {
						channel.sendMessage(message.getAuthor().getAsMention() + " Discord ID或是Youtube ID已經註冊過")
								.queue();
					}
				}
			}
			else if (parser.getCommand().equals(">verify_member")) {
				try {
					parser.parse();
				} catch (ArgumentParseException e) {
					e.printStackTrace();
					return;
				}
				
				verifyMember(channel, message.getAuthor(), event.getMember(), event.getGuild());
			}
			else if (parser.getCommand().equals(">sudo")) {
				try {
					parser.parse();
				} catch (ArgumentParseException e) {
					e.printStackTrace();
					return;
				}
				
				if (!member.hasPermission(Permission.ADMINISTRATOR)) {
					channel.sendMessage("Sorry! You don't have the permission to do this!").queue();
				}
				else {
					// admin command goes here
				}
			}
		}
	}

	private JSONObject readConfig() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("config/membership_verification.json"));
		scanner.useDelimiter("\\Z");
		JSONObject json = new JSONObject(scanner.next());
		scanner.close();

		return json;
	}

	private void verifyMember(MessageChannel channel, User author, Member member, Guild guild) {
		String youtubeId = userDb.getYoutubeIdByUserId(author.getId());
		if (youtubeId != null) {
			if (api.verify(youtubeId)) {
				String expireTime = userDb.verifyUser(author.getId());
				Role role = guild.getRoleById(discordRoleId);
				guild.addRoleToMember(member, role).queue();
				channel.sendMessage(new MessageBuilder()
						.append(author.getAsMention() + " 會員認證成功！下次認證時間為：")
						.append(expireTime, MessageBuilder.Formatting.BLOCK)
						.build()).queue();
			} else {
				channel.sendMessage(new MessageBuilder()
						.append(author.getAsMention() + " 認證失敗！\n")
						.append("請確認是否在みこ的FreeChat中送出訊息，或是等數分鐘後使用")
						.append(">verify_member", MessageBuilder.Formatting.BLOCK)
						.append("重試。\n如果依然無法完成認證，請聯絡管理員協助處理。")
						.build()).queue();
			}
		} else {
			channel.sendMessage(new MessageBuilder().append(author.getAsMention() + " 請先使用")
					.append(">register", MessageBuilder.Formatting.BLOCK)
					.append("綁定Youtube ID以及Discord ID").build())
					.queue();
		}
	}

	@Override
	public String getServiceManualName() {
		return "member_verify";
	}

	@Override
	public String getServiceMan() {
		return "# NAME\n" +
				"    member_verify - さくらみこ Youtube Channel Membership Verificaiton\n\n"
				+ "# SYNOPSIS\n"
				+ "    register <user_youtube_channel_id>\n"
				+ "    verify_member\n\n"
				+ "# DESCRIPTION\n"
				+ "    To get the membership role in this discord server, you will need to complete the verification process.\n"
				+ "    First do >register <user_youtube_channel_id> to bind your discord account id to your youtube channel id.\n"
				+ "    Note that once your account is binded, you could not change it without help from admin.\n"
				+ "    Do >verify_member to verify your membership via miko's free chat.";
	}
}
