package com.alchemist;

import java.time.Instant;
import java.util.ArrayList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * VxTweet record (data class).
 */
public record VxTweet(
    long dateEpoch,
    int likes,
    int replies,
    int retweets,
    ArrayList<String> mediaUrls,
    String text,
    String tweetId,
    String username,
    String userScreenName,
    String profileImageUrl
) {

  public String getTwitterUrl() {
    return String.format("https://twitter.com/%s/status/%s", userScreenName, tweetId);
  }

  public String getTweetAuthorUrl() {
    return String.format("https://twitter.com/%s", userScreenName);
  }

  /** Convert tweet to MessageEmbed. */
  public MessageEmbed toMessageEmbed() {
    EmbedBuilder builder = new EmbedBuilder()
        .setColor(2533342)  // twitter blue color
        // I have no idea why is this, but couldn't upload twitter icon to imgur
        // stoled twitter icon from wikipedia instead.
        .setFooter("Twitter", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6f/Logo_of_Twitter.svg/2491px-Logo_of_Twitter.svg.png")
        .setTimestamp(Instant.ofEpochSecond(dateEpoch))
        .setAuthor(
          String.format("%s (@%s)", username, userScreenName),
          getTweetAuthorUrl(), profileImageUrl)
        .setDescription(text)
        .addField("Likes", String.valueOf(likes), true)
        .addField("Retweets", String.valueOf(retweets), true)
        .addField("", String.format("[Twitter link](%s)", getTwitterUrl()), false);

    for (String url : mediaUrls) {
      builder.setImage(url);
    }

    return builder.build();
  }
}
