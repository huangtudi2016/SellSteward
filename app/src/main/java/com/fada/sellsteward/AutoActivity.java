package com.fada.sellsteward;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fada.sellsteward.myweibo.sina.net.AccessToken;
import com.fada.sellsteward.myweibo.sina.util.AuthoSharePreference;
import com.fada.sellsteward.myweibo.sina.util.MyWeiboManager;
import com.fada.sellsteward.myweibo.sina.util.WeiboConstParam;
import com.fada.sellsteward.myweibo.sina.weibo.WebViewActivity;
import com.fada.sellsteward.utils.Constant;
import com.fada.sellsteward.utils.Logger;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;

public class AutoActivity extends BaseActivity {

	private View vSina;
	private View vTencent;
	private ImageView iv_sina;
	private ImageView iv_tencent;
	private OAuthV2 oAuthV2;//腾讯的
	 //!!!请根据您的实际情况修改!!!      认证成功后浏览器会被重定向到这个url中  必须与注册时填写的一致
   private String redirectUri="http://www.tencent.com/zh-cn/index.shtml";                   
   //!!!请根据您的实际情况修改!!!      换为您为自己的应用申请到的APP KEY
   private String clientId = "801313707"; 
   //!!!请根据您的实际情况修改!!!      换为您为自己的应用申请到的APP SECRET
   private String clientSecret="b4da33c7727bbe53fd63a61ee52ce1a5";

	@Override
	public void setMyContentView() {
		setContentView(R.layout.weibo_setting_mgr);
		
	}

	@Override
	public void onClick(View v) {
		Editor editor = sp.edit();
		switch (v.getId()) {
		case R.id.vSina:
			boolean b = sp.getBoolean("sinaweibo", false);
			if(!b){
				editor.putBoolean("sinaweibo", true);
				editor.commit();
				openSinaWeibo();
			}else{
				closeSinaWeibo();
				editor.putBoolean("sinaweibo", false);
				editor.commit();
			}
			break;
		case R.id.vTencent:
			boolean t = sp.getBoolean("tencentweibo", false);
			if(!t){
				editor.putBoolean("tencentweibo", true);
				editor.commit();
				openTencentWeibo();
			}else{
				closeTencentWeibo();
				editor.putBoolean("tencentweibo", false);
				editor.commit();
			}
			break;
		case R.id.btnBack:
			finish();
			break;
		}
		
	}

	private void closeSinaWeibo() {
		vSina.setBackgroundResource(R.drawable.ic_weibo_sina_n);
		iv_sina.setVisibility(View.GONE);
	}
	private void openSinaWeibo() {
		
		vSina.setBackgroundResource(R.drawable.ic_weibo_sina_p);
		iv_sina.setVisibility(View.VISIBLE);
		
		if(!isWeiboOk()){
			goAuthoActivity();
		}
	}
	private void closeTencentWeibo() {
		vTencent.setBackgroundResource(R.drawable.ic_weibo_tencent_n);
		iv_tencent.setVisibility(View.GONE);
	}
	private void openTencentWeibo() {
		
		vTencent.setBackgroundResource(R.drawable.ic_weibo_tencent_p);
		iv_tencent.setVisibility(View.VISIBLE);
		//腾讯weibo初始化
		oAuthV2=new OAuthV2(redirectUri);
		oAuthV2.setClientId(clientId);
		oAuthV2.setClientSecret(clientSecret);
		Intent intentT = new Intent(this, OAuthV2AuthorizeWebView.class);//创建Intent，使用WebView让用户授权
		intentT.putExtra("oauth", oAuthV2);
		startActivityForResult(intentT,2);
	}
	@Override
	public void setListeners() {
		vSina.setOnClickListener(this);
		vTencent.setOnClickListener(this);
//		btnBack.setOnClickListener(this);

	}

	@Override
	public void init() {
		
		title.setText("分享设置");
		vSina = findViewById(R.id.vSina);
		vTencent = findViewById(R.id.vTencent);
		iv_sina = (ImageView)findViewById(R.id.iv_sina);
		iv_tencent = (ImageView) findViewById(R.id.iv_tencent);
		boolean b = sp.getBoolean("sinaweibo", false);
		if(!b){
			closeSinaWeibo();
		}else{
			vSina.setBackgroundResource(R.drawable.ic_weibo_sina_p);
			iv_sina.setVisibility(View.VISIBLE);
		}
		if(!b){
			closeTencentWeibo();
		}else{
			vTencent.setBackgroundResource(R.drawable.ic_weibo_tencent_p);
			iv_tencent.setVisibility(View.VISIBLE);
		}
		

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode)
		{
			case Constant.REQUEST_AUTH_ACTIVITY_CODE:
			{
				onResultForAuthActivity(resultCode);
			}
			break;
			case 2://腾讯微博
			{
				if (resultCode==OAuthV2AuthorizeWebView.RESULT_CODE)    {
					oAuthV2=(OAuthV2) data.getExtras().getSerializable("oauth");
					if(oAuthV2.getStatus()==0)
						Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	   * TODO Auth认证
	   */
	public void goAuthoActivity()
	{
		Intent intent = new Intent();
		intent.setClass(this, WebViewActivity.class);
		startActivityForResult(intent, Constant.REQUEST_AUTH_ACTIVITY_CODE);
		
	}
		
	/**
	 * TODO 认证成功 
	 * @param resultCode
	 */
	private void onResultForAuthActivity(int resultCode)
	{
		switch (resultCode) {
			case RESULT_OK:
			{
				Logger.toast(this, "微博认证成功");
			}
			break;
		}
	}
	 /**
	  * TODO 判断密钥是否可用,如果可用,那么发微博
	  */
	 public boolean isWeiboOk()
	 {
		 String token = AuthoSharePreference.getToken(this);
		 mWeiboManager = MyWeiboManager.getInstance(WeiboConstParam.CONSUMER_KEY,
				 WeiboConstParam.CONSUMER_SECRET, 
				 WeiboConstParam.REDIRECT_URL);	
		 
		 if (!token.equals(""))
		 {
			 AccessToken accessToken = new AccessToken(token, WeiboConstParam.CONSUMER_SECRET);     
			 mWeiboManager.setAccessToaken(accessToken);      
			 return true;
		 }
			 return false; 
	 }
}
