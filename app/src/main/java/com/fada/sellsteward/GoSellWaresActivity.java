package com.fada.sellsteward;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fada.sellsteward.domain.Customer;
import com.fada.sellsteward.domain.InWares;
import com.fada.sellsteward.domain.SellWares;
import com.fada.sellsteward.service.MyApp;
import com.fada.sellsteward.utils.ActivityUtils;
import com.fada.sellsteward.utils.ImageUtil;
import com.fada.sellsteward.utils.ImageUtil.ImageCallback;
import com.fada.sellsteward.utils.Logger;
import com.fada.sellsteward.utils.MyAsyncTask;
import com.fada.sellsteward.utils.MyDateUtils;
import com.fada.sellsteward.view.scan.ErcodeScanActivity;

public class GoSellWaresActivity extends BaseActivity {
	private List<InWares> inWaresList;
	private CheckBox cbTabSelector;
	private TextView btnBalanceSummary;
	private TextView statOutgo,tvCustomerNum,tvCustomerName;
	private TextView btnDate;
	private ListView goSellwaresLV;
	@Override
	public void setMyContentView() {
		setContentView(R.layout.go_sell);
		goSellwaresLV = (ListView) findViewById(R.id.expandableListView1);
		goSellwaresLV.setDivider(null);// 定义不显示默认的线条.
		ll_customer = (LinearLayout) findViewById(R.id.ll_customer);
		cbTabSelector = (CheckBox) findViewById(R.id.cbTabSelector);
		btnBalanceSummary = (TextView)findViewById(R.id.btnBalanceSummary);
		tvCustomerName = (TextView)findViewById(R.id.tvCustomerName);
		tvCustomerNum = (TextView)findViewById(R.id.tvCustomerNum);
		statOutgo = (TextView)findViewById(R.id.statOutgo);
		btnDate = (TextView)findViewById(R.id.btnDate);
		goSellwaresLV.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				showConfirmDialog("确认删除吗", 11);
				basePosition=position;
				return false;
			}
		});
		
	
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(!isCode){
			refreshData();
		}
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyApp.app.goSellMap.clear();
	}
	@Override
	public void init() {
		title.setText("结算中心");
		btnRight.setText("结算");
		btnCentre.setText("搜索");
		String date = MyDateUtils.formatDateAndTime(System.currentTimeMillis());
		btnDate.setText(date);
		inWaresList=new ArrayList<InWares>();
		
	}

	
	private int basePosition;
	
	@Override
	public void getConfirmDialogOk(int flag) {
		switch (flag) {
		case 11:
			inWaresList.remove(inWaresList.get(basePosition));	
			tabPrice-=inWaresList.get(basePosition).getTabPrice();
			count-=inWaresList.get(basePosition).getIsSellTemp();
			MyApp.app.goSellMap.remove(inWaresList.get(basePosition).getCode());
			refreshData();
			break;
		}
	}
	private void refreshData() {
		new  MyAsyncTask() {
			@Override
			public void onPreExecute() {
				showProgressDialog();
			}
			@Override
			public void doInBackground() {
				Set<String> keySet = MyApp.app.goSellMap.keySet();
				inWaresList.clear();
				count=0;
				tabPrice=0;
				for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
					String key = (String) iterator.next();
					InWares inWares = MyApp.app.goSellMap.get(key);
					inWaresList.add(inWares);
					count+=inWares.getIsSellTemp();
					tabPrice+=inWares.getTabPrice()*inWares.getIsSellTemp();
				}
			}
			@Override
			public void onPostExecute() {
				closeProgressDialog();
				statOutgo.setText(count+"件");
				btnBalanceSummary.setText("共:"+tabPrice+"元");
				if(adapter==null){
					adapter=new MyAdapter();
					goSellwaresLV.setAdapter(adapter);
				}
				goSellwaresLV.requestLayout();
				adapter.notifyDataSetChanged();
				
			}
		}.execute();
	
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
				view = View.inflate(getApplicationContext(), R.layout.list_child_gosell_item, null);
				holder=new ViewHolder();
				holder.imageView1=(ImageView) view.findViewById(R.id.imageView1);
				holder.tvItemName=(TextView) view.findViewById(R.id.tvName);
				holder.tvItemType=(TextView) view.findViewById(R.id.tvType);
				holder.tvMoneySellTab=(TextView) view.findViewById(R.id.tvMoneySellTab);
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
			holder.tvCode.setText(inWares.getCode());
			holder.tvisSell.setText(""+inWares.getIsSellTemp());
			return view;
		}
		class ViewHolder{
			ImageView imageView1;
			TextView tvItemName;
			TextView tvItemType;
			TextView tvMoneySellTab;
			TextView tvCode;
			TextView tvisSell;
		}
  		
		ImageCallback callback=new ImageCallback() {
			@Override
			public void loadImage(Bitmap bitmap, String imagePath) {
				if(adapter==null){
					adapter=new MyAdapter();
				}
				goSellwaresLV.requestLayout();
				adapter.notifyDataSetChanged();
				
			}
		};
  	}
  		
	private MyAdapter adapter;
	private float tabPrice;
	private int count;
	private boolean addSellWaresOk;
	private LinearLayout ll_customer;
	private Customer customer;
	private boolean isCode;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnBack:
			finish();
			break;
		case R.id.ll_customer:
			ActivityUtils.startActivityForResult(this, CustomerDetailActivity.class, null, 300);
			break;
		case R.id.btnRight:
			if(inWaresList==null||inWaresList.size()==0){
				Logger.toast(this, "没有选择商品");
				return;
			}
			if(customer==null){
				Logger.toast(this, "没有选择客户");
				return;
			}
			refreshDataByMonth();
			break;
		case R.id.btnCentre:
			ActivityUtils.startActivity(this, SearchActivity.class);
			break;
		case R.id.cbTabSelector:
			isCode=true;
