package com.alchemist;

/**
 * Hololive member data class
 * @author greg8
 *
 */
public class HoloMember {
	
	public HoloMember(String id, String name, String generation, String ytId) {
		this.id = id;
		this.name = name;
		this.generation = generation;
		this.ytId = ytId;
	}
	
	public String getId() { return id; }
	public String getName() { return name; }
	public String getGeneration() { return generation; }
	public String getYoutubeId() { return ytId; }
	
	private String id;
	private String name;
	private String generation;
	private String ytId;
}
