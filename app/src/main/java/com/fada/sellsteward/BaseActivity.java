package com.fada.sellsteward;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fada.sellsteward.db.SellStewardDao;
import com.fada.sellsteward.db.SellStewardDaoImpl;
import com.fada.sellsteward.domain.Category;
import com.fada.sellsteward.domain.Grade;
import com.fada.sellsteward.myweibo.sina.util.MyWeiboManager;
import com.fada.sellsteward.service.MyApp;
import com.fada.sellsteward.service.SellService;
import com.fada.sellsteward.utils.CommonUtil;
import com.fada.sellsteward.utils.Constant;
import com.fada.sellsteward.utils.DensityUtil;
import com.fada.sellsteward.utils.Logger;
import com.fada.sellsteward.utils.MyDateUtils;
import com.fada.sellsteward.utils.NetUtil;
import com.fada.sellsteward.utils.RequestVo;
import com.fada.sellsteward.utils.ThreadPoolManager;
import com.fada.sellsteward.wheelview.NumericWheelAdapter;
import com.fada.sellsteward.wheelview.OnWheelChangedListener;
import com.fada.sellsteward.wheelview.WheelView;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends Activity implements OnClickListener{
	public Button btn;
	public AlertDialog dialog;
	public String dateAndTime=MyDateUtils.formatDateAndTime(System.currentTimeMillis());
	protected SellStewardDao dao;
	protected TextView title;
	protected TextView btnCentre;
	protected TextView btnRight;
//	protected TextView btnBack;
	protected MyWeiboManager mWeiboManager;
	protected TextView tvTime;
	public static int START_YEAR = 1990, END_YEAR = 2100;
	protected MyHandler handler;
	protected ThreadPoolManager threadPoolManager;
	protected ProgressDialog progressDialog;
	public BaseActivity() {
		threadPoolManager = ThreadPoolManager.getInstance();
	}
	public abstract interface DataCallback<T> {
		public abstract void processData(T paramObject, boolean paramBoolean);
	}
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	class MyHandler extends Handler{
		private Context context;
		private DataCallback callBack;
		private RequestVo reqVo;

		public MyHandler(Context context, DataCallback callBack,
				RequestVo reqVo) {
			this.context = context;
			this.callBack = callBack;
			this.reqVo = reqVo;
		}
		public void handleMessage(Message msg) {
			closeProgressDialog();
			if (msg.what == Constant.SUCCESS) {
				if (msg.obj == null) {
					CommonUtil.showInfoDialog(context,getString(R.string.networkError));
				} else {
					callBack.processData(msg.obj, true);
				}
			} else if (msg.what == Constant.NET_FAILED) {
				CommonUtil.showInfoDialog(context,getString(R.string.networkError));
			}
		}
		
	}
	/**
	 *TODO 从服务器上获取数据，并回调处理
	 * 
	 * @param reqVo
	 * @param callBack
	 */
	protected void getDataFromServer(RequestVo reqVo, DataCallback callBack) {
		showProgressDialog();
		handler = new MyHandler(this, callBack, reqVo);
		BaseTask taskThread = new BaseTask(reqVo, handler);// 线程类--获取数据
		this.threadPoolManager.addTask(taskThread);
		
	}
	class BaseTask implements Runnable {
		private RequestVo reqVo;
		private Handler handler;

		public BaseTask(RequestVo reqVo, Handler handler) {
			this.reqVo = reqVo;
			this.handler = handler;
		}

		@Override
		public void run() {
			Object obj = null;
			Message msg = new Message();
			if (getNetWorkStatus()) {
				obj = NetUtil.post(reqVo);// 真正和后台服务器交互的方法
				msg.what = Constant.SUCCESS;
				msg.obj = obj;
				handler.sendMessage(msg);
			} else {
				msg.what = Constant.NET_FAILED;
				msg.obj = obj;
				handler.sendMessage(msg);
			}
		}

	}
	/**
	 *TODO 关闭提示框
	 */
	protected void closeProgressDialog() {
		if (this.progressDialog != null)
			this.progressDialog.dismiss();
	}
	/**
	 *TODO 显示提示框
	 */
	protected void showProgressDialog() {
		if ((!isFinishing()) && (this.progressDialog == null)) {
			this.progressDialog = new ProgressDialog(this);
		}
		this.progressDialog.setTitle(getString(R.string.loadTitle));
		this.progressDialog.setMessage(getString(R.string.LoadContent));
		this.progressDialog.show();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		MyApp.app.addActivity(this);
		setMyContentView();
		dao = SellStewardDaoImpl.getDaoInstance(this);
		title = (TextView) findViewById(R.id.title);
		btnCentre=(TextView) findViewById(R.id.btnCentre);
		btnRight=(TextView) findViewById(R.id.btnRight);
//		btnBack=(TextView) findViewById(R.id.btnBack);
		sp = getSharedPreferences("config",  MODE_PRIVATE);
		edit = sp.edit();
		init();
		setListeners();
	}
	
	/**
	 *TODO 设制布局
	 */
	public abstract void setMyContentView() ;
	/***
	 *TODO R.id.title/btnCentre/btnRight/btnBack已经初始化好
	 */
	public abstract  void setListeners();
	/**
	 *TODO 初始化:已经初始化好的view:标题栏TextView:title,
	 * 下面标提中栏:btnCentre,
	 * 下面标提右栏:btnRight
	 * dao也初始化好了
	 */
	public abstract  void init();
	public DecimalFormat decimal;
	private ListView lvCategory;
	private List<Category> listCategory;
	private List<Grade> listGrade;
	private ListView lvGrade;
	protected SharedPreferences sp;
	public Editor edit;
	/**
	 *TODO @Description: TODO 弹出日期时间选择器
	 */
	public void showDateTimePicker() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);
		AlertDialog.Builder builder = new Builder(this);
		dialog = builder.create();
		// 找到dialog的布局文件
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.time_layout, null);

		// 年
		final WheelView wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
		wv_year.setCyclic(true);// 可循环滚动
		wv_year.setLabel("年");// 添加文字
		wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

		// 月
		final WheelView wv_month = (WheelView) view.findViewById(R.id.month);
		wv_month.setAdapter(new NumericWheelAdapter(1, 12));
		wv_month.setCyclic(true);
		wv_month.setLabel("月");
		wv_month.setCurrentItem(month);

		// 日
		final WheelView wv_day = (WheelView) view.findViewById(R.id.day);
		wv_day.setCyclic(true);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdapter(1, 29));
			else
				wv_day.setAdapter(new NumericWheelAdapter(1, 28));
		}
		wv_day.setLabel("日");
		wv_day.setCurrentItem(day - 1);

		// 时
		final WheelView wv_hours = (WheelView) view.findViewById(R.id.hour);
		wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
		wv_hours.setCyclic(true);
		wv_hours.setCurrentItem(hour);

		// 分
		final WheelView wv_mins = (WheelView) view.findViewById(R.id.mins);
		wv_mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		wv_mins.setCyclic(true);
		wv_mins.setCurrentItem(minute);

		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + START_YEAR;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String
						.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(wv_month
						.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0)
							|| year_num % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
							.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);

		// 根据屏幕密度来指定选择器字体的大小
		int textSize = 0;
		
		textSize = DensityUtil.dip2px(this, 14);

		wv_day.TEXT_SIZE = textSize;
		wv_hours.TEXT_SIZE = textSize;
		wv_mins.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;

		TextView btn_sure = (TextView) view.findViewById(R.id.btn_datetime_sure);
		TextView btn_cancel = (TextView) view
				.findViewById(R.id.btn_datetime_cancel);
		// 确定
		btn_sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 如果是个数,则显示为"02"的样式
				String parten = "00";
				decimal = new DecimalFormat(parten);
				 //设置日期的显示
				dateAndTime=(wv_year.getCurrentItem() + START_YEAR) + "-"
						 + decimal.format((wv_month.getCurrentItem() + 1)) + "-"
						 + decimal.format((wv_day.getCurrentItem() + 1)) + " "
						 + decimal.format(wv_hours.getCurrentItem()) + ":"
						 + decimal.format(wv_mins.getCurrentItem());
				if (tvTime!=null) {
					
					tvTime.setText(dateAndTime);
				}

				dialog.dismiss();
			}
		});
		// 取消
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		// 设置dialog的布局,并显示
		dialog.setView(view,0,0,0,0);
		dialog.show();
		
	}
	/**
	 *TODO @Description: TODO 弹出输入对话框
	 */
	public void showEditDialog(final int flag) {
		
		AlertDialog.Builder builder = new Builder(this);
		dialog = builder.create();
		// 找到dialog的布局文件
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_edit, null);
		final EditText etDialog = (EditText) view.findViewById(R.id.etDialog);
		TextView btn_sure = (TextView) view.findViewById(R.id.btn_datetime_sure);
		TextView btn_cancel = (TextView) view.findViewById(R.id.btn_datetime_cancel);
		// 确定
		btn_sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				 String et = etDialog.getText().toString();
				 getDialogData(flag,et+"");
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
	/**
	 *TODO @Description: TODO 设置密码
	 */
	public void showEditDialogPsd(final int flag) {
		
		AlertDialog.Builder builder = new Builder(this);
		dialog = builder.create();
		// 找到dialog的布局文件
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_editpsd, null);
		final EditText etOneDialog = (EditText) view.findViewById(R.id.etOneDialog);
		final EditText etTwoDialog = (EditText) view.findViewById(R.id.etTwoDialog);
		TextView btn_sure = (TextView) view.findViewById(R.id.btn_datetime_sure);
		TextView btn_cancel = (TextView) view.findViewById(R.id.btn_datetime_cancel);
		// 确定
		btn_sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String one = etOneDialog.getText().toString().trim();
				String two = etTwoDialog.getText().toString().trim();
				if(one.equals("")){
					Logger.toast(getApplicationContext(), "密码不能为空");
				}else if(one.equals(two)){
					edit.putString("psd", one);
					edit.commit();
					Logger.toast(getApplicationContext(), "密码设置成功");
					btnRight.setText("修改密码");
					
				}else{
					Logger.toast(getApplicationContext(), "两次密码输入不一致");
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
	/**
	 *TODO @Description: TODO 修改密码
	 */
	public void showEditDialogRePsd(final int flag) {
		
		AlertDialog.Builder builder = new Builder(this);
		dialog = builder.create();
		// 找到dialog的布局文件
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_editrepsd, null);
		final EditText etOldDialog = (EditText) view.findViewById(R.id.etOldDialog);
		final EditText etNewDialog = (EditText) view.findViewById(R.id.etNewDialog);
		TextView btn_sure = (TextView) view.findViewById(R.id.btn_datetime_sure);
		TextView btn_cancel = (TextView) view.findViewById(R.id.btn_datetime_cancel);
		// 确定
		btn_sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String old = etOldDialog.getText().toString().trim();
				String etNew = etNewDialog.getText().toString().trim();
				String psd = sp.getString("psd", "");
				if(!old.equals(psd)){
					Logger.toast(getApplicationContext(), "旧密码输入错误");
				}else if(etNew.equals("")){
					Logger.toast(getApplicationContext(), "新密码不能为空");
					
				}else{
					edit.putString("psd", etNew);
					edit.commit();
					Logger.toast(getApplicationContext(), "密码修改成功");
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
	
	/**
	 *TODO @Description: TODO 弹出种类选择对话框
	 */
	public void showCategoryDialog(final int flag) {
		
		AlertDialog.Builder builder = new Builder(this);
		dialog = builder.create();
		// 找到dialog的布局文件
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_category, null);
		TextView btn_sure = (TextView) view.findViewById(R.id.tvDialog);
		lvCategory = (ListView) view.findViewById(R.id.expandableListView1);
		listCategory = dao.queryAllCategory();
		if(listCategory==null){
			ImageView imageView = new ImageView(this);
			imageView.setImageResource(R.drawable.ic_empty_chart);
			lvCategory.setEmptyView(imageView);
		}
		lvCategory.setDivider(null);// 定义不显示默认的线条.
		lvCategory.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(listCategory!=null){
					getDialogData(flag,listCategory.get(position).getName());
					dialog.dismiss();
				}
				
			}
		});
		if(listCategory!=null)
		lvCategory.setAdapter(new BaseAdapter() {
			class ViewHolder{
				TextView tvCategoryName;		
			}
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder holder;
				View view;
				if(convertView==null){
					LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
					view = inflater.inflate(R.layout.list_catogroy_item, null);
					holder=new ViewHolder();
					holder.tvCategoryName=(TextView) view.findViewById(R.id.tvCategoryName);
					view.setTag(holder);
				}else{
					view=convertView;
					holder=(ViewHolder) view.getTag();
					
				}
				Category category = listCategory.get(position);
				if(category!=null&&category.getName()!=null)
					holder.tvCategoryName.setText(listCategory.get(position).getName());
				return view;
			}
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				return listCategory.get(position);
			}
			
			@Override
			public int getCount() {
				int size=0;
				if(listCategory!=null){
					size = listCategory.size();
				}
				return size;
			}
		});
		// 关闭
		btn_sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		// 设置dialog的布局,并显示
		dialog.setView(view,0,0,0,0);
		dialog.show();
	}
	/**
	 * @Description: TODO 弹出客户等级选择对话框
	 */
	public void showGradeDialog(final int flag) {
		
		AlertDialog.Builder builder = new Builder(this);
		dialog = builder.create();
		// 找到dialog的布局文件
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_category, null);
		TextView btn_sure = (TextView) view.findViewById(R.id.tvDialog);
		btn_sure.setText("客户等级");
		lvGrade = (ListView) view.findViewById(R.id.expandableListView1);
		listGrade = dao.queryAllGrade();
		if(listGrade==null){
		ImageView imageView = new ImageView(this);
		imageView.setImageResource(R.drawable.ic_empty_chart);
		lvGrade.setEmptyView(imageView);
		}
		lvGrade.setDivider(null);// 定义不显示默认的线条.
		listGrade = dao.queryAllGrade();
		lvGrade.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(listGrade!=null){
					getDialogData(flag,listGrade.get(position).getComments());
					dialog.dismiss();
				}
				
			}
		});
		
		lvGrade.setAdapter(new BaseAdapter() {
			class ViewHolder{
				TextView tvGradeName;		
			}
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder holder;
				View view;
				if(convertView==null){
					LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
					view = inflater.inflate(R.layout.list_catogroy_item, null);
					holder=new ViewHolder();
					holder.tvGradeName=(TextView) view.findViewById(R.id.tvName);
					view.setTag(holder);
				}else{
					view=convertView;
					holder=(ViewHolder) view.getTag();
					
				}
				holder.tvGradeName.setText(listGrade.get(position).getComments());
				return view;
			}
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				return listGrade.get(position);
			}
			
			@Override
			public int getCount() {
				return listGrade.size();
			}
		});
		// 关闭
		btn_sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		// 设置dialog的布局,并显示
		dialog.setView(view,0,0,0,0);
		dialog.show();
	}
	/**
	 *TODO 对话框数据返还
	 * @param et
	 * @param flag
	 */
	public void getDialogData(int flag,String...et){};
	/**
	 * @Description: TODO 弹出确认对话框
	 */
	public void showConfirmDialog(String title,final int flag) {
		
		AlertDialog.Builder builder = new Builder(this);
		dialog = builder.create();
		// 找到dialog的布局文件
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_confirm, null);
		TextView tvDialog = (TextView) view.findViewById(R.id.tvDialog);
		if (title!=null) {
			tvDialog.setText(title);
		}
		TextView btn_sure = (TextView) view.findViewById(R.id.btn_datetime_sure);
		TextView btn_cancel = (TextView) view.findViewById(R.id.btn_datetime_cancel);
		// 确定
		btn_sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getConfirmDialogOk(flag);
				exitOk(flag);
				dialog.dismiss();
			}
		});
		// 取消
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				getConfirmDialogCancel(flag);
				dialog.dismiss();
			}
		});
		// 设置dialog的布局,并显示
		dialog.setView(view,0,0,0,0);
		dialog.show();
		
	}
	/**
	 *  TODO 对话框确认
	 * @param flag
	 */
	public void getConfirmDialogOk(int flag){};
	/**
	 *  TODO 对话框确认
	 * @param flag
	 */
	public void exitOk(int flag){
		if(flag==111){
			Intent name=new Intent(this,SellService.class);
			stopService(name);
			MyApp.app.exit();
		}
	};
	/**
	 * TODO 对话框取消
	 * @param flag
	 */
	public void getConfirmDialogCancel(int flag){};
	/**
	 *TODO 判断网络是否可用
	 * @return
	 */
	private boolean getNetWorkStatus() {  
		  
		   boolean netSataus = false;  
		   ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);  
		  
		   cwjManager.getActiveNetworkInfo();  
		  
		   if (cwjManager.getActiveNetworkInfo() != null) {  
		   netSataus = cwjManager.getActiveNetworkInfo().isAvailable();  
		   }  
		  
		   if (!netSataus) {  
		   Builder b = new AlertDialog.Builder(this).setTitle("没有可用的网络")  
		   .setMessage("是否对网络进行设置？");  
		   b.setPositiveButton("是", new DialogInterface.OnClickListener() {  
		   public void onClick(DialogInterface dialog, int whichButton) {  
		   Intent mIntent = new Intent("/");  
		   ComponentName comp = new ComponentName(  
		   "com.android.settings",  
		   "com.android.settings.WirelessSettings");  
		   mIntent.setComponent(comp);  
		   mIntent.setAction("android.intent.action.VIEW");  
		   startActivityForResult(mIntent,0);   
		   }  
		   }).setNeutralButton("否", new DialogInterface.OnClickListener() {  
		   public void onClick(DialogInterface dialog, int whichButton) {  
		   dialog.cancel();  
		   }  
		   }).show();  
		   }  
		  
		   return netSataus;  
		   } 
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		showConfirmDialog("确定要退出吗", 111);
		return super.onOptionsItemSelected(item);
	}

}
