package com.fada.sellsteward.gallery_3d;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

@SuppressWarnings("deprecation")
public class MyGallery extends Gallery {

	private int maxZoom = -250;// 图片缩放最大值
	private int maxRotateAngle = 50;// 最大选中角度
	private int centerOfGallery;
	private Camera camera = new Camera();

	public MyGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setStaticTransformationsEnabled(true);// 这个属性开启,图片才能变化,每个构造方法都要开启,因为不确定什么时候调用哪个方法去创建对象
	}

	public MyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		setStaticTransformationsEnabled(true);
	}

	public MyGallery(Context context) {
		super(context);
		setStaticTransformationsEnabled(true);
	}

	// 注:以下两个中心点都是以屏幕边缘参照为起始点
	// 得到要变形的那张图片中心点位置(也就是中心图片的旁边那张)用其离屏幕最左边的距离+自已宽的一半得到
	private int centerOfView(View view) {

		return view.getLeft() + view.getWidth() / 2;
	}

	// Gallery图片展示中点,则上减去左右边距,除以二,则得到Gallery宽度,然后加上左边距,则得到其与屏幕边缘的距离,为中心点坐标(参照屏幕边缘)
	private int centerOfGallery() {
		// setPadding(50, 0, 30, 0);
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
				+ getPaddingLeft();
	}

	// 控件大小改变 ,比如横竖屏切换,一启动应用也会执行这个方法,在这里定义中心点的初始化,则保证中心点保持正确.
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		centerOfGallery = centerOfGallery();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	// 一旦图片变化,就会执行这个方法
	@Override
	protected boolean getChildStaticTransformation(View child, Transformation t) {
		t.clear();// 清空上一次图片变形效果
		t.setTransformationType(Transformation.TYPE_MATRIX);// 设置图片变化的方式
		// 定义图片旋转,如果图片在正中,不旋转,如果不在则只让图片旋转对应的角度.如果超过50,则只让其只旋转50度
		int rotateAngle = 0;
		// 获得图片中心点坐标
		int centerOfChild = centerOfView(child);
		int width = child.getWidth();
		if (centerOfChild == centerOfGallery) {
			// 图片在中心位置 不旋转
			transformationBitmap((ImageView) child, t, rotateAngle);
		} else {
			// 旋转角度,用Gallery中心点减去要旋转图片的中心点坐标
			rotateAngle = (int) ((float) (centerOfGallery - centerOfChild)/ width * maxRotateAngle);//内旋
			//rotateAngle = (int) ((float) (centerOfChild - centerOfGallery)/ width * maxRotateAngle);//外旋
			// -50 < || > 50 取绝对值判断是否大于最大旋转角度,如果大于,再判断旋转角度的正负,代表图片的两端.
			if (Math.abs(rotateAngle) > maxRotateAngle) {
				rotateAngle = rotateAngle < 0 ? -maxRotateAngle: maxRotateAngle;
			}
			// 传入新的旋转角度,调用图片旋转方法.
			transformationBitmap((ImageView) child, t, rotateAngle);
		}
		return true;// 自已控制图片变化则返回true.而不调用下面的方法.
		// return super.getChildStaticTransformation(child, t);
	}

	// 让图片变形的方法
	private void transformationBitmap(ImageView child, Transformation t,
			int rotateAngle) {
		camera.save();// 记录图片变形效果

		Matrix imageMatrix = t.getMatrix();
		int rotate = Math.abs(rotateAngle);
		int width = child.getWidth();
		int height = child.getHeight();
		camera.translate(0.0f, 0.0f, 100.0f);

		if (rotate < maxRotateAngle) {
			// 定义一个图片缩放值.动态的
			float zoom = (float) (rotate * 1.5 + maxZoom);
			// 图片缩放,记住要是浮点数
			camera.translate(0.0f, 0.0f, zoom);
			// 图片透明度设置,让透明度随图片变化而变化. 255表示不透明,0表示完全透明,必须大于0
			// ,这里随便设(因为旋转角度最大才50,所以这里是大于0的)
			child.setAlpha((int) (255 - rotate * 2.5));
		}
		// 以下设置旋转,可以以x轴,也可以以y轴,也可以以z轴,也可以混合,效果都不一样.
		// X轴
		// camera.rotateX(rotateAngle);
		// z轴
		// camera.rotateZ(rotateAngle);
		// 以y轴 进行旋转,个人觉得比较好看.
		camera.rotateY(rotateAngle);
		// 设好后,将图片的效果交给imageMatrix
		camera.getMatrix(imageMatrix);
		// Preconcats matrix相当于右乘矩阵
		// Postconcats matrix相当于左乘矩阵。
		// imageMatrix.preTranslate(-(imageWidth/2), -(imageHeight/2));
		// imageMatrix.postTranslate((imageWidth/2), (imageHeight/2));
		/*
		 * 这两行代码意思可能就不那么明显了，先说如果不加这两行代码，会是一个什么情况， 默认情况下，动画是以对象的左上角为起点的，如果这样的话，
		 * 动画的效果就变成了可见对象在它的左上角开始，逐渐向右下角扩大，这显然不是我们期望的。
		 * 所以我们前面用到的halfWidth，halfHeight就用到了，这里保存了可见对象的一半宽度和高度，也就是中点，
		 * 使用上面这两个方法后，就会改变动画的起始位置，动画默认是从右下角开始扩大的，
		 * 使用matrix.preTranslate(-halfWidth, -halfHeight) 就把扩散点移到了中间，
		 * 同样，动画的起始点为左上角，使用matrix.postTranslate(halfWidth,
		 * halfHeight)就把起始点移到了中间， 这样就实现我们期望的效果了。
		 */

		imageMatrix.preTranslate(-(width / 2), -(height / 2));
		imageMatrix.postTranslate(width / 2, height / 2);

		camera.restore();// 还原

	}

}
