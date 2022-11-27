package com.example.calculatorapp;

public class NumberToken extends Token{
    private boolean isConstant;

    public NumberToken(String content, boolean isConstant){
        super(content);
        this.isConstant = isConstant;
    }

    public boolean getIsConstant() {
        return isConstant;
    }
}
