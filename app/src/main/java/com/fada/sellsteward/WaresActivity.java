package com.fada.sellsteward;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fada.sellsteward.domain.Image;
import com.fada.sellsteward.domain.InWares;
import com.fada.sellsteward.domain.SellWares;
import com.fada.sellsteward.gallery_3d.ImageZoomView;
import com.fada.sellsteward.gallery_3d.MyAdapter;
import com.fada.sellsteward.gallery_3d.MyGallery;
import com.fada.sellsteward.service.MyApp;
import com.fada.sellsteward.utils.ActivityUtils;
import com.fada.sellsteward.utils.ImageUtil;
import com.fada.sellsteward.utils.ImageUtil.ImageCallback;
import com.fada.sellsteward.utils.Logger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WaresActivity extends BaseActivity {
	private MyGallery gallery;
	private List<String> cacheFiles;
	private RelativeLayout linBar;
	private RelativeLayout rLzoomView;
	private ImageZoomView mZoomView;
	private FrameLayout backGallery;
	
	private FrameLayout fl_gallery3d;
	private ImageView imageView3;
	private TextView tvCode;
	private TextView btnBack;
	private TextView tvName;
	private TextView tvType;
	private TextView tvMoneyOut;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			linBar.setVisibility(View.GONE);
		};
	};
	private Intent intent;
	private MyAdapter adapter;
	private String type;
	private TextView tvAmount;
	private int count;
	private TextView tvAmountOut;
	private TextView tvAmountDown;
	private TextView tvAmountAdd;
	

	@Override
	public void setMyContentView() {
		setContentView(R.layout.wares);

	}

	@Override
	public void setListeners() {
		imageView3.setOnClickListener(this);
//		btnBack.setOnClickListener(this);
		backGallery.setOnClickListener(this);
		btnRight.setOnClickListener(this);
		btnCentre.setOnClickListener(this);
		tvAmountDown.setOnClickListener(this);
		tvAmountAdd.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvAmountDown://减少数量
			if(count-1<1){
				Logger.toast(this, "数量必须大于1");
			}else{
				tvAmountOut.setText(""+(--count));
				inWares.setIsSellTemp(count);
			}
			break;
		case R.id.tvAmountAdd://增加数量
			
			int sum=inWares.getAmount()-inWares.getIsSell();
			if(count+1>sum){
				Logger.toast(this, "库存最大为"+sum);
			}else{
				tvAmountOut.setText(""+(++count));
				inWares.setIsSellTemp(count);
			}
			break;
		case R.id.backGallery:
			rLzoomView.setVisibility(View.GONE);
			break;
		case R.id.imageView3:
			//ll_wares.setVisibility(View.GONE);
			if(adapter!=null)
				fl_gallery3d.setVisibility(View.VISIBLE);
			break;
		case R.id.btnRight:
			if(inWares.getAmount()-inWares.getIsSell()<=0){
				Logger.toast(this, "商品已售完");
			}else{
				MyApp.app.goSellMap.put(inWares.getCode(), inWares);
				ActivityUtils.startActivityForData(WaresActivity.this, GoSellWaresActivity.class, inWares.getCode());
				finish();
			}
			
			break;
		case R.id.btnCentre://修改
			if(type.equals("sellWares")){
				MyApp.app.obj=sellWares;
				ActivityUtils.startActivityForData(WaresActivity.this, InputSellActivity.class, inWares.getCode());
			}else{
				MyApp.app.obj=inWares;
				ActivityUtils.startActivityForData(WaresActivity.this, InputInWaresActivity.class, inWares.getCode());
			}
			finish();
			break;
		
		case R.id.btnBack:
			finish();
			break;
		}

	}
	@Override
	public void init() {
		btnRight.setText("出售");
		btnCentre.setText("修改");
		fl_gallery3d = (FrameLayout) findViewById(R.id.fl_gallery3d);
		imageView3 = (ImageView) findViewById(R.id.imageView3);
		tvName = (TextView) findViewById(R.id.tvName);
		tvType = (TextView) findViewById(R.id.tvType);
		tvCode = (TextView) findViewById(R.id.tvCode);
		tvAmount = (TextView) findViewById(R.id.tvAmount);
		tvAmountOut = (TextView) findViewById(R.id.tvAmountOut);
		tvAmountDown = (TextView) findViewById(R.id.tvAmountDown);
		tvAmountAdd = (TextView) findViewById(R.id.tvAmountAdd);
		tvMoneyOut = (TextView) findViewById(R.id.tvMoneyOut);
		//btnBack = (TextView) findViewById(R.id.btnBack);
		linBar = (RelativeLayout) findViewById(R.id.LinBar);
		linBar = (RelativeLayout) findViewById(R.id.LinBar);
		rLzoomView = (RelativeLayout) findViewById(R.id.RLzoomView);
		 backGallery=(FrameLayout) findViewById(R.id.backGallery);
		gallery = (MyGallery) findViewById(R.id.myGallery);
		mZoomView = (ImageZoomView) findViewById(R.id.zoomView);
		cacheFiles = new ArrayList<String>();
		intent = getIntent();
		if (intent!=null) {
			type = intent.getStringExtra("data");
			if(type.equals("inWares")){
				inWares=(InWares) MyApp.app.obj;
				MyApp.app.obj=null;
			}else if(type.equals("sellWares")){
				sellWares= (SellWares) MyApp.app.obj;
				inWares=sellWares.getInWares();
				MyApp.app.obj=null;
			}else {
				inWares=dao.queryInWaresByCode(type);
			}
			if (inWares != null) {
				tvName.setText(inWares.getWares().getName());
				tvType.setText(inWares.getWares().getCategory().getName());
				tvMoneyOut.setText(inWares.getTabPrice() + "");
				tvCode.setText(inWares.getCode());
				if(sellWares==null){
					tvAmount.setText("剩余:"+(inWares.getAmount()-inWares.getIsSell()));
				}else{
					tvAmount.setText("售出:"+sellWares.getAmount()+" 剩余:"+(inWares.getAmount()-inWares.getIsSell()));
				}
				Integer id = inWares.getWares().get_id();
				String imagePath = inWares.getWares().getImagePath();
				List<Image> listImage = dao.queryIamgeByWaresId(id);
				if (listImage != null && listImage.size() != 0) {
					for (int i = 0; i < listImage.size(); i++) {
						cacheFiles.add(listImage.get(i).getPath());
					}
				}else{
					if (imagePath!=null) {
						cacheFiles.add(imagePath);
					}
				}
				if (imagePath != null) {
					Bitmap bitmap = ImageUtil.loadImage(imagePath, null, callback);
					if (bitmap != null) {
						imageView3.setImageBitmap(bitmap);
					}
				}
			}
		}
		if(cacheFiles!=null&&cacheFiles.size()!=0){
			List<String> imagePath=new ArrayList<String>();
			for (String path:cacheFiles) {
				File file = new File(path);
				if (file.exists()) {
					imagePath.add(path);
				}
			}
			if(imagePath.size()>0){
				adapter = new MyAdapter(this, imagePath, linBar, gallery,handler,mZoomView,rLzoomView);
				gallery.setAdapter(adapter);
			}
		}
		

	}
	ImageCallback callback=new ImageCallback() {
		@Override
		public void loadImage(Bitmap bitmap, String imagePath) {
			Logger.a();
			imageView3.setImageBitmap(bitmap);
		}
	};
	private InWares inWares;
	private SellWares sellWares;
	@Override
	protected void onDestroy() {
		gallery = null;
		super.onDestroy();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		int i = rLzoomView.getVisibility();
		int j = fl_gallery3d.getVisibility();
		System.out.println(i);
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (i == 0) {
				rLzoomView.setVisibility(View.GONE);
				return true;
			}
			if (j == 0) {
				fl_gallery3d.setVisibility(View.GONE);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
