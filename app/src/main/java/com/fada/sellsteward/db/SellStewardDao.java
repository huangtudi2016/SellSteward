package com.fada.sellsteward.db;

import java.util.List;

import com.fada.sellsteward.domain.Category;
import com.fada.sellsteward.domain.Customer;
import com.fada.sellsteward.domain.Grade;
import com.fada.sellsteward.domain.Image;
import com.fada.sellsteward.domain.InWares;
import com.fada.sellsteward.domain.SellWares;
import com.fada.sellsteward.domain.Wares;

public interface SellStewardDao {
	/**
	 * 增加商品种类
	 * @param name String 商品种类名称
	 * @throws Exception 
	 */
	public abstract boolean addCategory(String category) throws Exception;
	
	/**
	 * 增加商品
	 * @param wares 商品对象
	 * @throws Exception 
	 */
	public abstract boolean addWares(Wares wares) throws Exception;
/**
 * 增加入库商品
 * @param inWares 入库商品对象
 * @throws Exception 
 */
	public abstract boolean addInWares(InWares inWares) throws Exception;
	/**
	 * 增加出售商品
	 * @param sellWares 出售商品对象
	 * @throws Exception 
	 */
	public abstract boolean addSellWares(SellWares sellWares) throws Exception;

	/*--------------------顾客---------------------------*/
	/**
	 * 增加顾客
	 * @param name String 商品种类名称
	 * @throws Exception 
	 */
	public abstract boolean addCustomer(Customer customer) throws Exception;
	/**
	 * 查询全部顾客
	 * @return
	 */
	public abstract List<Customer> queryAllCustomer();
	/**
	 * 根据顾客名查询其买的所有商品
	 * @param name
	 * @return
	 */
	public abstract List<SellWares> querySellWaresByCustomerName(String name);
	/**
	 * 根据顾客电话查询其买的所有商品
	 * @param phone
	 * @return
	 */
	public abstract List<SellWares> querySellWaresByCustomerPhone(String phone);
	/**
	 * 根据顾客id其买的所有商品
	 * @param _id
	 * @return
	 */
	public abstract List<SellWares> querySellWaresByCustomerId(int _id);
	/**
	 * 查询顾客
	 * @param _id
	 * @return
	 */
	public abstract Customer queryCustomerById(int _id);
	/**
	 * 查询顾客通过名字
	 * @param _id
	 * @return
	 */
	public abstract Customer queryCustomerByName(String name);
	/**
	 * 查询顾客通过电话
	 * @param _id
	 * @return
	 */
	public abstract Customer queryCustomerByPhone(String phone);
	/**
	 * 查询顾客通过备注
	 * @param _id
	 * @return
	 */
	public abstract Customer queryCustomerByComments(String comments);
	/**
	 * 修改Customer
	 * @param sellWares
	 * @return boolean
	 * @throws Exception 
	 */
	public abstract boolean updateCustomer(Customer customer) throws Exception;
	
	/**
	 * 删除Customer
	 * @param category
	 * @return boolean
	 * @throws Exception 
	 */
	public abstract boolean deleteCustomer(Customer customer) throws Exception;
	
	
	
	/*--------------------商品查询---------------------------*/
	/**
	 * 查询全部商品
	 * @return
	 */
	public abstract List<Wares> queryAllWares();
	/**
	 * 根据种类id查询商品
	 * @param category_id 种类id
	 * @return 商品集合
	 */
	public abstract List<Wares> queryWaresByCategoryId(int category_id);
	
	/**
	 * 根据图片path查询商品
	 * @param imagePath
	 * @return
	 */
	public abstract Wares queryWaresByImagePath(String imagePath);
	/**
	 * 根据商名名称查询商品
	 * @param name
	 * @return
	 */
	public abstract Wares queryWaresByName(String name);
	/**
	 * 根据商品id查询商品
	 * @param _id
	 * @return
	 */
	public abstract Wares queryWaresById(int _id);

	/*---------------------查询种类--------------------------*/
	/**
	 * 根据id查询种类
	 * @param _id
	 * @return
	 */
	public abstract Category queryCategoryById(int _id);
	/**
	 * 根据种类名查询种类对象
	 * @param category
	 * @return
	 */
	public abstract Category queryCategoryByCategory(String category);
	/**
	 * 查询所有种类
	 * @return
	 */
	public abstract List<Category> queryAllCategory();

