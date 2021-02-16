package com.example.teamnovahelper.Database;

public class CheckOutDictionary {
    private String image;
    private String name;
    private String Date;
    private String Sort;

    public String getSort() {return Sort;}
    public void setSort(String sort) {Sort = sort;}

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public CheckOutDictionary(String image, String name, String date, String sort) {
        this.image = image;
        this.name = name;
        this.Date = date;
        this.Sort = sort;
    }
}
