package com.fada.sellsteward;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fada.sellsteward.domain.Category;
import com.fada.sellsteward.domain.InWares;
import com.fada.sellsteward.service.MyApp;
import com.fada.sellsteward.utils.ActivityUtils;
import com.fada.sellsteward.utils.ImageUtil;
import com.fada.sellsteward.utils.ImageUtil.ImageCallback;
import com.fada.sellsteward.utils.Logger;
import com.fada.sellsteward.utils.MyAsyncTask;
import com.fada.sellsteward.view.KeywordsFlow;

public class SearchActivity extends BaseActivity {
	private KeywordsFlow keywordsFlow; 

	public   List<Category> keywords=new ArrayList<Category>();
	public   List<InWares> inWaresList=new ArrayList<InWares>();
	private AutoCompleteTextView at_search;
	private ImageView ok;
	private MyAdapter adapter;
	private ListView lv_result;

	private String categoryName;

	private Intent intent;

	private List<Category> allCategory;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok:
			categoryName=at_search.getText().toString();
			getInWaresDate(categoryName);
//			keywordsFlow.rubKeywords();  
//			feedKeywordsFlow(keywordsFlow, keywords);  
//	        keywordsFlow.go2Show(KeywordsFlow.ANIMATION_OUT);  
			break;
		case R.id.at_search:
			//TODO 
			
			
		
			break;
		case R.id.btnBack:
//			keywordsFlow.rubKeywords();  
//			feedKeywordsFlow(keywordsFlow, keywords);  
//			keywordsFlow.go2Show(KeywordsFlow.ANIMATION_OUT);  
			finish();
			break;

		default:
			break;
		}

	}
