package com.fada.sellsteward;



import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fada.sellsteward.domain.InWares;
import com.fada.sellsteward.domain.Wares;
import com.fada.sellsteward.service.IService;
import com.fada.sellsteward.service.MyApp;
import com.fada.sellsteward.service.SellService;
import com.fada.sellsteward.utils.ActivityUtils;
import com.fada.sellsteward.utils.Logger;
import com.fada.sellsteward.utils.MyAsyncTask;
import com.fada.sellsteward.utils.MyDateUtils;
import com.fada.sellsteward.utils.NetUtil;
import com.fada.sellsteward.view.scan.ErcodeScanActivity;
import com.umeng.fb.FeedbackAgent;

public class MainActivity extends BaseActivity implements OnClickListener{
	private TextView btnTally;
	private TextView btnDetail;
	private TextView btnStat;
	private TextView btnAccount;
	private TextView tvValue1;
	private TextView tvValue2;
	private TextView tvValue3;
	private TextView btnBudget;
	private TextView tvMsgCount;
	private TextView btnSetting;
	private RelativeLayout txm;
	private RelativeLayout btnSearch;
	private Button btnSync;
	private ImageView btnMarket;
	private int month;
	private Handler handler1 = new Handler() {
	public void handleMessage(android.os.Message msg) {
		
		
	};
};
	@Override
	public void setMyContentView() {
		setContentView(R.layout.home_screen);
		
		btnTally = (TextView) findViewById(R.id.btnTally);
		tvValue1 = (TextView) findViewById(R.id.tvValue1);
		tvValue2 = (TextView) findViewById(R.id.tvValue2);
		tvValue3 = (TextView) findViewById(R.id.tvValue3);
		tvMsgCount = (TextView) findViewById(R.id.tvMsgCount);
		btnDetail = (TextView) findViewById(R.id.btnDetail);//出售
		btnStat = (TextView) findViewById(R.id.btnStat);//进货
		btnAccount = (TextView) findViewById(R.id.btnAccount);//客户
		btnBudget = (TextView) findViewById(R.id.btnBudget);//库存
		btnSetting= (TextView) findViewById(R.id.btnSetting);//更多
		txm = (RelativeLayout) findViewById(R.id.txm);//扫描
		btnSearch = (RelativeLayout) findViewById(R.id.btnSearch);//扫描
		btnSync = (Button) findViewById(R.id.btnSync);
		btnMarket = (ImageView) findViewById(R.id.btnMarket);
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnTally:
			ActivityUtils.startActivity(this, InputSellActivity.class);
			break;
		case R.id.btnMarket:
			 FeedbackAgent agent = new FeedbackAgent(this);
			 agent.startFeedbackActivity();
			break;
		case R.id.btnDetail:
			ActivityUtils.startActivity(this, SellDetailActivity.class);
			break;
		case R.id.btnStat:
			ActivityUtils.startActivity(this, DetailInWaresActivity.class);
			break;
		case R.id.btnAccount:
			ActivityUtils.startActivity(this, WaresDetailActivity.class);
			break;
		case R.id.btnBudget:
			ActivityUtils.startActivity(this, CustomerDetailActivity.class);
			break;
		case R.id.btnSetting:
			ActivityUtils.startActivity(this, MoreActivity.class);
			break;
		case R.id.txm:
//			ActivityUtils.startActivityForResult(this, CaptureActivity.class, "",200);
			ActivityUtils.startActivityForResult(this, ErcodeScanActivity.class, "",200);
			//ActivityUtils.startActivity(this, PlayWeiboActivity.class);
			break;
		case R.id.btnSync://同步
			 if(NetUtil.hasNetwork(this)){
				ActivityUtils.startActivity(this, BKDataActivity.class);
			 }
			
			break;
		case R.id.btnSearch://搜索
			ActivityUtils.startActivity(this, SearchActivity.class);
			break;
		case R.id.tvMsgCount://提醒
			if(noStockWares!=null){
				MyApp.app.obj=noStockWares;
				System.out.println(noStockWares);
			}
			ActivityUtils.startActivity(this,WaresNoStockDetailActivity.class);
			break;

		default:
			break;
		}
		
	}
	
	@Override
	public void setListeners() {
		btnTally.setOnClickListener(this);
		btnDetail.setOnClickListener(this);
		btnStat.setOnClickListener(this);
		btnAccount.setOnClickListener(this);
		btnBudget.setOnClickListener(this);
		btnSetting.setOnClickListener(this);
		txm.setOnClickListener(this);
		btnSync.setOnClickListener(this);
		tvMsgCount.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		btnMarket.setOnClickListener(this);
		
		
		
	}
	@Override
	public void init() {
		month = MyDateUtils.formatMonth(System.currentTimeMillis());
		year = MyDateUtils.formatYear(System.currentTimeMillis());
		refreshData();
		Intent service=new Intent(this,SellService.class);
		startService(service);
		conn = new MyConn();
		bindService(service, conn, BIND_AUTO_CREATE);
		if(sp.getBoolean("noti", false)){
			tvMsgCount.setText("1");
		}
	}
	private List<Wares> noStockWares;

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
  				if (noStockWares != null && noStockWares.size() != 0) {
  					tvMsgCount.setText("1");
  				} else {
  					tvMsgCount.setText("");
  				}
  				
  			}}.execute();
	}
	@Override
	protected void onStart() {
		refreshNotification();
		super.onStart();
	}
	@Override
	protected void onDestroy() {
		iService=null;
		unbindService(conn);
		conn=null;
		super.onDestroy();
	}
	private IService iService;
	private ServiceConnection conn;
	private String code;
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
	private int year;
	@Override
	protected void onResume() {
		super.onResume();
		 refreshData();
	}
	//动态显示获取数据的方法
  	private void refreshData() {
  		new  MyAsyncTask(){
  			private float inMoney;
  			private float monthAllSell;
  			private float monthProfit;
			
			@Override
  			public void onPreExecute() {
				

  			}
  			@Override
  			public void doInBackground(){ 
  				inMoney = dao.queryInWaresByMonthInMoney(year, month);
  				monthAllSell = dao.querySellWaresByMonthAllSell(year, month);
  				monthProfit = dao.querySellWaresByMonthProfit(year, month);
  			}
  			@Override
  			public void onPostExecute() {
  				tvValue1.setText(monthAllSell+"");
  				tvValue2.setText(inMoney+"");
  				tvValue3.setText(monthProfit+"");
  				
  			}}.execute();
  	}
  	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 200:
			code = data.getStringExtra("code");
        	if (code!=null) {
        			InWares inWares = dao.queryInWaresByCode(code);
        			if (inWares!=null) {
        				ActivityUtils.startActivityForData(MainActivity.this, WaresActivity.class, code);
        				
					}else{
						showConfirmDialog("没有查询该商品,要添加吗", 11);
						return;
					}
        			
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	public void getConfirmDialogOk(int flag) {
		switch (flag) {
		case 11:
			Intent intent1=new Intent(this, InputInWaresActivity.class);
			intent1.putExtra("code", code);
			startActivity(intent1);
			break;

		default:
			break;
		}
		super.getConfirmDialogOk(flag);
	}

	private boolean isExit;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (!isExit) {
				Logger.toast(this, "再按一次返回键退出");
				isExit = true;
				new Thread() {
					public void run() {
						SystemClock.sleep(3000);
						isExit = false;
					};
				}.start();
				return true;
			}
			MyApp.app.exit();
		}
		return super.onKeyDown(keyCode, event);

	}
}