	/*--------------入库商品查询--------------------*/
	/**
	 * 根据入库时间查询入库商品
	 * @param inTime
	 * @return
	 */
	public abstract InWares queryInWaresByInTime(long inTime);
	/**
	 * 根据进货价格查询入库商品
	 * @param inPrice
	 * @return
	 */
	public abstract InWares queryInWaresByPrice(Float inPrice);
	/**
	 * 根据id查询入库商品
	 * @param _id
	 * @return
	 */
	public abstract InWares queryInWaresById(int _id);
/**
 * 查询所有和库商品
 * @return
 */
	public abstract List<InWares> queryAllInWares();
/**
 * 根据商品id查询入库商品
 * @param wares_id
 * @return
 */
	public abstract List<InWares> queryInWaresByWaresId(int wares_id);
/**
 * 根据商品名查询入库商品
 * @param name
 * @return
 */
	public abstract List<InWares> queryInWaresByName(String name);
/**
 * 根据商品条形码查询入库商品
 * @param code
 * @return
 */
	public abstract InWares queryInWaresByCode(String code);
/**
 * 根据种类id查询入库商品
 * @param category_id
 * @return
 */
	public abstract List<InWares> queryInWaresByCategoryId(int category_id);
	/**
	 * 根据种类名查询入库商品
	 * @param categoryName
	 * @return
	 */
	public abstract List<InWares> queryInWaresByCategoryName(String categoryName);
	
	/*----------------出售商品-------------*/
/**
 * 根据id查询出售商品
 * @param _id
 * @return
 */
	public abstract SellWares querySellWaresById(int _id);
/**
 * 根据出售时间查询入库商品
 * @param outTime
 * @return
 */
	public abstract SellWares querySellWaresByOutTime(long outTime);
/**
 * 根据出售价格查询出售商品
 * @param outPrice
 * @return
 */
	public abstract SellWares querySellWaresByOutPrice(Float outPrice);
/**
 * 根据利润查询出售商品
 * @param profit
 * @return
 */
	public abstract SellWares querySellWaresByProfit(Float profit);
/**
 * 根据入库商品id查询出售商品
 * @param inwares_id
 * @return
 */
	public abstract SellWares querySellWaresByInwaresId(int inwares_id);
/**
 * 查询所有出售商品
 * @return
 */
	public abstract List<SellWares> queryAllSellWares();
/**
 * 根据名称查询出库商品
 * @param name
 * @return
 */
	public abstract List<SellWares> querySellWaresByName(String name);
/**
 * 根据种类id查询出售商品
 * @param category_id
 * @return
 */
	public abstract List<SellWares> querySellWaresByCategoryId(int category_id);
	/**
	 * 根据条形码查询出库商品
	 * @param code
	 * @return
	 */
	public abstract SellWares querySellWaresByCode(String code);
	/**
	 * 根据种类名查询销售商品
	 * @param string
	 */
	public abstract  List<SellWares>  querySellWaresByCategoryName(String CategoryName);
	/*------------------修改商品---------------------*/
	
	/**
	 * 修改category
	 * @param category
	 * @return boolean
	 * @throws Exception 
	 */
	public abstract boolean updateCategory(Category category) throws Exception;
	/**
	 * 修改Wares	
	 * @param wares
	 * @return boolean
	 * @throws Exception 
	 */
	public abstract boolean updateWares(Wares wares) throws Exception;
	/**
	 * 修改inWares
	 * @param inWares
	 * @param oldInwares 
	 * @return boolean
	 * @throws Exception 
	 */
	public abstract boolean updateInWares(InWares inWares, InWares oldInwares) throws Exception;
	/**
	 * 修改sellWares
	 * @param sellWares
	 * @return boolean
	 * @throws Exception 
	 */
	public abstract boolean updateSellWares(SellWares sellWares,SellWares oldSellWares) throws Exception;
	
	/**
	 * 删除category
	 * @param category
	 * @return boolean
	 * @throws Exception 
	 */
	public abstract boolean deleteCategory(Category category) throws Exception;
	/**
	 * 删除Wares	
	 * @param wares
	 * @return boolean
	 * @throws Exception 
	 */
	public abstract boolean deleteWares(Wares wares) throws Exception;
	/**
	 * 删除inWares
	 * @param inWares
	 * @return boolean
	 * @throws Exception 
	 */
	public abstract boolean deleteInWares(InWares inWares) throws Exception;
	/**
	 * 删除category
	 * @param category
	 * @return boolean
	 * @throws Exception 
	 */
	public abstract boolean deleteSellWares(SellWares sellWares) throws Exception;
	/**
	 * 根据图片路径查看出售商品详情
	 * @param imagePath
	 * @return
	 */
	public abstract List<SellWares> querySellWaresByImagePath(String imagePath);
	/**
	 * 根据图片路径查看入库商品详情
	 * @param imagePath
	 * @return
	 */
	public abstract List<InWares> queryInWaresByImagePath(String imagePath);
	/**
	 * 添加图片
	 * @param image
	 * @return
	 * @throws Exception 
	 */
	boolean addImage(Image image) throws Exception;
	/**
	 * 添加客户级别
	 * @param grade
	 * @return
	 * @throws Exception 
	 */
	boolean addGrade(Grade grade) throws Exception;
	/**
	 * 删除图片
	 * @param image
	 * @return
	 */
	boolean deleteImage(Image image);
	boolean deleteGrade(Grade image) throws Exception;

