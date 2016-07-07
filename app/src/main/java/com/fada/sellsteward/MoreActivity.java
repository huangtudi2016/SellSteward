package com.fada.sellsteward;


import android.content.Intent;
import android.view.View;

import com.fada.sellsteward.gallery_3d.Iamge3DActivity;
import com.fada.sellsteward.utils.ActivityUtils;
import com.fada.sellsteward.utils.NetUtil;
import com.fada.sellsteward.view.MyImageView;

public class MoreActivity extends BaseActivity {
	private MyImageView c_constellation;
	private MyImageView c_recommend;
	private MyImageView c_idea;
	private MyImageView c_joke;
	private final String mbApiKey = "dDQzPa2OqafGdokqzCjl6Mrc";
	private final String mbRootPath = "/apps/销售管家"; // 用户测试的根目录

	@Override
	public void setMyContentView() {
		setContentView(R.layout.activity_more);
	}

	@Override
	public void init() {
		title.setText("更多");
		if(sp.getString("psd", "").equals("")){
			btnRight.setText("设置密码");
		}else{
			btnRight.setText("修改密码");
		}
		c_constellation = (MyImageView) findViewById(R.id.c_constellation);
		c_recommend = (MyImageView) findViewById(R.id.c_recommend);
		c_idea = (MyImageView) findViewById(R.id.c_idea);
		c_joke = (MyImageView) findViewById(R.id.c_joke);

	}

	@Override
	public void setListeners() {
//		btnBack.setOnClickListener(this);
		btnRight.setOnClickListener(this);
		c_constellation.setOnClickListener(this);
		c_recommend.setOnClickListener(this);
		c_idea.setOnClickListener(this);
		c_joke.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRight:
			String tv_psd = btnRight.getText().toString();
			if(tv_psd.equals("设置密码")){
				showEditDialogPsd(1);
			}else{
				showEditDialogRePsd(0);
			}
			break;
		case R.id.btnBack:
			finish();
			break;
		case R.id.c_idea:
			if(NetUtil.hasNetwork(this)){
				ActivityUtils.startActivity(this, BKDataActivity.class);
			}
			
			break;
		case R.id.c_joke:
			ActivityUtils.startActivity(this, Iamge3DActivity.class);
			break;
		case R.id.c_constellation://关于
			ActivityUtils.startActivity(this, AboutActivity.class);
			
			break;
		case R.id.c_recommend:
			ActivityUtils.startActivity(this, AutoActivity.class);
			//showConfirmDialog("模拟数据会清空本地数据,确认吗?", 11);
			break;

		}

	}

	@Override
	public void getConfirmDialogOk(int flag) {
		switch (flag) {
		case 11:
		//	refreshDataByMonth();
			break;
		}
	}

	@Override
	public void getDialogData(int flag, String... et) {
		switch (flag) {
		case 21:
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

	

}
