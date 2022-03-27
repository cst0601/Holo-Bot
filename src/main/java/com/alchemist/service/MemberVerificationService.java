package com.alchemist.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alchemist.YoutubeApi;
import com.alchemist.jsonResponse.youtube.LiveStreamChatMessageList;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberVerificationService extends ListenerAdapter implements Service {
	
	private Logger logger;
	private YoutubeApi api;
	
	public MemberVerificationService () {
		logger = LoggerFactory.getLogger(MemberVerificationService.class);
		
		try {
			api = new YoutubeApi();
		} catch (Exception e) {
			logger.warn("Failed to read credentials and build youtube api.");
			e.printStackTrace();
		}
		
		try {
			LiveStreamChatMessageList list = api.requestLiveStreamChat();
			for (int i = 0; i < 15; i++) {
				logger.info("Message info: " + list.getMessage(i).getDisplayName() + " is member: " + list.getMessage(i).isChatSponsor());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
