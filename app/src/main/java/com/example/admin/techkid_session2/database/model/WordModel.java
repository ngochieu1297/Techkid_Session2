package com.example.admin.techkid_session2.database.model;

public class WordModel {
    public int id;
    public String origin;
    public String explanation;
    public String type;
    public String pronunciation;
    public String imageUrl;
    public String example;
    public String example_trans;
    public int topic_id;
    public int topic_level;

    public WordModel(int id, String origin, String explanation, String type, String pronunciation, String imageUrl, String example, String example_trans, int topic_id, int topic_level) {
        this.id = id;
        this.origin = origin;
        this.explanation = explanation;
        this.type = type;
        this.pronunciation = pronunciation;
        this.imageUrl = imageUrl;
        this.example = example;
        this.example_trans = example_trans;
        this.topic_id = topic_id;
        this.topic_level = topic_level;
    }


}
