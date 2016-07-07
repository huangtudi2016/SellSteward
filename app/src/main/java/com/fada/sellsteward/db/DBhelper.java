package com.fada.sellsteward.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class DBhelper extends SQLiteOpenHelper{

	private static SQLiteOpenHelper mInstance;
	private final static String NAME = "SellSteward.db";
	
	private DBhelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public static synchronized SQLiteOpenHelper getInstance(Context context){
		
		if (mInstance==null) {
			mInstance=new DBhelper(context, NAME, null, 1);
		}
		return mInstance;
		
	}
	
	//CREATE TABLE categorys(_id INTEGER PRIMARY KEY AUTOINCREMENT,category TEXT)//创建类别表
	//创建商品:CREATE TABLE wares(_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,imagePath TEXT,stock int,category_id int,CONSTRAINT category_id_fk FOREIGN KEY(category_id) REFERENCES categorys(_id))
//入库商品:CREATE TABLE inwares(_id INTEGER PRIMARY KEY AUTOINCREMENT,inTime Date,inPrice float,wares_id int,CONSTRAINT wares_id_fk FOREIGN KEY(wares_id) REFERENCES wares(_id))
//出库商品:CREATE TABLE sellwares(_id INTEGER PRIMARY KEY AUTOINCREMENT,outTime Date,outPrice float,profit float,inwares_id int,CONSTRAINT inwares_id_fk FOREIGN KEY(inwares_id) REFERENCES inwares(_id))
//insert into categorys(id,category) values(1,"上衣")
	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建类别表
         db.execSQL("CREATE TABLE categorys(_id INTEGER PRIMARY KEY AUTOINCREMENT,category TEXT)");
         //创建等级表
         db.execSQL("CREATE TABLE grade(_id INTEGER PRIMARY KEY AUTOINCREMENT,comments TEXT)");
         //顾客表:名字,电话
         db.execSQL("CREATE TABLE customer(_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,phone TEXT,comments TEXT,total float,grade_id int,CONSTRAINT grade_id_fk FOREIGN KEY(grade_id) REFERENCES grade(_id))");
       //创建商品表
         db.execSQL("CREATE TABLE wares(_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,imagePath TEXT,stock int,category_id int,CONSTRAINT category_id_fk FOREIGN KEY(category_id) REFERENCES categorys(_id))");
         //创建图片集合表
         db.execSQL("CREATE TABLE images(_id INTEGER PRIMARY KEY AUTOINCREMENT,imagePath TEXT,wares_id int,CONSTRAINT wares_id_fk FOREIGN KEY(wares_id) REFERENCES wares(_id))");
       //入库商品表
         db.execSQL("CREATE TABLE inwares(_id INTEGER PRIMARY KEY AUTOINCREMENT,inTime Date,inPrice float,wares_id int,tabPrice float,code TEXT,isSell int,amount int,CONSTRAINT wares_id_fk FOREIGN KEY(wares_id) REFERENCES wares(_id))");
         //出库商品表:出库时间,卖出价格,利润,入库商品约束
         db.execSQL("CREATE TABLE sellwares(_id INTEGER PRIMARY KEY AUTOINCREMENT,outTime Date,outPrice float,profit float,inwares_id int,customer_id int,amount int,CONSTRAINT inwares_id_fk FOREIGN KEY(inwares_id) REFERENCES inwares(_id),CONSTRAINT customer_id_fk FOREIGN KEY(customer_id) REFERENCES customer(_id))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 //db.execSQL("CREATE TABLE wares(_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,code bigint,imagePath TEXT,stock int,category_id int,CONSTRAINT category_id_fk FOREIGN KEY(category_id) REFERENCES categorys(_id))");
		// db.execSQL("CREATE TABLE sellwares(_id INTEGER PRIMARY KEY AUTOINCREMENT,outTime Date,outPrice float,profit float,inwares_id int,CONSTRAINT inwares_id_fk FOREIGN KEY(inwares_id) REFERENCES inwares(_id))");

	}


}
