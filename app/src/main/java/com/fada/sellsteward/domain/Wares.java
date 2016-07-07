package com.fada.sellsteward.domain;

import java.io.Serializable;
import java.util.List;

public class Wares implements Serializable {
    private static final long serialVersionUID = 4019416437392483412L;
    private List<InWares> inWaresList;

    public List<InWares> getInWaresList() {
        return inWaresList;
    }

    public void setInWaresList(List<InWares> inWaresList) {
        this.inWaresList = inWaresList;
    }

    public Wares() {
        super();
    }


    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Wares(Integer _id, String name, String imagePath, Integer stock, Category category) {
        super();
        this._id = _id;
        this.category = category;
        this.name = name;
        this.stock = stock;
        this.imagePath = imagePath;
    }

    public Wares(String name, String imagePath, Category category) {
        super();
        this.category = category;
        this.name = name;
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Wares [_id=" + _id + ", category="
                + category.getName() + ", name=" + name + ", imagePath=" + imagePath
                + ", stock=" + stock + "]";
    }

    private Integer _id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer get_id() {
        return _id;
    }

    private Category category;
    private String name;
    private String imagePath;
    private Integer stock;//库存
}
