package com.fada.sellsteward.myweibo.sina.weibo;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.fada.sellsteward.R;
import com.fada.sellsteward.myweibo.sina.net.AccessToken;
import com.fada.sellsteward.myweibo.sina.net.Utility;
import com.fada.sellsteward.myweibo.sina.net.WeiboException;
import com.fada.sellsteward.myweibo.sina.util.AuthoSharePreference;
import com.fada.sellsteward.myweibo.sina.util.IWeiboClientListener;
import com.fada.sellsteward.myweibo.sina.util.MyWeiboManager;
import com.fada.sellsteward.myweibo.sina.util.WeiboConstParam;
import com.fada.sellsteward.utils.Logger;

public class WebViewActivity extends Activity implements IWeiboClientListener{

//	public final static int 
	
	private WebView mWebView;
	
	private View progressBar;
	
	private WeiboWebViewClient mWeiboWebViewClient;
	
	private MyWeiboManager mWeiboManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_layout);
		
		initView();
		initData();
		
		
		Logger.d("onCreate", "MainThread().getId() = " + Thread.currentThread().getId());
	}

	public void initView()
	{
		mWebView = (WebView) findViewById(R.id.webview);
	    mWebView.setVerticalScrollBarEnabled(false);
	    mWebView.setHorizontalScrollBarEnabled(false);
	    mWebView.requestFocus();
	    
	    WebSettings webSettings = mWebView.getSettings();
	    webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setSupportZoom(true);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		
		progressBar = findViewById(R.id.show_request_progress_bar);
		
	}

	
	public void initData()
	{
		mWeiboWebViewClient = new WeiboWebViewClient();
		mWebView.setWebViewClient(mWeiboWebViewClient);
		
		CookieSyncManager.createInstance(this);
		 
		
		
		mWeiboManager = MyWeiboManager.getInstance(WeiboConstParam.CONSUMER_KEY,
													WeiboConstParam.CONSUMER_SECRET, 
													WeiboConstParam.REDIRECT_URL);
		
		String authoUrl = mWeiboManager.getAuthoUrl();

		mWebView.loadUrl(authoUrl);

	}

	
	private void showProgress()
	{
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				progressBar.setVisibility(View.VISIBLE);
			}
		});
		
	}
	
	private void hideProgress()
	{
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				progressBar.setVisibility(View.INVISIBLE);
			}
		});


	}

	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Auth cancel", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onComplete(Bundle values) {
		// TODO Auto-generated method stub
			
			CookieSyncManager.getInstance().sync();
		
	        String access_token = values.getString("access_token");
	        String expires_in = values.getString("expires_in");
	        String remind_in = values.getString("remind_in");
	        String uid = values.getString("uid");
	        
	        Logger.d("onComplete", "access_token = " + access_token + 
	        									"\nexpires_in = " + expires_in);

			AuthoSharePreference.putToken(this, access_token);
			AuthoSharePreference.putExpires(this, expires_in);
			AuthoSharePreference.putRemind(this, remind_in);		  
			AuthoSharePreference.putUid(this, uid);
			      
	        AccessToken accessToken = new AccessToken(access_token, WeiboConstParam.CONSUMER_SECRET);
	        mWeiboManager.setAccessToaken(accessToken);       
	        
	        setResult(RESULT_OK);
	        finish();
	}

	@Override
	public void onWeiboException(WeiboException e) {
		// TODO Auto-generated method stub
		Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
	}
	 
	
	
	
	 private class WeiboWebViewClient extends WebViewClient {

	        @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	            Logger.d("WeiboWebViewClient", "shouldOverrideUrlLoading url = " + url);
	            showProgress();
	            view.loadUrl(url);
	            return super.shouldOverrideUrlLoading(view, url);
	        }

	        @Override
	        public void onReceivedError(WebView view, int errorCode, String description,
	                String failingUrl) {
	            
	            Logger.d("WeiboWebViewClient", "onReceivedError failingUrl = " + failingUrl);
	            super.onReceivedError(view, errorCode, description, failingUrl);
	        }

	        @Override
	        public void onPageStarted(WebView view, String url, Bitmap favicon) {
	        	
	        	Logger.d("WeiboWebViewClient", "onPageStarted url = " + url + "\nthreadid = " +
	        			Thread.currentThread().getId());
	        	
	        	showProgress();
	        	if (url.startsWith(mWeiboManager.getRedictUrl())) {
	               handleRedirectUrl(view, url, WebViewActivity.this);
	               view.stopLoading();
	               return;
	            }
	        	
	            super.onPageStarted(view, url, favicon);

	        }

	        @Override
	        public void onPageFinished(WebView view, String url) {
	        	Logger.d("WeiboWebViewClient", "onPageFinished url = " + url);
	        	hideProgress();
	            super.onPageFinished(view, url);
	        }
	        
	        private  boolean handleRedirectUrl(WebView view, String url, IWeiboClientListener listener) 
	   	 	{
	   			Bundle values = Utility.parseUrl(url);
	   	        String error = values.getString("error");
	   	        String error_code = values.getString("error_code");
	   	      

	   	        Logger.d("handleRedirectUrl", "error = " + error + "\n error_code = " + error_code);
	   	        if (error == null && error_code == null)
	   	        {
	   	        	listener.onComplete(values);
	   	        }else if (error.equals("access_denied"))
	   	        {
	   	        	listener.onCancel();
	   	        }else{	 
	   	        	WeiboException weiboException = new WeiboException(error, Integer.parseInt(error_code));
	   	        	listener.onWeiboException(weiboException);
	   	        }
	   	        
	   	        return false;
	   	 	}
	 }
	 

	
}
