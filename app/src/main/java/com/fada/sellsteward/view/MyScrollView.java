package com.fada.sellsteward.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;



public class MyScrollView extends ViewGroup{

	private GestureDetector gestureDetector;
	private Scroller scroller;
	
	private Context ctx;
	
	private IMyScrollListener myScrollListener;
	/**
	 * 当在xml中定义时,要实现这个方法
	 * @param context
	 * @param attrs
	 */
	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setWillNotDraw(true);
		// TODO Auto-generated constructor stub
	}

	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setWillNotDraw(true);
	}

	/**
	 * 当自已创建view时直接实现这个方法就可以
	 * @param context
	 */
	public MyScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		ctx = context;
		initView();
	}

	private void initView() {
		scroller = new Scroller(ctx);
		gestureDetector = new GestureDetector(ctx, new OnGestureListener() {
			
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
					float distanceY) {
				scrollBy((int)distanceX, 0);
				return false;
			}
			
			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//gestureDetector.onTouchEvent(event);
	//下面这样操作就可替换gestureDetector的onScroll方法了.
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastX = event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			
			scrollBy((int) (mLastX - event.getX()), 0);
			
			mLastX = event.getX();
			
			break;
		case MotionEvent.ACTION_UP:
			moveToDest();
			break;

		}
		
		return true;
	}
	
	
	/**
	 * 让view滑动到适当的位置 
	 * leo 2013-1-22
	 */
	private void moveToDest() {
		//得到当前处理屏幕中View的索引,注:哪个View存在超过半个ViewGroup就是当前的View
		int destId = (getScrollX()+getWidth()/2)/getWidth();
		
		moveToDest(destId);
	}

	
	public void moveToDest(int destId) {
		//当前view的偏移量:用当前处于屏幕中的View的索引乘以宽度去减去移动的距离就可以得到:画图可以理解
		int distance = destId*getWidth() - getScrollX();
		
		if(myScrollListener!=null){
			myScrollListener.moveToDest(destId);
		}
		
//		scrollBy(distance, 0);//
		
		scroller.startScroll(getScrollX(), getScrollY(), distance, 0,Math.abs(distance));
//		Tools.logleo("startScroll getScrollX:"+getScrollX());
		invalidate();
	}
	
	@Override
	public void computeScroll() {
		if(scroller.computeScrollOffset()){
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			invalidate();
		}
	}
	@Override
	protected void onDraw(Canvas canvas) {
		System.out.println("ondraw");
		super.onDraw(canvas);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		System.out.println("onMeasure");
		MeasureSpec.getSize(widthMeasureSpec);
		MeasureSpec.getMode(widthMeasureSpec);
		
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			view.measure(widthMeasureSpec, heightMeasureSpec);
		}
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(ev);
	}
	
	/**
	 * 水平移动时拖拽
	 */
	private boolean isDrop;
	
	private float mLastX;
	private float mLastY;
	
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastX = ev.getX();
			mLastY = ev.getY();
			isDrop = false;
			break;
		case MotionEvent.ACTION_MOVE:
			float distanceX = Math.abs(ev.getX()-mLastX);
			float distanceY = Math.abs(ev.getY() - mLastY);
			mLastX = ev.getX();
			mLastY = ev.getY();
			
			if(distanceX > 10 && distanceX> distanceY){
				isDrop = true;
			}
			
			break;
		case MotionEvent.ACTION_UP:
			isDrop = false;
			break;

		}
		return isDrop;
	}
	

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		System.out.println("onLayout");
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			view.layout(l+i*getWidth(), t, r+i*getWidth(), b);
		}
	}

	public IMyScrollListener getMyScrollListener() {
		return myScrollListener;
	}

	public void setMyScrollListener(IMyScrollListener myScrollListener) {
		this.myScrollListener = myScrollListener;
	}

	public interface IMyScrollListener{
		public void moveToDest(int destId);
	}
	
	
}
