package com.fada.sellsteward;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
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

import com.fada.sellsteward.db.SellStewardDaoImpl;
import com.fada.sellsteward.domain.InWares;
import com.fada.sellsteward.service.MyApp;
import com.fada.sellsteward.utils.ActivityUtils;
import com.fada.sellsteward.utils.ImageUtil;
import com.fada.sellsteward.utils.ImageUtil.ImageCallback;
import com.fada.sellsteward.utils.Logger;
import com.fada.sellsteward.utils.MyAsyncTask;
import com.fada.sellsteward.utils.MyDateUtils;

public class DetailInWaresActivity extends BaseActivity {
	private int basePosition;
	private List<InWares> inWaresList;
	private Intent intent;
	private CheckBox cbTabSelector;
	private TextView btnDatePrev;
	private TextView btnDateNext;
	private TextView btnBalanceSummary;
	private TextView statOutgo;
	private TextView btnDate;
	private MyAdapter adapter;
	private int limit=30;//这里面的30表示每次查出的数据条数,自已定义
	private int offset=1;//表示从第行数据开始查
	private int total;//这个表示总条目数
	private int yearNow;
	@Override
	public void setMyContentView() {
		setContentView(R.layout.detail_inware);
		cbTabSelector = (CheckBox) findViewById(R.id.cbTabSelector);
		btnDatePrev = (TextView)findViewById(R.id.btnDatePrev);
		btnDateNext = (TextView)findViewById(R.id.btnDateNext);
		btnDate = (TextView)findViewById(R.id.btnDate);
		btnBalanceSummary = (TextView)findViewById(R.id.btnBalanceSummary);
		lv = (ListView) findViewById(R.id.expandableListView1);
		statOutgo = (TextView)findViewById(R.id.statOutgo);
		inWaresList=new ArrayList<InWares>();
		 setListViewListener(lv);
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(lv!=null&&adapter!=null&&MyApp.app.isRefresh){
			refreshData(true);
		}
	}
	@Override
	public void init() {
		if(dao==null){
			dao=SellStewardDaoImpl.getDaoInstance(this);
		}
		btnRight.setText("搜索");
		title.setText("进货详情");
		monthNow = MyDateUtils.formatMonth(System.currentTimeMillis());
		yearNow = MyDateUtils.formatYear(System.currentTimeMillis());
		btnDate.setText(yearNow+"年"+monthNow+"月");
		refreshData(false);
		intent = getIntent();
		
	}
	private void setListViewListener(final ListView inwaresLV) {
		inwaresLV.setDivider(null);// 定义不显示默认的线条.
		inwaresLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(intent!=null){
					int resultCode = intent.getFlags();
					if (resultCode==100) {
					Logger.d(null, resultCode+"");
					intent.putExtra("data", inWaresList.get(position));
					DetailInWaresActivity.this.setResult(resultCode, intent);
					finish();
					}else{
						InWares inWares = inWaresList.get(position);
						if (inWares!=null) {
							String type = "inWares";
							MyApp.app.obj=inWares;
							ActivityUtils.startActivityForData(DetailInWaresActivity.this, WaresActivity.class, type);
						}
					}
				}
			}
		});
		inwaresLV.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				showConfirmDialog("确认要删除吗", 1);
				basePosition = position;
				return false;
			}
		});
		inwaresLV.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState==OnScrollListener.SCROLL_STATE_IDLE) {//SCROLL_STATE_IDLE表示当滚动停止状态.
					int position = inwaresLV.getLastVisiblePosition();//获得当前可见条目的最后一行索引
					int endPosition=inWaresList.size();//获得当前list的数据最后一行索引
					if (position==endPosition-1) {
						offset+=30;
						if (offset>total) {//如果大于总条目表示数据加载完了
							Logger.toast(DetailInWaresActivity.this, "没有更多数据了");
						}else{
							refreshData(false);//刷新数据
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
			}
		
		});
	}
	private int  monthNow;
	private float monthAllSell;
	private ListView lv;
	
	//动态显示获取数据的方法
  	private void refreshData(final boolean isAdd) {
  		new  MyAsyncTask(){
			@Override
  			public void onPreExecute() {
  				showProgressDialog();
  			}
  			@Override
  			public void doInBackground(){ 
  				monthAllSell = dao.queryInWaresByMonthInMoney(yearNow, monthNow);
  				total=dao.queryInWaresTotalByMonth(yearNow, monthNow);
  				if(isAdd){
  					offset=1;
  					inWaresList.clear();
  				}
  				List<InWares> list = dao.queryInWaresByMonth(yearNow, monthNow, offset, limit);
  				if(list!=null){
  					inWaresList.addAll(list);
  				}
  			}
  			@Override
  			public void onPostExecute() {
  					MyApp.app.isRefresh=false;
	  				closeProgressDialog();
	  				btnBalanceSummary.setText("入库总额:"+monthAllSell+"元");
	  				statOutgo.setText(total+"件");
		  			if(adapter==null){
		  				adapter=new MyAdapter();
		  				lv.setAdapter(adapter);
		  			}else{
		  				lv.requestLayout();
			  			adapter.notifyDataSetChanged();
		  			}
		  			
  				}
  			}.execute();
  	}
  	
	
	@Override
	public void getConfirmDialogOk(int flag) {
		switch (flag) {
		case 1:
			try {
				
				boolean b = dao.deleteInWares(inWaresList.get(basePosition));
				if (b) {
					Logger.toast(this, "删除成功");
				}else{
					Logger.toast(this, "删除失败");
					return;
				}
				refreshData(true);
				
			} catch (Exception e) {
				Logger.toast(this, e.toString());
				e.printStackTrace();
			}
			break;

		default:
			break;
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
					lv.setAdapter(adapter);
				}else{
					lv.requestLayout();
					adapter.notifyDataSetChanged();
				}
				
				
			}
		};
  	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRight:
			ActivityUtils.startActivity(this, SearchActivity.class);
			break;
		case R.id.btnBack:
			finish();
			break;
		case R.id.cbTabSelector:
			ActivityUtils.startActivityForResult(this, InputInWaresActivity.class, null, 100);
			 break;
		case R.id.btnDatePrev:
			if(monthNow-1<=0){
				monthNow=12;
				yearNow-=1;
			}else{
				monthNow=monthNow-1;
			}
			btnDate.setText(yearNow+"年"+monthNow+"月");
			refreshData(true);
			break;
		case R.id.btnDateNext:
			if(monthNow+1>12){
				monthNow=1;
				yearNow+=1;
			}else{
				monthNow=monthNow+1;
			}
			btnDate.setText(yearNow+"年"+monthNow+"月");
			refreshData(true);
			break;
		
		}
		
	}
	
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        switch (resultCode) {
			case 100://获得商品名
				refreshData(true);
				break;
			}
	    }
	
	@Override
	public void setListeners() {
		cbTabSelector.setOnClickListener(this);
//		btnBack.setOnClickListener(this);
		btnRight.setOnClickListener(this);
		btnDatePrev.setOnClickListener(this);
		btnDateNext.setOnClickListener(this);
		
	}


	
}
