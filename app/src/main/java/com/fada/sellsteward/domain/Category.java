package com.fada.sellsteward.domain;

import java.io.Serializable;
import java.util.List;

public class Category implements Serializable {

	private static final long serialVersionUID = -4078201484033172064L;
	private Integer _id; 
	@Override
	public String toString() {
		return "Category [_id=" + _id + ", name=" + name + "]";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Wares> getWares() {
		return wares;
	}
	public Category() {
		super();
	}
	
	public Category(Integer _id, String name) {
		super();
		this._id = _id;
		this.name = name;
	}
	public void setWares(List<Wares> wares) {
		this.wares = wares;
	}
	public Integer get_id() {
		return _id;
	}
	private String name; 
	private List<Wares> wares; 

}
