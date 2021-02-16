package com.example.teamnovahelper.Database;

public class FeedbackDictionary {
    private String Feedback_Person;
    private String Feedback_Date;
    private String Feedback_Contents;
    public String getFeedback_Person() {
        return Feedback_Person;
    }
    public void setFeedback_Person(String feedback_person) {
        this.Feedback_Person = feedback_person;
    }
    public String getFeedback_Date() {
        return Feedback_Date;
    }
    public void setFeedback_Date(String feedback_date) {
        Feedback_Date = feedback_date;
    }
    public String getFeedback_Contents() {
        return Feedback_Contents;
    }
    public void setKorean(String feedback_contents) {
        Feedback_Contents = feedback_contents;
    }
    public FeedbackDictionary(String feedback_person, String feedback_date, String feedback_contents) {
        this.Feedback_Person = feedback_person;
        this.Feedback_Date = feedback_date;
        this.Feedback_Contents = feedback_contents;
    }
}

