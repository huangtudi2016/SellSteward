package com.fada.sellsteward;

import java.util.ArrayList;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaAccount;
import com.baidu.frontia.FrontiaUser;
import com.baidu.frontia.api.FrontiaAuthorization;
import com.baidu.frontia.api.FrontiaAuthorization.MediaType;
import com.baidu.frontia.api.FrontiaAuthorizationListener.AuthorizationListener;
import com.baidu.frontia.api.FrontiaPersonalStorage;
import com.fada.sellsteward.utils.Conf;
import com.fada.sellsteward.utils.Logger;
import com.fada.sellsteward.utils.MyDateUtils;
import com.fada.sellsteward.utils.NetUtil;

public class BKDataActivity extends BaseActivity {

	private TextView bkName;
	private TextView btnDate;
	private Handler myHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 2://下载成功执行
				closeProgressDialog();
				Logger.toast(BKDataActivity.this,"还原成功,重启软件生效");
				break;
			case 3://上传成功执行
				closeProgressDialog();
				Logger.toast(BKDataActivity.this,"备份成功");
				edit.putLong("BKdate", System.currentTimeMillis());
				edit.commit();
				refreshBKdate();
				break;
			case 4://删除成功执行
				Logger.toast(BKDataActivity.this,"开始备份");
				break;
			case 5://删除成功执行
				closeProgressDialog();
				break;

			default:
				break;
			}
		};
	};
	private FrontiaPersonalStorage mCloudStorage;
	private FrontiaAuthorization authorization;
	public void getConfirmDialogOk(int flag) {
		switch (flag) {
		case 1:
			//action.deleteDB(this);
			showProgressDialog();
			break;
		case 2:
			showProgressDialog();
			break;

		default:
			break;
		}
	};
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnBack:
			finish();
			break;
		case R.id.btnCentre:
			if(NetUtil.hasNetwork(this)){
				showConfirmDialog("会覆盖本地数据,确认吗?", 1);
			}
			
			break;
		case R.id.btnRight:
			if(NetUtil.hasNetwork(this)){
				showConfirmDialog("会覆盖服务器数据,确认吗?", 2);
			}
			break;
		}

	}

	@Override
	public void setMyContentView() {
		boolean isInit = Frontia.init(getApplicationContext(), Conf.APIKEY);
		Logger.d("isInit="+isInit);
		if(isInit){
			ArrayList<String> list = new ArrayList<String>();
			list.add("basic");
			list.add("netdisk");
			mCloudStorage = Frontia.getPersonalStorage();
			authorization = Frontia.getAuthorization();
			FrontiaAccount currentAccount = Frontia.getCurrentAccount();
			if(null != currentAccount && FrontiaAccount.Type.USER == currentAccount.getType()) {
				FrontiaUser user = (FrontiaUser) currentAccount;
				if(!MediaType.BAIDU.toString().equals(user.getPlatform())) {
					authorization.bindBaiduOAuth(this, list, new AuthorizationListener() {
						
						@Override
						public void onSuccess(FrontiaUser user) {
							//TODO
						}
						
						@Override
						public void onFailure(int errCode, String errMsg) {
							Logger.d("errCode="+errCode+"errMsg="+errMsg);
							Logger.toast(getApplicationContext(),"获取百度token失败，错误为:"+errCode+""+errMsg+"，只有获取百度token才能使用个人文件存储功能，请返回重新尝试登录");
							
						}
						
						@Override
						public void onCancel() {
							Logger.toast(getApplicationContext(),"只有获取百度token才能使用个人文件存储功能，请返回重新尝试登录");
							
						}
						
					});
				}
				
			} else {
				authorization.authorize(this,MediaType.BAIDU.toString(),list,new AuthorizationListener() {
					
					@Override
					public void onSuccess(FrontiaUser arg0) {
						Frontia.setCurrentAccount(arg0);
						//TODO
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						Logger.d("arg0="+arg0+"arg1="+arg1);
						
					}
					
					@Override
					public void onCancel() {
						
					}
				});
			}
			setContentView(R.layout.activity_baidu);
		}
		

	}
	
	@Override
	public void setListeners() {
//		btnBack.setOnClickListener(this);
		btnCentre.setOnClickListener(this);
		btnRight.setOnClickListener(this);
		

	}
	
	@Override
	public void init() {
		title.setText("备份数据");
		bkName = (TextView) findViewById(R.id.bkName);
		btnDate = (TextView) findViewById(R.id.btnDate);
        refreshBKdate();

	}
	/**
	 * 刷新备份时间
	 */
    private void refreshBKdate(){ 
    	long BKDate = sp.getLong("BKdate", 0);
    	String time="";
    	if(BKDate==0){
    		time="还未备份";
    	}else{
    		 time = MyDateUtils.formatDateAndTime(BKDate);
    	}
    	btnDate.setText(time);
    	
    	
    } 
}
