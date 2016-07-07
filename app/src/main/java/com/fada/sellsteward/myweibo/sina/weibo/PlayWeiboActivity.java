package com.fada.sellsteward.myweibo.sina.weibo;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fada.sellsteward.R;
import com.fada.sellsteward.domain.Customer;
import com.fada.sellsteward.domain.InWares;
import com.fada.sellsteward.domain.Wares;
import com.fada.sellsteward.myweibo.sina.net.AccessToken;
import com.fada.sellsteward.myweibo.sina.net.AsyncWeiboRunner.RequestListener;
import com.fada.sellsteward.myweibo.sina.net.WeiboException;
import com.fada.sellsteward.myweibo.sina.util.AuthoSharePreference;
import com.fada.sellsteward.myweibo.sina.util.MyWeiboManager;
import com.fada.sellsteward.myweibo.sina.util.WeiboConstParam;
import com.fada.sellsteward.utils.Constant;
import com.fada.sellsteward.utils.ImageUtil;
import com.fada.sellsteward.utils.Logger;
import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;

public class PlayWeiboActivity extends Activity implements OnClickListener, TextWatcher, RequestListener{


    private ProgressDialog progressDialog = null;
	
	private TextView mCloseBtn;
	private RelativeLayout mSendBtn;
	private TextView tvCountPrompt;
	private EditText etText;
	private OAuthV2 oAuthV2;//腾讯的
	 //!!!请根据您的实际情况修改!!!      认证成功后浏览器会被重定向到这个url中  必须与注册时填写的一致
    private String redirectUri="http://www.tencent.com/zh-cn/index.shtml";                   
    //!!!请根据您的实际情况修改!!!      换为您为自己的应用申请到的APP KEY
    private String clientId = "801313707"; 
    //!!!请根据您的实际情况修改!!!      换为您为自己的应用申请到的APP SECRET
    private String clientSecret="b4da33c7727bbe53fd63a61ee52ce1a5";
	
	private MyWeiboManager mWeiboManager;
	
	public static final int WEIBO_MAX_LENGTH = 140;

	private String weiboContentString = "";

