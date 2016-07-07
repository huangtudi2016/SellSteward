package com.fada.sellsteward;

import java.io.File;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.fada.sellsteward.domain.Category;
import com.fada.sellsteward.domain.Customer;
import com.fada.sellsteward.domain.Grade;
import com.fada.sellsteward.domain.InWares;
import com.fada.sellsteward.domain.SellWares;
import com.fada.sellsteward.domain.Wares;
import com.fada.sellsteward.utils.Logger;
import com.fada.sellsteward.utils.MyAsyncTask;
import com.umeng.fb.FeedbackAgent;

public class AboutActivity extends BaseActivity {
	private TextView checkUpdate,tvAboutVersions;

	@Override
	public void setMyContentView() {
		setContentView(R.layout.setting_about);
	}

	@Override
	public void init() {
		title.setText("关于");
		checkUpdate = (TextView) findViewById(R.id.checkUpdate);
		tvAboutVersions = (TextView) findViewById(R.id.tvAboutVersions);
		PackageInfo packageInfo;//包信息
		try {
			packageInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
			String versionName = packageInfo.versionName;
			tvAboutVersions.setText("当前版本:"+versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}  
	}

	@Override
	public void setListeners() {
//		btnBack.setOnClickListener(this);
		checkUpdate.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnBack:
			finish();
			break;
	
		case R.id.checkUpdate:
			 FeedbackAgent agent = new FeedbackAgent(this);
			// agent.sync();
			 agent.startFeedbackActivity();
			//showConfirmDialog("会清空本地数据,确认吗?", 11);
			break;

		}

	}

	@Override
	public void getConfirmDialogOk(int flag) {
		switch (flag) {
		case 11:
			refreshDataByMonth();
			break;
		}
	}

	@Override
	public void getDialogData(int flag, String... et) {
		switch (flag) {
		case 21:
			refreshDataByMonth();
			break;
		case 22:
			break;
		case 23:
			break;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 100:// 获得商品名

			break;
		}

	}

	// 获取测试数据
	public void testAdd() throws Exception {

		dao.addCategory("外套");
		dao.addCategory("裤子");
		dao.addCategory("裙子");
		dao.addCategory("包包");
		dao.addCategory("鞋子");
		Grade grade = new Grade(null, "普通客户");
		dao.addGrade(grade);
		grade = new Grade(null, "铜牌客户");
		dao.addGrade(grade);
		grade = new Grade(null, "银牌客户");
		dao.addGrade(grade);
		grade = new Grade(null, "金牌客户");
		dao.addGrade(grade);
		grade = new Grade(null, "vip客户");
		dao.addGrade(grade);
		for (int i = 0; i < 20; i++) {

			Customer customer = new Customer("张三" + i, "1531322521" + i, "");
			dao.addCustomer(customer);
		}
		File dir = Environment.getExternalStorageDirectory();
		File file = new File(dir, "DCIM/Camera/");
		File[] files = file.listFiles();
		int j = 1;
		List<Category> allCategory = dao.queryAllCategory();
		for (int i = 0; i < 100; i++) {
			if(i>=files.length)i=0;
			String imagePath = files[i].getAbsolutePath();
			if (j > allCategory.size())
				j = 1;
			Wares wares = new Wares("精致修身无袖连衣裙" + i, imagePath,
					dao.queryCategoryById(j));
			j++;
			dao.addWares(wares);
		}
		long l = 24 * 60 * 60 * 1000L;
		List<Wares> wares = dao.queryAllWares();
		for (int i = 0; i < wares.size(); i++) {
			InWares inWares = new InWares(System.currentTimeMillis() + l * i,
					21f + i, wares.get(i), 21f * 2 + i, "12345678" + i,i);
			dao.addInWares(inWares);
		}
		List<InWares> inWares = dao.queryAllInWares();
		int ii = dao.queryAllCustomer().size();
		ii -= 1;
		j = 1;
		for (int i = 0; i < inWares.size()/2; i++) {
			if (j >= ii)
				j = 1;
			SellWares sellWares = new SellWares(System.currentTimeMillis() + l
					* i, 23f * 2 + i, inWares.get(i), dao.queryCustomerById(j),1);
			j++;
			dao.addSellWares(sellWares);
		}
		

	}

	/**
	 * 获取测试数据
	 */
	private void refreshDataByMonth() {
		new MyAsyncTask() {
			@Override
			public void onPreExecute() {

				showProgressDialog();
			}

			@Override
			public void doInBackground(){
					try {
						//testAdd();
						File file = new File("/data/data/com.fada.sellsteward/databases/SellSteward.db");
						if(file.exists()){
							file.delete();
						}
					} catch (Exception e) {
						Logger.d( "模拟数据出错了...");
						e.printStackTrace();
					}
			}

			@Override
			public void onPostExecute() {
				closeProgressDialog();
				Logger.toast(AboutActivity.this, "清除数据成功");
			}
		}.execute();
	}

}
