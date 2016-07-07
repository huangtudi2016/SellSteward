package com.fada.sellsteward;


import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fada.sellsteward.domain.Customer;
import com.fada.sellsteward.domain.InWares;
import com.fada.sellsteward.domain.SellWares;
import com.fada.sellsteward.domain.Wares;
import com.fada.sellsteward.myweibo.sina.net.AccessToken;
import com.fada.sellsteward.myweibo.sina.util.AuthoSharePreference;
import com.fada.sellsteward.myweibo.sina.util.MyWeiboManager;
import com.fada.sellsteward.myweibo.sina.util.WeiboConstParam;
import com.fada.sellsteward.myweibo.sina.weibo.PlayWeiboActivity;
import com.fada.sellsteward.myweibo.sina.weibo.WebViewActivity;
import com.fada.sellsteward.scalecode.CaptureActivity;
import com.fada.sellsteward.service.MyApp;
import com.fada.sellsteward.utils.ActivityUtils;
import com.fada.sellsteward.utils.Constant;
import com.fada.sellsteward.utils.ImageUtil;
import com.fada.sellsteward.utils.Logger;
import com.fada.sellsteward.utils.MyDateUtils;
import com.fada.sellsteward.view.scan.ErcodeScanActivity;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;


public class InputSellActivity extends BaseActivity {
	private ImageView ivPic;
	private ImageView ivCodePic;
	private TextView tvCode;
	private TextView tvName;
	private TextView tvType;
	private TextView tvMoneyOut;
	private TextView tvMoneySellTab;
	private TextView tvComments;
	private TextView tvSave;
	private InWares inwares;
	private TextView tvCustomerName;
	private TextView tvCustomerNum;
	private TextView tvCountPrompt,tvCodeScan,tvAmount,tvAmountDown,tvAmountAdd;
	private Customer customer;
	private CheckBox cbSina;
	private CheckBox cbTencent;
	 //!!!请根据您的实际情况修改!!!      认证成功后浏览器会被重定向到这个url中  必须与注册时填写的一致
    private String redirectUri="http://www.tencent.com/zh-cn/index.shtml";                   
    //!!!请根据您的实际情况修改!!!      换为您为自己的应用申请到的APP KEY
    private String clientId = "801313707"; 
    //!!!请根据您的实际情况修改!!!      换为您为自己的应用申请到的APP SECRET
    private String clientSecret="b4da33c7727bbe53fd63a61ee52ce1a5";
    
    private OAuthV2 oAuth;
	private boolean flag;
	private int count=1;//显示数量
	private int sum;//商品剩余数量
	
