package com.fada.sellsteward;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fada.sellsteward.domain.InWares;
import com.fada.sellsteward.utils.ActivityUtils;
import com.fada.sellsteward.utils.ImageUtil;
import com.fada.sellsteward.utils.MyAsyncTask;
import com.fada.sellsteward.utils.MyDateUtils;
import com.fada.sellsteward.utils.ImageUtil.ImageCallback;
import com.fada.sellsteward.utils.Logger;
import com.fada.sellsteward.view.MyViewGroup;
import com.fada.sellsteward.view.MyViewGroup.MyScrollListener;

public class DetailInWaresSellActivity extends BaseActivity {
	private int basePosition;
	private LinearLayout group;
	private MyViewGroup viewGroup;
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
	private int offset=0;//表示从第行数据开始查
	private int total;//这个表示总条目数
	private Map<Integer,Integer> totalMap;
	private Map<Integer,ListView> viewMap;
	
	private Map<Integer,Integer> groupMap;
	private int setIdNow;//现在展示的Listview的id
	private int yearNow;
	@Override
	public void setMyContentView() {
		setContentView(R.layout.detail_inware);
		group = (LinearLayout) findViewById(R.id.statBaselayout);
		cbTabSelector = (CheckBox) findViewById(R.id.cbTabSelector);
		btnDatePrev = (TextView)findViewById(R.id.btnDatePrev);
		btnDateNext = (TextView)findViewById(R.id.btnDateNext);
		btnBalanceSummary = (TextView)findViewById(R.id.btnBalanceSummary);
		statOutgo = (TextView)findViewById(R.id.statOutgo);
		btnDate = (TextView)findViewById(R.id.btnDate);
		
	
	}
	@Override
	public void init() {
		title.setText("未售商品");
		viewGroup = new MyViewGroup(this);
		totalMap = new HashMap<Integer, Integer>();//月份与该月商品总数的集合
		viewMap = new HashMap<Integer,ListView>();//月份与对应的ListView的集合
		groupMap = new HashMap<Integer,Integer>();//groupView的id与对应月份的集合
		monthNow = MyDateUtils.formatMonth(System.currentTimeMillis());
		yearNow = MyDateUtils.formatYear(System.currentTimeMillis());
		btnDate.setText(yearNow+"年"+monthNow+"月");
		refreshData();
		intent = getIntent();
		viewGroup.setOnMyScrollListener(new MyScrollListener() {
			@Override
			public void moveToSet(int setId) {
				 Integer month = groupMap.get(setId);
				 if(month!=null&&setIdNow!=setId){
					 refreshDataByMonth(month);
					 setIdNow = setId;
					 totalNow=totalMap.get(month);
					 btnDate.setText(yearNow+"年"+month+"月");
				 }
			}
		});
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
					DetailInWaresSellActivity.this.setResult(resultCode, intent);
					finish();
					}else{
						InWares inWares = inWaresList.get(position);
						if (inWares!=null) {
							String code = inWares.getCode();
							if (code!=null) {
								ActivityUtils.startActivityForData(DetailInWaresSellActivity.this, WaresActivity.class, code);
							}
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
						if (offset>totalNow) {//如果大于总条目表示数据加载完了
							Toast.makeText(DetailInWaresSellActivity.this, "没有更多数据了", 1).show();
						}
						//refreshData();//刷新数据
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
	private int totalNow;
	private int  monthNow;
	private float monthAllSell;
	
	//动态显示获取数据的方法
  	private void refreshData() {
  		new  MyAsyncTask(){
			@Override
  			public void onPreExecute() {
  				
  				showProgressDialog();
  			}
  			@Override
  			public void doInBackground(){ 
  			//执行查询前,得到总条目
  				if (total==0) {
  					monthAllSell = dao.queryInWaresSellByMonthInMoney(yearNow, monthNow);
  					total=dao.queryInWaresTotal();
  					InWares inWaresStart = dao.queryInWaresById(1);
  					InWares inWaresend = dao.queryInWaresById(total);
  					if(inWaresStart!=null&&inWaresend!=null){
  						int monthStart = MyDateUtils.formatMonth(inWaresStart.getInTime());
  						int monthEnd = MyDateUtils.formatMonth(inWaresend.getInTime());
  						int j=0;
  						for (int i = monthStart; i <= monthEnd; i++) {
							int count = dao.queryInWaresSellByMonthCount(yearNow, i);
							if (count==0) {
								continue;
							}
							totalMap.put(i,count);
							View view = getLayoutInflater().inflate(R.layout.group_listview, null);
							ListView lv= (ListView) view.findViewById(R.id.expandableListView1);
							viewGroup.addView(view);
							viewMap.put(i, lv);
							groupMap.put(j,i);
							j++;
						}
  						
  					}
  				}
  				if (totalMap.size()!=0) {
					totalNow = totalMap.get(monthNow);
				}
  				//得到adapter需要的数据集合,没有直接创建
  				if (inWaresList==null&&totalNow!=0) {
  					inWaresList = dao.queryInWaresSellByMonth(yearNow, monthNow, offset, limit);
  				}else{
  				//有的话直接把得到的集合添加进去
  					if(inWaresList!=null)
  						inWaresList.addAll(dao.queryInWaresSellByMonth(yearNow, monthNow, offset, limit));
  				}
  			}
  			@Override
  			public void onPostExecute() {
  				//TODO
  				closeProgressDialog();
  				btnBalanceSummary.setText("入库总额:"+monthAllSell+"元");
  				statOutgo.setText(totalNow+"件");
  				if(viewMap.size()>0){
  					group.addView(viewGroup);
  				}
  				if(viewMap.size()>0&&viewMap.get(monthNow)!=null){
	  			//执行后关闭进度条
	  				if (adapter==null) {//如果adapter为空则应该是第一次添加,那么创建一个
	  					adapter=new MyAdapter();
	  					if(inWaresList!=null)
	  						viewMap.get(monthNow).setAdapter(adapter);
	  				}else{
	  				//如果有就直接刷新,这样就避免了每次加载完都跳到第一行.
	  				//但记住,这个跟数据库无直接关联,如果数据集合没有变化,这个是没有效果的,只有从重查询数据库得到新集合才可以..
	  					viewMap.get(monthNow).requestLayout();
	  					
	  				}
	  				adapter.notifyDataSetChanged();
	  				setListViewListener(viewMap.get(monthNow));
  				}
  			}}.execute();
  	}
  	/**
  	 * 刷新某一个月份的集合
  	 */
  	private void refreshDataByMonth(final int month) {
  		new  MyAsyncTask(){
  			@Override
  			public void onPreExecute() {
  				
  				showProgressDialog();
  			}
  			@Override
  			public void doInBackground(){ 
  				if (totalMap.size()!=0) {
  					totalNow = totalMap.get(month);
  				}
  				monthAllSell = dao.queryInWaresSellByMonthInMoney(yearNow, month);
  				//得到adapter需要的数据集合,没有直接创建
  				if (inWaresList==null&&totalNow!=0) {
  					inWaresList = dao.queryInWaresSellByMonth(yearNow, month, offset, limit);
  				}else{
  					//有的话直接把得到的集合添加进去
  					if(inWaresList!=null)
  						inWaresList.addAll(dao.queryInWaresSellByMonth(yearNow, month, offset, limit));
  				}
  			}
  			@Override
  			public void onPostExecute() {
  				//TODO
  				closeProgressDialog();
  				btnBalanceSummary.setText("入库总额:"+monthAllSell+"元");
  					statOutgo.setText(totalNow+"件");
  				//执行后关闭进度条
  				if(viewMap.get(month)!=null){
	  				if (inWaresList!=null&&monthNow!=month) {//如果adapter为空则应该是第一次添加,那么创建一
	  					viewMap.get(month).setAdapter(adapter);
	  					monthNow=month;
	  				}else{
	  					//如果有就直接刷新,这样就避免了每次加载完都跳到第一行.
	  					//但记住,这个跟数据库无直接关联,如果数据集合没有变化,这个是没有效果的,只有从重查询数据库得到新集合才可以..
	  					viewMap.get(month).requestLayout();
	  					adapter.notifyDataSetChanged();
	  				}
	  				setListViewListener(viewMap.get(month));
  				}
  			}}.execute();
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
				
				inWaresList=dao.queryAllInWares();
				if(adapter==null){
					adapter=new MyAdapter();
					viewMap.get(monthNow).setAdapter(adapter);
				}
				viewMap.get(monthNow).requestLayout();
				adapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
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
			if(inWares.getIsSell()==1){
				holder.tvisSell.setTextColor(color);
				holder.tvisSell.setText("未售");
			}else{
				color = getResources().getColor(R.color.red);
				holder.tvisSell.setTextColor(color);
				holder.tvisSell.setText("已售出");
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
					viewMap.get(monthNow).setAdapter(adapter);
				}
				viewMap.get(monthNow).requestLayout();
				adapter.notifyDataSetChanged();
				
			}
		};
  	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnBack:
			finish();
			break;
		case R.id.cbTabSelector:
			ActivityUtils.startActivityForResult(this, InputInWaresActivity.class, null, 100);
			 break;
		case R.id.btnDatePrev:
			//TODO
			showEditDialog(1);
			break;
		case R.id.btnDateNext:
			//TODO
			showEditDialog(2);
			break;
		
		}
		
	}
	
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        switch (resultCode) {
			
			case 100://获得商品名
				 inWaresList = dao.queryAllInWares();
				if(adapter==null){
					adapter=new MyAdapter();
					if(viewMap.get(monthNow)!=null){
						viewMap.get(monthNow).setAdapter(adapter);
						viewMap.get(monthNow).requestLayout();
					}else{
						refreshData();
					}
					
				}
				
				adapter.notifyDataSetChanged();
				break;
			}
	        
	    }
	
	@Override
	public void setListeners() {
		cbTabSelector.setOnClickListener(this);
//		btnBack.setOnClickListener(this);
		btnDatePrev.setOnClickListener(this);
		btnDateNext.setOnClickListener(this);
		
	}


	
}
