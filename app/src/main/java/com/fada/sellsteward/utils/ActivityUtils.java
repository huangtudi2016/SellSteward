package com.fada.sellsteward.utils;
import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;

public class ActivityUtils  {

	public static void startActivityAndFinish(Activity context, Class<?> cls){
		Intent intent = new Intent(context,cls);
		context.startActivity(intent);
		context.finish();
	}
	
	
	public static void startActivity(Activity context, Class<?> cls){
		Intent intent = new Intent(context,cls);
		context.startActivity(intent);
	}
	
	public static void startActivityForData(Activity context, Class<?> cls,String data){
		Intent intent = new Intent(context,cls);
		intent.putExtra("data", data);
		context.startActivity(intent);
	}
	/**
	 * 并动一个结果返还的Activity
	 * @param context
	 * @param cls class
	 * @param data 数据
	 * @param flag 标记.
	 */
	public static void startActivityForResult(Activity context, Class<?> cls,String data,int flag){
		Intent intent = new Intent(context,cls);
		intent.putExtra("data", data);
		intent.setFlags(flag);
		context.startActivityForResult(intent, flag);
	}
	
	public static void startActivityForSerializable(Activity context, Class<?> cls,Serializable data){
		Intent intent = new Intent(context,cls);
		intent.putExtra("Serializable", data);
		context.startActivity(intent);
	}
}

