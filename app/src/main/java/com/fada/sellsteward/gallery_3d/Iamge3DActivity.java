package com.fada.sellsteward.gallery_3d;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fada.sellsteward.R;
import com.fada.sellsteward.utils.Logger;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class Iamge3DActivity extends Activity {

	private MyGallery gallery;
	private List<String> cacheFiles;
	private File sdc;
	private RelativeLayout linBar;
	private RelativeLayout rLzoomView;
	private ImageZoomView mZoomView;
	FrameLayout backGallery;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			linBar.setVisibility(View.GONE);
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main3d);
		linBar = (RelativeLayout) findViewById(R.id.LinBar);
		rLzoomView = (RelativeLayout) findViewById(R.id.RLzoomView);
		 backGallery=(FrameLayout) findViewById(R.id.backGallery);
		gallery = (MyGallery) findViewById(R.id.myGallery);
		mZoomView = (ImageZoomView) findViewById(R.id.zoomView);
		cacheFiles = new ArrayList<String>();
		sdc = Environment.getExternalStorageDirectory();
		File file = new File(sdc, "DCIM/Camera/");
		if(file!=null&&file.exists()){
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				cacheFiles.add(fileList[i].getAbsolutePath());
			}
			// dialog = new ProgressDialog(this);
			// dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // 设置进度条样式
			// dialog.setTitle("加载中");
			// dialog.setMessage("请稍候...");
			// dialog.setCancelable(false);
			MyAdapter adapter = new MyAdapter(this, cacheFiles, linBar, gallery,handler,mZoomView,rLzoomView);
			gallery.setAdapter(adapter);
		}else{
			Logger.toast(this, "暂无照片");
		}
		
//		gallery.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				String imagePath = cacheFiles.get(position);
//				Intent intent = new Intent(Iamge3DActivity.this,ImageActivity.class);
//				intent.putExtra("imagePath", imagePath);
//				Iamge3DActivity.this.startActivity(intent);
//			}
//		});
		backGallery.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				rLzoomView.setVisibility(View.GONE);
				
			}
		});
	}

	@Override
	protected void onDestroy() {
		gallery = null;
		super.onDestroy();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// @ExportedProperty(mapping={@IntToString(from=0, to="VISIBLE"),
		// @IntToString(from=4, to="INVISIBLE"), @IntToString(from=8,
		// to="GONE")})
		int i = rLzoomView.getVisibility();
		System.out.println(i);
		if (i == 0) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK){
				rLzoomView.setVisibility(View.GONE);
				return true;
			}
		}
		
		
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
