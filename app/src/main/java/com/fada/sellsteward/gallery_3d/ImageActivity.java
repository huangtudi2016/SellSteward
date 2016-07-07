package com.fada.sellsteward.gallery_3d;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.fada.sellsteward.R;

public class ImageActivity extends Activity {
	/** Called when the activity is first created. */
	private ImageZoomView mZoomView;
	private ZoomState mZoomState;
	private Bitmap mBitmap;
	private SimpleZoomListener mZoomListener;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mZoomView.setImage(mBitmap);
			mZoomState=new ZoomState();
			mZoomView.setZoomState(mZoomState);
			mZoomListener = new SimpleZoomListener();
			mZoomListener.setZoomState(mZoomState);
			mZoomView.setOnTouchListener(mZoomListener);
			resetZoomState();
		}
	};
	private String imagePath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		mZoomView = (ImageZoomView) findViewById(R.id.zoomView);
		File sdc = Environment.getExternalStorageDirectory();
		imagePath = getIntent().getStringExtra("imagePath");
		if (imagePath == null) {
			File file = new File(sdc, "DCIM/Camera/imghh.jpg");
			imagePath = file.getAbsolutePath();
		}
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				mBitmap = getZoomBitmap(imagePath);
				handler.sendEmptyMessage(0);
			}
		});
		thread.start();
	}
	/**
	 * 缩放图片
	 * 
	 * @param imagePath
	 * @return 一个缩放好的bitmap
	 */
	private Bitmap getZoomBitmap(String imagePath) {
		// 解决图片内存溢出问题
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;// 这样就只返回图片参数
		// 获取这个图片的宽和高
		Bitmap bm = BitmapFactory.decodeFile(imagePath, options); // 此时返回bm为空
		options.inJustDecodeBounds = false;// 上面操作完后,要设回来,不然下面同样获取不到实际图片
		// 计算缩放比
		int be = (int) (options.outHeight / (float) 300);
		// 上面算完后一下如果比200大,那就be就大于1,那么就压缩be,如果比200小,那图片肯定很小了,直接按原图比例显示就行
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;// be=2.表示压缩为原来的1/2,以此类推
		// 重新读入图片，注意在这之前要把options.inJustDecodeBounds 设为 false!
		bm = BitmapFactory.decodeFile(imagePath, options);
		
		return bm;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBitmap != null)
			mBitmap.recycle();
		// mZoomView.setOnTouchListener(null);
		// mZoomState.deleteObservers();
	}

	private void resetZoomState() {
		mZoomState.setPanX(0.5f);
		mZoomState.setPanY(0.5f);
		mZoomState.setZoom(1f);
		mZoomState.notifyObservers();
	}
}