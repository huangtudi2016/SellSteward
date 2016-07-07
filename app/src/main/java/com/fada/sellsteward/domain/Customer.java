package com.fada.sellsteward.domain;

import java.io.Serializable;
import java.util.List;

public class Customer implements Serializable {

    private static final long serialVersionUID = 2104472353615186488L;
    private Integer _id;
    private String comments;
    private float total;
    private Grade grade;
    private String name;
    private String phone;
    private List<SellWares> sellWaresList;

    @Override
    public String toString() {
        return "Customer [_id=" + _id + ", comments=" + comments + ", total=" + total + ", grade=" + grade + ", name=" + name + ", phone=" + phone + ", sellWaresList=" + sellWaresList + "]";
    }

    public Customer() {
        super();
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<SellWares> getSellWaresList() {
        return sellWaresList;
    }

    public void setSellWaresList(List<SellWares> sellWaresList) {
        this.sellWaresList = sellWaresList;
    }


    public Integer get_id() {
        return _id;
    }

    public Customer(Integer _id, String name, String phone, String comments, Float total, Grade grade) {
        super();
        this._id = _id;
        this.comments = comments;
        this.grade = grade;
        this.name = name;
        this.total = total;
        this.phone = phone;
    }

    public Customer(String name, String phone, String comments) {
        super();
        this.comments = comments;
        this.name = name;
        this.phone = phone;
    }


}
