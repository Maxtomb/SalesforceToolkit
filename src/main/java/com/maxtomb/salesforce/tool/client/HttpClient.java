package com.maxtomb.salesforce.tool.client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpClient {

	public static void main(String[] args) {
	  try {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(
			"http://localhost:8081/app/data/Cloth.json");
		getRequest.addHeader("accept", "application/json");

		HttpResponse response = httpClient.execute(getRequest);

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
			   + response.getStatusLine().getStatusCode());
		}

		BufferedReader br = new BufferedReader(
                         new InputStreamReader((response.getEntity().getContent())));

		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}

		httpClient.getConnectionManager().shutdown();

	  } catch (ClientProtocolException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();
	  }

	}
	public static String getJsonData() throws Exception{
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(
			"http://localhost:8081/app/data/Cloth.json");
		getRequest.addHeader("accept", "application/json");

		HttpResponse response = httpClient.execute(getRequest);

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
			   + response.getStatusLine().getStatusCode());
		}

		BufferedReader br = new BufferedReader(
                         new InputStreamReader((response.getEntity().getContent())));

		StringBuilder sb = new StringBuilder();
		String output;
		while ((output = br.readLine()) != null) {
			sb.append(output);
		}
		httpClient.getConnectionManager().shutdown();
		return sb.toString();
	}
	
	public static String getJsonData(String url) throws Exception{
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(url);
		getRequest.addHeader("accept", "application/json");

		HttpResponse response = httpClient.execute(getRequest);

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
			   + response.getStatusLine().getStatusCode());
		}

		BufferedReader br = new BufferedReader(
                         new InputStreamReader((response.getEntity().getContent())));

		StringBuilder sb = new StringBuilder();
		String output;
		while ((output = br.readLine()) != null) {
			sb.append(output);
		}
		httpClient.getConnectionManager().shutdown();
		return sb.toString();
	}
}