	@Override
	public void setMyContentView() {
		setContentView(R.layout.input_outgo_sell);

	}
	private SellWares oldSellWares;
	private boolean isRevise;
	@Override
	public void init() {
		btnRight.setText("搜索");
		ivPic = (ImageView) findViewById(R.id.ivPic);
		ivCodePic = (ImageView) findViewById(R.id.ivCodePic);
		tvCodeScan = (TextView) findViewById(R.id.tvCodeScan);
		tvAmount = (TextView) findViewById(R.id.tvAmount);
		tvAmountDown = (TextView) findViewById(R.id.tvAmountDown);
		tvAmountAdd = (TextView) findViewById(R.id.tvAmountAdd);
		cbSina = (CheckBox) findViewById(R.id.cbSina);
		cbTencent = (CheckBox) findViewById(R.id.cbTencent);
		tvName = (TextView) findViewById(R.id.tvName);
		tvType = (TextView) findViewById(R.id.tvType);
		tvCode = (TextView) findViewById(R.id.tvCode);
		tvCustomerName = (TextView) findViewById(R.id.tvCustomerName);
		tvCustomerNum = (TextView) findViewById(R.id.tvCustomerNum);
		tvMoneyOut = (TextView) findViewById(R.id.tvMoneyOut);
		tvMoneySellTab = (TextView) findViewById(R.id.tvMoneySellTab);
		tvComments = (TextView) findViewById(R.id.tvComments);
		tvCountPrompt = (TextView) findViewById(R.id.tvCountPrompt);
		tvTime = (TextView) findViewById(R.id.tvTime);
		tvTime.setText(dateAndTime+"");
		tvSave = (TextView) findViewById(R.id.tvSave);
		//腾讯weibo初始化
		oAuth=new OAuthV2(redirectUri);
        oAuth.setClientId(clientId);
        oAuth.setClientSecret(clientSecret);
    	try {
			oldSellWares = (SellWares) MyApp.app.obj;
			MyApp.app.obj=null;
			if(oldSellWares==null){
				throw new Exception("没有sellWares对象");
			}
			dateAndTime=MyDateUtils.formatDateAndTime(oldSellWares.getOutTime());
			count=oldSellWares.getAmount();
			isRevise=true;
			title.setText("修改");
			inwares = oldSellWares.getInWares();
			setWareData(inwares);
			tvMoneyOut.setText(""+oldSellWares.getOutPrice());
			tvTime.setText(dateAndTime);
			tvAmount.setText(""+count);
			tvCustomerName.setText(""+oldSellWares.getCustoms().getName());
			tvCustomerNum.setText(""+oldSellWares.getCustoms().getPhone());
		} catch (Exception e) {
			e.printStackTrace();
			isRevise=false;
			title.setText("出单");
		}
		
	}
	@Override
	public void setListeners() {
//		btnBack.setOnClickListener(this);
		btnRight.setOnClickListener(this);
		ivCodePic.setOnClickListener(this);
		tvName.setOnClickListener(this);
		tvType.setOnClickListener(this);
		tvMoneyOut.setOnClickListener(this);
		ivCodePic.setOnClickListener(this);
		tvComments.setOnClickListener(this);
		tvTime.setOnClickListener(this);
		tvSave.setOnClickListener(this);
		tvCustomerName.setOnClickListener(this);
		tvCustomerNum.setOnClickListener(this);
		tvCountPrompt.setOnClickListener(this);
		cbSina.setOnClickListener(this);
		cbTencent.setOnClickListener(this);
		tvCodeScan.setOnClickListener(this);
		tvAmount.setOnClickListener(this);
		tvAmountDown.setOnClickListener(this);
		tvAmountAdd.setOnClickListener(this);
		
	}
	private void clearAllData(){
		tvName.setText("");
		tvType.setText("");
		tvMoneyOut.setText("");
		tvMoneySellTab.setText("");
		tvComments.setText("");
		tvCode.setText("");
		tvAmount.setText("1");
		ivPic.setImageResource(R.drawable.camera_btn_n);
	}
	@Override
	public void getDialogData(int flag,String...et) {
		try {
			switch (flag) {
			case 1:
				tvCustomerNum.setText(et[0]);
				break;
			case 2:
				Float.parseFloat(et[0]);
				tvMoneyOut.setText(et[0]);
				break;
			case 3:
				tvCustomerName.setText(et[0]);
				break;
			case 6:
				count=Integer.parseInt(et[0]);
				
				if(count<=0){
					Logger.toast(this, "数量必须大于1");
				}else if(count>sum){
					Logger.toast(this, "库存最大为"+sum);
				}else{
					tvAmount.setText(et[0]);
				}
				break;
			}
		} catch (NumberFormatException e) {
			Logger.toast(this, "只能是数字哦");
		}
	}
	@Override
	public void getConfirmDialogOk(int flag) {
		switch (flag) {
		case 11:
			
			break;
		default:
			break;
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvTime://时间
			showDateTimePicker();
			break;
		case R.id.tvName://商品名
			if (!isRevise) {
				ActivityUtils.startActivityForResult(this, DetailInWaresActivity.class, null, 100);
			}
			break;
		case R.id.tvComments://备注
			ActivityUtils.startActivityForResult(this, InputTextActivity.class, null,500);
			break;
		case R.id.tvCustomerName://输入客户名
			showEditDialog(3);
			break;
		case R.id.tvCountPrompt://选择客户
			ActivityUtils.startActivityForResult(this, CustomerDetailActivity.class, null, 300);
			break;
		case R.id.tvCustomerNum://客户电话
			showEditDialog(1);
			break;
		case R.id.tvMoneyOut://售价
			showEditDialog(2);
			break;
		case R.id.tvCode://条码
			showEditDialog(5);
			break;
		case R.id.btnBack://返回
			finish();
			break;
		case R.id.btnRight://搜索
			ActivityUtils.startActivityForResult(this, SearchActivity.class, null, 100);
			break;
		case R.id.tvCodeScan://扫描
//			ActivityUtils.startActivityForResult(this, CaptureActivity.class, "",200);
			ActivityUtils.startActivityForResult(this, ErcodeScanActivity.class, "",200);
			break;
		case R.id.tvAmount://数量
			showEditDialog(6);
			break;
		case R.id.tvAmountDown://减少数量
			if(count-1<1){
				Logger.toast(this, "数量必须大于1");
			}else{
				tvAmount.setText(""+(--count));
			}
			break;
		case R.id.tvAmountAdd://增加数量
			if(isRevise){
				sum+=oldSellWares.getAmount();
			}
			if(count+1>sum){
				Logger.toast(this, "库存最大为"+sum);
			}else{
				tvAmount.setText(""+(++count));
			}
			break;
		case R.id.cbSina://分享新浪微博
			if (!isWeiboOk()) {
				goAuthoActivity();
			}
			break;
		case R.id.cbTencent://腾讯微博
			if (!flag&&cbTencent.isChecked()) {
				Intent intentT = new Intent(this, OAuthV2AuthorizeWebView.class);//创建Intent，使用WebView让用户授权
				intentT.putExtra("oauth", oAuth);
				startActivityForResult(intentT,2);
			}
			break;
		case R.id.ivCodePic://扫描
//			ActivityUtils.startActivityForResult(this, CaptureActivity.class, "",200);
			ActivityUtils.startActivityForResult(this, ErcodeScanActivity.class, "",200);
			break;
		case R.id.tvSave://保存
			try {
				Long outTime = MyDateUtils.formatToLong(dateAndTime,"yyyy-MM-dd HH:mm");
				if (outTime == 0) {
					Logger.e(null, "日期转换出错");
					outTime = System.currentTimeMillis();
				}

				String outMoney = tvMoneyOut.getText().toString();
				if (outMoney == "") {
					Logger.toast(this, "不可以没有售价哦");
					return;
				}
				Float outPrice = Float.parseFloat(outMoney);
				if (outPrice <0) {
					Logger.toast(this, "售价必须大于0");
					return;
				}
				if (inwares == null) {
					Logger.toast(this, "没有该商品");
					return;
				}
				String customerName = tvCustomerName.getText().toString();
				String customerNum = tvCustomerNum.getText().toString();
				if (customer == null) {
					customer = new Customer(customerName, customerNum, "");
					dao.addCustomer(customer);
					customer=dao.queryCustomerByPhone(customerNum);
				}
				if(count<1){
					Logger.toast(this, "数量必须大于1");
					return;
				}
				SellWares sellinWares = new SellWares(outTime, outPrice,inwares, customer,count);
				if(isRevise){
					sellinWares.setProfit(outPrice-inwares.getInPrice());
					dao.updateSellWares(sellinWares,oldSellWares);
					MyApp.app.isRefresh=true;
					Logger.toast(this, "修改成功");
				}else{
					dao.addSellWares(sellinWares);
					Logger.toast(this, "添加成功");
				}
				
				if(cbSina.isChecked()&&isWeiboOk()){
					String weibo="哈哈 我刚刚卖出了"+tvName.getText().toString()+",卖了"+outMoney+"元钱,让生意来的更猛烈些吧^_^";	
					sendSinaWeibo(weibo);
					}
				if (cbTencent.isChecked()&&flag) {
					Intent intent= new Intent(this, PlayWeiboActivity.class);//创建Intent，转到调用Qweibo API的Activity
					intent.putExtra("oauth",oAuth);
					startActivity(intent);
				}
				clearAllData();
				Intent intent2 = getIntent();
				 intent2.putExtra("data", sellinWares);
				 setResult(intent2.getFlags(), intent2);
			} catch (NumberFormatException e) {
				Logger.toast(this, "价格只能是数字哦");
				return;
			} catch (Exception ex) {
				Logger.toast(this, "添加失败了...");
				Logger.e(null, ex.toString());
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}
			finish();
			break;
		}
	}

	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 super.onActivityResult(requestCode, resultCode, data);
	        Logger.d("InputSellWaresActivityonActivityResult", resultCode+"");
	        switch (resultCode) {
			case 100://获得商品
				Logger.d("InputSellWaresActivityonActivityResult", resultCode+"");
				inwares =(InWares) data.getSerializableExtra("data");
				if(inwares!=null){
					setWareData(inwares);
				}
				break;
			case 200: //扫描到了条码
				String code = data.getStringExtra("code");
	        	if (code!=null) {
	        		inwares = dao.queryInWaresByCode(code);
	        		if(!isRevise){
	        			if(inwares==null){
		        			Logger.toast(this, "仓库没有该商品哦,请先入库");
		        			return;
		        		}	
	        			setWareData(inwares);
	        		}else{
	        			tvCode.setText(code);
	        		}
	        		
	        		
				}
				break;
			case 300://得到客户
				customer=(Customer) data.getSerializableExtra("data");
				if(customer!=null){
					tvCustomerName.setText(customer.getName());
					tvCustomerNum.setText(customer.getPhone());
					}
				break;
			case 500://获得备注
				
				break;
			case 2://获得备注
	            if (resultCode==OAuthV2AuthorizeWebView.RESULT_CODE)    {
	                oAuth=(OAuthV2) data.getExtras().getSerializable("oauth");
	                if(oAuth.getStatus()==0)
	                    Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
	                flag=true;
	            }
				break;
			
			default:
				break;
			}
	        switch(requestCode)
			{
				case Constant.REQUEST_AUTH_ACTIVITY_CODE:
				{
					onResultForAuthActivity(resultCode);
				}
				break;
			}
	        
	    }
	private void setWareData(InWares inWares) {
		Wares wares = inwares.getWares();
		if (wares!=null) {
			if(wares.getImagePath()!=null)
			ivPic.setImageBitmap(ImageUtil.getZoomBitmap(wares.getImagePath(), null));
			tvName.setText(wares.getName().toString());
			tvType.setText(wares.getCategory().getName());
		}
		sum=inwares.getAmount()-inwares.getIsSell();
		tvMoneySellTab.setText(inwares.getTabPrice()+"");
		tvMoneyOut.setText(inwares.getTabPrice()+"");
		tvCode.setText(inwares.getCode());
	}
	//TODO 微博
	 
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
	 * TODO 发微博
	 */
	public void sendSinaWeibo(String weibo) {

		ActivityUtils.startActivityForData(this, PlayWeiboActivity.class, weibo);
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
}
