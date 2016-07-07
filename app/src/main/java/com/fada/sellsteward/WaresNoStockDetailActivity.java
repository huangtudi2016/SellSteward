package com.fada.sellsteward;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fada.sellsteward.domain.Category;
import com.fada.sellsteward.domain.Wares;
import com.fada.sellsteward.service.MyApp;
import com.fada.sellsteward.utils.ImageUtil;
import com.fada.sellsteward.utils.ImageUtil.ImageCallback;
import com.fada.sellsteward.utils.Logger;
import com.fada.sellsteward.utils.MyAsyncTask;
import com.fada.sellsteward.view.MyViewGroup;

public class WaresNoStockDetailActivity extends BaseActivity {
	private ListView waresLV;
	private List<Wares> waresList;
	private CheckBox cbTabSelector;
	private Intent intent;
	@Override
	public void setMyContentView() {
		setContentView(R.layout.detail_wares);
	}
	@Override
	public void init() {
		waresLV = (ListView) findViewById(R.id.expandableListView1);
		waresLV.setDivider(null);// 定义不显示默认的线条.
		cbTabSelector = (CheckBox) findViewById(R.id.cbTabSelector);
		cbTabSelector.setVisibility(View.GONE);
		btnBalanceSummary = (TextView) findViewById(R.id.btnBalanceSummary);
		intent=getIntent();
		int count = 0;
		title.setText("提醒");
		try {
			if (MyApp.app.obj != null) {
				waresList = (List<Wares>) MyApp.app.obj;
				count = waresList.size();
				MyApp.app.obj = null;
			}
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
		btnBalanceSummary.setText("总共:"+count+"种");
		if (waresList==null) {
			Logger.toast(this, "没有新的提醒");
		}else{
			adapter = new MyAdapter();
			waresLV.setAdapter(new MyAdapter());
		}
		//条目点击监听
		waresLV.setOnItemClickListener(new OnItemClickListener() {

			

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					if(intent!=null){
						int resultCode = intent.getFlags();
						intent.putExtra("data", waresList.get(position));
						if (resultCode==100) {
							WaresNoStockDetailActivity.this.setResult(resultCode, intent);
							finish();
						}else{
							intent.setClass(WaresNoStockDetailActivity.this, InputInWaresActivity.class);
							WaresNoStockDetailActivity.this.startActivity(intent);
						}
					}
			}
		});
		//滑动监听
		waresLV.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState==OnScrollListener.SCROLL_STATE_IDLE) {//SCROLL_STATE_IDLE表示当滚动停止状态.
					
				}
				
			}
			//这个方法用于获得一些滚动的数据
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
		//长按监听
		waresLV.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				showConfirmDialog("确认要清除这个提醒吗", 1);
				basePosition = position;
				return false;
			}
		});
		
		
	}
	private int basePosition;
	@Override
	public void getConfirmDialogOk(int flag) {
		switch (flag) {
		case 1:
				boolean b = waresList.remove(waresList.get(basePosition));
				if (b) {
					Logger.toast(this, "清除成功");
				} else {
					Logger.toast(this, "清除失败");
					return;
				}
				if (adapter == null&&waresList!=null) {
					adapter = new MyAdapter();
					waresLV.setAdapter(adapter);
				}
				//waresList = dao.queryAllWares();
				waresLV.requestLayout();
				adapter.notifyDataSetChanged();
			break;
		}
	}
  	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			int count=0;
			if(waresList!=null){
				count = waresList.size();
			}
			return count;
		}

		@Override
		public Object getItem(int position) {
			return waresList.get(position);
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
				view = View.inflate(getApplicationContext(), R.layout.list_child_wares_item, null);
				holder=new ViewHolder();
				holder.imageView1=(ImageView) view.findViewById(R.id.imageView1);
				holder.tvItemName=(TextView) view.findViewById(R.id.tvItemName);
				holder.tvItemType=(TextView) view.findViewById(R.id.tvItemType);
				holder.tvItemStock=(TextView) view.findViewById(R.id.tvItemStock);
				view.setTag(holder);
			}else{
				view=convertView;
				holder=(ViewHolder) view.getTag();
				
			}
			Wares wares = waresList.get(position);
			Bitmap bitmap = ImageUtil.loadImage(wares.getImagePath(), null, callback);
			if(bitmap!=null)
				holder.imageView1.setImageBitmap(bitmap);
			holder.imageView1.setImageBitmap(ImageUtil.getImageByMap(wares.getImagePath()));
			holder.tvItemName.setText(wares.getName());
			holder.tvItemType.setText(wares.getCategory().getName());
			holder.tvItemStock.setText(wares.getStock()+"");
			return view;
		}
		class ViewHolder{
			ImageView imageView1;
			TextView tvItemName;
			TextView tvItemType;
			TextView tvItemStock;
		}
		ImageCallback callback=new ImageCallback() {
			@Override
			public void loadImage(Bitmap bitmap, String imagePath) {
				waresLV.requestLayout();
				adapter.notifyDataSetChanged();
				
			}
		};
  	}
 
  	private MyAdapter adapter;
	private TextView btnBalanceSummary;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnBack:
			finish();
			break;
		}
		
		
	}
	

	
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        switch (resultCode) {
			
			case 100://获得商品名
				
				break;
			}
	        
	    }
	
	@Override
	public void setListeners() {
//		btnBack.setOnClickListener(this);
		
	}


	
}
