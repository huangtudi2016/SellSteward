package com.fada.sellsteward.domain;

import java.io.Serializable;
import java.util.List;

public class InWares implements Serializable {
	private static final long serialVersionUID = -1307050163933524975L;
	private Integer _id;// 自动增长id
	private String code;
	private int isSell;// 售出数量
	private int isSellTemp=1;// 售出数量
	private int amount=1;//总数量

	

	public int getIsSellTemp() {
		return isSellTemp;
	}

	public void setIsSellTemp(int isSellTemp) {
		this.isSellTemp = isSellTemp;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getIsSell() {
		return isSell;
	}

	public void setIsSell(int isSell) {
		this.isSell = isSell;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Float getTabPrice() {
		return tabPrice;
	}

	public void setTabPrice(Float tabPrice) {
		this.tabPrice = tabPrice;
	}
	public InWares(Long inTime, Float inPrice,Wares wares,  Float tabPrice,String code,int amount) {
		super();
		this.wares = wares;
		this.inPrice = inPrice;
		this.tabPrice = tabPrice;
		this.inTime = inTime;
		this.code = code;
		this.amount = amount;
	}

	public InWares(Integer _id,Long inTime, Float inPrice,Wares wares,  Float tabPrice,String code,int isSell,int amount) {
		super();
		this._id = _id;
		this.wares = wares;
		this.inPrice = inPrice;
		this.tabPrice = tabPrice;
		this.inTime = inTime;
		this.code = code;
		this.isSell=isSell;
		this.amount = amount;
		
	}

	public Wares getWares() {
		return wares;
	}

	public void setWares(Wares wares) {
		this.wares = wares;
	}

	public List<SellWares> getSellWaresList() {
		return sellWaresList;
	}

	public void setSellWaresList(List<SellWares> sellWaresList) {
		this.sellWaresList = sellWaresList;
	}


	public Float getInPrice() {
		return inPrice;
	}

	public void setInPrice(Float inPrice) {
		this.inPrice = inPrice;
	}

	public Long getInTime() {
		return inTime;
	}

	public void setInTime(Long inTime) {
		this.inTime = inTime;
	}

	public Integer get_id() {
		return _id;
	}

	private Wares wares;
	private List<SellWares> sellWaresList;

	public InWares() {
		super();
	}

	private Float inPrice;// 进货价格
	private Float tabPrice;// 进货标价
	private Long inTime;// 入库时间



	@Override
	public String toString() {
		return "InWares [_id=" + _id + ", code=" + code + ", isSell=" + isSell
				+ ", isSellTemp=" + isSellTemp + ", amount=" + amount
				+ ", wares=" + wares + ", sellWaresList=" + sellWaresList
				+ ", inPrice=" + inPrice + ", tabPrice=" + tabPrice
				+ ", inTime=" + inTime + "]";
	}

}
