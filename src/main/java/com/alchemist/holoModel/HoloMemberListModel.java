package com.alchemist.holoModel;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.alchemist.HoloMember;
import com.alchemist.HoloMemberData;
import com.alchemist.HoloMemberData.DIVISION;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Model of getting hololive member list
 * @author greg8
 *
 */
public class HoloMemberListModel {	
	public MessageEmbed getHoloMemberList() {		
		Map<String, String> memberByGeneration = getMemberByGeneration(DIVISION.JP);
		
		/* TODO: think of a better data structure to sort member by generation */
		EmbedBuilder builder = getEmbedPrototype();
		builder.addField("Generation 0", memberByGeneration.get("0"), false);
		builder.addField("Generation 1", memberByGeneration.get("1"), false);
		builder.addField("Generation 2", memberByGeneration.get("2"), false);
		builder.addField("Generation 3", memberByGeneration.get("3"), false);
		builder.addField("Generation 4", memberByGeneration.get("4"), false);
		builder.addField("Generation 5", memberByGeneration.get("5"), false);
		builder.addField("Generation 6", memberByGeneration.get("6"), false);
		builder.addField("Hololive Gamers", memberByGeneration.get("gamers"), false);
		builder.addField("INNK Music", memberByGeneration.get("INNK Music"), false);
		
		return builder.build();
	}
	
	public MessageEmbed getHoloEnMemberList() {
		Map<String, String> memberByGeneration = getMemberByGeneration(DIVISION.EN);
		
		EmbedBuilder builder = getEmbedPrototype();
		builder.addField("EN Generation 1", memberByGeneration.get("EN"), false);
		builder.addField("Project Hope", memberByGeneration.get("HOPE"), false);
		builder.addField("EN Generation 2", memberByGeneration.get("EN_2"), false);
		
		return builder.build();
	}
	
	public MessageEmbed getHoloIdMemberList() {
		Map <String, String> memberByGeneration = getMemberByGeneration(DIVISION.ID);
		
		EmbedBuilder builder = getEmbedPrototype();
		builder.addField("ID Generation 1", memberByGeneration.get("ID_1"), false);
		builder.addField("ID Generation 2", memberByGeneration.get("ID_2"), false);
		
		return builder.build();
	}
	
	private Map<String, String> getMemberByGeneration(DIVISION division) {
		String memberInfo = " - %s: [%s](https://www.youtube.com/channel/%s)\n";
		
		Map<String, String> memberByGeneration = new HashMap<String, String>();
		for (HoloMember member: HoloMemberData.getInstance().getAvaliableMembers(division)) {
			if (memberByGeneration.get(member.getGeneration()) == null) {
				memberByGeneration.put(
						member.getGeneration(), "");	// first init to empty string
			}
			memberByGeneration.put(						// add stuffs
					member.getGeneration(),
					memberByGeneration.get(member.getGeneration()) +
					String.format(memberInfo,
							member.getId(),
							member.getName(),
							member.getYoutubeId())
					);
		}
			
		return memberByGeneration;
	}
	
	private EmbedBuilder getEmbedPrototype() {
		EmbedBuilder builder = new EmbedBuilder()
				.setTitle(">holo list")
				.setColor(Color.red)
				.addField("List of availble members", "- <id>: <name>", false)
				.setFooter("35P | Chikuma", "https://i.imgur.com/DOb1GZ1.png");
		return builder;
	}
}
