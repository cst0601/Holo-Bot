package com.alchemist.service;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alchemist.ArgParser;
import com.alchemist.StreamNotifierRunner;
import com.alchemist.exceptions.ArgumentParseException;

public class StreamNotifierService extends ListenerAdapter implements Service {
	
	public StreamNotifierService() {
		logger = LoggerFactory.getLogger(StreamNotifierService.class);
	}
	
	@Override
	public void onReady(ReadyEvent event) {
		try {
			streamNotifierRunner = new StreamNotifierRunner(event.getJDA(), serviceMessageBox);
			streamNotifierRunner.start();
			logger.info("StreamNotifierService ready!");
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("Error occured when reading config file, skip runner build.");
		}
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		MessageChannelUnion channel = event.getChannel();
		String msg = event.getMessage().getContentDisplay();	// get readable version of the message

		Member member = event.getMember();
		parser = new ArgParser(msg);
		
		if (parser.getCommand().equals(">sudo") && member.hasPermission(Permission.ADMINISTRATOR)) {
			try { parser.parse(); } 
			catch (ArgumentParseException e1) { e1.printStackTrace(); }
			
			if (parser.getCommandSize() > 1) {
				if (parser.getCommand().equals(">sudo") &&
					parser.getCommand(1).equals("stream_flush")) {
				
					try {
						streamNotifierRunner.sendMessage("flush");
						streamNotifierRunner.interrupt();
						channel.sendMessage("upcoming stream cache cleared.").queue();
					} catch (InterruptedException e) {
						logger.warn("Interrupt occured when flushing stream cache.");
						e.printStackTrace();
					}
				}
				else if (parser.getCommand().equals(">sudo") &&
					parser.getCommand(1).equals("stream_list")) {
				
					try {
						streamNotifierRunner.sendMessage("list");
						streamNotifierRunner.interrupt();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		}
	}
	
	public void terminate() {
		// if runner did not build on ready
		if (streamNotifierRunner == null)
			return;
		
		logger.info("Terminating stream notifier...");
		try {
			streamNotifierRunner.sendMessage("stop");
			streamNotifierRunner.interrupt();
			streamNotifierRunner.join();
		} catch (InterruptedException e) {
			logger.warn("Interrupt occured when stopping StreamNotifierRunner. Failed to terminate stream notifier.");
			e.printStackTrace();
		}
	}
	
	private Logger logger;
	private StreamNotifierRunner streamNotifierRunner = null;
	private ArgParser parser = null;
	private BlockingQueue<String> serviceMessageBox = new LinkedBlockingQueue<String>();
}
