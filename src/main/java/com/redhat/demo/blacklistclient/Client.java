package com.redhat.demo.blacklistclient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import org.apache.http.impl.client.DefaultHttpClient;


import org.apache.commons.io.IOUtils;

public class Client {
	
	private String resourceName;

	static final String DEFAULT_WS_URL = "http://localhost:9080/blacklist/";

	public String checkIfOnBlacklist(String name) throws IOException {

		// convert to lower case and replace consecutive blanks to a single '-'
		String transformedName = name.toLowerCase().replaceAll("\\s+", "-");
		
		System.out.println("Name: " + name + " Transformed Name: " + transformedName);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(DEFAULT_WS_URL + transformedName);
		getRequest.addHeader("accept", "application/json");

		HttpResponse response = httpClient.execute(getRequest);

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
			   + response.getStatusLine().getStatusCode());
		}

		String content = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
		httpClient.getConnectionManager().shutdown();

//		System.out.println("***********");
//		System.out.println(content);
		return ((content.indexOf("yes") >= 0)? "yes": "no");
	}

	public static void main(String[] args) throws IOException {

		Client service = new Client();

		
		System.out.println("***********");
		System.out.println("******Testing checkIfOnBlacklist*****");

		List<String> list = Arrays.asList("Jason Bourne", "Jason     bourne", "James bond", "james Dean");
		for (String name : list) {
			System.out.println(name + " in blacklist: " + service.checkIfOnBlacklist(name));
		}
	}

}
