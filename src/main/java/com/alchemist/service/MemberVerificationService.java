package com.alchemist.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alchemist.ArgParser;
import com.alchemist.UserDB;
import com.alchemist.YoutubeApi;
import com.alchemist.exceptions.ArgumentParseException;
import com.alchemist.exceptions.EntryExistException;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class MemberVerificationService extends ListenerAdapter implements Service {

	private Logger logger;
	private UserDB userDb;
	private YoutubeApi api;
	private ArgParser parser;
	private String discordRoleId;
	private final static String CONFIG_FILE_PWD = "config/membership_verification.json";

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
		MessageChannelUnion channel = event.getChannel();
		Member member = event.getMember();

		String msg = message.getContentDisplay();

		parser = new ArgParser(msg);

		if (parser.getCommand().equals(">register")) {
			try {
				parser.parse();
			} catch (ArgumentParseException e) {
				e.printStackTrace();
				return;
			}
			
			if (parser.getCommandSize() < 2) {
				channel.sendMessage(new MessageCreateBuilder().addContent("Error: Usage: ")
						.addContent(MarkdownUtil.codeblock(">register <youtube_id>")).build()).queue();
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
			
			verifyMember(channel, event.getMember(), event.getGuild());
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
				handleAdminCommands(parser, channel, event.getGuild());
			}
		}
	}

	private JSONObject readConfig() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(CONFIG_FILE_PWD));
		scanner.useDelimiter("\\Z");
		JSONObject json = new JSONObject(scanner.next());
		scanner.close();

		return json;
	}
	
	private void handleAdminCommands(ArgParser parser, MessageChannelUnion channel, Guild guild) {
		if (parser.getCommandSize() > 2) {
			String commandName = parser.getCommand(1);
			if (commandName.equals("manual_verify")) {
				if (parser.getCommandSize() >= 3) {
					manualVerifyMember(parser.getCommand(2), channel, guild);
				}
				else {
					channel.sendMessage("Error: Command needs to contain target member discord id.").queue();
				}
			}
			else if (commandName.equals("manual_register")) {
				if (parser.getCommandSize() >= 4) {
					try {
						register(parser.getCommand(2), parser.getCommand(3));
						channel.sendMessage("Registered user: " + parser.getCommand(2) +
											" with youtube channel: " + parser.getCommand(3)).queue();
					} catch (EntryExistException e) {
						channel.sendMessage(" Discord ID或是Youtube ID已經註冊過").queue();
					}
				}
				else {
					channel.sendMessage("Error: Command requires member discord ID and Youtube channel ID.").queue();
				}
			}
			else if (commandName.equals("remove")) {
				if (parser.getCommandSize() >= 3) {
					long removedEntityCount = userDb.removeUserByDcId(parser.getCommand(2));
					channel.sendMessage("Removed " + removedEntityCount + " entries related to disocrd ID: " + parser.getCommand(2)).queue();
				}
				else {
					channel.sendMessage("Error: Command requires member discord ID.").queue();
				}
			}
			else if (commandName.equals("update_state")) {
				channel.sendMessage("Not yet implemeted.").queue();
				// next time
				// ^^^^^^^^^ What is this suppose to be???
			}
			else if (commandName.equals("update_role")) {
				if (parser.getCommandSize() >= 3) {
					try {
						updateConfigFile(parser.getCommand(2));
						discordRoleId = parser.getCommand(2);
						channel.sendMessage("Updated role id to " + parser.getCommand(2)).queue();
						logger.info("Member role id updated to " + parser.getCommand(2));
					} catch (Exception e) {
						channel.sendMessage("Error: config file not found.").queue();
						logger.warn("Failed to update config file, stack trace as follow:");
						logger.warn(e.toString());
					}
				}
				else {
					channel.sendMessage("Error: Command requires new role ID.").queue();
				}
			}
		}
	}
	
	private void manualVerifyMember(String dcId, MessageChannelUnion channel, Guild guild) {
		Member member = guild.retrieveMemberById(dcId).complete();
		String youtubeId = userDb.getYoutubeIdByUserId(member.getId());
		
		if (youtubeId == null) {
			channel.sendMessage("Error: user needs to be registered before verification.").queue();
			return;
		}
		
		String expireTime = userDb.verifyUser(member.getId());
		Role role = guild.getRoleById(discordRoleId);
		guild.addRoleToMember(member, role).queue();
		channel.sendMessage(new MessageCreateBuilder()
				.addContent(member.getAsMention() + " 會員認證成功！下次認證時間為：")
				.addContent(MarkdownUtil.codeblock(expireTime))
				.build()).queue();
	}

	private void verifyMember(MessageChannelUnion channel, Member member, Guild guild) {
		String youtubeId = userDb.getYoutubeIdByUserId(member.getId());
		logger.info("User " + member.getId() + " attempted to verify with yt_id " + youtubeId);
		if (youtubeId != null) {
			if (api.verify(youtubeId)) {
				String expireTime = userDb.verifyUser(member.getId());
				Role role = guild.getRoleById(discordRoleId);
				guild.addRoleToMember(member, role).queue();
				channel.sendMessage(new MessageCreateBuilder()
						.addContent(member.getAsMention() + " 會員認證成功！下次認證時間為：")
						.addContent(MarkdownUtil.codeblock(expireTime))
						.build()).queue();
			} else {
				channel.sendMessage(new MessageCreateBuilder()
						.addContent(member.getAsMention() + " 認證失敗！\n")
						.addContent("請確認是否在みこ的FreeChat中送出訊息，或是等數分鐘後使用")
						.addContent(MarkdownUtil.codeblock(">verify_member"))
						.addContent("重試。\n如果依然無法完成認證，請聯絡管理員協助處理。")
						.build()).queue();
			}
		} else {
			channel.sendMessage(new MessageCreateBuilder().addContent(member.getAsMention() + " 請先使用")
					.addContent(MarkdownUtil.codeblock(">register"))
					.addContent("綁定Youtube ID以及Discord ID").build())
					.queue();
		}
	}
	
	// duplication code in com.alchemist.TwitterBroadcastConfig
	// update config file member role id
	private void updateConfigFile(String newRoleId) throws JSONException, IOException {
		JSONObject config = readConfig();
		config.put("discord_role_id", newRoleId);
		Writer writer = config.write(
			new FileWriter(CONFIG_FILE_PWD, Charset.forName("UTF-8")),
			4, 0);
		writer.close();
	}

	@Override
	public String getServiceManualName() {
		return "member_verify";
	}

	@Override
	public String getServiceMan() {
		return "NAME\n" +
				"        member_verify - さくらみこ Youtube Channel Membership Verificaiton\n\n"
				+ "SYNOPSIS\n"
				+ "        register <user_youtube_channel_id>\n"
				+ "        verify_member\n\n"
				+ "DESCRIPTION\n"
				+ "        To get the membership role in this discord server, you will need to complete the verification process.\n"
				+ "        First do >register <user_youtube_channel_id> to bind your discord account id to your youtube channel id.\n"
				+ "        Note that once your account is binded, you could not change it without help from admin.\n"
				+ "        Do >verify_member to verify your membership via miko's free chat.";
	}
}
