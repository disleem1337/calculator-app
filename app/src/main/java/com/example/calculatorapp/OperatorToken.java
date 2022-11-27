package com.example.calculatorapp;

public class OperatorToken extends Token{
    enum OperatorType{
        LEFT,
        RIGHT,
        PREFIX,
        SUFFIX
    }
    private OperatorType operatorType;
    private int precedence;

    public OperatorToken(String content, OperatorType operatorType, int precedence){
        super(content);
        this.operatorType = operatorType;
        this.precedence = precedence;
    }

    public OperatorType getOperatorType() {
        return operatorType;
    }

    public int getPrecedence() {
        return precedence;
    }
}