	Image queryIamgeId(int _id);

	Grade queryGradeById(int _id);

	List<Grade> queryAllGrade();

	List<Image> queryAllImage();

	boolean updateImage(Image image) throws Exception;

	boolean updateGrade(Grade grade) throws Exception;

	Grade queryGradeByComments(String comments);

	Image queryIamgeByPath(String imagePath);

	List<Customer> queryCustomerByGradeId(int grade_id);
	List<Image> queryIamgeByWaresId(int wares_id);
	/**
	 * 更新库存
	 * @param stock 库存
	 * @param wares
	 * @return
	 */
	boolean updateWaresStock(Integer stock, Wares wares);
	/**
	 * 更新客户购买量
	 * @param stock 库存
	 * @param wares
	 * @return
	 */
	boolean updateCustomerTotal(Float total, Customer customer)throws Exception;
	
	/**
	 * 倒序查询所有
	 * @return
	 */
	List<SellWares> queryAllSellWaresOrderByDesc();
	/**
	 * 
	 * @param total 总共查多少
	 * @param offset 从第几行开始查
	 * @return
	 */
	List<SellWares> queryAllSellWaresOrderByLimit(int total,int offset);
	/**
	 * 
	 * @param total 总共查多少
	 * @param offset 从第几行开始查
	 * @return
	 */
	List<InWares> queryInWaresListOrderByLimit(int total, int offset);
	/**
	 * 
	 * @param total 总共查多少
	 * @param offset 从第几行开始查
	 * @return
	 */
	List<Wares> queryWaresOrderByLimit(int total, int offset);
	/**
	 * 倒序查询 
	 * @return
	 */
	List<Wares> queryAllWaresByDesc();
	/**
	 * 倒序查询 
	 * @return
	 */
	List<InWares> queryAllInWaresByDesc();

	int queryWaresTotal();

	int queryInWaresTotal();

	int querySellWaresTotal();
	/**
	 * 查询特定月份的所有条目总数
	 * @param year
	 * @param month
	 * @return
	 */
	int querySellWaresByMonthCount(int year, int month);
	/**
	 * 查询特定月份的所有销售额
	 * @param year
	 * @param month
	 * @return
	 */
	float querySellWaresByMonthAllSell(int year, int month);
	/**
	 * 查询特定月份的所有利润
	 * @param year
	 * @param month
	 * @return
	 */
	float querySellWaresByMonthProfit(int year, int month);
	/**
	 * 查询指定月的出售条目数
	 * @param year 年
	 * @param month 月
	 * @param total 总共查多少条
	 * @param offset 从第几条开始查
	 * @return
	 */
	List<SellWares> querySellWaresByMonth(int year, int month, int total,
			int offset);
	/**
	 * 查询指定月的入库总数
	 * @param year
	 * @param month
	 * @return
	 */
	int queryInWaresByMonthCount(int year, int month);
	/**
	 * 查询指定月的入库总额
	 * @param year
	 * @param month
	 * @return
	 */
	float queryInWaresByMonthInMoney(int year, int month);
	/**
	 * 查询指定月入库商品集合
	 * @param year 年
	 * @param month 月
	 * @param total 总共查多少条
	 * @param offset 从第几条开始查
	 * @return
	 */
	List<InWares> queryInWaresByMonth(int year, int month, int total, int offset);
	/**
	 * 获得所有客户数量
	 * @return
	 */
	int queryCustomerCount();
	/**
	 * 获得所有品种数量
	 * @return
	 */
	int queryWaresCount();
	/**
	 * 查所有出售商品总额
	 * @param year
	 * @param month
	 * @return
	 */
	float querySellWaresAllSell();
	/**
	 * 查询所有出售商品总数
	 * @return
	 */
	int querySellWaresCount();
	/**
	 * 指定月可售商品集合
	 * @param year
	 * @param month
	 * @param total
	 * @param offset
	 * @return
	 */
	List<InWares> queryInWaresSellByMonth(int year, int month, int total,
			int offset);
	/**
	 * 指定月可售商品总数
	 * @param year
	 * @param month
	 * @return
	 */
	int queryInWaresSellByMonthCount(int year, int month);
	/**
	 * 指定月可售商品总额
	 * @param year
	 * @param month
	 * @return
	 */
	float queryInWaresSellByMonthInMoney(int year, int month);
	/**
	 * 查出所有无库存商品
	 * @return
	 */
	List<Wares> queryWaresNoStock();
	/**
	 * 查询商品的最后入库时间
	 * @return
	 */
	long getQuetyInWaresEndTime();

	int queryInWaresTotalByMonth(int year, int month);

	
	
}