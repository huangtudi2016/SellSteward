package com.fada.sellsteward;

import com.fada.sellsteward.utils.Logger;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InputTextActivity extends BaseActivity {
	private EditText etText;
	private RelativeLayout btnOk;
	private TextView tvCountPrompt;

	@Override
	public void setMyContentView() {
		setContentView(R.layout.input_text);
		etText = (EditText) findViewById(R.id.etText);
		btnOk = (RelativeLayout) findViewById(R.id.btnOk);
		tvCountPrompt = (TextView) findViewById(R.id.tvCountPrompt);
		
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnOk://确定
			if (i==0) {
				Logger.toast(InputTextActivity.this,"您输入超出字数啦!");
				return;
			}
			intent = getIntent();
			if (intent!=null) {
				int flag = intent.getFlags();
				intent.putExtra("data",etText.getText().toString());
				setResult(flag, intent);
			}
			finish();
			break;
		case R.id.btnBack://返回
			finish();
			break;
	
		}

	}
	private int i=1;
	private Intent intent;
	@Override
	public void setListeners() {
//		btnBack.setOnClickListener(this);
		btnOk.setOnClickListener(this);
		etText.addTextChangedListener(new TextWatcher() {
			
			//内容变化就会执行
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			//这里就可以作相应的处理,比如:实现边输入边查询
				String sAgeFormat = getResources().getString(R.string.weiboCharacterCanInput);  
				String sAgeFormat1 = getResources().getString(R.string.weiboInputTextExceedCount); 
				
				String text = s.toString();
				byte[] bs = text.getBytes();
				i = (280-bs.length)/2;
				String sFinalAge = String.format(sAgeFormat, i);  
				if (i<=0) {
					int ii=(bs.length-280)/2;
					sFinalAge = String.format(sAgeFormat1, ii);  
					//Logger.toast(InputTextActivity.this,"您输入超出字数啦!");
				}
				sFinalAge = String.format(sAgeFormat, i);  
				tvCountPrompt.setText(sFinalAge);
			}
			//内容变化前执行的方法
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			//内容变化后执行的方法
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}

	@Override
	public void init() {
		title.setText("编辑");

	}

}
