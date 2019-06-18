package com.redhat.demo.abnclient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;


import org.apache.commons.io.IOUtils;

public class Client {
	
	private String resourceName;


	static final String DEFAULT_ABN_URL = "http://dm-demo-default.apps.cluster-anz-f723.anz-f723.openshiftworkshop.com/abnlookup";

	public String checkabn(String abn) throws IOException {

		System.out.println("ABN: " + abn);
		// Get the URL from an environment
		String url = System.getProperty("ABN_URL") == null ? DEFAULT_ABN_URL : System.getProperty("ABN_URL");
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
			HttpPost postRequest = new HttpPost(url);
			postRequest.addHeader("accept", "application/json");
			String json = "{\"Abn\":\"" + abn + "\"}";

			postRequest.setEntity(new StringEntity(json));

			HttpResponse response = httpClient.execute(postRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			String content = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
			JSONObject jsonObject = new JSONObject(content);
			String message = (String) jsonObject.get("Message");
			String abnStatus = (String) jsonObject.get("AbnStatus");
			return abnStatus + "," + message;
		} finally {
			httpClient.getConnectionManager().shutdown();
		}


	}

	public static void main(String[] args) throws IOException {

		Client service = new Client();

		List<String> list = Arrays.asList("35090438485");
		for (String abn : list) {
			System.out.println("Abn " + abn + " lookup result " + service.checkabn(abn));
		}
		/*
		{
    "AbnStatus": "Active",
    "Message": "RED HAT ASIA-PACIFIC PTY. LTD."
}
		 */
	}

}
