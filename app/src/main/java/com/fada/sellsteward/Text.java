package com.fada.sellsteward;

import java.io.File;
import java.util.List;

import android.os.Environment;
import android.test.AndroidTestCase;

import com.fada.sellsteward.db.SellStewardDao;
import com.fada.sellsteward.db.SellStewardDaoImpl;
import com.fada.sellsteward.domain.Customer;
import com.fada.sellsteward.domain.Grade;
import com.fada.sellsteward.domain.InWares;
import com.fada.sellsteward.domain.SellWares;
import com.fada.sellsteward.domain.Wares;

public class Text extends AndroidTestCase {

	public void testAdd() throws Exception {

		SellStewardDao dao= SellStewardDaoImpl.getDaoInstance(getContext());
		dao.addCategory("外套");
		dao.addCategory("裤子");
		dao.addCategory("裙子");
		dao.addCategory("包包");
		dao.addCategory("鞋子");
		Grade grade = new Grade(null, "普通客户");
		dao.addGrade(grade);
		grade = new Grade(null, "铜牌客户");
		dao.addGrade(grade);
		grade = new Grade(null, "银牌客户");
		dao.addGrade(grade);
		grade = new Grade(null, "金牌客户");
		dao.addGrade(grade);
		grade = new Grade(null, "vip客户");
		dao.addGrade(grade);
		for (int i = 0; i < 20; i++) {

			Customer customer = new Customer("张三" + i, "1531322521" + i,
					"");
			dao.addCustomer(customer);
		}
		File dir = Environment.getExternalStorageDirectory();
		File file = new File(dir, "DCIM/Camera/");
		File[] files = file.listFiles();
		int j=1;
		for (int i = 0; i < files.length; i++) {
			String imagePath = files[i].getAbsolutePath();
			if(j>5)j=1;
			Wares wares = new Wares("韩版小外套"+i, imagePath, dao.queryCategoryById(j));
			j++;
			dao.addWares(wares);
		}
		long l=24*60*60*1000L;
		List<Wares> wares = dao.queryAllWares();
		for (int i = 0; i < wares.size(); i++) {
			InWares inWares=new InWares(System.currentTimeMillis()+l*i, 21f+i, wares.get(i), 21f*2+i, "123456"+i,i);
			dao.addInWares(inWares);
		}
		List<InWares> inWares = dao.queryAllInWares();
		int ii = dao.queryAllCustomer().size();
		ii-=1;
		j=1;
		for (int i = 0; i < inWares.size(); i++) {
			if(j>=ii)j=1;
			SellWares sellWares=new SellWares(System.currentTimeMillis()+l*i, 23f*2+i,  inWares.get(i), dao.queryCustomerById(j),1);
			j++;
			dao.addSellWares(sellWares);
		}
		for (int i = 0; i < wares.size(); i++) {
			InWares inWare=new InWares(System.currentTimeMillis()+l*i, 21f+i, wares.get(i), 21f*2+i, "123456"+i,i);
			dao.addInWares(inWare);
		}

	}

	public void testDelete() throws Exception {
		SellStewardDao dao= SellStewardDaoImpl.getDaoInstance(getContext());
		long l=24*60*60*1000L;
		List<Wares> wares = dao.queryAllWares();
		for (int i = 0; i < wares.size(); i++) {
			InWares inWares=new InWares(System.currentTimeMillis()+l*i, 21f+i, wares.get(i), 21f*2+i, "123456"+i,i);
			dao.addInWares(inWares);
		}
	}

	public void testUpdate() {
		SellStewardDao dao= SellStewardDaoImpl.getDaoInstance(getContext());
		dao.queryCategoryByCategory("裙子");
		dao.queryCategoryById(2);
		dao.queryInWaresByCategoryId(2);
		dao.queryInWaresByCode("3455555444");
		dao.queryInWaresById(4);
		dao.queryInWaresByName("漂亮的裙子");
		dao.queryInWaresByPrice(33.33f);
		dao.queryInWaresByWaresId(1);

		System.out.println(dao.queryAllCategory());
	}

	public void testU() {
		SellStewardDao dao= SellStewardDaoImpl.getDaoInstance(getContext());
		List<SellWares> list = dao.querySellWaresByCategoryId(1);
		System.out.println(dao.queryAllCategory());
	}

}
