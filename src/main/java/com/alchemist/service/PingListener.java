package com.alchemist.service;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.logging.Logger;

import com.alchemist.TwitterBroadcaster;

/**
 * Listener of some basic commands: ping, sudo exit, about
 * @author greg8
 *
 */
public class PingListener extends ListenerAdapter implements Service {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		//JDA jda = event.getJDA();
		
		// Event specific information
		//User author = event.getAuthor();
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
	
		String msg = message.getContentDisplay();	// get readable version of the message
		//boolean isBot = author.isBot();
		
		if (event.isFromType(ChannelType.TEXT)) {
			//Guild guild = event.getGuild();
			//TextChannel textChannel = event.getTextChannel();
			Member member = event.getMember();
			
			String [] command = CommandUtil.parseCommand(msg);
			
			if (command[0].equals(">ping")) {
				if (command.length > 1) {
					if (command[1].equals("sl")) {
						channel.sendMessage(new MessageBuilder()
							.append("STEAM LOCOMOTIVE !!!", MessageBuilder.Formatting.BOLD)
							.appendCodeBlock(
									"                        (  ) (@@) ( )  (@)  ()    @@    O     @     O     @      O\n" + 
									"                 (@@@)  \n" + 
									"              (    )\n" + 
									"            (@@@@)\n" + 
									"          (   )\n" + 
									"      ====        ________                ___________\n" + 
									"  _D _|  |_______/        \\__I_I_____===__|_________|\n" + 
									"   |(_)---  |   H\\________/ |   |        =|___ ___|      _________________\n" + 
									"   /     |  |   H  |  |     |   |         ||_| |_||     _|                \\_____A\n" + 
									"  |      |  |   H  |__--------------------| [___] |   =|                        |\n" + 
									"  | ________|___H__/__|_____/[][]~\\_______|       |   -|                        |\n" + 
									"  |/ |   |-----------I_____I [][] []  D   |=======|____|________________________|_\n" + 
									"__/ =| o |=-~O=====O=====O=====O\\ ____Y___________|__|__________________________|_\n" + 
									" |/-=|___|=    ||    ||    ||    |_____/~\\___/          |_D__D__D_|  |_D__D__D_|\n" + 
									"  \\_/      \\__/  \\__/  \\__/  \\__/      \\_/               \\_/   \\_/    \\_/   \\_/\n"
									+ "*Full screen to see better :)*",
									null)
							.build()).queue();
						return;
					}
					else if (command[1].equals("ls")) {
						channel.sendMessage(new MessageBuilder()
							.append("P\nO\nN\nG", MessageBuilder.Formatting.BOLD)
							.build()).queue();
						return;
					}
				}
				channel.sendMessage("pong!").queue();
			}
			
			else if (command[0].equals(">sudo") && command[1].equals("exit")) {
				if (!member.hasPermission(Permission.ADMINISTRATOR)) {
					channel.sendMessage("Sorry! You don't have the permission to do this!").queue();
					return;
				}
				channel.sendMessage("Exiting...").queue();
				Logger.getLogger(PingListener.class.getName()).info("Received exit command, terminating...");
				
				for (Object listener: event.getJDA().getRegisteredListeners()) {
					if (listener instanceof TwitterBroadcaster) {
						((TwitterBroadcaster)listener).terminate();
					}
				}

				System.exit(0);
			}
		}
	}

	@Override
	public String getServiceName() {
		// TODO Auto-generated method stub
		return "ping";
	}

	@Override
	public String getServiceMan() {
		return
			"# NAME\n"
			+ "    ping - pong!\n\n"
			+ "# SYNOPSIS\n"
			+ "    ping [ls]\n\n"
			+ "# COMMANDS\n"
			+ "    * ls: A listed version of ping\n";
	}

}
