package com.alchemist;

import net.dv8tion.jda.api.EmbedBuilder;
import java.awt.Color;

public class CommandAbout {
	public CommandAbout () {
		embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle("Discord MP-NExT Manual", null);
		embedBuilder.setColor(Color.red);
		embedBuilder.setDescription("List of commands and usage of MP-NExT");
		embedBuilder.addField("Title of field", "Test of field (inline=false)", false);
		embedBuilder.addField("Title of field", "Test of field (inline=false)", false);
		embedBuilder.addBlankField(false);
		embedBuilder.addField("Title of field", "Test of field (inline=true)", true);
		embedBuilder.addField("Title of field", "Test of field (inline=true)", true);
		embedBuilder.setAuthor("MP NExT", null, "https://i.imgur.com/GndAbTC.png");
		embedBuilder.setFooter("35P Chikuma", "https://i.imgur.com/DOb1GZ1.png");
	}
	
	/**
	 * Issue the command
	 */
	public void issue() {
	
	}
	
	private EmbedBuilder embedBuilder;
}
