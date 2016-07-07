package com.fada.sellsteward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fada.sellsteward.db.SellStewardDao;
import com.fada.sellsteward.db.SellStewardDaoImpl;
import com.fada.sellsteward.domain.Category;
import com.fada.sellsteward.domain.InWares;
import com.fada.sellsteward.domain.SellWares;
import com.fada.sellsteward.domain.Wares;
import com.fada.sellsteward.service.MyApp;
import com.fada.sellsteward.utils.ActivityUtils;
import com.fada.sellsteward.utils.ImageUtil;
import com.fada.sellsteward.utils.ImageUtil.ImageCallback;
import com.fada.sellsteward.utils.Logger;
import com.fada.sellsteward.utils.MyAsyncTask;
import com.fada.sellsteward.utils.MyDateUtils;

public class SellDetailActivity extends BaseActivity {
	private ExpandableListView sellELV;
	private List<String> groupNames;
	private Map<Integer, List<SellWares>> childrenCacheInfos;
	private SellStewardDao dao;
	private MyAdapter adapter;
	private int basePosition;
	private int groupPosition;
	private CheckBox cbTabSelector;
	private TextView statOutgo;
	private TextView btnBalanceSummary,btnDatePrev,btnDateNext,btnDate;
	private int monthNow;
	private int yearNow;
	@Override
	public void setMyContentView() {
		setContentView(R.layout.detail_tab);
		cbTabSelector = (CheckBox) findViewById(R.id.cbTabSelector);
		btnDatePrev = (TextView)findViewById(R.id.btnDatePrev);
		btnDateNext = (TextView)findViewById(R.id.btnDateNext);
		btnDate = (TextView)findViewById(R.id.btnDate);
		statOutgo = (TextView) findViewById(R.id.statOutgo);
		btnBalanceSummary = (TextView) findViewById(R.id.btnBalanceSummary);
		sellELV = (ExpandableListView) findViewById(R.id.expandableListView1);
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(sellELV!=null&&adapter!=null&&MyApp.app.isRefresh){
			refreshData(true);
		}
	}
	@Override
	public void init() {
		title.setText("销售详情");
		if(dao==null){
			dao=SellStewardDaoImpl.getDaoInstance(this);
		}
		monthNow = MyDateUtils.formatMonth(System.currentTimeMillis());
		yearNow = MyDateUtils.formatYear(System.currentTimeMillis());
		btnDate.setText(yearNow+"年"+monthNow+"月");
		//初始化组集合
		groupNames=new ArrayList<String>();
		// 定义数组,前面表示组索引,后面表示组内容view的集合
		childrenCacheInfos = new HashMap<Integer,List<SellWares>>();
		refreshData(false);
//		adapter = new MyAdapter();
//		sellELV.setAdapter(new MyAdapter());
		sellELV.setGroupIndicator(null);// 定义不显示默认箭头.
		sellELV.setDivider(null);// 定义不显示默认的线条.
		sellELV.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				SellWares sellWares = childrenCacheInfos.get(groupPosition).get(childPosition);
				if (sellWares!=null) {
					String type = "sellWares";
					MyApp.app.obj=sellWares;
					ActivityUtils.startActivityForData(SellDetailActivity.this, WaresActivity.class, type);
				}
				return false;
			}
		});
		sellELV.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				itemLongClick(view);
				return false;
			}
		});
	}
	private void itemLongClick(View view){
		final int tag1 = (Integer)view.getTag(R.id.tvCategoryName);      
        final int tag2 = (Integer)view.getTag(R.id.tvName); 
		switch (tag1) {
		case -1://操作父item
			
			break;

		default://不是1,那就都是操作子view
			showConfirmDialog("确认要删除吗", 1);
			basePosition = tag1;
			groupPosition = tag2;
			break;
		}
	}
	@Override
	public void getConfirmDialogOk(int flag) {
		switch (flag) {
		case 1:
			try {
				List<SellWares> list = childrenCacheInfos.get(groupPosition);
				SellWares sellWares = list.get(basePosition);
				boolean b = dao.deleteSellWares(sellWares);
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
				throw new RuntimeException(e);
			}
			break;
		}
	}
	private int monthCount;
	private float monthAllSell;
	 //动态显示获取数据的方法
  	private void refreshData(final boolean isAdd) {
  		new  MyAsyncTask(){
  		
  			@Override
  			public void onPreExecute() {
  				//TODO
  				showProgressDialog();
  			}
  			@Override
  			public void doInBackground(){ 
				monthCount = dao.querySellWaresByMonthCount(yearNow, monthNow);
  				Logger.d("monthCount=="+monthCount);
  				monthAllSell = dao.querySellWaresByMonthAllSell(yearNow, monthNow);
  				Logger.d("monthAllSell=="+monthAllSell);
  				if(isAdd){
  					childrenCacheInfos.clear();
  					groupNames.clear();
  				}
  				List<SellWares> allSellWares = dao.querySellWaresByMonth(yearNow, monthNow, 0,0 );
  				Logger.d("allSellWares=="+allSellWares);
  				if(allSellWares!=null&&allSellWares.size()>0){
  					List<Category> categoryList = dao.queryAllCategory();
  					for (int i = 0; i < categoryList.size(); i++) {
  						groupNames.add(categoryList.get(i).getName());
					}
  					for (int i = 0; i < categoryList.size(); i++) {
  						int category_id = categoryList.get(i).get_id();
  						List<SellWares> sellList=new ArrayList<SellWares>();
  						for(SellWares sellWares:allSellWares){
  							int id=sellWares.getInWares().getWares().getCategory().get_id();
  		  					if(id==category_id){
  		  						sellList.add(sellWares);
  		  					}
  		  				}
  						childrenCacheInfos.put(i, sellList);
  					}
  				}
  			}
  			@Override
  			public void onPostExecute() {
  				MyApp.app.isRefresh=false;
  				closeProgressDialog();
  			//执行后关闭进度条
  				if (adapter==null) {//如果adapter为空则应该是第一次添加,那么创建一个
  					if(childrenCacheInfos!=null&&childrenCacheInfos.size()>0){
  						adapter=new MyAdapter();
  						sellELV.setAdapter(adapter);
  						sellELV.expandGroup(groupNames.size()-1);
  					}
  				}else{
  				//如果有就直接刷新,这样就避免了每次加载完都跳到第一行.
  				//但记住,这个跟数据库无直接关联,如果数据集合没有变化,这个是没有效果的,只有从重查询数据库得到新集合才可以..
  					sellELV.requestLayout();
  					adapter.notifyDataSetChanged();
  				}
  				statOutgo.setText(monthCount+"件");
  				btnBalanceSummary.setText("总销售额:"+monthAllSell+"元");
  			}}.execute();
  	}
  //getGroupCount与getChildrenCount是先于getView执行的,所以可以把查询数据库获取数据的方法定义在这里,让其一组一组获取.
  	private class MyAdapter extends BaseExpandableListAdapter {

  		// 返回多少个分组 注:这个一定要
  		@Override
  		public int getGroupCount() {
  			int count=0;
  			if(groupNames!=null){
  				count=groupNames.size();
  			}
  			return count;
  		}
  		//这个方法返回对应的每个分组的view个数 注:这个一定要
  		@Override
  		public int getChildrenCount(int groupPosition) {// 0 开始
  			List<SellWares> childreninfos;
  			if (childrenCacheInfos.get(groupPosition)!=null) {//这里首先判断一下是否集合里是否有这个组的数据
  				childreninfos = childrenCacheInfos.get(groupPosition);
  				return childreninfos.size();
  			}
  			return 0;
  		}
  		//返回对应的组对象的数据
  		@Override
  		public Object getGroup(int groupPosition) {
  			return childrenCacheInfos.get(groupPosition);
  		}
  		//返回对应组中对应的子view对象的数据
  		@Override
  		public Object getChild(int groupPosition, int childPosition) {
  			return childrenCacheInfos.get(groupPosition).get(childPosition);
  		}
  		//返回对应的组索引
  		@Override
  		public long getGroupId(int groupPosition) {
  			return groupPosition;
  		}
  		//返回对应的子view索引
  		@Override
  		public long getChildId(int groupPosition, int childPosition) {
  			return childPosition;
  		}
  		//若hasStableIds()是true，那么getChildId必须要返回稳定的ID
  		@Override
  		public boolean hasStableIds() {
  			//组和子元素是否持有稳定的ID,也就是底层数据的改变不会影响到它们。
  			//返回值:返回一个Boolean类型的值，如果为TRUE，意味着相同的ID永远引用相同的对象。
  			return false;
  		}
  		class ViewGroupHolder{
  			TextView tvCategoryName;
  		}
  		//返回对应的组view 注:必须有
  		@Override
  		public View getGroupView(int groupPosition, boolean isExpanded,
  				View convertView, ViewGroup parent) {
  			ViewGroupHolder groupHolder;
			View viewGroup;
			if (convertView==null) {
				viewGroup = View.inflate(getApplicationContext(), R.layout.list_catogroy_item, null);
				groupHolder=new ViewGroupHolder();
				groupHolder.tvCategoryName=(TextView) viewGroup.findViewById(R.id.tvCategoryName);
				viewGroup.setTag(groupHolder);
			}else{
				viewGroup=convertView;
				groupHolder=(ViewGroupHolder) viewGroup.getTag();
				
			}
			viewGroup.setTag(R.id.tvCategoryName, -1);
			viewGroup.setTag(R.id.tvName, groupPosition);
			String categoryName = groupNames.get(groupPosition);
			groupHolder.tvCategoryName.setText(categoryName);
			return viewGroup;
  		}
  		//返回组view下的子view 注:必须有
  		@Override
  		public View getChildView(int groupPosition, int childPosition,
  				boolean isLastChild, View convertView, ViewGroup parent) {
  			ViewHolder holder;
			View view;
			if (convertView==null) {
				view = View.inflate(getApplicationContext(), R.layout.list_child_sellitem, null);
				holder=new ViewHolder();
				holder.imageView1=(ImageView) view.findViewById(R.id.imageView1);
				holder.tvItemName=(TextView) view.findViewById(R.id.tvName);
				holder.tvAmount=(TextView) view.findViewById(R.id.tvAmount);
				holder.tvItemType=(TextView) view.findViewById(R.id.tvType);
				holder.tvProfit=(TextView) view.findViewById(R.id.tvProfit);
				holder.tvMoneyOut=(TextView) view.findViewById(R.id.tvMoneyOut);
				holder.tvCustomerName=(TextView) view.findViewById(R.id.tvCustomerName);
				holder.tvCustomerNum=(TextView) view.findViewById(R.id.tvCustomerNum);
				view.setTag(holder);
			}else{
				view=convertView;
				holder=(ViewHolder) view.getTag();
				
			}
			view.setTag(R.id.tvCategoryName, childPosition);
			view.setTag(R.id.tvName, groupPosition);
			SellWares sellWares = childrenCacheInfos.get(groupPosition).get(childPosition);
			if (sellWares==null) {
				return null;
			}
			InWares inWares = sellWares.getInWares();
			if(inWares==null){
				return null;
			}
			Wares wares = inWares.getWares();
			String imagePath=null;
			if (wares!=null) {
				imagePath = wares.getImagePath();
			}
			Bitmap bitmap=null;
			if(imagePath!=null)
				bitmap = ImageUtil.loadImage(sellWares.getInWares().getWares().getImagePath(), null, callback);
			if(bitmap!=null)
				holder.imageView1.setImageBitmap(bitmap);
			holder.tvItemName.setText(sellWares.getInWares().getWares().getName());
			holder.tvItemType.setText(sellWares.getInWares().getWares().getCategory().getName());
			holder.tvProfit.setText(sellWares.getProfit()+"");
			holder.tvMoneyOut.setText(sellWares.getOutPrice()+"");
			holder.tvCustomerName.setText(sellWares.getCustoms().getName());
			holder.tvCustomerNum.setText(sellWares.getCustoms().getPhone());
			holder.tvAmount.setText(""+sellWares.getAmount());
			return view;
		}
		
  		
  		//这信方法表示子view是否可以响应点击事件.true表示可以,false表示不可以.
  		@Override
  		public boolean isChildSelectable(int groupPosition, int childPosition) {
  			return true;
  		}
  		class ViewHolder{
			TextView tvAmount;
			ImageView imageView1;
			TextView tvItemName;
			TextView tvItemType;
			TextView tvProfit;
			TextView tvMoneyOut;
			TextView tvCustomerName;
			TextView tvCustomerNum;
			
		}
  		
		ImageCallback callback=new ImageCallback() {
			@Override
			public void loadImage(Bitmap bitmap, String imagePath) {
				if(adapter==null){
					adapter=new MyAdapter();
					sellELV.setAdapter(adapter);
				}
				sellELV.requestLayout();
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
		case R.id.btnDate:
			break;
		case R.id.cbTabSelector:
			ActivityUtils.startActivityForResult(this, InputSellActivity.class, null, 100);
			break;
		

		default:
			break;
		}
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 100:
			refreshData(true);
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	public void setListeners() {
//		btnBack.setOnClickListener(this);
		btnDatePrev.setOnClickListener(this);
		btnDateNext.setOnClickListener(this);
		btnDate.setOnClickListener(this);
		cbTabSelector.setOnClickListener(this);
	}


	
}
