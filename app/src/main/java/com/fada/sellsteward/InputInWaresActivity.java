package com.fada.sellsteward;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fada.sellsteward.domain.Category;
import com.fada.sellsteward.domain.InWares;
import com.fada.sellsteward.domain.Wares;
import com.fada.sellsteward.service.MyApp;
import com.fada.sellsteward.utils.ActivityUtils;
import com.fada.sellsteward.utils.ImageUtil;
import com.fada.sellsteward.utils.Logger;
import com.fada.sellsteward.utils.MyDateUtils;
import com.fada.sellsteward.view.scan.ErcodeScanActivity;

public class InputInWaresActivity extends BaseActivity {

	private LinearLayout llCamera;
	private ImageView ivPic;
	private ImageView ivCodePic;
	private String fileName;
	private TextView tvCode,tvCodeScan;
	private TextView tvAmount;
	private TextView tvName;
	private TextView tvType;
	private TextView tvMoneyIn;
	private TextView tvMoneyTab;
	private TextView tvComments,tvAmountDown,tvAmountAdd;
	private TextView tvSave;
	private Wares wares;
	private String code;
	private Intent intent;
	private boolean isRevise;
	private InWares oldInwares;
	

	@Override
	public void setMyContentView() {
		setContentView(R.layout.input_outgo_tab);

	}

	@Override
	public void init() {
		
		
		llCamera = (LinearLayout) findViewById(R.id.llCamera);
		ivPic = (ImageView) findViewById(R.id.ivPic);
		ivCodePic = (ImageView) findViewById(R.id.ivCodePic);
		tvCode = (TextView) findViewById(R.id.tvCode);
		tvCodeScan = (TextView) findViewById(R.id.tvCodeScan);
		tvAmount = (TextView) findViewById(R.id.tvAmount);
		tvAmountDown = (TextView) findViewById(R.id.tvAmountDown);
		tvAmountAdd = (TextView) findViewById(R.id.tvAmountAdd);
		tvName = (TextView) findViewById(R.id.tvName);
		tvType = (TextView) findViewById(R.id.tvType);
		tvMoneyIn = (TextView) findViewById(R.id.tvMoneyIn);
		tvMoneyTab = (TextView) findViewById(R.id.tvMoneyTab);
		tvComments = (TextView) findViewById(R.id.tvComments);
		tvTime = (TextView) findViewById(R.id.tvTime);
		tvTime.setText(dateAndTime+"");
		tvSave = (TextView) findViewById(R.id.tvSave);
		try {
			oldInwares = (InWares) MyApp.app.obj;
			MyApp.app.obj=null;
			if(oldInwares==null){
				throw new Exception("没有inWares对象");
			}
			dateAndTime=MyDateUtils.formatDateAndTime(oldInwares.getInTime());
			count=oldInwares.getAmount();
			isRevise=true;
			title.setText("修改");
			wares=oldInwares.getWares();
			setWares(wares);
			tvCode.setText(""+oldInwares.getCode());
			tvMoneyIn.setText(""+oldInwares.getInPrice());
			tvMoneyTab.setText(""+oldInwares.getTabPrice());
			tvTime.setText(dateAndTime);
			tvAmount.setText(""+count);
		} catch (Exception e) {
			e.printStackTrace();
			isRevise=false;
			MyApp.app.obj=null;
			title.setText("入库");
		}
		intent=getIntent();
		if(!isRevise&&intent!=null){
			try {
				Serializable extra = intent.getSerializableExtra("data");
				if(extra!=null){
					wares = (Wares)extra;
					if(wares!=null){
						setWares(wares);
					}
				}
			} catch (Exception e) {
				Logger.e(null, e.toString());
				throw new RuntimeException(e);
			}
			String code=intent.getStringExtra("code");
			if(code!=null)
				tvCode.setText(code);
		}
		
	}
	@Override
	public void setListeners() {
//		btnBack.setOnClickListener(this);
		llCamera.setOnClickListener(this);
		ivCodePic.setOnClickListener(this);
		tvCode.setOnClickListener(this);
		tvAmount.setOnClickListener(this);
		tvAmountDown.setOnClickListener(this);
		tvAmountAdd.setOnClickListener(this);
		tvName.setOnClickListener(this);
		tvType.setOnClickListener(this);
		tvMoneyIn.setOnClickListener(this);
		ivCodePic.setOnClickListener(this);
		tvCodeScan.setOnClickListener(this);
		tvMoneyTab.setOnClickListener(this);
		tvComments.setOnClickListener(this);
		tvTime.setOnClickListener(this);
		tvSave.setOnClickListener(this);
		
	}
	private void clearAllData(){
		tvName.setText("");
		tvType.setText("");
		tvMoneyIn.setText("");
		tvMoneyTab.setText("");
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
				Float.parseFloat(et[0]);
				tvMoneyTab.setText(et[0]);
				break;
			case 2:
				Float.parseFloat(et[0]);
				tvMoneyIn.setText(et[0]);
				break;
			case 3:
				tvType.setText(et[0]);
				break;
			case 4:
				tvName.setText(et[0]);
				break;
			case 5:
				tvCode.setText(et[0]);
				break;
			case 6:
				count=Integer.parseInt(et[0]);
				if(count>0){
					tvAmount.setText(et[0]);
				}else{
					Logger.toast(this, "数量必须大于1");
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
		case 12:
			fileName=" ";
			break;

		default:
			break;
		}
	}
	private int count=1;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvTime://时间
			showDateTimePicker();
			break;
		case R.id.tvComments://备注
			ActivityUtils.startActivityForResult(this, InputTextActivity.class, null,500);
			break;
		case R.id.tvMoneyTab://标价
			showEditDialog(1);
			break;
		
