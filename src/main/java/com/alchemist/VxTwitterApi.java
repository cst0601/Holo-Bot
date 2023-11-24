package com.alchemist;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import com.alchemist.exceptions.HttpException;
import com.alchemist.jsonResponse.VxTwitterApiJsonResponse;

public class VxTwitterApi extends Api {	
	public VxTwitterApi() {
		super();
	}
	
	public VxTweet getTweet(String url) throws HttpException, IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder(URI.create(url)).build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		if (response.statusCode() == 200) {
			VxTwitterApiJsonResponse jsonResponse = new VxTwitterApiJsonResponse(response.statusCode(), response.body());
			return jsonResponse.getVxTweet();
		}
		
		throw new HttpException("Error occured when sending request to vxtwitter.", response.statusCode());
	}
}
