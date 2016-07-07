package com.fada.sellsteward.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


public class ImageUtil {
	public static final int HARD_CACHE_CAPACITY = 30;// 定义map集合存的图片数量

	public final static HashMap<String, Bitmap> mHardBitmapCache = new LinkedHashMap<String, Bitmap>(
			HARD_CACHE_CAPACITY / 2, 0.75f, true) {
		public static final long serialVersionUID = -6716765964916804088L;

		@Override
		protected boolean removeEldestEntry(
				LinkedHashMap.Entry<String, Bitmap> eldest) {
			if (size() > HARD_CACHE_CAPACITY) {
				// 当map的size大于30时，把最近不常用的key放到mSoftBitmapCache中，从而保证mHardBitmapCache的效率
				mSoftBitmapCache.put(eldest.getKey(),
						new SoftReference<Bitmap>(eldest.getValue()));
				return true;
			} else
				return false;
		}
	};
	/**
	 *TODO 当mHardBitmapCache的key大于30的时候，会根据LRU算法把最近没有被使用的key放入到这个缓存中。
	 * Bitmap使用了SoftReference，当内存空间不足时，此cache中的bitmap会被垃圾回收掉
	 */
	public static Map<String, SoftReference<Bitmap>> mSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			HARD_CACHE_CAPACITY / 2);

	/**
	 *TODO 从缓存中获取图片
	 */
	public static Bitmap getBitmapFromCache(String imagePath) {
		// 先从mHardBitmapCache缓存中获取
		synchronized (mHardBitmapCache) {
			final Bitmap bitmap = mHardBitmapCache.get(imagePath);
			if (bitmap != null) {
				// 如果找到的话，把元素移到linkedhashmap的最前面，从而保证在LRU算法中是最后被删除
				mHardBitmapCache.remove(imagePath);
				mHardBitmapCache.put(imagePath, bitmap);
				return bitmap;
			}
		}
		// 如果mHardBitmapCache中找不到，到mSoftBitmapCache中找,但要先判断一下这里是否包含key,如果不包含肯定没有
		if (mSoftBitmapCache.containsKey(imagePath)) {
			SoftReference<Bitmap> bitmapReference = mSoftBitmapCache
					.get(imagePath);
			if (bitmapReference != null) {
				final Bitmap bitmap = bitmapReference.get();
				if (bitmap != null) {
					return bitmap;
				} else {
					// 如果key存在,但Soft里的Bitmap为空,那么清除这一组对象
					mSoftBitmapCache.remove(imagePath);
				}
			}
		}
		return null;
	}
	/**
	 * 
	 *TODO @author Mathew
	 *
	 */
	public interface ImageCallback{
		public void loadImage(Bitmap bitmap,String imagePath);
	}
	/**
	 *TODO 从本地或者服务端加载图片
	 * @return
	 * @throws IOException 
	 */
	public static Bitmap loadImage(final String imagePath,final String imgUrl,final ImageCallback callback) {
		Bitmap bitmap = getImageByMap(imagePath);
		if(bitmap != null){
			return bitmap;
		}else{//从网上加载
			final Handler handler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					if(msg.obj!=null){
						Bitmap bitmap = (Bitmap) msg.obj;
						callback.loadImage(bitmap, imagePath);
					}
				}
			};
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
//						Looper looper = Looper.getMainLooper();
//						if(looper==null)Looper.prepare();
//						Looper.loop();
						Message msg = handler.obtainMessage();
						if(imgUrl==null){
							putImageToMap(imagePath);
							handler.sendMessage(msg);
						}else{
							URL url = new URL(imgUrl);
							Logger.e("图片加载", imgUrl);
							URLConnection conn = url.openConnection();
							conn.setConnectTimeout(5000);
							conn.setReadTimeout(5000);
							conn.connect();
							BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(),8192) ;
							Bitmap bitmap = BitmapFactory.decodeStream(bis);
							//保存文件到sd卡
							saveToSD(imagePath,bitmap);
							msg.what=Constant.GET_IMAGE_SUCCESS;
							msg.obj = bitmap;
							handler.sendMessage(msg);
						}
					} catch (IOException e) {
						Log.e(ImageUtil.class.getName(), "保存图片到本地存储卡出错！");
					}
				}
			};
			ThreadPoolManager.getInstance().addTask(runnable);
		}
		return null;
	}
	/**
	 *TODO 保存图片至sd卡
	 * @param bitmap
	 */
	public static void saveToSD(final String fileName, final Bitmap bitmap) {
		new Thread() {
			public void run() {
				FileOutputStream b = null;
				try {
					b = new FileOutputStream(fileName);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						b.flush();
						b.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();

	}
	/**
	 *TODO 保存图片至sd卡 用默认路径
	 * @param bitmap
	 */
	public static String saveToSD(Bitmap bitmap) {
		File directory = Environment.getExternalStorageDirectory();
		File file = new File(directory,"SellImage/");
		if (!file.exists()) {
			file.mkdirs();// 创建文件夹
		}
		String fileName = new File(file, System.currentTimeMillis()+".jpg").getAbsolutePath();
		saveToSD(fileName,bitmap);
		return fileName;
	}

	/**
	 *TODO 从Map获得图片,如果没有,则添加
	 * 
	 * @param imagePath
	 *            String图片路径
	 */
	public static Bitmap getImageByMap(String imagePath) {
		Bitmap bitmap = getBitmapFromCache(imagePath);
		if (bitmap==null) {
			return null;
		}
		return bitmap;
	}
	/**
	 *TODO 添加3DBitMap进map集合,并与路径对应
	 * 
	 * @param imagePath
	 *            String图片路径
	 */
	public static void put3DImageToMap(String imagePath) {
		if (!mHardBitmapCache.containsKey(imagePath)) {
			Bitmap bitmap = get3DImageView(imagePath);
			if (bitmap==null) {
				return;
			}
			mHardBitmapCache.put(imagePath, bitmap);
		}
	}
	/**
	 *TODO 添加BitMap进map集合,并与路径对应
	 * 
	 * @param imagePath
	 *            String图片路径
	 */
	public static void putImageToMap(String imagePath) {
		if (!mHardBitmapCache.containsKey(imagePath)) {
			Bitmap bitmap = getZoomBitmap(imagePath, 150f);
			if (bitmap==null) {
				return;
			}
			mHardBitmapCache.put(imagePath, bitmap);
		}
	}
	
	/**
	 *TODO 将一个图片压缩并制成3D模式
	 * 
	 * @param imagePath
	 *            图片路径
	 * @return Bitmap
	 */
	public static Bitmap get3DImageView(String imagePath) {
		Bitmap bm = getZoomBitmap(imagePath,200f);
		if (bm==null) {
			return null;
		}
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 获得矩形,用于对图片进行变换，以拉伸，扭曲,反转等特效,对于颜色处理用ColorMatrix
		Matrix matrix = new Matrix();
		// 水平翻转 x轴 -1
		// 垂直翻转 y轴 -1
		matrix.setScale(1, -1);
		Bitmap reflectionBitmap = Bitmap.createBitmap(bm, 0, height / 2, width,
				height / 2, matrix, false);
		// 绘制空的新图片
		Bitmap bitmap = Bitmap.createBitmap(width, height + height / 2,
				Config.ARGB_8888);
		// 给空的新图片进行上图
		Canvas canvas = new Canvas(bitmap);
		// 让Canvas消除锯齿,这样图片会平滑
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG));
		// 从上到下,绘原图:资源图片,左上角坐标(x,y)这里都是0,画笔(因为图片已经有了,所以不需要画笔)
		canvas.drawBitmap(bm, 0, 0, null);
		// 绘制一个空白矩形,让立体感更强
		int reflectionGap = 4;// 定义中问矩形的高度
		// 画矩形,前四个参数表示两个坐标,一个左上角(相对于原图起始点),一个右下角(相对于左上角,没有负),
		canvas.drawRect(0, height, width, height + reflectionGap, new Paint());
		// 画反转图
		canvas.drawBitmap(reflectionBitmap, 0, height + reflectionGap, null);
		// ps 遮罩 、 线性渐变
		Paint paint = new Paint();
		// paint 遮罩
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// 线性渐变
		// 0x70ffffff, 0x00ffffff,
		/**
		 * //线性渐变填充 shader着色器 //在位图上Y方向花砖模式 TileMode：（一共有三种） CLAMP
		 * ：如果渲染器超出原始边界范围，会复制范围内边缘染色。 REPEAT ：横向和纵向的重复渲染器图片，平铺。 MIRROR
		 * ：横向和纵向的重复渲染器图片，这个和REPEAT 重复方式不一样，他是以镜像方式平铺。
		 * 参数前面表示两个坐标:开始渐变的位置和结束渐变的位置.这里设的是反转图片位置.后面是渐变的两个颜色.最后是一个模式.
		 */
		LinearGradient shader = new LinearGradient(0, height, 0,
				bitmap.getHeight(), 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);// 设置刷子 着色器 （上色）
		paint.setAntiAlias(true);// paint设置使图片平滑 消除锯齿
		// 这里的两个坐标是相对于全局坐标点(也就是整个图片的左上角(0,0))
		canvas.drawRect(0, height, width, bitmap.getHeight(), paint);

		return bitmap;
	}

	/**
	 *TODO 缩放图片
	 * 
	 * @param imagePath
	 * @param Float scale 压缩比率 可以为空,那么是默认是200,越大压缩比越小
	 * @return 一个缩放好的bitmap
	 */
	public static Bitmap getZoomBitmap(String imagePath,Float scale) {
		if (scale==null) 
			scale=200f;
		// 解决图片内存溢出问题
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;// 这样就只返回图片参数
		// 获取这个图片的宽和高
		Bitmap bm = BitmapFactory.decodeFile(imagePath, options); // 此时返回bm为空
		options.inJustDecodeBounds = false;// 上面操作完后,要设回来,不然下面同样获取不到实际图片
		// 计算缩放比
		int be = (int) (options.outHeight / (float) scale);
		// 上面算完后一下如果比200大,那就be就大于1,那么就压缩be,如果比200小,那图片肯定很小了,直接按原图比例显示就行
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;// be=2.表示压缩为原来的1/2,以此类推
		// 重新读入图片，注意在这之前要把options.inJustDecodeBounds 设为 false!
		bm = BitmapFactory.decodeFile(imagePath, options);
		
		return bm;
	}
}
