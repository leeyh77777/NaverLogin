package com.snslogin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.models.dto.Member;

public abstract class SocialLogin {
	
	public abstract void clearSession(HttpServletRequest request);
	
	public abstract String getCodeURL(HttpServletRequest request);
	
	public abstract String getAccessToken(HttpServletRequest request) throws Exception;
	public abstract String getAccessToken(HttpServletRequest request, String code, String state) throws Exception;
	
	public abstract HashMap<String, String> getUserProfile(String accessToken);
	
	public abstract boolean isJoin(HashMap<String, String> userInfo, HttpServletRequest request);
	
	public abstract boolean login(HttpServletRequest request);
	
	public abstract Member getSocialUserInfo(HttpServletRequest request);
	
	
	public JSONObject httpRequest(String apiURL) throws Exception {
		return httpRequest(apiURL, null);
	}
	
	public JSONObject httpRequest(String apiURL, HashMap<String, String> headers) throws Exception {
		URL url = new URL(apiURL);
		
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		
		if(headers != null) {
			Iterator<String> ir = headers.keySet().iterator();
			while(ir.hasNext()) {
				String key = ir.next();
				String value = headers.get(key);
				conn.setRequestProperty(key, value);
			}
		}
		
		int statusCode = conn.getResponseCode();
		InputStream in;
		if (statusCode == HttpURLConnection.HTTP_OK) {
			in = conn.getInputStream();
		} else {
			in = conn.getErrorStream();
		}
		
		InputStreamReader isr = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(isr);
		
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = br.readLine()) != null) {
			sb.append(line);
		}
		
		br.close();
		isr.close();
		in.close();
		
		JSONObject json = (JSONObject)new JSONParser().parse(sb.toString());
		
		return json;
		
	}
}

