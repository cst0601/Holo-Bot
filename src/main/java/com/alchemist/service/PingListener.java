package com.alchemist.service;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alchemist.ArgParser;
import com.alchemist.Config;
import com.alchemist.exceptions.ArgumentParseException;

/**
 * Listener of some basic commands: ping, sudo exit, about
 * @author greg8
 *
 */
public class PingListener extends ListenerAdapter implements Service {

	private ArgParser parser = null;

	public PingListener() {
		logger = LoggerFactory.getLogger(PingListener.class);
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message message = event.getMessage();
		MessageChannelUnion channel = event.getChannel();

		String msg = message.getContentDisplay();	// get readable version of the message

		Member member = event.getMember();
		parser = new ArgParser(msg);

		// only parse if command comes with >ping or >sudo
		// BTW this code is fugly :((
		if (parser.getCommand().equals(">ping") || parser.getCommand().equals(">sudo")) {
			try {
				parser.parse();
			} catch (ArgumentParseException e) {
				e.printStackTrace();
				return;
			}
		}

		if (parser.getCommand().equals(">ping")) {
			ping(channel);
		}

		else if (parser.getCommandSize() > 1) {
			if (parser.getCommand().equals(">sudo") && parser.getCommand(1).equals("exit")) {
				if (!Config.getConfig().isAdmin(member.getIdLong())) {
					channel.sendMessage("Sorry! You don't have the permission to do this!").queue();
				}
				else {
					channel.sendMessage("Exiting...").queue();
					logger.info("Received exit command, terminating...");

					for (Object listener: event.getJDA().getRegisteredListeners()) {
						((Service)listener).terminate();
					}

					System.exit(0);
				}
			}
		}
	}

	private void ping(MessageChannelUnion channel) {
		if (parser.getCommandSize() > 1) {
			if (parser.getCommand(1).equals("sl")) {
				channel.sendMessage(new MessageCreateBuilder()
					.addContent(MarkdownUtil.bold("YOU HAVE PINGED A STEAM LOCOMOTIVE !!!"))
					.addContent(MarkdownUtil.codeblock(
							"						 (	) (@@) ( )	(@)  ()    @@	 O	   @	 O	   @	  O\n" +
							"				  (@@@)  \n" +
							"			   (	)\n" +
							"			 (@@@@)\n" +
							"		   (   )\n" +
							"	   ====		   ________				   ___________\n" +
							"  _D _|  |_______/		   \\__I_I_____===__|_________|\n" +
							"	|(_)---  |	 H\\________/ |   |		   =|___ ___|	   _________________\n" +
							"	/	  |  |	 H	|  |	 |	 |		   ||_| |_||	 _|				   \\_____A\n" +
							"  |	  |  |	 H	|__--------------------| [___] |   =|						 |\n" +
							"  | ________|___H__/__|_____/[][]~\\_______|		|	-|						  |\n" +
							"  |/ |   |-----------I_____I [][] []  D   |=======|____|________________________|_\n" +
							"__/ =| o |=-~O=====O=====O=====O\\ ____Y___________|__|__________________________|_\n" +
							" |/-=|___|=	||	  ||	||	  |_____/~\\___/		  |_D__D__D_|  |_D__D__D_|\n" +
							"  \\_/		 \\__/	\\__/  \\__/  \\__/		 \\_/				\\_/   \\_/    \\_/   \\_/\n"
							+ "*Full screen to see better :)*"))
					.build()).queue();
			}
			else if (parser.getCommand(1).equals("ls")) {
				channel.sendMessage(new MessageCreateBuilder()
					.addContent(MarkdownUtil.bold("P\nO\nN\nG"))
					.build()).queue();
			}
		}
		else
			channel.sendMessage("pong!").queue();

	}

	@Override
	public String getServiceManualName() {
		// TODO Auto-generated method stub
		return "ping";
	}

	@Override
	public String getServiceMan() {
		return
			"NAME\n"
			+ "		   ping - pong!\n\n"
			+ "SYNOPSIS\n"
			+ "		   ping [options]\n\n"
			+ "OPTIONS\n"
			+ "		   ls: A listed version of ping\n"
			+ "		   sl: Steam Locomotive";
	}

	private Logger logger;
}
