package com.fada.sellsteward;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fada.sellsteward.domain.Wares;
import com.fada.sellsteward.service.IService;
import com.fada.sellsteward.service.MyApp;
import com.fada.sellsteward.service.SellService;
import com.fada.sellsteward.utils.ActivityUtils;
import com.fada.sellsteward.utils.MyAsyncTask;
import com.fada.sellsteward.view.ProgressImage;
import com.umeng.update.UmengUpdateAgent;

public class ProImageActivity extends BaseActivity {
	private Button startBtn;
	private RelativeLayout mainRLayout;
	private LinearLayout leftLayout;
	private LinearLayout rightLayout;
	private LinearLayout animLayout;
	private RelativeLayout shareLayout;
	private RelativeLayout sharebgLayout;
	private ProgressImage piv;
	
	private int pivDeep = 0;
	
	private IService iService;
	private ServiceConnection conn;
	private class MyConn implements ServiceConnection{

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iService=(IService) service;
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
	}

private List<Wares> noStockWares;

	
	@Override
	protected void onStart() {
		refreshNotification();
		super.onStart();
	}
	// 查询无库存产品
		private void refreshNotification() {
			new  MyAsyncTask(){
				@Override
	  			public void onPreExecute() {

	  			}
	  			@Override
	  			public void doInBackground(){ 
	  				while (iService == null) {
	  				}
	  				if (iService != null){
	  					noStockWares = iService.getNoStockWares();
	  				}
	  			}
	  			@Override
	  			public void onPostExecute() {
	  				MyApp.app.obj=noStockWares;
	  				SharedPreferences sp = getSharedPreferences("config",  MODE_PRIVATE);
	  				Editor edit = sp.edit();
	  				if (noStockWares != null && noStockWares.size() != 0) {
	  					edit.putBoolean("noti", true);
	  					edit.commit();
	  				}else{
	  					edit.putBoolean("noti", false);
	  					edit.commit();
	  				}
	  			}}.execute();
		}

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				piv.setProgress(pivDeep);
				break;
			case 2:
				new Thread(){
					public void run() {
						while (true) {

							pivDeep = (pivDeep + 5) % 100;

							handler.sendEmptyMessage(1);
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
								e.printStackTrace();
								throw new RuntimeException(e);
							}
						}
					};
				}.start();
				break;
			}
		}
		
	};
	
	/**
     * 创建快捷方式
     */
    public void createDeskShortCut() {

           //创建快捷方式的Intent
           Intent shortcutIntent = new Intent( "com.android.launcher.action.INSTALL_SHORTCUT");
           //不允许重复创建
           shortcutIntent.putExtra("duplicate",false);
           //需要显示的名称
           shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name)); 
           //快捷图片
           Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(),R.drawable.red_bg);
           shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,icon);
           Intent intent = new Intent(getApplicationContext(),ProImageActivity.class);
           //下面两个属性是为了当应用程序卸载时桌面 上的快捷方式会删除
           intent.setAction("android.intent.action.MAIN");
           intent.addCategory("android.intent.category.LAUNCHER");
           //点击快捷图片，运行的程序主入口,也就是按了图标后,进入这个intent定义的AndroidLayoutActivity
           shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,intent);
           //发送广播。OK
           sendBroadcast(shortcutIntent);
    }
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.startBtn:
			animLayout.setVisibility(View.VISIBLE);
			mainRLayout.setBackgroundResource(R.drawable.whatsnew_bg);
			Animation leftOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_left);
			Animation rightOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_right);
//			Animation leftOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadedout_to_left_down);
//			Animation rightOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadedout_to_right_down);
			leftLayout.setAnimation(leftOutAnimation);
			rightLayout.setAnimation(rightOutAnimation);
			leftOutAnimation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					shareLayout.setVisibility(View.GONE);
					sharebgLayout.setBackgroundColor(Color.BLACK);
					
				}
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				@Override
				public void onAnimationEnd(Animation animation) {
					leftLayout.setVisibility(View.GONE);
					rightLayout.setVisibility(View.GONE);
					ActivityUtils.startActivityAndFinish(ProImageActivity.this, MainActivity.class);
					overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
				}
			});
			break;
		}
	
		
	}
	@Override
	public void setMyContentView() {
		UmengUpdateAgent.update(this);
		UmengUpdateAgent.setUpdateCheckConfig(false);
		//查询有没有提醒
		Intent service=new Intent(this,SellService.class);
		startService(service);
		conn = new MyConn();
		bindService(service, conn, BIND_AUTO_CREATE);
		setContentView(R.layout.main);
		
	}
	@Override
	public void setListeners() {
		startBtn.setOnClickListener(this);
		
	}
	@Override
	public void init() {
		handler.sendEmptyMessage(2);
		mainRLayout = (RelativeLayout) findViewById(R.id.mainRLayout);
		shareLayout = (RelativeLayout) findViewById(R.id.share);
		sharebgLayout = (RelativeLayout) findViewById(R.id.sharebg);
		startBtn = (Button) findViewById(R.id.startBtn);
		animLayout = (LinearLayout) findViewById(R.id.animLayout);
		leftLayout  = (LinearLayout) findViewById(R.id.leftLayout);
		rightLayout = (LinearLayout) findViewById(R.id.rightLayout);
		piv = (ProgressImage) findViewById(R.id.my_pro_iv);
		//createDeskShortCut();
		SharedPreferences preferences = getSharedPreferences("first",
				Context.MODE_PRIVATE);
		boolean isFirst = preferences.getBoolean("isfrist", true);
		if (isFirst) {
			createDeskShortCut();
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("isfrist", false);
			editor.commit();
		}
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(iService!=null){
			iService=null;
		}
		if(conn!=null){
			unbindService(conn);
			conn=null;
		}
	}
}
