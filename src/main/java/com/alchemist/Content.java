package com.alchemist;

/**
 * YT Content base class
 * @author greg8
 *
 */
public class Content {
	protected final String urlPrefix = "https://www.youtube.com/watch?v=%s";
	
	protected String getYtUrl(String id) {
		return String.format(urlPrefix, id);
	}
}
