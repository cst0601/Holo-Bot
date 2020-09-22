package com.alchemist;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.*;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import com.alchemist.jsonResponse.JsonResponse;


public class YoutubeApi extends Api{
	public YoutubeApi(String key) {
		super();
		
		apiKey = key;
		initChannelId();
	}
	
	/**
	 * Make a request to Youtube API by name of vtuber.
	 * If the name is not found avaulable, return null (debt)
	 * @param vtubeName
	 * @return JsonResponse of youtube api
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public JsonResponse request(String vtubeName) throws IOException, InterruptedException
	{
		if(channelId.get(vtubeName) == null) {	// vtuber not found
			return null;
		}
	    
		request = HttpRequest.newBuilder()
				.uri(URI.create(String.format(
						defaultUrl, channelId.get(vtubeName), apiKey)))
				.build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		return new JsonResponse(response.statusCode(), response.body());
	}
	
	/**
	 * Get available member names that is linked with a channel id.
	 * @return list of member names
	 */
	public ArrayList<String> getAvailableMembers() {
		ArrayList<String> memberNames = new ArrayList<String>();
		for (Enumeration<String> e = channelId.keys(); e.hasMoreElements();)
			memberNames.add((String) e.nextElement());
		return memberNames;
	}
	
	/**
	 * Crappy implementation, change it if not lazy
	 */
	private void initChannelId() {
		channelId.put("sora", "UCp6993wxpyDPHUpavwDFqgg");
		channelId.put("miko", "UC-hM6YJuNYVAmUWxeIr9FeA");
		channelId.put("suisei", "UC5CwaMl1eIgY8h02uZw7u8A");
		channelId.put("roboco", "UCDqI2jOz0weumE8s7paEk6g");
		channelId.put("marine", "UCCzUftO8KOVkV4wQG1vkUvg");
		channelId.put("korone", "UChAnqc_AY5_I3Px5dig3X1Q");
		channelId.put("watame", "UCqm3BQLlJfvkTsX_hvm0UmA");
		channelId.put("lamy", "UCFKOVgVbGmX65RxO3EtH3iw");
		channelId.put("azki", "UC0TXe_LYZ4scaW2XMyi5_kw");
		channelId.put("fubuki", "UCdn5BQ06XqgXoAxIhbqw5Rg");
		channelId.put("haato", "UC1CfXB_kRs3C-zaeTG3oGyg");
		channelId.put("mel", "UCD8HOxPs4Xvsm8H0ZxXGiBw");
		channelId.put("akirose", "UCFTLzh12_nrtzqBPsTCqenA");
		channelId.put("maturi", "UCQ0UDLQCjY0rmuxCDE38FGg");
		channelId.put("aqua", "UC1opHUrw8rvnsadT-iGp7Cg");
		channelId.put("shion", "UCXTpFs_3PqI41qX2d9tL2Rw");
		channelId.put("ayame", "UC7fk0CB07ly8oSl0aqKkqFg");
		channelId.put("choco", "UC1suqwovbL1kzsoaZgFZLKg");
		channelId.put("subaru", "UCvzGlP9oQwU--Y0r9id_jnA");
		channelId.put("mio", "UCp-5t9SrOQwXMU7iIjQfARg");
		channelId.put("okayu", "UCvaTdHTWBGv3MKj3KVqJVCw");
		channelId.put("rushia", "UCl_gCybOJRIgOXw6Qb4qJzQ");
		channelId.put("pekora", "UC1DCedRgGHBdm81E1llLhOQ");
		channelId.put("noel", "UCdyqAaZDKHXg4Ahi7VENThQ");
		channelId.put("flare", "UCvInZx9h3jC2JzsIzoOebWg");
		channelId.put("towa", "UC1uv2Oq6kNxgATlCiez59hw");
		channelId.put("coco", "UCS9uQI-jC3DE0L4IpXyvr6w");
		channelId.put("kanata", "UCZlDXzGoo7d44bwdNObFacg");
		channelId.put("luna", "UCa9Y57gfeY0Zro_noHRVrnw");
		channelId.put("nene", "UCAWSyEs_Io8MtpY3m-zqILA");
		channelId.put("botan", "UCUKD-uaobj9jiqB-VXt71mA");
		channelId.put("aloe", "UCgZuwn-O7Szh9cAgHqJ6vjw");
		channelId.put("polka", "UCK9V2B22uJYu3N7eR_BT9QA");
	}
		
	private final String apiKey;
	private final String defaultUrl = "https://www.googleapis.com/youtube/v3/se"
			+ "arch?part=snippet&channelId=%s&eventType=live&type=video&key=%s";
	private Dictionary<String, String> channelId = new Hashtable<String, String>();
	private HttpRequest request;
}
