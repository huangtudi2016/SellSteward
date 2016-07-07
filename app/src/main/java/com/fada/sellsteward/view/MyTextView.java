package com.fada.sellsteward.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView{

	private TextPaint mPaint;

	private int currentX = 0;
	
	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		mPaint = new TextPaint(getPaint());
        mPaint.setStyle(TextPaint.Style.FILL_AND_STROKE);
//        mPaint.setShadowLayer(2.0F, 2.0F, 2.0F, Color.RED);
        mPaint.setColor(Color.RED);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.save();
        canvas.clipRect(0, 0, currentX, getBottom());
        canvas.drawText(getText().toString(), 0, getBaseline(), mPaint);
        canvas.restore();
       
        if (currentX<=getWidth()) {
        	 currentX++;
            invalidate();
		}
        
	}
}
