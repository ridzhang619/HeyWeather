package com.heyweather.app.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection urlConnection = null;
				try {
					URL url = new URL(address);
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.setConnectTimeout(8000);
					urlConnection.setReadTimeout(8000);
					InputStream in = urlConnection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					//StringBuilderÆ´½Ó×Ö·û´®ÓÃ
					StringBuilder response = new StringBuilder();
					String line = null;
					while ((line = reader.readLine())!=null) {
						response.append(line);
					}
					if (listener!=null) {
						listener.onSuccess(response.toString());
					}
				} catch (Exception e) {
					if (listener!=null) {
						listener.onFail();
					}
				} finally{
					if (urlConnection!=null) {
						urlConnection.disconnect();
					}
				}
			}
		}).start();
	}
}
