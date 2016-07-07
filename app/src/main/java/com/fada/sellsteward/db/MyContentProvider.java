package com.fada.sellsteward.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class MyContentProvider extends ContentProvider {
	private final static String authority = "com.fada.sellsteward.db.MyContentProvider";
	private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	private final static int CATEGORYS = 11;//商品类别表
	private final static int WARES = 21;//商品表
	private final static int INWARES = 31;//入库商品表
	private final static int SELLWARES = 41;//出库商品表
	private final static int CUSTOMER = 51;//出库商品表
	private final static int IMAGES = 61;//出库商品表
	private final static int GRADE = 71;//出库商品表
	private SQLiteOpenHelper dbHelper;

	@Override
	public boolean onCreate() {
		dbHelper = DBhelper.getInstance(getContext());
		matcher.addURI(authority, "categorys", CATEGORYS);
		matcher.addURI(authority, "wares", WARES);
		matcher.addURI(authority, "inwares", INWARES);
		matcher.addURI(authority, "sellwares", SELLWARES);
		matcher.addURI(authority, "customer", CUSTOMER);
		matcher.addURI(authority, "images", IMAGES);
		matcher.addURI(authority, "grade", GRADE);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		 SQLiteDatabase db = dbHelper.getReadableDatabase();
		 Cursor cursor=null;
		switch (matcher.match(uri)) {
		case CATEGORYS:
			 cursor = db.query("categorys", projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case WARES:
			 cursor = db.query("wares", projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case INWARES:
			 cursor = db.query("inwares", projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case SELLWARES:
			 cursor = db.query("sellwares", projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case CUSTOMER:
			cursor = db.query("customer", projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case IMAGES:
			cursor = db.query("images", projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case GRADE:
			cursor = db.query("grade", projection, selection, selectionArgs, null, null, sortOrder);
			break;
		default:
			throw new IllegalArgumentException("没有匹配的uri " + uri);
		}
		
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		 SQLiteDatabase db = dbHelper.getWritableDatabase();
		 long id=0;
		 switch (matcher.match(uri)) {
			case CATEGORYS:
				id = db.insert("categorys", null, values);
				break;
			case WARES:
				id = db.insert("wares", null, values);
				break;
			case INWARES:
				 id = db.insert("inwares", null, values);
				break;
			case SELLWARES:
				id=db.insert("sellwares", null, values);
				break;
			case CUSTOMER:
				id=db.insert("customer", null, values);
				break;
			case IMAGES:
				id=db.insert("images",null, values);
				break;
			case GRADE:
				id=db.insert("grade", null, values);
				break;
			default:
				throw new IllegalArgumentException("没有匹配的uri " + uri);
			}
		
		return  ContentUris.withAppendedId(uri, id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		 SQLiteDatabase db = dbHelper.getWritableDatabase();
		 int id=0;
		 switch (matcher.match(uri)) {
			case CATEGORYS:
				 id = db.delete("categorys", selection, selectionArgs);
				break;
			case WARES:
				 id =db.delete("wares", selection, selectionArgs);
				break;
			case INWARES:
				 id =db.delete("inwares", selection, selectionArgs);
				break;
			case SELLWARES:
				 id =db.delete("sellwares", selection, selectionArgs);
				break;
			case CUSTOMER:
				id =db.delete("customer", selection, selectionArgs);
				break;
			case IMAGES:
				id=db.delete("images",selection, selectionArgs);
				break;
			case GRADE:
				id=db.delete("grade", selection, selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("没有匹配的uri " + uri);
			}
		return id;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		 SQLiteDatabase db = dbHelper.getWritableDatabase();
		 int id=0;
		 switch (matcher.match(uri)) {
			case CATEGORYS:
				id = db.update("categorys", values, selection, selectionArgs);
				break;
			case WARES:
				id = db.update("wares", values, selection, selectionArgs);
				break;
			case INWARES:
				id = db.update("inwares", values, selection, selectionArgs);
				break;
			case SELLWARES:
				id = db.update("sellwares", values, selection, selectionArgs);
				break;
			case CUSTOMER:
				id = db.update("customer", values, selection, selectionArgs);
				break;
			case IMAGES:
				id=db.update("images",values, selection, selectionArgs);
				break;
			case GRADE:
				id=db.update("grade",values, selection, selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("没有匹配的uri " + uri);
			}
		return id;
	}
}
