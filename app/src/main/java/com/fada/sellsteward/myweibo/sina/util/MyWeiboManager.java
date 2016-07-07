package com.fada.sellsteward.myweibo.sina.util;

import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;
import android.util.Log;

import com.fada.sellsteward.myweibo.sina.net.AccessToken;
import com.fada.sellsteward.myweibo.sina.net.AsyncWeiboRunner;
import com.fada.sellsteward.myweibo.sina.net.AsyncWeiboRunner.RequestListener;
import com.fada.sellsteward.myweibo.sina.net.Oauth2AccessTokenHeader;
import com.fada.sellsteward.myweibo.sina.net.Utility;
import com.fada.sellsteward.myweibo.sina.net.Weibo;
import com.fada.sellsteward.myweibo.sina.net.WeiboException;
import com.fada.sellsteward.myweibo.sina.net.WeiboParameters;

public class MyWeiboManager {

	private Weibo mWeibo;
	
	private static MyWeiboManager mWeiboManager;
	
	
	private String mAppkey;
	private String mRedictUrl;
	
	public static MyWeiboManager getInstance(String appkey, String secret, String redictUrl)
	{
		if (mWeiboManager == null)
		{
			return new MyWeiboManager(appkey, secret, redictUrl);
		}
		
		return mWeiboManager;
	}
	
	public static MyWeiboManager getInstance()
	{
		return mWeiboManager;
	}
	
	
	private MyWeiboManager(String appkey, String secret, String redictUrl)
	{
		mWeibo = Weibo.getInstance();	
		mWeibo.setupConsumerConfig(appkey, secret);	
		mWeibo.setRedirectUrl(redictUrl);
		Utility.setAuthorization(new Oauth2AccessTokenHeader());
		
		mAppkey = appkey;
		mRedictUrl = redictUrl;
		
		Utility.setAuthorization(new Oauth2AccessTokenHeader());
	}
		
	public String getRedictUrl()
	{
		return mRedictUrl;
	}
	
	public String getAppKey()
	{
		return mAppkey;
	}
	/**
	 * 看令牌是否有效
	 * @return 无效返回false
	 */
	public boolean isSessionValid()
	{
		return mWeibo.isSessionValid();
	}
	
	// 		 获得AUTHO认证URL地址
	public  String getAuthoUrl()
	{
		WeiboParameters parameters = new WeiboParameters();
		parameters.add("client_id", mWeibo.getAppKey());
	    parameters.add("response_type", "token");
	    parameters.add("redirect_uri", mWeibo.getRedirectUrl());
	    parameters.add("display", "mobile");

	    if (mWeibo.isSessionValid()) {
	        parameters.add(Weibo.TOKEN, mWeibo.getAccessToken().getToken());
	    }
	     
	    String url = Weibo.URL_OAUTH2_ACCESS_AUTHORIZE + "?" + Utility.encodeUrl(parameters);

	    return url;
	}

	public void setAccessToaken(AccessToken accessToken)
	{
		mWeibo.setAccessToken(accessToken);
	}
	
	
	/**
	 *  发送文本微博
	 * @param context 上下文
	 * @param content 文本内容
	 * @param listener 发送微博完成监听
	 * @return 
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws WeiboException
	 */
	 public String update(Context context, String content, RequestListener listener) 
	 throws MalformedURLException, IOException, WeiboException 
	 {
		 Log.i("", "update.............");
		 WeiboParameters bundle = new WeiboParameters();
		 bundle.add("source", getAppKey());
		 bundle.add("status", content);
//		 //如果要发送带图片微博
//		 File file=new File(Environment.getExternalStorageDirectory(),"DCIM/MM/mm.png");
//		 bundle.add("pic", file.getAbsolutePath());
		
		 
		 String rlt = "";
		 String url = Weibo.SERVER + "statuses/update.json";
		 AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(mWeibo);
		 weiboRunner.request(context, url, bundle, Utility.HTTPMETHOD_POST, listener);
		 return rlt;
	 }
	 
	
}
