package com.example.calculatorapp;

public class ButtonInfo {
    private int id;
    private String content;

    public ButtonInfo(int id, String content){
        this.id = id;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
