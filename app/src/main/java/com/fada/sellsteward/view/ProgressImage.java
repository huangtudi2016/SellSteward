package com.fada.sellsteward.view;

import com.fada.sellsteward.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ProgressImage extends ImageView{

	private Drawable maskDraw;
	
	
	private int mProcess = 20;
	
	public ProgressImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		maskDraw = context.getResources().getDrawable(R.drawable.red_bg);
	}

	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		int topline = (int) (getHeight() - getHeight()*getmProcess()/100);
		canvas.clipRect(0, topline , getWidth(), getHeight());
		maskDraw.draw(canvas);
		canvas.restore();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		maskDraw.setBounds(0, 0, getWidth(), getHeight());
	}



	public float getmProcess() {
		return mProcess;
	}

	/**
	 * �����µĽ���Ժ��Զ�ˢ��
	 */
	public void setProgress(int mProcess) {
		if(mProcess>100 ){
			this.mProcess = 100;
		}
		if(mProcess<0 ){
			this.mProcess = 0;
		}
		
		
		this.mProcess = mProcess;
		invalidate();
	}
	
}