/**
 * 获取搜索数据
 */
	private void getInWaresDate(final String categoryName) {
		
		if(categoryName!=null&&!categoryName.trim().equals("")){
			
			new MyAsyncTask() {
				@Override
				public void onPreExecute() {
					showProgressDialog();
					// TODO Auto-generated method stub
					
				}
				@Override
				public void doInBackground() {
					Category category = dao.queryCategoryByCategory(categoryName);
					if(category!=null&&category.get_id()!=null){
						List<InWares> listInWares = dao.queryInWaresByCategoryId(category.get_id());
						if(listInWares!=null){
							inWaresList.clear();
							inWaresList.addAll(listInWares);
						}
					}
					
					
				}
				@Override
				public void onPostExecute() {
					closeProgressDialog();
					if(inWaresList!=null&&inWaresList.size()>0){
						keywordsFlow.setVisibility(View.GONE);
						lv_result.setVisibility(View.VISIBLE);
						if(adapter==null){
							adapter=new MyAdapter();
							lv_result.setAdapter(adapter);
						}else{
							lv_result.requestLayout();
							adapter.notifyDataSetChanged();
						}	
					}else{
						keywordsFlow.setVisibility(View.VISIBLE);
						Logger.toast(SearchActivity.this, "没有搜索到结果");
					}
				}
				
			
			}.execute();
		}else{
			Logger.toast(SearchActivity.this, "请输入搜索内容");
		}
	}

	@Override
	public void setMyContentView() {
		setContentView(R.layout.activity_serach); 

	}

	@Override
	public void setListeners() {
	    ok.setOnClickListener(this);  
	    at_search.setOnClickListener(this);  
//	    btnBack.setOnClickListener(this);
	    KeyOnClickListener keyOnClickListener = new KeyOnClickListener();
	  	keywordsFlow.setOnItemClickListener(keyOnClickListener);

	}
	private static void feedKeywordsFlow(KeywordsFlow keywordsFlow, List<Category> arr) {  
		if(arr!=null&&arr.size()!=0){
			 Random random = new Random();  
			    for (int i = 0; i < KeywordsFlow.MAX; i++) {  
			        int ran = random.nextInt(arr.size());  
			        String tmp = arr.get(ran).getName();  
			        keywordsFlow.feedKeyword(tmp);  
			    }  
		}
	   
	}  
	@Override
	public void init() {
		title.setText("搜索");
		intent = getIntent();
		allCategory = dao.queryAllCategory();
		if(allCategory!=null){
			keywords.addAll(allCategory);
		}
		keywordsFlow = (KeywordsFlow) findViewById(R.id.frameLayout1);
		at_search = (AutoCompleteTextView) findViewById(R.id.at_search);
		setAutoText();
		at_search.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView textView=(TextView) view;
				categoryName=textView.getText().toString();
				getInWaresDate(categoryName);
				
			}
		});
		
		lv_result = (ListView) findViewById(R.id.expandableListView1);
		lv_result.setDivider(null);
		lv_result.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(intent!=null){
					int resultCode = intent.getFlags();
					if (resultCode==100) {
					Logger.d(null, resultCode+"");
					intent.putExtra("data", inWaresList.get(position));
					SearchActivity.this.setResult(resultCode, intent);
					finish();
					}else{
						InWares inWares = inWaresList.get(position);
						if (inWares!=null) {
							String type = "inWares";
							MyApp.app.obj=inWares;
							ActivityUtils.startActivityForData(SearchActivity.this, WaresActivity.class, type);
							finish();
						}
					}
				}
				
			}

		});
		lv_result.setVisibility(View.GONE);
		ok = (ImageView) findViewById(R.id.ok);
		keywordsFlow.setDuration(800l);  
		// 添加  
	    feedKeywordsFlow(keywordsFlow, keywords);  
	    keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);  
	    //keywordsFlow.rubKeywords();  
	   // keywordsFlow.rubKeywords();  
	  

	}
	/**
	 * 定义自动提示
	 */
	private void setAutoText() {
		new MyAsyncTask() {
			ArrayList<String> items = new ArrayList<String>();
			@Override
			public void onPreExecute() {
				
			}
			
			@Override
			public void onPostExecute() {
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchActivity.this, R.layout.textview_auto, items);
				at_search.setAdapter(adapter);
			}
			
			@Override
			public void doInBackground() {
				if(allCategory!=null){
					for (Category category : allCategory) {
						items.add(category.getName());
					}
			}
		}}.execute();
	
	}
	class KeyOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			categoryName=((TextView)v).getText().toString();
			Logger.d("categoryName:="+categoryName);
			 getInWaresDate(categoryName);
		}
	}
	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if(inWaresList==null)
				return 0;
			return inWaresList.size();
		}

		@Override
		public Object getItem(int position) {
			return inWaresList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			View view;
			if (convertView==null) {
				view = View.inflate(getApplicationContext(), R.layout.list_child_in_item, null);
				holder=new ViewHolder();
				holder.imageView1=(ImageView) view.findViewById(R.id.imageView1);
				holder.tvItemName=(TextView) view.findViewById(R.id.tvName);
				holder.tvItemType=(TextView) view.findViewById(R.id.tvType);
				holder.tvMoneySellTab=(TextView) view.findViewById(R.id.tvMoneySellTab);
				holder.tvMoneyIn=(TextView) view.findViewById(R.id.tvMoneyIn);
				holder.tvCode=(TextView) view.findViewById(R.id.tvCode);
				holder.tvisSell=(TextView) view.findViewById(R.id.tvisSell);
				view.setTag(holder);
			}else{
				view=convertView;
				holder=(ViewHolder) view.getTag();
				
			}
			InWares inWares = inWaresList.get(position);
		
			Bitmap bitmap = ImageUtil.loadImage(inWares.getWares().getImagePath(), null, callback);
			if(bitmap!=null)
				holder.imageView1.setImageBitmap(bitmap);
			holder.tvItemName.setText(inWares.getWares().getName());
			holder.tvItemType.setText(inWares.getWares().getCategory().getName());
			holder.tvMoneySellTab.setText(inWares.getTabPrice()+"");
			holder.tvMoneyIn.setText(inWares.getInPrice()+"");
			holder.tvCode.setText(inWares.getCode());
			int color = getResources().getColor(R.color.black);
			if(inWares.getAmount()-inWares.getIsSell()<=0){
				color = getResources().getColor(R.color.red);
				holder.tvisSell.setTextColor(color);
				holder.tvisSell.setText("总数:"+inWares.getAmount()+" 已售完");
			}else{
				holder.tvisSell.setTextColor(color);
				holder.tvisSell.setText("总数:"+inWares.getAmount()+"   剩余:"+(inWares.getAmount()-inWares.getIsSell()));
			}
			return view;
		}
		class ViewHolder{
			ImageView imageView1;
			TextView tvItemName;
			TextView tvItemType;
			TextView tvMoneySellTab;
			TextView tvMoneyIn;
			TextView tvCode;
			TextView tvisSell;
		}
  		
		ImageCallback callback=new ImageCallback() {
			
			@Override
			public void loadImage(Bitmap bitmap, String imagePath) {
				if(adapter==null){
					adapter=new MyAdapter();
					lv_result.setAdapter(adapter);
				}else{
					lv_result.requestLayout();
					adapter.notifyDataSetChanged();
				}
				
				
			}
		};
  	}
	
}
