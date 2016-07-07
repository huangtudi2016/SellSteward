package com.fada.sellsteward.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.fada.sellsteward.db.SellStewardDao;
import com.fada.sellsteward.db.SellStewardDaoImpl;
import com.fada.sellsteward.domain.Wares;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class SellService extends Service {
	private SellStewardDao dao;
	private List<Wares> noStock;
//	public Handler handler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			noStock=(List<Wares>) msg.obj;
//			
//		};
//	};
	
//private class MyReciver extends BroadcastReceiver{
//
//	@Override
//	public void onReceive(Context context, Intent intent) {
//		
//		
//	}
//	
//}
	@Override
	public void onCreate() {
		super.onCreate();
		if(dao==null){
			dao= SellStewardDaoImpl.getDaoInstance(getApplicationContext());
		}
		Timer timer=new Timer();
		//从后台查询无库存产品
		TimerTask task = new TimerTask() {
			public void run() {
				 noStock = dao.queryWaresNoStock();
			}
		};
		//开始过1秒执行,以后每隔15秒执行一次
		timer.schedule(task, 1000, 1000*600L);
		
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if(dao==null){
			dao= SellStewardDaoImpl.getDaoInstance(getApplicationContext());
		}
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		dao=null;
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		
		return new MyBinder();
	}
	private class MyBinder extends Binder implements IService{
		public List<Wares> getNoStockWares(){
			return noStock;
			
		}
	}
}
