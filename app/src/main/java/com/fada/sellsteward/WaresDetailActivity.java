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
import com.fada.sellsteward.utils.ImageUtil;
import com.fada.sellsteward.utils.ImageUtil.ImageCallback;
import com.fada.sellsteward.utils.Logger;
import com.fada.sellsteward.utils.MyAsyncTask;

public class WaresDetailActivity extends BaseActivity {
	private ListView waresLV;
	private List<Wares> waresList;
	private CheckBox cbTabSelector;
	private LinearLayout rootOutgoTab;
	private Intent intent;
	private int limit=30;//这里面的30表示每次查出的数据条数,自已定义
	private int offset=0;//表示从第行数据开始查
	private int total;//这个表示总条目数
	@Override
	public void setMyContentView() {
		setContentView(R.layout.detail_wares);
	}
	@Override
	public void init() {
		waresLV = (ListView) findViewById(R.id.expandableListView1);
		waresLV.setDivider(null);// 定义不显示默认的线条.
		rootOutgoTab = (LinearLayout) findViewById(R.id.rootOutgoTab);
		cbTabSelector = (CheckBox) findViewById(R.id.cbTabSelector);
		btnBalanceSummary = (TextView) findViewById(R.id.btnBalanceSummary);
		title.setText("库存");
		intent = getIntent();
		refreshData(false);
		if (waresList==null) {
			//ImageView ivEmpty =(ImageView) findViewById(R.id.ivEmpty);
			//ivEmpty.setVisibility(View.VISIBLE);
		}else{
			
			adapter = new MyAdapter();
			waresLV.setAdapter(adapter);
		}
			waresLV.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if(intent!=null){
						int resultCode = intent.getFlags();
						intent.putExtra("data", waresList.get(position));
						if (resultCode==100) {
							WaresDetailActivity.this.setResult(resultCode, intent);
							finish();
						}else{
							intent.setClass(WaresDetailActivity.this, InputInWaresActivity.class);
							WaresDetailActivity.this.startActivity(intent);
						}
					}
				}
			});
			waresLV.setOnScrollListener(new OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					if (scrollState==OnScrollListener.SCROLL_STATE_IDLE) {//SCROLL_STATE_IDLE表示当滚动停止状态.
						int position = waresLV.getLastVisiblePosition();//获得当前可见条目的最后一行索引
						int endPosition=waresList.size();//获得当前list的数据最后一行索引
						if (position==endPosition-1) {
							offset+=30;
							if (offset>total) {//如果大于总条目表示数据加载完了
								Toast.makeText(WaresDetailActivity.this, "没有更多数据了", 1).show();
							}
							refreshData(false);//刷新数据
						}
					}
					
				}
				//这个方法用于获得一些滚动的数据
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					// TODO Auto-generated method stub
					
				}
			});
			waresLV.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view,
						int position, long id) {
					showConfirmDialog("确认要删除吗", 1);
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
			try {
				boolean b = dao.deleteWares(waresList.get(basePosition));
				if (b) {
					Logger.toast(this, "删除成功");
				} else {
					Logger.toast(this, "删除失败");
					return;
				}

				if (adapter == null) {
					adapter = new MyAdapter();
					waresLV.setAdapter(adapter);
				}
				waresList = dao.queryAllWares();
				waresLV.requestLayout();
				adapter.notifyDataSetChanged();
			} catch (Exception e) {
				Logger.toast(this, e.toString());
				e.printStackTrace();
			}
			break;
		case 12:
			try {
				saveWares(name, category);
			} catch (Exception e) {
				Logger.e(null, e.toString());
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			rootOutgoTab.setVisibility(View.GONE);
			break;

		default:
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
  	private int count;//当前所有的库存数
  //动态显示获取数据的方法
  	private void refreshData(final boolean isAdd) {
  		new  MyAsyncTask(){
			@Override
  			public void onPreExecute() {
  				showProgressDialog();
  			}
  			@Override
  			public void doInBackground(){ 
  			//执行查询前,得到总条目
  				if (total==0) {
  					total=dao.queryWaresTotal();
  				}
  				count = dao.queryWaresCount();
  				
  				//得到adapter需要的数据集合,没有直接创建
  				if (waresList==null) {
  					waresList = dao.queryWaresOrderByLimit(offset,limit);
  					Logger.d("waresList=="+waresList);
  				}else{
  				//有的话直接把得到的集合添加进去
  					if(isAdd){
  						waresList.clear();
  					}
  					if(waresList!=null){
  						List<Wares> list = dao.queryWaresOrderByLimit(offset,limit);
  						if(list!=null)
  							waresList.addAll(list);
  					}
  				}
  			}
  			@Override
  			public void onPostExecute() {
  				
  				closeProgressDialog();
  				btnBalanceSummary.setText("总共:"+count+"种");
  			//执行后关闭进度条
  				if (waresList!=null) {//如果adapter为空则应该是第一次添加,那么创建一个
  					if(adapter==null){
  						adapter=new MyAdapter();
  						waresLV.setAdapter(adapter);
  					}else{
  		  				//如果有就直接刷新,这样就避免了每次加载完都跳到第一行.
  		  				//但记住,这个跟数据库无直接关联,如果数据集合没有变化,这个是没有效果的,只有从重查询数据库得到新集合才可以..
  		  					waresLV.requestLayout();
  		  					adapter.notifyDataSetChanged();
  		  				}
  					}
  					
  				
  			}}.execute();
  	}
  	private TextView tvName;
	private TextView tvType;
	private TextView tvTypeDes;
	private TextView tvSave;
	private TextView tvCancel;
	private String fileName;
	private Wares wares;
	private LinearLayout llCamera;
	private ImageView ivPic;
	private boolean flag;
	private MyAdapter adapter;
	private Category category;
	private String name;
	private TextView btnBalanceSummary;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnBack:
			finish();
			break;
		case R.id.llCamera://拍照
			  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 1);
			break;
		case R.id.cbTabSelector:
			rootOutgoTab.setVisibility(View.VISIBLE);
			if (!flag) {
				tvName = (TextView) findViewById(R.id.tvName);
				tvType = (TextView) findViewById(R.id.tvType);
				tvSave = (TextView) findViewById(R.id.tvSave);
				tvCancel = (TextView) findViewById(R.id.tvCancel);
				tvTypeDes = (TextView) findViewById(R.id.tvTypeDes);
				llCamera = (LinearLayout) findViewById(R.id.llCamera);
				ivPic = (ImageView) findViewById(R.id.ivPic);
				
				llCamera.setOnClickListener(this);
				tvTypeDes.setOnClickListener(this);
				tvCancel.setOnClickListener(this);
//				btnBack.setOnClickListener(this);
				tvName.setOnClickListener(this);
				tvType.setOnClickListener(this);
				tvSave.setOnClickListener(this);
				flag=true;
			}
			 break;
		case R.id.tvSave:
			try {
				name = tvName.getText().toString();
				if (name == "") {
					Logger.toast(this, "商品名不可以为空哦");
					return;
				}
				wares = dao.queryWaresByName(name);
				if (wares != null) {
					Logger.toast(this, "该商品已存在");
					return;
				} else {
					String categoryName = tvType.getText().toString();
					if (categoryName == "") {
						Logger.toast(this, "种类不可以为空哦");
						return;
					}
					
					category = dao.queryCategoryByCategory(categoryName);
					if (category == null) {
						dao.addCategory(categoryName);// 进行种类非空判断
						category = dao.queryCategoryByCategory(categoryName);
					}
					if (fileName == null) {
						showConfirmDialog("确认不要照片吗?", 12);
						return;
					}
					saveWares(name, category);
				}
			}catch (Exception ex) {
				Logger.e(null, ex.toString());
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}
			rootOutgoTab.setVisibility(View.GONE);
			break;
		case R.id.tvCancel:
			clearAllData();
			rootOutgoTab.setVisibility(View.GONE);
			break;
		case R.id.tvTypeDes:
			showCategoryDialog(3);
			break;
		
		case R.id.tvType:
			showEditDialog(3);
			break;
		case R.id.tvName:
			showEditDialog(4);
			break;
		}
		
	}
	private void saveWares(String name, Category category) throws Exception {
		wares = new Wares(name, fileName + "",category);
		boolean b = dao.addWares(wares);
		if(b){
			Logger.toast(this, "添加成功");
			refreshData(true);
		}else{
			Logger.toast(this, "添加失败");
			return;
		}
//		if (adapter == null) {
//			adapter = new MyAdapter();
//			waresLV.setAdapter(adapter);
//		}
//		waresList = dao.queryAllWares();
//		waresLV.requestLayout();
//		adapter.notifyDataSetChanged();
		clearAllData();
	}
	private void clearAllData(){
		tvName.setText("");
		tvType.setText("");
		ivPic.setImageResource(R.drawable.camera_btn_n);
	}
	

	@Override
	public void getDialogData(int flag, String... et) {
		switch (flag) {
		
		case 3:
			tvType.setText(et[0]);// 种类
			break;
		case 4:
			wares = dao.queryWaresByName(et[0]);
			if (wares != null) {
				Logger.toast(this, "该商品已存在");
			}
			tvName.setText(et[0]);// 商品名
			break;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case Activity.RESULT_OK:// 获得照片
			try {
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
					Logger.w(null, "没有检测到SD卡");
					return;
				}
				if (data != null) {
					Bundle bundle = data.getExtras();
					if (bundle != null) {
						Object object = bundle.get("data");
						if (object != null) {
							Bitmap bitmap = (Bitmap) object;
							if (bitmap != null) {
								fileName = ImageUtil.saveToSD(bitmap);
								ivPic.setImageBitmap(bitmap);// 将图片显示在ImageView里
							}
						}

					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 100:// 获得商品名

			break;
		}

	}

	@Override
	public void setListeners() {
		cbTabSelector.setOnClickListener(this);
//		btnBack.setOnClickListener(this);
		
	}


	
}