		case R.id.tvMoneyIn://进价
			showEditDialog(2);
			break;
		case R.id.tvType://种类
			 showCategoryDialog(3);
			break;
		case R.id.tvName://商品名
			if(!isRevise)
			ActivityUtils.startActivityForResult(this, WaresDetailActivity.class, null, 100);
			break;
		case R.id.tvCode://条码
			showEditDialog(5);
			break;
		case R.id.btnBack://返回
			finish();
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
			tvAmount.setText(""+(++count));
			break;
	
		case R.id.llCamera://拍照
			  intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
              startActivityForResult(intent, 0);
			break;
		case R.id.ivCodePic://扫描
//			ActivityUtils.startActivityForResult(this, CaptureActivity.class, "",200);
			ActivityUtils.startActivityForResult(this, ErcodeScanActivity.class, "",200);
			break;
		case R.id.tvCodeScan://扫描
//			ActivityUtils.startActivityForResult(this, CaptureActivity.class, "",200);
			ActivityUtils.startActivityForResult(this, ErcodeScanActivity.class, "",200);
			break;
		case R.id.tvSave://保存
			try {
				Long inTime=MyDateUtils.formatToLong(dateAndTime, "yyyy-MM-dd HH:mm");
				if (inTime==0) {
					Logger.e(null, "日期转换出错");
					inTime=System.currentTimeMillis();
				}
				String name = tvName.getText().toString();
				if(name==""){
					Logger.toast(this,"商品名不可以为空哦" );
					return;
				}
				String categoryName=tvType.getText().toString();
				if(categoryName==""){
					Logger.toast(this,"种类不可以为空哦" );
					return;
				}
					String inMoney = tvMoneyIn.getText().toString();
					if (inMoney =="") {
						Logger.toast(this, "不可以没有进价哦");
						return;
					}
					Float inPrice = Float.parseFloat(inMoney);
					String tabMoney = tvMoneyTab.getText().toString();
					if (tabMoney == "") {
						Logger.toast(this, "不可以没有标价哦");
						return;
					}
					Float tabPrice = Float.parseFloat(tabMoney);
					code = tvCode.getText().toString();
					if(code==""){
						Logger.toast(this, "不可以没有条码哦");
						return;
					}
					if(!isRevise&&dao.queryInWaresByCode(code)!=null){
						Logger.toast(this, "该商品已添加");
						return;
					}
				if (wares == null) {
					Category category = dao.queryCategoryByCategory(categoryName);
					if(category==null) {
						dao.addCategory(categoryName);//进行种类非空判断
						category=dao.queryCategoryByCategory(categoryName);
					}
					
					if (fileName==null) {
						showConfirmDialog("确认不要照片吗?", 12);
						return;
					}
					wares = new Wares(name, fileName+"",category);
					dao.addWares(wares);
					wares=dao.queryWaresByName(name);
				}
				InWares inWares = new InWares(inTime, inPrice, wares, tabPrice,code,count);
				if(isRevise){
					dao.updateInWares(inWares,oldInwares);
					MyApp.app.isRefresh=true;
					Logger.toast(this, "修改成功");
				}else{
					dao.addInWares(inWares);
					Logger.toast(this, "添加成功");
				}
				 clearAllData();
				 Intent intent2 = getIntent();
				 intent2.putExtra("data", inWares);
				 setResult(intent2.getFlags(), intent2);
				 finish();
				 
			}catch (NumberFormatException e) {
				Logger.toast(this, "价格只能是数字哦");
				return;
			} catch (Exception ex) {
				Logger.e(null, ex.toString());
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}
			break;
		}
	}

	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        Logger.d("InputInWaresActivityonActivityResult", resultCode+"");
	        switch (resultCode) {
			case Activity.RESULT_OK://获得照片
				 String sdStatus = Environment.getExternalStorageState();
		            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
		                Logger.w(null,"没有检测到SD卡");
		                return;
		            }
		            Bundle bundle = data.getExtras();
		            Object object = bundle.get("data");
		            if(object!=null){
		            	try {
							Bitmap bitmap = (Bitmap) object;
							if(bitmap!=null){
								ImageUtil.saveToSD(bitmap);
								ivPic.setImageBitmap(bitmap);// 将图片显示在ImageView里
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
		            }
				break;
			case 100://获得商品
				Logger.d("InputInWaresActivityonActivityResult", resultCode+"");
				wares =(Wares) data.getSerializableExtra("data");
				if (wares!=null) {
					setWares(wares);
        			
				}
				break;
			case 200: //扫描到了条码
				String code = data.getStringExtra("code");
	        	if (code!=null) {
	        			tvCode.setText(code);
				}
				break;
			case 300://获得进价
				
				break;
			case 400://获得标价
				
				break;
			case 500://获得备注
				
				break;
			
			default:
				break;
			}
	        
	    }

	private void setWares(Wares wares) {
		if(wares.getImagePath()!=null)
		ivPic.setImageBitmap(ImageUtil.getZoomBitmap(wares.getImagePath(), null));
		tvName.setText(wares.getName().toString());
		tvType.setText(wares.getCategory().getName());
	}
	
	
}
