package com.fada.sellsteward;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fada.sellsteward.domain.Customer;
import com.fada.sellsteward.domain.Grade;
import com.fada.sellsteward.utils.Logger;
import com.fada.sellsteward.view.MyViewGroup;

public class CustomerDetailActivity extends BaseActivity {
	private ListView customerLV;
	private List<Customer> customerList;
	private Intent intent;
	private CheckBox cbTabSelector;
	private int basePosition;
	@Override
	public void setMyContentView() {
		setContentView(R.layout.detail_wares);
		
	}
	@Override
	public void init() {
		customerLV = (ListView) findViewById(R.id.expandableListView1);
		cbTabSelector = (CheckBox) findViewById(R.id.cbTabSelector);
		btnBalanceSummary = (TextView) findViewById(R.id.btnBalanceSummary);
		initCount();
		title.setText("客户");
		customerList = dao.queryAllCustomer();
		customerLV.setDivider(null);// 定义不显示默认的线条.
		intent = getIntent();
		customerLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				if(intent!=null){
//					int resultCode = intent.getFlags();
//					Logger.d(null, resultCode+"");
//					intent.putExtra("data", customerList.get(position));
//					CustomerDetailActivity.this.setResult(resultCode, intent);
//					finish();
//				}
			}
		});
		customerLV.setOnItemLongClickListener(new OnItemLongClickListener() {
			

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				showConfirmDialog("确认要删除吗", 2);
				basePosition = position;
				return false;
			}
		});
		if (customerList==null) {
			ImageView ivEmpty =(ImageView) new ImageView(this);
			ivEmpty.setImageResource(R.drawable.ic_empty_chart);
			customerLV.setEmptyView(ivEmpty);
			
		}else{
			adapter = new MyAdapter();
			customerLV.setAdapter(adapter);
		}
		
		
	}
	/** 
	* @Title: initCount 
	* @Description: TODO() 
	* @param    
	* @return void    
	* @throws 
	*/
	private void initCount() {
		int customerCount = dao.queryCustomerCount();
		btnBalanceSummary.setText("总共:"+customerCount+"个");
	}

	@Override
	public void getConfirmDialogOk(int flag) {
		switch (flag) {
		case 2:
			try {
				boolean b = dao.deleteCustomer(customerList.get(basePosition));
				if (b) {
					initCount();
					Logger.toast(this, "删除成功");
				} else {
					Logger.toast(this, "删除失败");
					return;
				}

				if (adapter == null) {
					adapter = new MyAdapter();
					customerLV.setAdapter(adapter);
				}
				customerList = dao.queryAllCustomer();
				customerLV.requestLayout();
				adapter.notifyDataSetChanged();
			} catch (Exception e) {
				Logger.toast(this, e.toString());
				e.printStackTrace();
			}
			break;
		case 1:
			customerList = dao.queryAllCustomer();
			if (adapter==null) {
				adapter = new MyAdapter();
				customerLV.setAdapter(adapter);
			}
			customerLV.requestLayout();
			adapter.notifyDataSetChanged();
			break;

		default:
			break;
		}
	}
  	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (customerList==null) {
				return 0;
			}
			return customerList.size();
		}

		@Override
		public Object getItem(int position) {
			return customerList.get(position);
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
				view = View.inflate(getApplicationContext(), R.layout.list_customer_item, null);
				holder=new ViewHolder();
				holder.tvItemName=(TextView) view.findViewById(R.id.tvItemName);
				holder.tvItemType=(TextView) view.findViewById(R.id.tvItemType);
				holder.tvItemStock=(TextView) view.findViewById(R.id.tvItemStock);
				holder.tvItemCode=(TextView) view.findViewById(R.id.tvItemCode);
				view.setTag(holder);
			}else{
				view=convertView;
				holder=(ViewHolder) view.getTag();
				
			}
			Customer customer = customerList.get(position);
			if (customer!=null) {
			holder.tvItemName.setText(customer.getName());
			holder.tvItemType.setText(customer.getGrade().getComments());
			holder.tvItemStock.setText(customer.getTotal()+"");
			holder.tvItemCode.setText(customer.getPhone()+"");
			}
			return view;
		}
		class ViewHolder{
			TextView tvItemName;
			TextView tvItemType;
			TextView tvItemStock;
			TextView tvItemCode;
		}
  		
  	}
	private MyAdapter adapter;
	private TextView btnBalanceSummary;
	//private TextView grade_add;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnBack:
			finish();
			break;
		
		case R.id.cbTabSelector:
			showAddCustomerDialog(1);
			 break;
		}
		
	}
	
	
	@Override
	public void getDialogData(int flag, String... et) {
		switch (flag) {
		case 1:
			//grade_add.setText(et[0]);
			break;
		}
	}
	
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        switch (resultCode) {
			case 100://获得商品名
				
				break;
			case 200: //
				
				break;
			
			
			}
	        
	    }
	
	@Override
	public void setListeners() {
		cbTabSelector.setOnClickListener(this);
//		btnBack.setOnClickListener(this);
		
	}

	/**
	 * @Description: TODO 弹出添加客户对话框
	 */
	public void showAddCustomerDialog(final int flag) {
		
		AlertDialog.Builder builder = new Builder(this);
		dialog = builder.create();
		// 找到dialog的布局文件
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_addcustomer, null);
		final EditText name = (EditText) view.findViewById(R.id.etDialog);
		final EditText num = (EditText) view.findViewById(R.id.etText);
		//grade_add = (TextView) view.findViewById(R.id.tvCountPrompt);
		TextView btn_sure = (TextView) view.findViewById(R.id.btn_datetime_sure);
		TextView btn_cancel = (TextView) view.findViewById(R.id.btn_datetime_cancel);
//		// 选择等级
//		grade_add.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				showGradeDialog(1);
//			}
//		});
		// 确定
		btn_sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String customerName=name.getText().toString();
				String customerNum=num.getText().toString();
				try {
					Long.parseLong(customerNum);
				} catch (Exception e) {
					Logger.toast(CustomerDetailActivity.this, "只能是数字哦");
					return;
				}

				//				Grade grade=dao.queryGradeByComments(grade_add.getText().toString());
//				if(grade==null){
//					Logger.toast(CustomerDetailActivity.this, "不能没有客户等级");
//					return;
//				}
				Grade grade;
				try {
					if(dao.queryGradeById(1)==null){
					grade=new Grade(null, "普通客户");
					dao.addGrade(grade);
					}
					grade=dao.queryGradeById(1);
					
					Customer customer=new Customer(customerName, customerNum, "");
					dao.addCustomer(customer);
					initCount();
					Logger.toast(CustomerDetailActivity.this, "添加客户成功");
					getConfirmDialogOk(flag);
				} catch (Exception e) {
					Logger.toast(CustomerDetailActivity.this, e.toString());
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				
				dialog.dismiss();
				
			}
		});
		// 取消
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		// 设置dialog的布局,并显示
		dialog.setView(view,0,0,0,0);
		dialog.show();
		
	}
	
}
