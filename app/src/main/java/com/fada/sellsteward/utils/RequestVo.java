package com.fada.sellsteward.utils;

import java.net.URI;
import java.util.HashMap;

import android.content.Context;

public class RequestVo {
	public URI requestUrl;
	public Context context;
	public HashMap<String,String> requestDataMap;
	public BaseParser<?> jsonParser;
}