	private TextView title;
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==1){
				dismissProgressDialog();
				Logger.toast(PlayWeiboActivity.this, "腾讯微博发布成功");
			}
		};
	};
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	    setContentView(R.layout.input_text);
		
		initView();
		
		initData();
		Intent intent = getIntent();
		if (intent!=null) {
			String extra = intent.getStringExtra("data");
			etText.setText(extra);
			
			//腾讯微博处理,如果没有接收到oAuthV2,就自已去请求
			if(intent.getExtras()!=null&&intent.getExtras().getSerializable("oauth")!=null){
				oAuthV2=(OAuthV2) intent.getExtras().getSerializable("oauth");
			}else{
				//腾讯weibo初始化
				oAuthV2=new OAuthV2(redirectUri);
				oAuthV2.setClientId(clientId);
				oAuthV2.setClientSecret(clientSecret);
				Intent intentT = new Intent(this, OAuthV2AuthorizeWebView.class);//创建Intent，使用WebView让用户授权
				intentT.putExtra("oauth", oAuthV2);
				startActivityForResult(intentT,2);
			}
		}
	}

	
	
	public void initView()
	{

		mCloseBtn = (TextView) this.findViewById(R.id.btnBack);
		title = (TextView) this.findViewById(R.id.title);
		title.setText("分享");
		mCloseBtn.setOnClickListener(this);
		
        mSendBtn = (RelativeLayout) this.findViewById(R.id.btnOk);
        mSendBtn.setOnClickListener(this);
        tvCountPrompt = (TextView) this.findViewById(R.id.tvCountPrompt);
        etText = (EditText) this.findViewById(R.id.etText);
        etText.addTextChangedListener(this);    

      
	}
	
	
	public void initData()
	{
		mWeiboManager = MyWeiboManager.getInstance();
		if (mWeiboManager == null)
		{
			mWeiboManager = MyWeiboManager.getInstance(WeiboConstParam.CONSUMER_KEY,
							WeiboConstParam.CONSUMER_SECRET, 
							WeiboConstParam.REDIRECT_URL);	
			
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
			//cbSina.setBackgroundResource(R.drawable.cb_sina_c);
			}
			break;
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
	}
	public void showProgressDialog()
	{
		if (progressDialog == null)
		{
			progressDialog = ProgressDialog.show(this, "分享微博", "Sending...");
		
		}
	}
	
	public void dismissProgressDialog()
	{
		
		if (progressDialog != null)
		{
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

//
//	public void exit()
//	{
//		dismissProgressDialog();
//		
//		 Handler mHandler = new Handler();
//         mHandler.postDelayed(new exitRunnable(), 1000);
//	}
//	
//	class exitRunnable implements Runnable
//	{
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			System.exit(0);
//		}
//		
//	}
	
	@Override
	public void onClick(View v) {
	    
		switch(v.getId())
		{
		case R.id.btnBack:
			finish();
			break;
		case R.id.btnOk:
			send();
			sendTencent();
			break;
		}
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		 String mText = s.toString();
		 //这里就可以作相应的处理,比如:实现边输入边查询
		 String sAgeFormat = getResources().getString(R.string.weiboCharacterCanInput);  
		 String sAgeFormat1 = getResources().getString(R.string.weiboInputTextExceedCount); 
         String sFinalAge;
         int len = mText.length();
         if (len <= WEIBO_MAX_LENGTH) {
             len = WEIBO_MAX_LENGTH - len;
             sFinalAge = String.format(sAgeFormat, len); 
             if (!mSendBtn.isEnabled())
            	 mSendBtn.setEnabled(true);
         } else {
             len = len - WEIBO_MAX_LENGTH;
             tvCountPrompt.setTextColor(Color.RED);
             sFinalAge = String.format(sAgeFormat1, len); 
             if (mSendBtn.isEnabled())
            	 mSendBtn.setEnabled(false);
         }
			tvCountPrompt.setText(sFinalAge);
	}
	
	
	//TODO 退出软件
//	public void close()
//	{
//		Toast.makeText(this, "exit...", Toast.LENGTH_SHORT).show();
//		 exit();
//	}
	/**
	 * 发布腾讯微博
	 */
	public void sendTencent(){
		if(oAuthV2==null)return;
		showProgressDialog();
		new Thread(){public void run() {
				TAPI tAPI= new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
	            try {
	            	weiboContentString = etText.getText().toString();
	            	tAPI.add(oAuthV2, "json", weiboContentString, "127.0.0.1");
	            	handler.sendEmptyMessage(1);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	            tAPI.shutdownConnection();
		};}.start();
	}
	/**
	 * 发送微博
	 */
	public void send()
	{
		Log.i("", "send.............");
		
		 weiboContentString = etText.getText().toString();
		 if (weiboContentString.length() == 0)
		 {
			 Toast.makeText(this, "content can't be empty", Toast.LENGTH_SHORT).show();
			 return ;
		 }
		 //判断令牌是否有问题,有的话直接清除储存的令牌重新获取
		 if (!mWeiboManager.isSessionValid())
		 {
			 Toast.makeText(this, this.getString(R.string.please_login), Toast.LENGTH_SHORT).show();
			 AuthoSharePreference.putToken(this, "");
			 if(!isWeiboOk()){
					goAuthoActivity();
				}
			 return ;
		 }

         try {     
             mWeiboManager.update(this, weiboContentString, this);
             showProgressDialog();
         } catch (MalformedURLException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         } catch (WeiboException e) {
             e.printStackTrace();
             Log.e("", "e.errcode = " + e.getStatusCode());
         }	
         
	}
	
	

	public void limit()
	{
		 Dialog dialog = new AlertDialog.Builder(this).setTitle(R.string.attention)
         .setMessage(R.string.delete_all)
         .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int which) {
            	 etText.setText("");
             }
         }).setNegativeButton(R.string.cancel, null).create();
		 
		 dialog.show();
	}
	/**
	 * 微博发送成功回调
	 */
	 @Override
	    public void onComplete(String response) {
	        runOnUiThread(new Runnable() {

	            @Override
	            public void run() {
                 
	                Toast.makeText(PlayWeiboActivity.this, "新浪微博发布成功", Toast.LENGTH_LONG).show();
	              
	                dismissProgressDialog();
	                finish();
	         
	            }
	        });

	    }

	    @Override
	    public void onIOException(IOException e) {
	        // TODO Auto-generated method stub
         
		        runOnUiThread(new Runnable() {

		            @Override
		            public void run() {
	                 
		          	  Toast.makeText(PlayWeiboActivity.this, "onIOException", Toast.LENGTH_LONG).show();
			    	  
			          dismissProgressDialog();
		         
		            }
		        });
	    }

	    @Override
	    public void onError(final WeiboException e) {
	        runOnUiThread(new Runnable() {

	            @Override
	            public void run() {
	                Toast.makeText(
	                		PlayWeiboActivity.this,
	                        String.format(PlayWeiboActivity.this.getString(R.string.send_failed) + ":%s",
	                                e.getMessage()), Toast.LENGTH_LONG).show();
	      
	                dismissProgressDialog();
	            }
	        });


	    }

}
