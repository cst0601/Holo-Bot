package com.alchemist.service;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alchemist.ArgParser;
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
		MessageChannel channel = event.getChannel();
	
		String msg = message.getContentDisplay();	// get readable version of the message
		
		if (event.isFromType(ChannelType.TEXT)) {
			
			Member member = event.getMember();
			parser = new ArgParser(msg);
			
			// :((
			if (parser.getCommand().equals(">ping") || parser.getCommand().equals(">sudo"))
				try {
					parser.parse();
				} catch (ArgumentParseException e) {
					e.printStackTrace();
					return;
				}
			
			if (parser.getCommand().equals(">ping")) {
				ping(channel);
			}
			
			else if (parser.getCommandSize() > 1) 
				if (parser.getCommand().equals(">sudo") && parser.getCommand(1).equals("exit")) {
					if (!member.hasPermission(Permission.ADMINISTRATOR)) {
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
				else if (parser.getCommand().equals(">sudo") && parser.getCommand(1).equals("service")) {
					if (!member.hasPermission(Permission.ADMINISTRATOR)) {
						channel.sendMessage("Sorry! You don't have the permission to do this!").queue();
					}
					else { processServiceCommmand(event, parser, channel); }
				}
		}
	}
	
	private void processServiceCommmand(Event event, ArgParser parser, MessageChannel channel) {
		if (parser.getCommand(2).equals("list")) {
			channel.sendMessage(getServiceList(event)).queue();
		}
		if (parser.getCommandSize() > 3) {
			String serviceName = parser.getCommand(3);
			if (parser.getCommand(2).equals("start")) {
				if (isServiceRunning(parser.getCommand(3), event)) {
					channel.sendMessage("Service " + parser.getCommand(3) + " is already running.").queue();
				}
				else {
					logger.info("Starting service " + serviceName);
					
					try { 
						event.getJDA().addEventListener(createEventListenerByName(serviceName)); 
						logger.info("Service " + serviceName + " started.");
						channel.sendMessage("Service " + serviceName + " started.").queue();
					}
					catch(IllegalArgumentException e) {
						logger.warn("Cannot start service " + serviceName +
								    ". Either service does not exist or was not made available.");
						channel.sendMessage(
								"Cannot start service " + serviceName +
							    ". Either service does not exist or was not made available.").queue();
					}
				}
			}
			else if (parser.getCommand(2).equals("stop")) {
				try {
					event.getJDA().removeEventListener(
							getRunningEventListenerByName(serviceName, event.getJDA()));
					logger.info("Service " + serviceName + " stopped.");
					channel.sendMessage("Service " + serviceName + " stopped.").queue();
				} catch (IllegalArgumentException e) {
					logger.warn("Failed to stop " + serviceName + ". Service is not running in JDA.");
					channel.sendMessage("Failed to stop " + serviceName + ". Service is not running in JDA.").queue();
				}
			}
		}
	}
	
	private EventListener createEventListenerByName(String name) {
		switch (name) {
		case "com.alchemist.service.AboutListener":
			return new AboutListener();
		case "com.alchemist.service.BonkListener":
			return new BonkListener();
		case "com.alchemist.service.CountDownListener":
			return new CountDownListener();
		case "com.alchemist.service.RollListener":
			return new RollListener();
		case "com.alchemist.service.StreamNotifierService":
			return new StreamNotifierService();
		case "com.alchemist.service.TwitterBroadcastService":
			return new TwitterBroadcastService();
		case "com.alchemist.service.VtuberListener":
			return new VtubeListener();
		default:
			return null;
		}
	}
	
	private EventListener getRunningEventListenerByName(String name, JDA jda) {
		for (Service service: getRunningServices(jda)) {
			if (service.getServiceName().equals(name))
				return (EventListener) service;
		}
		return null;
	}
	
	private boolean isServiceRunning(String serviceName, Event event) {
		for (Service service: getRunningServices(event.getJDA())) {
			if (service.getServiceName().equals(serviceName)) {
				return true;
			}
		}
		return false;
	}
	
	private MessageEmbed getServiceList(Event event) {
		EmbedBuilder builder = new EmbedBuilder()
				.setTitle(">sudo service list")
				.setColor(Color.red);
		String serviceNames = "";
		for (Service service: getRunningServices(event.getJDA())) {
			serviceNames += " - " + service.getServiceName() + "\n";
		}
		builder.addField("Services", serviceNames, false);
		
		return builder.build();
	}
	
	@SuppressWarnings("unchecked")
	private List<Service> getRunningServices(JDA jda) {
		return (List<Service>)(Object)jda.getRegisteredListeners();
	}
	
	private void ping(MessageChannel channel) {
		if (parser.getCommandSize() > 1) {
			if (parser.getCommand(1).equals("sl")) {
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
			}
			else if (parser.getCommand(1).equals("ls")) {
				channel.sendMessage(new MessageBuilder()
					.append("P\nO\nN\nG", MessageBuilder.Formatting.BOLD)
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
			"# NAME\n"
			+ "    ping - pong!\n\n"
			+ "# SYNOPSIS\n"
			+ "    ping [ls]\n\n"
			+ "# COMMANDS\n"
			+ "    * ls: A listed version of ping\n";
	}

	private Logger logger;
}
