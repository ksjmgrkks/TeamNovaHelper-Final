package com.example.teamnovahelper.Database;

public class DotDictionary {
    private int DotYear;
    private int DotMonth;
    private int DotDay;
    public DotDictionary(int dotYear, int dotMonth, int dotDay) {
        this.DotYear = dotYear;
        this.DotMonth = dotMonth;
        this.DotDay = dotDay;
    }
    public int getDotYear() {
        return DotYear;
    }
    public void setDotYear(int dotYear) {
        DotYear = dotYear;
    }
    public int getDotMonth() {
        return DotMonth;
    }
    public void setDotMonth(int dotMonth) {
        DotMonth = dotMonth;
    }
    public int getDotDay() {
        return DotDay;
    }
    public void setDotDay(int dotDay) {
        DotDay = dotDay;
    }

}
