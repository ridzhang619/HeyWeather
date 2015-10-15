package com.heyweather.app.util;

public interface HttpCallbackListener {
	void onSuccess(String response);
	void onFail();
}