//			ActivityUtils.startActivityForResult(this, CaptureActivity.class, null, 100);
			ActivityUtils.startActivityForResult(this, ErcodeScanActivity.class, null, 100);
			 break;
		
		}
		
	}
	/**
	 * 结算
	 */
	private void refreshDataByMonth() {
		new MyAsyncTask() {
			@Override
			public void onPreExecute() {

				showProgressDialog();
			}

			@Override
			public void doInBackground(){
				addGoSellSwares();
			}

			@Override
			public void onPostExecute() {
				closeProgressDialog();
				Logger.toast(GoSellWaresActivity.this, "结算完成");
				finish();
			}
		}.execute();
	}
	private void addGoSellSwares() {
		for(InWares inWares:inWaresList){
			SellWares sellWares = new SellWares(System.currentTimeMillis(), inWares.getTabPrice(), inWares, customer,inWares.getIsSellTemp());
			try {
				addSellWaresOk = dao.addSellWares(sellWares);
				Logger.d("addSellWaresOk="+sellWares+addSellWaresOk);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 200:// 获得商品名
			String code = data.getStringExtra("code");
			
			if (code != null) {
				InWares inWares = dao.queryInWaresByCode(code);
    			if (inWares!=null) {
    				ActivityUtils.startActivityForData(GoSellWaresActivity.this, WaresActivity.class, code);
    				isCode=false;
				}
			}
			break;
		case 300://得到客户
			customer = (Customer) data.getSerializableExtra("data");
			if(customer!=null){
				tvCustomerName.setText(customer.getName());
				tvCustomerNum.setText(customer.getPhone());
				}
			break;
		}

	}

	@Override
	public void setListeners() {
		cbTabSelector.setOnClickListener(this);
//		btnBack.setOnClickListener(this);
		ll_customer.setOnClickListener(this);
		btnRight.setOnClickListener(this);
		btnCentre.setOnClickListener(this);
		
	}


	
}
