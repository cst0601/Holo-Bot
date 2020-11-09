package com.alchemist;

/**
 * Hololive member data class
 * @author greg8
 *
 */
public class HoloMember {
	
	public HoloMember(String id, String name, String generation,
			          String ytId, int apiId) {
		this.id = id;
		this.name = name;
		this.generation = generation;
		this.ytId = ytId;
		this.apiId = apiId;
	}
	
	public String getId() { return id; }
	public String getName() { return name; }
	public String getGeneration() { return generation; }
	public String getYoutubeId() { return ytId; }
	public int getApiId() { return apiId; }
	
	private String id;
	private String name;
	private String generation;
	private String ytId;
	private int apiId;	// id used in holotoolsapi
}
