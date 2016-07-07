package com.fada.sellsteward.domain;

import java.io.Serializable;

public class Image implements Serializable {

	private static final long serialVersionUID = -8324853188333147083L;
	
	private Integer _id; 
	private String path;
	private Wares wares;
	
	
	public Image() {
		super();
	}
	public Image(Integer _id, String path, Wares wares) {
		super();
		this._id = _id;
		this.path = path;
		this.wares = wares;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Wares getWares() {
		return wares;
	}
	public void setWares(Wares wares) {
		this.wares = wares;
	}
	public Integer get_id() {
		return _id;
	}
	@Override
	public String toString() {
		return "Image [_id=" + _id + ", path=" + path + ", wares=" + wares
				+ "]";
	}
	
}
