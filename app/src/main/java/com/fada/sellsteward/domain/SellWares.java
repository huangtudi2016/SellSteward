package com.fada.sellsteward.domain;

import java.io.Serializable;

public class SellWares implements Serializable{
	
	private static final long serialVersionUID = 4872866077379058045L;
	private Customer customer;
	private int amount=1;
	
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public Customer getCustoms() {
		return customer;
	}
	public void setCustoms(Customer customer) {
		this.customer = customer;
	}
	private Integer _id;
	public InWares getInWares() {
		return inWares;
	}
	public SellWares(Integer _id, Long outTime,Float outPrice,Float profit,InWares inWares,Customer customer,int amount) {
		super();
		this._id = _id;
		this.inWares = inWares;
		this.outTime = outTime;
		this.outPrice = outPrice;
		this.profit = profit;
		this.customer = customer;
		this.amount = amount;
	}
	public SellWares(Long outTime,Float outPrice,InWares inWares,Customer customer,int amount) {
		super();
		this.inWares = inWares;
		this.outTime = outTime;
		this.outPrice = outPrice;
		this.customer = customer;
		this.amount = amount;
	}


	
	@Override
	public String toString() {
		return "SellWares [customer=" + customer + ", amount=" + amount
				+ ", _id=" + _id + ", inWares=" + inWares + ", outTime="
				+ outTime + ", outPrice=" + outPrice + ", profit=" + profit
				+ "]";
	}
	public SellWares() {
		super();
	}

	public void setInWares(InWares inWares) {
		this.inWares = inWares;
	}
	public Long getOutTime() {
		return outTime;
	}
	public void setOutTime(Long outTime) {
		this.outTime = outTime;
	}
	public Float getOutPrice() {
		return outPrice;
	}
	public void setOutPrice(Float outPrice) {
		this.outPrice = outPrice;
	}
	public Float getProfit() {
		return profit;
	}
	public void setProfit(Float profit) {
		this.profit = profit;
	}
	public Integer get_id() {
		return _id;
	}
	
	private InWares inWares;//商品对象
	private Long outTime;//出售时间
	private Float outPrice;//出售价格
	private Float profit;//利润

}
