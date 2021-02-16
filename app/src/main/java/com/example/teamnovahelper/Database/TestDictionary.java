package com.example.teamnovahelper.Database;

public class TestDictionary {
    private String Name;
    private String Sort;
    private String Date;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSort() {
        return Sort;
    }

    public void setSort(String sort) {
        Sort = sort;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public TestDictionary(String name, String sort, String date) {
        this.Name = name;
        this.Sort = sort;
        this.Date = date;
    }
}
