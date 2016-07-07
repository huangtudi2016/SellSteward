package com.fada.sellsteward.domain;

import java.io.Serializable;
import java.util.List;

public class Grade implements Serializable {

	private static final long serialVersionUID = 8213615968484494700L;
	private Integer _id; 
	
	public Grade(Integer _id, String comments) {
		super();
		this._id = _id;
		this.comments = comments;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<Customer> getCustomerList() {
		return customerList;
	}

	public void setCustomerList(List<Customer> customerList) {
		this.customerList = customerList;
	}

	public Integer get_id() {
		return _id;
	}

	private String comments; 
	private List<Customer> customerList;
	
	@Override
	public String toString() {
		return "Grade [_id=" + _id + ", comments=" + comments
				+ ", customerList=" + customerList + "]";
	}

	public Grade() {
	}
}
