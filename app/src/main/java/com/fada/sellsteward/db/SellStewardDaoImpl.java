package com.fada.sellsteward.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.fada.sellsteward.domain.Category;
import com.fada.sellsteward.domain.Customer;
import com.fada.sellsteward.domain.Grade;
import com.fada.sellsteward.domain.Image;
import com.fada.sellsteward.domain.InWares;
import com.fada.sellsteward.domain.SellWares;
import com.fada.sellsteward.domain.Wares;
import com.fada.sellsteward.utils.Logger;
import com.fada.sellsteward.utils.MyDateUtils;

public class SellStewardDaoImpl implements  SellStewardDao {
	private  Uri categorysUri =Uri.parse( "content://com.fada.sellsteward.db.MyContentProvider/categorys");//商品类别表
	private  Uri  waresUri = Uri.parse( "content://com.fada.sellsteward.db.MyContentProvider/wares");//商品表
	private  Uri  inwaresUri = Uri.parse( "content://com.fada.sellsteward.db.MyContentProvider/inwares");//入库商品表
	private  Uri  sellwaresUri = Uri.parse( "content://com.fada.sellsteward.db.MyContentProvider/sellwares");//出库商品表
	private  Uri  customerUri = Uri.parse( "content://com.fada.sellsteward.db.MyContentProvider/customer");//出库商品表
	private  Uri  imagesUri = Uri.parse( "content://com.fada.sellsteward.db.MyContentProvider/images");//出库商品表
	private  Uri  gradeUri = Uri.parse( "content://com.fada.sellsteward.db.MyContentProvider/grade");//出库商品表
	private ContentResolver resolver;
	private ContentValues values;
	private static SellStewardDaoImpl daoImpl;
	private SellStewardDaoImpl(Context context){
		resolver = context.getContentResolver();
	}
	public synchronized static SellStewardDaoImpl getDaoInstance(Context context){
		if(daoImpl==null){
			daoImpl=new SellStewardDaoImpl(context);
		}
		return daoImpl;
	}
	/* (non-Javadoc)
	 * @see com.fada.sellsteward.db.SellStewardDao#addCategory(java.lang.String)
	 */
	@Override
	public boolean addCategory(String category) throws Exception{
		if (category==null) {
			throw new Exception("Category种类名不可以为空");
		}
		if (queryCategoryByCategory(category)!=null) {
			
			throw new Exception("Category种类名已存在");
		}
		values=new ContentValues();
		values.put("category", category);
		Uri uri = resolver.insert(categorysUri, values);
		long l = ContentUris.parseId(uri);
		if(l>0){
			return true;
		}
		
		return false;
	}
	@Override
	public boolean addImage(Image image) throws Exception{
		if (image.getWares().get_id()==null) {
			throw new Exception("wares_id不可以为空");
		}
		if (image.get_id()!=null&&queryIamgeId(image.get_id())!=null) {
			throw new Exception("图片已存在");
		}
		if (image.getPath()!=null&&queryIamgeByPath(image.getPath())!=null) {
			throw new Exception("图片已存在");
		}
		values=new ContentValues();
		values.put("imagePath", image.getPath());
		values.put("wares_id", image.getWares().get_id());
		Uri uri = resolver.insert(imagesUri, values);
		long l = ContentUris.parseId(uri);
		if(l>0){
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean addGrade(Grade grade) throws Exception{
		if (grade.getComments()==null) {
			throw new Exception("等级名不可以为空");
		}
		if (queryGradeByComments(grade.getComments())!=null) {
			throw new Exception("等级已存在");
		}
		values=new ContentValues();
		values.put("comments", grade.getComments());
		
		Uri uri = resolver.insert(gradeUri, values);
		long l = ContentUris.parseId(uri);
		if(l>0){
			return true;
		}
		
		return false;
	}
	

	/* (non-Javadoc)
	 * @see com.fada.sellsteward.db.SellStewardDao#addWares(com.fada.sellsteward.domain.Wares)
	 */
	@Override
	public boolean addWares(Wares wares) throws Exception{
		if (wares==null||wares.getCategory().get_id()==null) {
			throw new Exception("Category_id不可以为空");
		}
		if (wares.getName()!=null&&queryWaresByName(wares.getName())!=null) {
			throw new Exception("商品已存在");
		}
		
		values=new ContentValues();
		values.put("name", wares.getName());
		values.put("imagePath", wares.getImagePath());
		values.put("stock", 0);
		values.put("category_id", wares.getCategory().get_id());
		Uri uri = resolver.insert(waresUri, values);
		long l = ContentUris.parseId(uri);
		if(l>0){
			return true;
		}
		
		return false;
		
	}
	/* (non-Javadoc)
	 * @see com.fada.sellsteward.db.SellStewardDao#addInWares(com.fada.sellsteward.domain.InWares)
	 */
	@Override
	public boolean addInWares(InWares inWares) throws Exception{
		if (inWares.getWares().get_id()==null) {
			throw new Exception("Wares_id不可以为空");
		}
		
		if (inWares.getCode()==null) {
			throw new Exception("条码不可以为空");
		}
		if (inWares.get_id()!=null&&queryInWaresById(inWares.get_id())!=null) {
			throw new Exception("商品已存在");
		}
		values=new ContentValues();
		values.put("inTime", inWares.getInTime());
		values.put("inPrice", inWares.getInPrice());
		values.put("wares_id", inWares.getWares().get_id());
		values.put("tabPrice", inWares.getTabPrice());
		values.put("code", inWares.getCode());
		values.put("isSell", inWares.getIsSell());
		values.put("amount", inWares.getAmount());
		Uri contentUri=resolver.insert(inwaresUri, values);
		long l = ContentUris.parseId(contentUri);
		if(l>0){
			Wares wares = inWares.getWares();
			if(wares!=null&&wares.getStock()!=null){
				updateWaresStock(wares.getStock()+inWares.getAmount(),wares);
			}
			return true;
		}
		
		return false;
		
	}
	/* (non-Javadoc)
	 * @see com.fada.sellsteward.db.SellStewardDao#addSellWares(com.fada.sellsteward.domain.SellWares)
	 */
	@Override
	public boolean addSellWares(SellWares sellWares) throws Exception{
		InWares inWares = sellWares.getInWares();
		if (inWares==null||inWares.get_id()==null) {
			throw new Exception("InWares_id不可以为空");
		}
		Integer id = inWares.get_id();
		
		if (inWares.getAmount()==0) {
			throw new Exception("该商品已售完");
		}
		Wares wares = inWares.getWares();
		if(wares!=null&&wares.getStock()!=null){
			
			wares=queryWaresById(wares.get_id());
			int stock=wares.getStock();
			stock=stock-sellWares.getAmount();
			if(stock<0)throw new Exception("商品没有库存了");
			Customer customs = sellWares.getCustoms();
		if(customs!=null){
			customs=queryCustomerByPhone(customs.getPhone());
			Float total=customs.getTotal()+(sellWares.getOutPrice()*sellWares.getAmount());
			if(!updateCustomerTotal(total,customs)){
				throw new Exception("客户等级不存在或添加失败");
			}
		}
		Float profit=(sellWares.getOutPrice()-inWares.getInPrice());
		values=new ContentValues();
		values.put("outTime", sellWares.getOutTime());
		values.put("outPrice", sellWares.getOutPrice());
		values.put("profit", profit);
		values.put("inwares_id", id);
		values.put("amount", sellWares.getAmount());
		values.put("customer_id", sellWares.getCustoms().get_id());
		Uri uri = resolver.insert(sellwaresUri, values);
		long l = ContentUris.parseId(uri);
		if(l>0){
				updateWaresStock(stock,wares);
				updateInWaresIsSell(inWares.getIsSell()+sellWares.getAmount(), inWares);
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean addCustomer(Customer customer) throws Exception {
		if (customer==null||customer.getName()==null) {
			throw new Exception("顾客名不可以为空");
		}
		if (customer.getPhone()!=null) {
			if(queryCustomerByPhone(customer.getPhone())!=null){
				Logger.d("顾客已存在");
				return false;
			}
		}
		if (queryGradeById(1)==null) {
			Grade grade=new Grade(null, "普通客户");
			addGrade(grade);
		}
		values=new ContentValues();
		values.put("name", customer.getName());
		values.put("phone", customer.getPhone()+"");
		values.put("comments", customer.getComments()+"");
		values.put("total",customer.getTotal() );
		values.put("grade_id", 1);
		Uri uri = resolver.insert(customerUri, values);
		long l = ContentUris.parseId(uri);
		if(l>0){
			return true;
		}
		return false;
	}
	@Override
	public boolean updateCategory(Category category) throws Exception {
		if (category.getName()==null) {
			throw new Exception("Category种类名不可以为空");
		}
		
		values=new ContentValues();
		values.put("category", category.getName());
		String where="_id=?";
		String[] selectionArgs=new String[]{category.get_id()+""};
		int i = resolver.update(categorysUri, values, where, selectionArgs);
		if (i!=0) {
			return true;	
			}
		return false;
	}
	@Override
	public boolean updateImage(Image image) throws Exception{
		if (image.getWares().get_id()==null) {
			throw new Exception("wares_id不可以为空");
		}
		values=new ContentValues();
		values.put("imagePath", image.getPath());
		String where="_id=?";
		String[] selectionArgs=new String[]{image.get_id()+""};
		int i = resolver.update(imagesUri,values,where, selectionArgs);
		if (i!=0) {
			return true;	
			}
		return false;
	}
	@Override
	public boolean updateGrade(Grade grade) throws Exception{
		if (grade.getComments()==null) {
			throw new Exception("grade种类名不可以为空");
		}
		values=new ContentValues();
		values.put("comments", grade.getComments());
		String where="_id=?";
		String[] selectionArgs=new String[]{grade.get_id()+""};
		int i = resolver.update(gradeUri,values,where, selectionArgs);
		if (i!=0) {
			return true;	
			}
		return false;
	}
	@Override
	public boolean updateWares(Wares wares) throws Exception {
		if (wares.getCategory().get_id()==null) {
			throw new Exception("Category_id不可以为空");
		}
		values=new ContentValues();
		values.put("name", wares.getName());
		values.put("imagePath", wares.getImagePath());
		values.put("stock", wares.getStock());
		String where="_id=?";
		String[] selectionArgs=new String[]{wares.get_id()+""};
		int i = resolver.update(waresUri, values, where, selectionArgs);
		if (i!=0) {
			return true;	
			}
		return false;
	}
	@Override
	public boolean updateWaresStock(Integer stock,Wares wares){
		if(queryWaresById(wares.get_id())==null)
			return false;
		values=new ContentValues();
		values.put("stock", stock);
		String where="_id=?";
		String[] selectionArgs=new String[]{wares.get_id()+""};
		int i = resolver.update(waresUri, values, where, selectionArgs);
		if (i!=0) {
			return true;	
		}
		return false;
	}
	@Override
	public boolean updateInWares(InWares inWares,InWares oldInWares) throws Exception {
		if (oldInWares.getWares().get_id()==null) {
			throw new Exception("Wares_id不可以为空");
		}
		values=new ContentValues();
		values.put("inTime", inWares.getInTime());
		values.put("inPrice", inWares.getInPrice());
		values.put("tabPrice", inWares.getTabPrice());
		values.put("amount", inWares.getAmount());
		values.put("code", inWares.getCode());
		String where="_id=?";
		String[] selectionArgs=new String[]{oldInWares.get_id()+""};
		int i = resolver.update(inwaresUri, values, where, selectionArgs);
		if (i!=0) {
			int stock =oldInWares.getWares().getStock()+(inWares.getAmount()-oldInWares.getAmount());
			if(stock<0)stock=0;
			updateWaresStock(stock, oldInWares.getWares());
			return true;	
			}
		return false;
	}
	/**
	 * 
	 * @param isSell 
	 * @param inWares
	 * @return
	 * @throws Exception
	 */
	private boolean updateInWaresIsSell(int isSell,InWares inWares){
		if(inWares==null)return false; 
		values=new ContentValues();
		values.put("isSell", isSell);
		String where="_id=?";
		String[] selectionArgs=new String[]{inWares.get_id()+""};
		int i = resolver.update(inwaresUri, values, where, selectionArgs);
		if (i!=0) {
			return true;	
		}
		return false;
	}
	@Override
	public boolean updateSellWares(SellWares sellWares,SellWares oldsSellWares) throws Exception {
		if (oldsSellWares.getInWares().get_id()==null) {
			throw new Exception("InWares_id不可以为空");
		}
		Float profit=(sellWares.getOutPrice()-sellWares.getInWares().getInPrice())*sellWares.getAmount();
		values=new ContentValues();
		values.put("outTime", sellWares.getOutTime());
		values.put("outPrice", sellWares.getOutPrice());
		values.put("profit", profit);
		values.put("amount", sellWares.getAmount());
		String where="_id=?";
		String[] selectionArgs=new String[]{oldsSellWares.get_id()+""};
		int i = resolver.update(sellwaresUri, values, where, selectionArgs);
		if (i!=0) {
			Customer customer = oldsSellWares.getCustoms();
			float total=customer.getTotal()-(sellWares.getAmount()*sellWares.getOutPrice());
			if(total<0){total=0;}
			updateCustomerTotal(total, customer);
			Wares wares = sellWares.getInWares().getWares();
			int stock=wares.getStock()-oldsSellWares.getAmount()+sellWares.getAmount();
			if(stock<0){stock=0;}
			updateWaresStock(stock,wares );
			updateInWaresIsSell(oldsSellWares.getInWares().getIsSell()+(sellWares.getAmount()-oldsSellWares.getAmount()), sellWares.getInWares());
			return true;	
			}
		return false;
	}
	@Override
	public boolean updateCustomer(Customer customer) throws Exception {
		if (customer==null||customer.getName()==null) {
			throw new Exception("customer名不可以为空");
		}
		values=new ContentValues();
		values.put("name", customer.getName());
		values.put("phone", customer.getPhone()+"");
		values.put("comments", customer.getComments()+"");
		values.put("total", customer.getTotal()+"");
		int i = resolver.update(customerUri, values, "_id=?",new String[]{customer.get_id()+""});
		if (i!=0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean updateCustomerTotal(Float total, Customer customer)
			throws Exception {
		values = new ContentValues();
		values.put("total", total);
		int gradeid = 1;
		if (queryGradeById(gradeid) == null) {
			addGrade(new Grade(null, "普通客户"));
		}
		if (total > 200) {
			gradeid = 2;
			if (queryGradeById(gradeid) == null) {
				addGrade(new Grade(null, "铜牌客户"));
			}
		}
		if (total > 500) {
			gradeid = 3;
			if (queryGradeById(gradeid) == null) {
				addGrade(new Grade(null, "银牌客户"));
			}
		}
		if (total > 800) {
			gradeid = 4;
			if (queryGradeById(gradeid) == null) {
				addGrade(new Grade(null, "金牌客户"));
			}
		}
		if (total > 1000) {
			gradeid = 5;
			if (queryGradeById(gradeid) == null) {
				addGrade(new Grade(null, "vip客户"));
			}
		}
		values.put("grade_id", gradeid);
		int i = resolver.update(customerUri, values, "_id=?",new String[] { customer.get_id() + "" });
		if (i != 0) {
			return true;
		}
		return false;
	}
	
	/*--------------------商品查询---------------------------*/
	@Override
	public List<Wares> queryAllWaresByDesc(){
		return getQueryWaresList(null,null,"_id desc");
	}
	@Override
	public List<Wares> queryAllWares(){
		return getQueryWaresList(null,null,null);
	}
	@Override
	public List<Wares> queryWaresByCategoryId(int category_id){
		return getQueryWaresList("category_id=?",new String[]{category_id+""},"_id desc");
	}
	@Override
	public List<Wares> queryWaresOrderByLimit(int total,int offset){
		return getQueryWaresList(null,null,"_id desc limit "+total+","+offset);
	}
	@Override
	public Wares queryWaresByImagePath(String imagePath) {
		return getQueryWares("imagePath", imagePath);
	}
	@Override
	public Wares queryWaresByName(String name){
		return getQueryWares("name",name);
		
	}
	@Override
	public Wares queryWaresById(int _id){
		return getQueryWares("_id",_id+"");
	}
	
	/**
	 * 查询Wares的List集合
	 * @param selection where后的String
	 * @param selectionArgs String[]
	 * @return List<Wares>
	 */
	private List<Wares> getQueryWaresList(String selection,String[] selectionArgs,String orderby) {
		Cursor cursor = resolver.query(waresUri, new String[]{"_id","name","imagePath","stock","category_id"}, selection, selectionArgs, orderby);
		List<Wares> list=null;
		while(cursor.moveToNext()){
			Category category = queryCategoryById(cursor.getInt(4));
			if(category==null)continue;
			if (list==null){list=new ArrayList<Wares>();}
			Wares wares= new Wares(cursor.getInt(0),  cursor.getString(1), cursor.getString(2), cursor.getInt(3),category);
			if (wares.getName()!=null) {
				list.add(wares);
			}
		}
		cursor.close();
		return list;
		
	}
	@Override
	public int queryWaresTotal() {
		Cursor cursor = resolver.query(waresUri, new String[]{"name"}, null, null, null);
		int total=0;
		while(cursor.moveToNext()){
			total++;
		}
		cursor.close();
		return total;
		
	}
	/**
	 * 商品查询工具
	 * @param selection 参数:String
	 * @return Category对象
	 */
	private Wares getQueryWares(String selection,String selectionArgs) {
		Cursor cursor = resolver.query(waresUri, new String[]{"_id","name","imagePath","stock","category_id"}, selection+"=?", new String[]{selectionArgs}, null);
		if(cursor.moveToNext()){
			Category category = queryCategoryById(cursor.getInt(4));
			if(category==null)return null;
			 Wares wares = new Wares(cursor.getInt(0),  cursor.getString(1), cursor.getString(2), cursor.getInt(3),category);
			 cursor.close();
			 if (wares.getName()==null||wares.getName()=="") return null;
				 return wares;
		}
		cursor.close();
		return null;
	}
	
	/*---------------------查询种类--------------------------*/
	@Override
	public Category queryCategoryById(int _id){
		return getQuetyCategory("_id",_id+"");
		
	}
	@Override
	public Category queryCategoryByCategory(String category){
		return getQuetyCategory("category",category);
		
	}
	@Override
	public List<Category> queryAllCategory(){
		Cursor cgCursor=resolver.query(categorysUri, null, null, null, null);
		List<Category> list=null;
		while(cgCursor.moveToNext()){
			if (list==null){list=new ArrayList<Category>();}
			Category category = new Category(cgCursor.getInt(0),cgCursor.getString(1));
			if(category.getName()==null||category.getName()=="")continue;
			list.add(category);
		}
		cgCursor.close();
		return list;
		
	}
	/**
	 * 种类查询工具
	 * @param selection 参数:可以是name和_id
	 * @return Category对象
	 */
	private Category getQuetyCategory(String selection,String selectionArgs) {
		Cursor cgCursor=resolver.query(categorysUri, null, selection+"=?", new String[]{selectionArgs}, null);
		if(cgCursor.moveToNext()){
			if(cgCursor.getString(1)==null||cgCursor.getString(1)==""){
				cgCursor.close();
				return null;
			}
			Category category = new Category(cgCursor.getInt(0),cgCursor.getString(1));
			cgCursor.close();
			return category;
		}
		cgCursor.close();
		return null;
	}
	
	/*--------------入库商品查询--------------------*/
	@Override
	public InWares queryInWaresByInTime(long inTime){
		return getQuetyInWares("inTime", inTime+"");
		
	}
	@Override
	public InWares queryInWaresByPrice(Float inPrice){
		return getQuetyInWares("inPrice", inPrice+"");
		
	}
	@Override
	public InWares queryInWaresById(int _id){
		return getQuetyInWares("_id",_id+"");
	}
	@Override
	public InWares queryInWaresByCode(String code){
		return getQuetyInWares("code",code);
	}
	@Override
	public List<InWares> queryAllInWaresByDesc(){
		return getQueryInWaresList(null,null,"inTime desc");
	}
	@Override
	public List<InWares> queryAllInWares(){
		return getQueryInWaresList(null,null,null);
	}
	@Override
	public int queryInWaresTotal(){
		Cursor cursor = resolver.query(inwaresUri, new String[]{"isSell","amount"}, null, null, null);
		int total=0;
		while(cursor.moveToNext()){
			total+=(cursor.getInt(1)-cursor.getInt(0));
		}
		cursor.close();
		return total;
		
	}
	@Override
	public int queryInWaresTotalByMonth(int year,int month){
		long l1 = MyDateUtils.formatToLong(year+"-"+month+"-1", "yyyy-MM-dd");
		long l2 = MyDateUtils.formatToLong(year+"-"+(month+1)+"-1", "yyyy-MM-dd");
		Cursor cursor = resolver.query(inwaresUri, new String[]{"amount"}, "inTime>? and inTime<?", new String[]{""+l1,""+l2}, null);
		int total=0;
		while(cursor.moveToNext()){
			total+=cursor.getInt(0);
		}
		cursor.close();
		return total;
		
	}
	@Override
	public List<InWares> queryInWaresListOrderByLimit(int total,int offset){
		return getQueryInWaresList(null,null," inTime desc limit "+total+","+offset);
	}
	@Override
	public List<InWares> queryInWaresByWaresId(int wares_id){
		return getQueryInWaresList("wares_id=?",new String[]{wares_id+""}," inTime desc");
	}
	@Override
	public List<InWares> queryInWaresByImagePath(String imagePath) {
		Wares wares = queryWaresByImagePath(imagePath);
		return queryInWaresByWaresId(wares.get_id());
	}
	@Override
	public List<InWares> queryInWaresByName(String name){
		Wares wares = queryWaresByName(name);
		return queryInWaresByWaresId(wares.get_id());
	}

	
	@Override
	public List<InWares> queryInWaresByCategoryId(int category_id){
		List<Wares> waress = queryWaresByCategoryId(category_id);
		List<InWares> listIn=null;
		for(Wares wares:waress){
			if (listIn==null){listIn=new ArrayList<InWares>();}
			List<InWares> inwaresList = getQueryInWaresList("wares_id=?",new String[]{wares.get_id()+""}," inTime desc");
			if(inwaresList!=null&&inwaresList.size()>0){
				listIn.addAll(inwaresList);
			}
		}
		return listIn;
	}
	@Override
	public List<InWares> queryInWaresByCategoryName(String categoryName){
		Category category = queryCategoryByCategory(categoryName);
		return queryInWaresByCategoryId(category.get_id());
		
	}
	/**
	 * 入库商品查询工具
	 * @param selection String
	 * @param wares Wares对象
	 * @return InWares对象
	 */
	private InWares getQuetyInWares(String selection,String selectionArgs) {
		Cursor cursor = resolver.query(inwaresUri, new String[]{"_id","inTime","inPrice","wares_id","tabPrice","code","isSell","amount"}, selection+"=?", new String[]{selectionArgs}, null);
		if (cursor.moveToNext()) {
			InWares inWares = new InWares(cursor.getInt(0), cursor.getLong(1), cursor.getFloat(2), queryWaresById(cursor.getInt(3)),cursor.getFloat(4),cursor.getString(5),cursor.getInt(6),cursor.getInt(7));
			cursor.close();
			return inWares;
		}
		cursor.close();
		return null;
	}
	@Override
	public long getQuetyInWaresEndTime() {
		Cursor cursor = resolver.query(inwaresUri, new String[]{"inTime"}, null,null, null);
		long l=0;
		while (cursor.moveToNext()) {
			long l2 = cursor.getLong(0);
			if(l2>l)l=l2;
		}
		cursor.close();
		return l;
	}
	
	/**
	 * 查询入库商品InWares的List集合
	 * @param selection where后的String=?
	 * @param selectionArgs String[]
	 * @return List<Wares>
	 */
	private List<InWares> getQueryInWaresList(String selection,String[] selectionArgs,String orderby) {
		Cursor cursor = resolver.query(inwaresUri, new String[]{"_id","inTime","inPrice","wares_id","tabPrice","code","isSell","amount"}, selection, selectionArgs, orderby);
		List<InWares> list=null;
		while(cursor.moveToNext()){
			Wares wares = queryWaresById(cursor.getInt(3));
			if (wares==null||wares.getName()==null||wares.getName()=="") continue;
			if (list==null){list=new ArrayList<InWares>();}
			InWares inwares=new InWares(cursor.getInt(0), cursor.getLong(1), cursor.getFloat(2), queryWaresById(cursor.getInt(3)),cursor.getFloat(4),cursor.getString(5),cursor.getInt(6),cursor.getInt(7));
			list.add(inwares);
		}
		cursor.close();
		return list;
	}

	@Override
	public SellWares querySellWaresById(int _id){
		return getQuetySellWares("_id",_id+"");
	}
	@Override
	public Image queryIamgeId(int _id){
		return getQuetyIamge("_id",_id+"");
	}
	@Override
	public Image queryIamgeByPath(String imagePath){
		return getQuetyIamge("imagePath",imagePath+"");
	}
	@Override
	public Grade queryGradeById(int _id){
		return getQuetyGrade("_id",_id+"");
	}
	@Override
	public Grade queryGradeByComments(String comments){
		return getQuetyGrade("comments",comments+"");
	}
	@Override
	public List<Grade> queryAllGrade(){
		return getQuetyGradeList(null,null);
	}
	@Override
	public List<Image> queryAllImage(){
		return getQuetyImageList(null,null);
	}
	@Override
	public List<Image> queryIamgeByWaresId(int wares_id) {
		if (wares_id!=0) {
			return getQuetyImageList("wares_id=?",new String[]{wares_id+""});
		}
		return null;
	}
	private Grade getQuetyGrade(String selection, String selectionArgs) {
		Cursor cursor = resolver.query(gradeUri, new String[]{"_id","comments"}, selection+"=?", new String[]{selectionArgs}, null);
		if (cursor.moveToNext()) {
			if (cursor.getString(1) == null) {
				cursor.close();
				return null;
			}
			Grade grade = new Grade(cursor.getInt(0), cursor.getString(1));
			cursor.close();
			return grade;
		}
		cursor.close();
		return null;
	}
	private List<Grade> getQuetyGradeList(String selection, String[] selectionArgs) {
		Cursor cursor = resolver.query(gradeUri, new String[]{"_id","comments"}, selection, selectionArgs, null);
		List<Grade> list=null;
		while (cursor.moveToNext()) {
			 if (list==null) {list=new ArrayList<Grade>();}
			 Grade grade = queryGradeById(cursor.getInt(0));
			 if(grade==null||grade.getComments()==null||grade.getComments()=="")continue;
			 list.add(grade);
		}
		cursor.close();
		 return list;
	}
	
	/**
	 * 查图片集合
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	private List<Image> getQuetyImageList(String selection, String[] selectionArgs) {
			Cursor cursor = resolver.query(imagesUri, new String[] { "_id","imagePath", "wares_id" }, selection, selectionArgs, null);
			List<Image> list = null;
			while (cursor.moveToNext()) {
				if (list == null) {
					list = new ArrayList<Image>();
				}
				Image image = queryIamgeId(cursor.getInt(0));
				if (image == null || image.getPath() == ""|| image.getPath() == null)continue;
				list.add(image);
			}
			cursor.close();
			return list;
	}

	private Image getQuetyIamge(String selection, String selectionArgs) {
		Cursor cursor = resolver.query(imagesUri, new String[]{"_id","imagePath","wares_id"}, selection+"=?", new String[]{selectionArgs}, null);
		if (cursor.moveToNext()) {
			Wares wares = queryWaresById(cursor.getInt(2));
			if (wares == null || wares.getName() == ""|| wares.getName() == null){
				cursor.close();
				return null;
			}
			if (cursor.getString(1) == null || cursor.getString(1) == ""){
				cursor.close();
				return null;
			}
			Image image = new Image(cursor.getInt(0), cursor.getString(1), wares);
			cursor.close();
			return image;
		}
		cursor.close();
		return null;
	}
	@Override
	public SellWares querySellWaresByOutTime(long outTime){
		return getQuetySellWares("outTime", outTime+"");
		
	}
	@Override
	public SellWares querySellWaresByOutPrice(Float outPrice){
		return getQuetySellWares("outPrice", outPrice+"");
		
	}
	@Override
	public SellWares querySellWaresByProfit(Float profit){
		return getQuetySellWares("profit", profit+"");
		
	}
	@Override
	public SellWares querySellWaresByCode(String code) {
		InWares inWares = queryInWaresByCode(code);
		if (inWares!=null) {
			SellWares sellWares = querySellWaresByInwaresId(inWares.get_id());
			if(sellWares!=null)
				return sellWares;
		}
		return null;
		
	}
	@Override
	public SellWares querySellWaresByInwaresId(int inwares_id){
		return getQuetySellWares("inwares_id", inwares_id+"");
		
	}
	
	/**
	 * 查询工具
	 * @param selection
	 * @param selectionArgs
	 * @param orderby 
	 * @return
	 */
	private SellWares getQuetySellWares(String selection, String selectionArgs) {
		Cursor cursor = resolver.query(sellwaresUri, new String[]{"_id","outTime","outPrice","profit","inwares_id","customer_id","amount"}, selection+"=?", new String[]{selectionArgs}, null);
		if (cursor.moveToNext()) {
			InWares inWares = queryInWaresById(cursor.getInt(4));
			if (inWares==null||inWares.getInPrice()==0){ cursor.close();return null;}
			Customer customer=queryCustomerById(cursor.getInt(5));
			if (customer==null||customer.getName()==""||customer.getName()==null){ cursor.close();return null;}
			 SellWares sellWares = new SellWares(cursor.getInt(0), cursor.getLong(1), cursor.getFloat(2), cursor.getFloat(0), inWares,customer,cursor.getInt(6));
			 cursor.close();
			 return sellWares;
		}
		cursor.close();
		return null;
	}
	/**
	 * 出售商品集合查询工具
	 * @param selection
	 * @param selectionArgs
	 * @param orderby
	 * @return
	 */
	private List<SellWares> getQuetySellWaresOrderBy(String selection, String selectionArgs,String orderby) {
		if(orderby==null)orderby=" outTime desc";
		String[] selectionArg=null;
		if(selectionArgs!=null){
			selectionArg=new String[]{selectionArgs};
		}
		Cursor cursor = resolver.query(sellwaresUri, new String[]{"_id","outTime","outPrice","profit","inwares_id","customer_id","amount"}, selection, selectionArg, orderby);
		List<SellWares> list=null;
		while(cursor.moveToNext()){
			if(list==null)list=new ArrayList<SellWares>();
			InWares inWares = queryInWaresById(cursor.getInt(4));
			if (inWares==null||inWares.getInPrice()==0||inWares.getIsSell()==1){ continue;}
			Customer customer=queryCustomerById(cursor.getInt(5));
			if (customer==null||customer.getName()==""||customer.getName()==null){continue;}
			SellWares sellWares = new SellWares(cursor.getInt(0), cursor.getLong(1), cursor.getFloat(2), cursor.getFloat(0), inWares,customer,cursor.getInt(6));
			list.add(sellWares);
		}
		cursor.close();
		return list;
	}
	
	@Override
	public List<SellWares> queryAllSellWares(){
		return getQuetySellWaresOrderBy(null,null,null);
	}
	@Override
	public int querySellWaresTotal() {
		Cursor cursor = resolver.query(sellwaresUri, new String[]{"outTime"}, null, null, null);
		int total=0;
		while(cursor.moveToNext()){
			total++;
		}
		cursor.close();
		return total;
		
	}
	@Override
	public List<SellWares> queryAllSellWaresOrderByDesc(){
		return getQuetySellWaresOrderBy(null,null," outTime desc");
	}
	@Override
	public List<SellWares> queryAllSellWaresOrderByLimit(int total,int offset){
		return getQuetySellWaresOrderBy(null,null," outTime desc limit "+total+","+offset);
	}

	@Override
	public List<SellWares> querySellWaresByName(String name){
		Wares wares = queryWaresByName(name);
		return getQuetySellWaresOrderBy("wares_id=?",wares.get_id()+"",null);
	}
	@Override
	public List<SellWares> querySellWaresByImagePath(String imagePath){
		Wares wares = queryWaresByImagePath(imagePath);
		return getQuetySellWaresOrderBy("wares_id=?",wares.get_id()+"",null);
	}
	
	@Override
	public List<SellWares> querySellWaresByCategoryId(int category_id){
		
		List<InWares> inWaresByCategoryId = queryInWaresByCategoryId(category_id);
		List<SellWares> list=null;
		if (inWaresByCategoryId!=null) {
			if(list==null)list=new ArrayList<SellWares>();
			for (InWares inWares :inWaresByCategoryId) {
				if(inWares.getIsSell()==0){
					SellWares sellWares = querySellWaresByInwaresId(inWares.get_id());
					list.add(sellWares);
				}
			}
		}
		 return list;
	}
	@Override
	public List<SellWares> querySellWaresByCategoryName(String CategoryName) {
		Category category = queryCategoryByCategory(CategoryName);
		return querySellWaresByCategoryId(category.get_id());
		
	}
	
	private List<SellWares> getQueryByInWares(List<InWares> inWaresList) {
		List<SellWares> list=null;
		if(inWaresList==null){
			return null;
		}
		 for (int i = 0; i < inWaresList.size(); i++) {
			 if (list==null) {list=new ArrayList<SellWares>();}
			 list.add(querySellWaresByInwaresId(inWaresList.get(i).get_id()));
		}
		 return list;
	}

	/*-------------删除功能--------------------*/
	@Override
	public boolean deleteImage(Image image) {
		String where="_id=?";
		String[] selectionArgs=new String[]{image.get_id()+""};
		int i = resolver.delete(imagesUri, where, selectionArgs);
		if (i!=0) {
		return true;	
		}
		return false;
	}

	@Override
	public boolean deleteGrade(Grade grade) throws Exception {
		String where = "_id=?";
		String[] selectionArgs = new String[] { grade.get_id() + "" };
		if (grade.get_id() != null) {
			List<Customer> list = queryCustomerByGradeId(grade.get_id());
			if (list != null && list.size() > 0) {
				throw new Exception("还有该等级顾客存在");
			}
			int i = resolver.delete(gradeUri, where, selectionArgs);
			if (i != 0) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean deleteCategory(Category category) throws Exception {
		String where="_id=?";
		String[] selectionArgs=new String[]{category.get_id()+""};
		if(category!=null&&category.get_id()!=null){
			List<Wares> list = queryWaresByCategoryId(category.get_id());
			if (list != null &&list.size() > 0) {
				throw new Exception("还有该种类商品存在");
			}
		int i = resolver.delete(categorysUri, where, selectionArgs);
		if (i!=0) {
			return true;	
		}
		}
		return false;
	}

	@Override
	public boolean deleteCustomer(Customer customer) throws Exception {
		String where = "_id=?";
		if (customer != null && customer.get_id() != null) {
			String[] selectionArgs = new String[] { customer.get_id() + "" };
			List<SellWares> list = querySellWaresByCustomerId(customer.get_id());
			if (list != null && list.size() > 0) {
				throw new Exception("还有顾客购买记录存在");
			}
			int i = resolver.delete(customerUri, where, selectionArgs);
			if (i != 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean deleteWares(Wares wares) throws Exception {
		String where = "_id=?";
		String[] selectionArgs = new String[] { wares.get_id() + "" };
		if (wares != null && wares.get_id() != null) {
			List<InWares> list = queryInWaresByWaresId(wares.get_id());
			if (list != null&&list.size() > 0) {
				throw new Exception("还有该商品进货记录存在");
			}
			int i = resolver.delete(waresUri, where, selectionArgs);
			if (i != 0) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean deleteInWares(InWares inWares) throws Exception {
		String where="_id=?";
		String[] selectionArgs=new String[]{inWares.get_id()+""};
		if (inWares != null && inWares.get_id() != null) {
			SellWares list = querySellWaresByInwaresId(inWares.get_id());
			if (list != null) {
				throw new Exception("还有出售该商品记录存在");
			}
			
			int i = resolver.delete(inwaresUri, where, selectionArgs);
			if (i != 0) {
				Wares wares =inWares.getWares();
				if (wares != null && wares.getStock() != null) {
					int stock = wares.getStock() +inWares.getIsSell()-inWares.getAmount();
					if (stock <0)
						stock = 0;
					updateWaresStock(stock, wares);
				}
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean deleteSellWares(SellWares sellWares) throws Exception {
		InWares inWares = sellWares.getInWares();
		if(inWares==null){
			throw new Exception("商品不存在");
		}
		String where="_id=?";
		String[] selectionArgs=new String[]{sellWares.get_id()+""};
		int i = resolver.delete(sellwaresUri, where, selectionArgs);
		if (i!=0) {
			Wares wares =inWares.getWares();
			if (wares != null && wares.getStock() != null) {
				int stock = wares.getStock()+sellWares.getAmount();
				updateInWaresIsSell(inWares.getIsSell()-sellWares.getAmount(), inWares);
				updateWaresStock(stock, wares);
			}
			Customer customs = sellWares.getCustoms();
			if (customs != null ) {
				Float total = customs.getTotal()-sellWares.getOutPrice()*sellWares.getAmount();
				if(total<0)total=0f;
				updateCustomerTotal(total, customs);
			}
			return true;	
		}
		return false;
	}
	/*------------------顾客-----------------------*/

	@Override
	public Customer queryCustomerById(int _id) {
		return getQuetyCustomer("_id", _id+"");
	}
	@Override
	public List<Customer> queryCustomerByGradeId(int grade_id) {
		return getQuetyListCustomer("grade_id", new String[]{grade_id+""});
		}
	
	@Override
	public Customer queryCustomerByName(String name) {
		return getQuetyCustomer("name", name);
	}
	@Override
	public Customer queryCustomerByPhone(String phone) {
		return getQuetyCustomer("phone", phone);
	}
	@Override
	public Customer queryCustomerByComments(String comments) {
		return getQuetyCustomer("comments", comments);
	}
	@Override
	public List<Customer> queryAllCustomer() {
		return getQuetyListCustomer(null, null);
	}
	/**
	 * 查询顾客的工具类
	 * @param selection
	 * @param selectionArgs
	 * @return Customer对象
	 */
	private Customer getQuetyCustomer(String selection, String selectionArgs) {
		Cursor cursor = resolver.query(customerUri, new String[]{"_id","name","phone","comments","total","grade_id"}, selection+"=?", new String[]{selectionArgs}, null);
		if(cursor.moveToNext()){
			Grade grade=queryGradeById(cursor.getInt(5));
			if(grade==null||grade.getComments()==null||grade.getComments()==""){cursor.close();return null;}
			if(cursor.getString(1)==null||cursor.getString(1)==""){cursor.close();return null;}
			Customer customer= new Customer(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)+"",cursor.getFloat(4),grade);
			cursor.close();
			return customer;
		}
		cursor.close();
		return null;
	}
	/**
	 * 查询顾客集合的工具类
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	private List<Customer> getQuetyListCustomer(String selection,
			String[] selectionArgs) {
		Cursor cursor = resolver.query(customerUri, new String[]{"_id","name","phone","comments","total","grade_id"}, selection, selectionArgs, null);
		List<Customer> list=null;
		while(cursor.moveToNext()){
			if (list==null){list=new ArrayList<Customer>();}
			Grade grade=queryGradeById(cursor.getInt(5));
			if(grade==null||grade.getComments()==null||grade.getComments()=="")continue;
			if(cursor.getString(1)==null||cursor.getString(1)=="")continue;
			Customer customer= new Customer(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)+"",cursor.getFloat(4),grade);
			list.add(customer);
		}
		cursor.close();
		return list;
	}
	@Override
	public List<SellWares> querySellWaresByCustomerName(String name) {
		Customer customer = queryCustomerByName(name);
		return querySellWaresByCustomerId(customer.get_id());
		 
	}
	
	@Override
	public List<SellWares> querySellWaresByCustomerPhone(String phone) {
		Customer customer = queryCustomerByPhone(phone);
		return querySellWaresByCustomerId(customer.get_id());
	}
	@Override
	public List<SellWares> querySellWaresByCustomerId(int customer_id) {
		return getQuetySellWaresOrderBy("customer_id=?", customer_id+"",null);
	}
	/**
	 * 查询指定月入库商品集合
	 * @param year 年
	 * @param month 月
	 * @param total 总共查多少条
	 * @param offset 从第几条开始查
	 * @return
	 */
	@Override
	public List<InWares> queryInWaresByMonth(int year,int month,int total,int offset) {
		long l1 = MyDateUtils.formatToLong(year+"-"+month+"-1", "yyyy-MM-dd");
		long l2 = MyDateUtils.formatToLong(year+"-"+(month+1)+"-1", "yyyy-MM-dd");
		//Cursor cursor = resolver.query(inwaresUri, new String[]{"_id","inTime","inPrice","wares_id","tabPrice","code","isSell"}, null, null, " inTime desc limit "+total+","+offset);
		Cursor cursor = resolver.query(inwaresUri, new String[]{"_id","inTime","inPrice","wares_id","tabPrice","code","isSell","amount"}, "inTime>? and inTime<?", new String[]{""+l1,""+l2}, " inTime desc");
		List<InWares> list=null;
		while(cursor.moveToNext()){
			Wares wares = queryWaresById(cursor.getInt(3));
			if (wares==null||wares.getName()==null||wares.getName()=="") continue;
			if (list==null){list=new ArrayList<InWares>();}
			InWares inwares=new InWares(cursor.getInt(0), cursor.getLong(1), cursor.getFloat(2), queryWaresById(cursor.getInt(3)),cursor.getFloat(4),cursor.getString(5),cursor.getInt(6),cursor.getInt(7));
			list.add(inwares);
		}
		cursor.close();
		return list;
	}
	/**
	 * 查询指定月入库商品集合
	 * @param year 年
	 * @param month 月
	 * @param total 总共查多少条
	 * @param offset 从第几条开始查
	 * @return
	 */
	@Override
	public List<InWares> queryInWaresSellByMonth(int year,int month,int total,int offset) {
		long l1 = MyDateUtils.formatToLong(year+"-"+month+"-1", "yyyy-MM-dd");
		long l2 = MyDateUtils.formatToLong(year+"-"+(month+1)+"-1", "yyyy-MM-dd");
		//Cursor cursor = resolver.query(inwaresUri, new String[]{"_id","inTime","inPrice","wares_id","tabPrice","code","isSell"}, null, null, " inTime desc limit "+total+","+offset);
		Cursor cursor = resolver.query(inwaresUri, new String[]{"_id","inTime","inPrice","wares_id","tabPrice","code","isSell","amount"}, "inTime>? and inTime<?", new String[]{""+l1,""+l2}, " inTime desc");
		List<InWares> list=null;
		while(cursor.moveToNext()){
			Wares wares = queryWaresById(cursor.getInt(3));
			if (wares==null||wares.getName()==null||wares.getName()=="") continue;
			if (list==null){list=new ArrayList<InWares>();}
			InWares inwares=new InWares(cursor.getInt(0), cursor.getLong(1), cursor.getFloat(2), queryWaresById(cursor.getInt(3)),cursor.getFloat(4),cursor.getString(5),cursor.getInt(6),cursor.getInt(7));
			list.add(inwares);
		}
		cursor.close();
		return list;
	}
	/**
	 * 查询指定月的入库总额
	 * @param year
	 * @param month
	 * @return
	 */
	@Override
	public float queryInWaresByMonthInMoney(int year,int month) {
		long l1 = MyDateUtils.formatToLong(year+"-"+month+"-1", "yyyy-MM-dd");
		long l2 = MyDateUtils.formatToLong(year+"-"+(month+1)+"-1", "yyyy-MM-dd");
		Cursor cursor = resolver.query(inwaresUri, new String[]{"inPrice","amount"}, "inTime>? and inTime<?", new String[]{""+l1,""+l2}, null);
		float in=0;
		while(cursor.moveToNext()){
			float inPrice = cursor.getFloat(0)* cursor.getInt(1);
				in+=inPrice;
		}
		cursor.close();
		return in;
	}
	/**
	 * 查询指定月的入库可售总额
	 * @param year
	 * @param month
	 * @return
	 */
	@Override
	public float queryInWaresSellByMonthInMoney(int year,int month) {
		long l1 = MyDateUtils.formatToLong(year+"-"+month+"-1", "yyyy-MM-dd");
		long l2 = MyDateUtils.formatToLong(year+"-"+(month+1)+"-1", "yyyy-MM-dd");
		Cursor cursor = resolver.query(inwaresUri, new String[]{"inPrice","isSell","amount"}, "inTime>? and inTime<?", new String[]{""+l1,""+l2}, null);
		float in=0;
		while(cursor.moveToNext()){
			int isSell = cursor.getInt(1);
			int amount = cursor.getInt(2);
			if(amount-isSell<=0){continue;}
			float inPrice = cursor.getFloat(0)*cursor.getInt(2);
			in+=inPrice;
		}
		cursor.close();
		return in;
	}
	
	/**
	 * 查询指定月的入库总数
	 * @param year
	 * @param month
	 * @return
	 */
	@Override
	public int queryInWaresByMonthCount(int year,int month) {
		long l1 = MyDateUtils.formatToLong(year+"-"+month+"-1", "yyyy-MM-dd");
		long l2 = MyDateUtils.formatToLong(year+"-"+(month+1)+"-1", "yyyy-MM-dd");
		Cursor cursor = resolver.query(inwaresUri, new String[]{"amount"},"inTime>? and inTime<?", new String[]{""+l1,""+l2}, null);
		int i=0;
		while(cursor.moveToNext()){
				i+=cursor.getInt(0);
		}
		cursor.close();
		return i;
	}
	/**
	 * 查询指定月的可售入库总数
	 * @param year
	 * @param month
	 * @return
	 */
	@Override
	public int queryInWaresSellByMonthCount(int year,int month) {
		long l1 = MyDateUtils.formatToLong(year+"-"+month+"-1", "yyyy-MM-dd");
		long l2 = MyDateUtils.formatToLong(year+"-"+(month+1)+"-1", "yyyy-MM-dd");
		Cursor cursor = resolver.query(inwaresUri, new String[]{"amount","isSell"}, "inTime>? and inTime<?", new String[]{""+l1,""+l2}, null);
		int i=0;
		while(cursor.moveToNext()){
			int amount = cursor.getInt(0);
			int isSell = cursor.getInt(1);
			if(amount-isSell>0){
				i+=(amount-isSell);
			}
		}
		cursor.close();
		return i;
	}
	/**
	 * 查询指定月的出售条目数 如果total=0,那么全部查询
	 * @param year 年
	 * @param month 月
	 * @param total 总共查多少条
	 * @param offset 从第几条开始查
	 * @return
	 */
	@Override
	public List<SellWares> querySellWaresByMonth(int year,int month,int offset ,int total) {
		long l1 = MyDateUtils.formatToLong(year+"-"+month+"-1", "yyyy-MM-dd");
		long l2 = MyDateUtils.formatToLong(year+"-"+(month+1)+"-1", "yyyy-MM-dd");
		Cursor cursor=null;
		if(total==0){
			 cursor = resolver.query(sellwaresUri, new String[]{"_id","outTime","outPrice","profit","inwares_id","customer_id","amount"},"outTime>? and outTime<?", new String[]{""+l1,""+l2}, " outTime desc ");
		}else{
			 cursor = resolver.query(sellwaresUri, new String[]{"_id","outTime","outPrice","profit","inwares_id","customer_id","amount"}, "outTime>? and outTime<?", new String[]{""+l1,""+l2}, " outTime desc limit "+offset+","+total);
		}
		List<SellWares> list=null;
		while(cursor.moveToNext()){
			if(list==null)list=new ArrayList<SellWares>();
			InWares inWares = queryInWaresById(cursor.getInt(4));
			if (inWares==null){ continue;}
			Customer customer=queryCustomerById(cursor.getInt(5));
			if (customer==null||customer.getName()==""||customer.getName()==null){continue;}
			SellWares sellWares = new SellWares(cursor.getInt(0), cursor.getLong(1), cursor.getFloat(2), cursor.getFloat(0), inWares,customer,cursor.getInt(6));
			sellWares.setProfit(cursor.getFloat(3));
			list.add(sellWares);
		}
		cursor.close();
		return list;
	}
	/**
	 * 查询特定月份的所有利润
	 * @param year
	 * @param month
	 * @return
	 */
	@Override
	public float querySellWaresByMonthProfit(int year,int month) {
		long l1 = MyDateUtils.formatToLong(year+"-"+month+"-1", "yyyy-MM-dd");
		long l2 = MyDateUtils.formatToLong(year+"-"+(month+1)+"-1", "yyyy-MM-dd");
		Cursor cursor = resolver.query(sellwaresUri, new String[]{"amount","profit"}, "outTime>? and outTime<?", new String[]{""+l1,""+l2}, null);
		float profit=0;
		while(cursor.moveToNext()){
			float f = cursor.getInt(0)*cursor.getFloat(1);
			profit+=f;
		}
		cursor.close();
		return profit;
	}
	/**
	 * 查询特定月份的所有销售额
	 * @param year
	 * @param month
	 * @return
	 */
	@Override
	public float querySellWaresByMonthAllSell(int year,int month) {
		long l1 = MyDateUtils.formatToLong(year+"-"+month+"-1", "yyyy-MM-dd");
		long l2 = MyDateUtils.formatToLong(year+"-"+(month+1)+"-1", "yyyy-MM-dd");
		Cursor cursor = resolver.query(sellwaresUri, new String[]{"amount","outPrice"}, "outTime>? and outTime<?", new String[]{""+l1,""+l2}, null);
		float allSell=0;
		
		while(cursor.moveToNext()){
			float f = cursor.getInt(0)*cursor.getFloat(1);
			allSell+=f;
		}
		cursor.close();
		return allSell;
	}
	
	@Override
	public float querySellWaresAllSell() {
		Cursor cursor = resolver.query(sellwaresUri, new String[]{"outPrice","amount"}, null, null, null);
		float allSell=0;
		while(cursor.moveToNext()){
			float f = cursor.getFloat(0)*cursor.getInt(1);
			allSell+=f;
		}
		cursor.close();
		return allSell;
	}
	/**
	 * 查询所有销售数量
	 * @param year
	 * @param month
	 * @return
	 */
	@Override
	public int querySellWaresCount() {
		Cursor cursor = resolver.query(sellwaresUri, new String[]{"outTime"}, null, null, null);
		int i=0;
		while(cursor.moveToNext()){
			if(cursor.getLong(0)!=0L);
				i+=1;
		}
		cursor.close();
		return i;
	}
	/**
	 * 查询特定月份的所有条目总数
	 * @param year
	 * @param month
	 * @return
	 */
	@Override
	public int querySellWaresByMonthCount(int year,int month) {
		long l1 = MyDateUtils.formatToLong(year+"-"+month+"-1", "yyyy-MM-dd");
		long l2 = MyDateUtils.formatToLong(year+"-"+(month+1)+"-1", "yyyy-MM-dd");
		Cursor cursor = resolver.query(sellwaresUri, new String[]{"amount"}, "outTime>? and outTime<?", new String[]{""+l1,""+l2}, null);
		int i=0;
		while(cursor.moveToNext()){
			i+=cursor.getInt(0);
		}
		cursor.close();
		return i;
	}
	
	@Override
	public int queryCustomerCount() {
		Cursor cursor = resolver.query(customerUri, new String[]{"name"}, null, null, null);
		int i=0;
		while(cursor.moveToNext()){
			if(cursor.getString(0)!=null)
				i+=1;
		}
		cursor.close();
		return i;
	}
	
	@Override
	public int queryWaresCount() {
		Cursor cursor = resolver.query(waresUri, new String[]{"name"}, null, null, null);
		int i=0;
		while(cursor.moveToNext()){
			if(cursor.getString(0)!=null)
				i+=1;
		}
		cursor.close();
		return i;
	}
	@Override
	public List<Wares> queryWaresNoStock(){
		return getQueryWaresList("stock=?", new String[]{0+""}, null);
//		Cursor cursor = resolver.query(waresUri, new String[]{"_id","name","imagePath","stock","category_id"}, null, null, null);
//		List<Wares> list=null;
//		while(cursor.moveToNext()){
//			if(cursor.getInt(3)>0)continue;
//			Category category = queryCategoryById(cursor.getInt(4));
//			if(category==null)continue;
//			if (list==null){list=new ArrayList<Wares>();}
//			Wares wares= new Wares(cursor.getInt(0),  cursor.getString(1), cursor.getString(2), cursor.getInt(3),category);
//			if (wares.getName()!=null) {
//				list.add(wares);
//			}
//		}
//		cursor.close();
//		return list;
	}
	
	
	
	
}
