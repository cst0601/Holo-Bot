package com.alchemist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

import com.alchemist.service.*;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

/**
 * Discord bot main.
 * @author chikuma
 *
 */
public class DiscordMpNext {
	private JDA jda;
	private Logger logger;

	public static void main(String[] args) {
		try {
			new DiscordMpNext().startUp(args);
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load some confidential informations [Discord bot token / youtube api
	 * key] and creates JDA
	 * @throws LoginException
	 */
	private void startUp (String args[]) throws LoginException {
		logger = LoggerFactory.getLogger(DiscordMpNext.class);

		try {
			JDABuilder builder = JDABuilder.createDefault(Config.getConfig().discordToken)
					.enableIntents(GatewayIntent.MESSAGE_CONTENT)
					.addEventListeners(new PingListener())	// system commands
					.addEventListeners(new RollListener())
					.addEventListeners(new ManualListener())
					.addEventListeners(new VtubeListener())
					.addEventListeners(new AboutListener())
					.addEventListeners(new BonkListener())
					.addEventListeners(new CountDownListener())	// special event
					.addEventListeners(new StreamNotifierService())
					.setActivity(Activity.of(Activity.ActivityType.PLAYING,
											 "Say >man to seek help!"));

			builder = buildOptionalService(builder);

			jda = builder.build();
			jda.awaitReady();

			logger.info("Finish building JDA!");
		} catch (InterruptedException e) {
			// await is a blocking method, if interrupted
			e.printStackTrace();
		}
	}

	/**
	 * Builds optional service if specified in .properties file. Currently
	 * includes twitter broadcast service and member verification service.
	 * @param builder
	 * @param properties
	 * @return
	 */
	private JDABuilder buildOptionalService(JDABuilder builder) {
		Config config = Config.getConfig();
		if (config.isTwitterBroadcastServiceOn) {
			builder.addEventListeners(new TwitterBroadcastService());
		}
		else {
			logger.info("Build: Twitter broadcast service is disabled.");
		}

		if (config.isTwitterUrlReplaceServiceOn) {
			builder.addEventListeners(new TwitterUrlReplaceListener());
		}
		else {
			logger.info("Build: Twitter Url replace service is disabled.");
		}

		if (config.isMemberVerificationServiceOn) {
			builder.addEventListeners(new MemberVerificationService());
		}
		else {
			logger.info("Build: Member verification service is disabled.");
		}

		return builder;
	}
}
