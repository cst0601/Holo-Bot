package com.alchemist.holomodel;

import com.alchemist.HoloMember;
import com.alchemist.HoloMemberData;
import com.alchemist.HoloMemberData.Division;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Model of getting hololive member list.
 *
 */
public class HoloMemberListModel {
  /**
   * Get MessageEmbed listing Hololive JP member.
   */
  public MessageEmbed getHoloMemberList() {
    Map<String, String> memberByGeneration = getMemberByGeneration(Division.JP);

    /* TODO: think of a better data structure to sort member by generation */
    EmbedBuilder builder = getEmbedPrototype();
    builder.addField("Generation 0", memberByGeneration.get("0"), false);
    builder.addField("Generation 1", memberByGeneration.get("1"), false);
    builder.addField("Generation 2", memberByGeneration.get("2"), false);
    builder.addField("Generation 3", memberByGeneration.get("3"), false);
    builder.addField("Generation 4", memberByGeneration.get("4"), false);
    builder.addField("Generation 5", memberByGeneration.get("5"), false);
    builder.addField("Hololive Gamers", memberByGeneration.get("gamers"), false);
    builder.addField("Holo X", memberByGeneration.get("6"), false);
    builder.addField("ReGLOSS", memberByGeneration.get("regloss"), false);
    builder.addField("FLOW GLOW", memberByGeneration.get("flow_glow"), false);

    return builder.build();
  }

  /**
   * Get EN member.
   */
  public MessageEmbed getHoloEnMemberList() {
    Map<String, String> memberByGeneration = getMemberByGeneration(Division.EN);

    EmbedBuilder builder = getEmbedPrototype();
    builder.addField("Myth", memberByGeneration.get("EN"), false);
    builder.addField("Council/Promise", memberByGeneration.get("council_promise"), false);
    builder.addField("Advent", memberByGeneration.get("advent"), false);
    builder.addField("Justice", memberByGeneration.get("justice"), false);

    return builder.build();
  }

  /**
   * Get ID member.
   */
  public MessageEmbed getHoloIdMemberList() {
    Map<String, String> memberByGeneration = getMemberByGeneration(Division.ID);

    EmbedBuilder builder = getEmbedPrototype();
    builder.addField("ID Generation 1", memberByGeneration.get("ID_1"), false);
    builder.addField("ID Generation 2", memberByGeneration.get("ID_2"), false);

    return builder.build();
  }

  private Map<String, String> getMemberByGeneration(Division division) {
    String memberInfo = " - %s: [%s](https://www.youtube.com/channel/%s)\n";

    Map<String, String> memberByGeneration = new HashMap<String, String>();
    for (HoloMember member : HoloMemberData.getInstance().getAvaliableMembers(division)) {
      if (memberByGeneration.get(member.getGeneration()) == null) {
        memberByGeneration.put(
            member.getGeneration(), ""); // first init to empty string
      }
      memberByGeneration.put(
          member.getGeneration(),
          memberByGeneration.get(member.getGeneration())
            + String.format(memberInfo,
                member.getId(),
                member.getName(),
                member.getYoutubeId()));
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
