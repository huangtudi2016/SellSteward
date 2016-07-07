package com.fada.sellsteward.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MyViewGroup extends ViewGroup {

	private Context context;
	private GestureDetector detector;
	private MyScrollListener myScrollListener;

	/**
	 * 当自已创建view时直接实现这个方法就可以
	 * @param context
	 */
	public MyViewGroup(Context context) {
		super(context);
		this.context=context;
		scroller = new Scroller(context);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			view.measure(widthMeasureSpec, heightMeasureSpec);
		}
	}
	/**
	 * 这个定义里面的子view是以什么方式排列的,ViewGroup是固定的,但里面的子view大小是不受限制的.
	 * changed表示布局是否改变,后面依次是左,上,右,下(都是指ViewGroup)
	 * getChildCount表示获得所有的子view的个数
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			view.layout(l+i*getWidth(), t, r+i*getWidth(), b);
		}

	}
	//float mLastX;
	private Scroller scroller;
	private float mLastX;
	private float mLastY;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//这里这个mLastX必须和onInterceptTouchEvent的mLastX是同一个值,不然会得到的偏移值会不准,移动会乱.
			mLastX=event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			//对于滑动而言正负,是相对于屏幕,也就是左划,移动需要一个正值,右划,需要一个负值.
			float instace=mLastX-event.getX();
			scrollBy((int)instace, 0);
			mLastX = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			if(getDisID()<=0){
				moveToSet(0);
				return true;
			}
			if(getDisID()>=getChildCount()-1){
				moveToSet(getChildCount()-1);
				return true;
			}
			moveSet();
			break;
		}
		return true;
	}
	//处理事件是否要传递
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastX = ev.getX();
			mLastY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			float absx = Math.abs(ev.getX()-mLastX);
			float absy = Math.abs(ev.getY()-mLastY);
			mLastX = ev.getX();
			mLastY = ev.getY();
			if (Math.abs(absx)>10&&absx>absy) {
				return true;
			}
			break;

		}
		return false;
	}

	/**
	 * 计算获得当前处理屏幕上的View的id
	 * @return 当前view的id
	 */
	private int getDisID() {
		int disId=(getScrollX()+getWidth()/2)/getWidth();
		return disId;
		
	}
	private void moveSet() {
		int disId=getDisID();
		moveToSet(disId);
		
	}

	private void moveToSet(int disId) {
		int instace=disId*getWidth()-getScrollX();
		if(myScrollListener!=null){
			myScrollListener.moveToSet(disId);
		}
		scroller.startScroll(getScrollX(), getScrollY(), instace, 0,Math.abs(instace));
		invalidate();
		
		//表示在当前位置的基础上,x偏移instace ,y方向偏移0 而ScrollTo是偏移至里面的坐标.
		//scrollBy(instace, 0);
	}
	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			invalidate();
		}
	}
	

	/**
	 * 当在xml中定义时,要实现这个方法
	 * @param context
	 * @param attrs
	 */
	public MyViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public void setOnMyScrollListener(MyScrollListener l){
		this.myScrollListener=l;
	}
	public interface MyScrollListener{
		void moveToSet(int setId);
	}

}
