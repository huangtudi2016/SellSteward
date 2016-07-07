package com.fada.sellsteward.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;

import com.baidu.frontia.FrontiaApplication;
import com.fada.sellsteward.domain.InWares;

public class MyApp extends FrontiaApplication {
	private List<Activity> list = new LinkedList<Activity>();
	public static MyApp app;
	public Object obj;
	public boolean isRefresh;
	public HashMap<String, InWares> goSellMap;
	private Bitmap bitmap;

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void addActivity(Activity activity) {
		list.add(activity);
	}

	public void exit() {
		for (Activity activity : list) {
			activity.finish();
		}
		System.exit(0);
		;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
		goSellMap = new HashMap<String, InWares>();
		// 注册crashHandler全局异常捕获
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
	}
}
