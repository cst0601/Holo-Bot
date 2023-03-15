package com.alchemist.service;

import java.awt.Color;
import java.util.Random;
import java.util.Stack;

import com.alchemist.ArgParser;
import com.alchemist.exceptions.ArgumentParseException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class RollListener extends ListenerAdapter implements Service {
	
	/**
	 * Generate a sequence of random numbers.
	 * @param diceSize
	 * @param diceNumber
	 * @return
	 */
	public Stack<Integer> rollDice (int diceSize, int diceNumber) {
		Stack<Integer> results = new Stack<Integer>();
		Random rand = new Random();
		
		for (int i = 0; i < diceNumber; ++i) 
			results.push(rand.nextInt(diceSize) + 1);
		
		return results;
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		// Event specific information
		Message message = event.getMessage();
		MessageChannelUnion channel = event.getChannel();
	
		String msg = message.getContentDisplay();	// get readable version of the message
		
		ArgParser parser = new ArgParser(msg);
			
		if (parser.getCommand().equals(">roll")) {
			
			try {
				parser.parse();
			} catch (ArgumentParseException e) {
				e.printStackTrace();
				return;
			}
			
			if (parser.getCommandSize() < 2) {
				channel.sendMessage(new MessageCreateBuilder()
						.addContent("Error: Usage: ")
						.addContent(MarkdownUtil.codeblock(">roll <dice_size> [roll_number]"))
						.build()).queue();
			}
			else {
				int diceSize = 0, rollNumber = 1;
				
				try {
					diceSize = Integer.parseInt(parser.getCommand(1));
					if (parser.getCommandSize() > 2)
						rollNumber = Integer.parseInt(parser.getCommand(2));
				} catch (NumberFormatException e) {
					channel.sendMessage(new MessageCreateBuilder()
						.addContent("Error: Invalid input, ")
						.addContent(MarkdownUtil.codeblock("<dice_size>"))
						.addContent(" and ")
						.addContent(MarkdownUtil.codeblock("[roll_number]"))
						.addContent(" must be integers.").build()).queue();
					return;
				}
				
				
				if (diceSize <= 0) {
					channel.sendMessage("Error: Dice size must be positive integer").queue();
					return;
				}
				if (rollNumber > 20) {
					channel.sendMessage("Error: Cannot accept dice roll " + 
									 	"experiments more than 20.").queue();
					return;
				}
				
				Stack<Integer> result = rollDice(diceSize, rollNumber);
				EmbedBuilder embedBuilder = new EmbedBuilder()
					.setTitle(">roll " + diceSize + " " + rollNumber)
					.setColor(Color.red)
					.setFooter("35P Chikuma", "https://i.imgur.com/DOb1GZ1.png");
				
				long resultTotal = 0;
				boolean totalOverflow = false;
				StringBuffer resultBuffer = new StringBuffer();
				while (!result.empty()) {
					int current = result.pop();
					resultBuffer.append(current + ", ");
					
					if (resultTotal + current > Integer.MAX_VALUE)
						totalOverflow = true;
					resultTotal += current;
				}
				embedBuilder.addField("Results", resultBuffer.toString(), false);
				embedBuilder.addField("Total",
						totalOverflow? "*Overflow occured during summing process, "
								+ "try a smaller number. >:/": Long.toString(resultTotal), false);
				
				channel.sendMessageEmbeds(embedBuilder.build()).queue();
			}
		}
	}
	
	@Override
	public String getServiceManualName() {
		return "roll";
	}

	@Override
	public String getServiceMan() {
		return "NAME\n"
		+ "        roll - Roll some dice\n\n"
		+ "SYNOPSIS\n"
		+ "        roll dice_size roll_number\n\n"
		+ "COMMANDS\n"
		+ "        dice_size: Dice size, should be in ranage of: 0 < dice_size <= 2e31-1.\n"
		+ "        roll_number: Times you want to toss the dice, maximum 20.\n";
	}
}
