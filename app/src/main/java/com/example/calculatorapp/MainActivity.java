package com.example.calculatorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ButtonInfo[] buttonInfos = new ButtonInfo[]{
            new FunctionButtonInfo(R.id.btnSin, "sin"),
            new OperatorButtonInfo(R.id.btnSqrt, "√"),
            new OperatorButtonInfo(R.id.btnFactorial, "!"),
            new OperatorButtonInfo(R.id.btnPercentage, "%"),
            new ButtonInfo(R.id.btnAc, ""),
            new OperatorButtonInfo(R.id.btnParanthesis, "("),
            new OperatorButtonInfo(R.id.btnPower, "^"),
            new OperatorButtonInfo(R.id.btnDivide, "/"),
            new NumberButtonInfo(R.id.btnSeven, "7"),
            new NumberButtonInfo(R.id.btnEight, "8"),
            new NumberButtonInfo(R.id.btnNine, "9"),
            new OperatorButtonInfo(R.id.btnMultiply, "*"),
            new NumberButtonInfo(R.id.btnFour, "4"),
            new NumberButtonInfo(R.id.btnFive, "5"),
            new NumberButtonInfo(R.id.btnSix, "6"),
            new OperatorButtonInfo(R.id.btnAddition, "+"),
            new NumberButtonInfo(R.id.btnOne, "1"),
            new NumberButtonInfo(R.id.btnTwo, "2"),
            new NumberButtonInfo(R.id.btnThree, "3"),
            new OperatorButtonInfo(R.id.btnSub, "-"),
            new NumberButtonInfo(R.id.btnPi, "π"),
            new NumberButtonInfo(R.id.btnDot, "."),
            new NumberButtonInfo(R.id.btnZero, "0"),
            new ButtonInfo(R.id.btnDel, ""),
            new ButtonInfo(R.id.btnEquals, "="),
    };

    TextView formulaText;
    TextView resultText;

    private final OperatorToken OP_ADD = new OperatorToken("+", OperatorToken.OperatorType.LEFT, 4);
    private final OperatorToken OP_SUB = new OperatorToken("-", OperatorToken.OperatorType.LEFT, 4);
    private final OperatorToken OP_MUL = new OperatorToken("*", OperatorToken.OperatorType.LEFT, 5);
    private final OperatorToken OP_DIV = new OperatorToken("/", OperatorToken.OperatorType.LEFT, 5);

    private final OperatorToken OP_MIN = new OperatorToken("-", OperatorToken.OperatorType.PREFIX, 6);
    private final OperatorToken OP_POW = new OperatorToken("^", OperatorToken.OperatorType.RIGHT, 6);
    private final OperatorToken OP_FACT = new OperatorToken("!", OperatorToken.OperatorType.SUFFIX, 7);
    private final OperatorToken OP_PERCENT = new OperatorToken("%", OperatorToken.OperatorType.SUFFIX, 8);
    private final OperatorToken OP_SQRT = new OperatorToken("√", OperatorToken.OperatorType.PREFIX, 9);

    private final OperatorToken OP_LPARAN = new OperatorToken("(", OperatorToken.OperatorType.PREFIX, 0);
    private final OperatorToken OP_RPARAN = new OperatorToken(")", OperatorToken.OperatorType.SUFFIX, 0);

    private final HashMap<String, NumberToken> constantTokens = new HashMap<String, NumberToken>(){
        {
            put("π", new NumberToken("3.1415", true));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        formulaText = findViewById(R.id.formulaText);
        resultText = findViewById(R.id.resultText);

        for(ButtonInfo btnInfo: buttonInfos){
            Button btn = findViewById(btnInfo.getId());

            if(btn != null){
                btn.setOnClickListener(this);
            }
        }
    }

    private void update(){
        try{
            ArrayList<Token> tokens = tokenize(formulaText.getText().toString());
            double value = evaluate(tokens);
            formulaText.setText(String.valueOf(value));
            resultText.setText("");
        }catch(Exception e){
            resultText.setText("Hata");
            Log.d("Hata", e.toString());
        }
    }

    private void silentUpdate(){
        try{
            ArrayList<Token> tokens = tokenize(formulaText.getText().toString());
            resultText.setText(String.valueOf(evaluate(tokens)));
        }catch(Exception e){
            resultText.setText("");
            Log.d("Hata", e.toString());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.btnDel){
            if(formulaText.getText().toString().length() > 0){
                formulaText.setText(formulaText.getText().toString().substring(0, formulaText.getText().toString().length() - 1));
            }
            silentUpdate();
            return;
        }

        if(id == R.id.btnAc){
            formulaText.setText("");
            resultText.setText("");

            silentUpdate();
            return;
        }

        if(id == R.id.btnEquals){
            update();
            return;
        }

        for(ButtonInfo btnInfo: buttonInfos){
            if(id == btnInfo.getId()){
                Button btn = (Button)v;

                if(btnInfo instanceof OperatorButtonInfo){
                    onClickOperator((OperatorButtonInfo) btnInfo);
                }else if(btnInfo instanceof NumberButtonInfo){
                    onClickNumber((NumberButtonInfo) btnInfo);
                }else if(btnInfo instanceof FunctionButtonInfo){
                    onClickFunction((FunctionButtonInfo) btnInfo);
                }

                silentUpdate();
            }
        }
        Log.d("Info", v.getId() + "");
    }

    private void onClickNumber(NumberButtonInfo numberButtonInfo){
        formulaText.setText(formulaText.getText() + numberButtonInfo.getContent());
    }

    private int getOpenParanthesis(ArrayList<Token> tokens){
        int result = 0;

        for(int i = 0;i < tokens.size();i++){
            if(tokens.get(i).getContent() == "("){
                result++;
            }else if(tokens.get(i).getContent() == ")"){
                result--;
            }
        }

        return result;
    }

    private void onClickOperator(OperatorButtonInfo operatorButtonInfo){
        try{
            String content = operatorButtonInfo.getContent();

            if(content == "("){
                ArrayList<Token> tokens = tokenize(formulaText.getText().toString());

                if(!tokens.isEmpty()){
                    Token lastToken = tokens.get(tokens.size() - 1);

                    if(getOpenParanthesis(tokens) > 0 && (lastToken instanceof NumberToken ||
                            (lastToken instanceof OperatorToken && ((OperatorToken)lastToken).getOperatorType() == OperatorToken.OperatorType.SUFFIX)) ){
                        content = ")";
                    }
                }
            }
            formulaText.setText(formulaText.getText() + content);
        }catch(Exception e){
            Log.d("Hata", e.toString());
        }

    }

    private void onClickFunction(FunctionButtonInfo functionButtonInfo){
        formulaText.setText(formulaText.getText() + functionButtonInfo.getContent() + "(");
    }

    private double performFunction(FunctionToken functionToken, Stack<Token> output) throws Exception{
        double operand1 = Double.parseDouble(output.pop().getContent());

        switch (functionToken.getContent()){
            case "sin":
                return Math.sin(Math.toRadians(operand1));
        }

        throw new Exception("Bilinmeyen fonksiyon: " + functionToken.getContent());
    }

    private double performOperator(OperatorToken operatorToken, Stack<Token> output) throws  Exception{
        double operand1 = Double.parseDouble(output.pop().getContent());
        double operand2 = 0;

        OperatorToken.OperatorType operatorType = operatorToken.getOperatorType();

        if(operatorType == OperatorToken.OperatorType.LEFT || operatorType == OperatorToken.OperatorType.RIGHT)
            operand2 = Double.parseDouble(output.pop().getContent());

        switch(operatorToken.getContent()){
            case "+":
                return operand1 +  operand2;
            case "*":
                return operand1 * operand2;
            case "/":
                return operand2 / operand1;
            case "^":
                return Math.pow(operand2, operand1);
            case "!":
            {
                double current = 1;
                double j = operand1;

                while(j > 1){
                    current *= j;
                    j--;
                }

                return current;
            }
            case "%":
                return operand1 / 100;
            case "√":
                return Math.sqrt(operand1);
            case "-":
            {
                if(operatorType == OperatorToken.OperatorType.LEFT){
                    return operand2 - operand1;
                }else{
                    return -operand1;
                }
            }
        }

        throw new Exception("Bilinmeyen oeprator: " + operatorToken.getContent());
    }

    private double evaluate(ArrayList<Token> tokens) throws Exception{
        if(tokens.isEmpty())
            return 0.0;

        Stack<Token> output = new Stack<Token>();
        Stack<Token> operators = new Stack<Token>();

        for(int i = 0;i < tokens.size();i++){
            Token currentToken = tokens.get(i);

            if(currentToken instanceof NumberToken){
                NumberToken currentNumberToken = (NumberToken) currentToken;

                if(currentNumberToken.getIsConstant()){
                    NumberToken constantToken = constantTokens.get(currentNumberToken.getContent());

                    if(constantToken != null){
                        output.push(constantToken);
                    }else{
                        throw new Exception("Bilinmeyen sabit");
                    }
                }else{
                    output.push(currentNumberToken);
                }
            }else if(currentToken instanceof FunctionToken){
                operators.push(currentToken);
            }else if(currentToken.getContent() == "("){
                operators.push(currentToken);
            }else if(currentToken.getContent() == ")"){
                while(!operators.isEmpty() && operators.peek() instanceof  OperatorToken && operators.peek().getContent() != "("){
                    OperatorToken operatorToken = (OperatorToken) operators.pop();
                    double value = performOperator(operatorToken, output);
                    output.push(new NumberToken(String.valueOf(value), false));
                }

                if(operators.isEmpty() || operators.peek().getContent() != "(")
                    throw new Exception("Eşleşmeyen parantez kullanımı");

                operators.pop();

                if(!operators.isEmpty() && operators.peek() instanceof FunctionToken){
                    double value = performFunction((FunctionToken)operators.pop(), output);
                    output.push(new NumberToken(String.valueOf(value),false));
                }
            }else if(currentToken instanceof OperatorToken){
                while(!operators.isEmpty() && operators.peek().getContent() != "(" &&
                        (
                                ((OperatorToken)operators.peek()).getPrecedence() > ((OperatorToken)currentToken).getPrecedence()
                                ||
                                        (
                                                ((OperatorToken)operators.peek()).getPrecedence() == ((OperatorToken)currentToken).getPrecedence()
                                                &&  ((OperatorToken)currentToken).getOperatorType() == OperatorToken.OperatorType.LEFT
                                                )
                                )
                ){
                    OperatorToken operatorToken = (OperatorToken) operators.pop();
                    double value = performOperator(operatorToken, output);
                    output.push(new NumberToken(String.valueOf(value), false));
                }

                operators.push(currentToken);
            }
        }

        while(!operators.isEmpty()){
            OperatorToken operatorToken = (OperatorToken) operators.pop();

            if(operatorToken.getContent() == "(" || operatorToken.getContent() == ")" )
                throw new Exception("Eşleşmeyen parantez kullanımı 2");

            double value = performOperator(operatorToken, output);
            output.push(new NumberToken(String.valueOf(value), false));
        }

        if(output.size() > 1){
            throw new Exception("Hatalı ifade");
        }

        return Double.parseDouble(output.pop().getContent());
    }

    private ArrayList<Token> tokenize(String expression) throws Exception{
        ArrayList<Token> tokens = new ArrayList<Token>();

        for(int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            switch(c){
                case '(':
                    tokens.add(OP_LPARAN);
                    continue;
                case ')':
                    tokens.add(OP_RPARAN);
                    continue;
                case '+':
                    tokens.add(OP_ADD);
                    continue;
                case '*':
                    tokens.add(OP_MUL);
                    continue;
                case '/':
                    tokens.add(OP_DIV);
                    continue;
                case '^':
                    tokens.add(OP_POW);
                    continue;
                case '!':
                    tokens.add(OP_FACT);
                    continue;
                case '%':
                    tokens.add(OP_PERCENT);
                    continue;
                case '√':
                    tokens.add(OP_SQRT);
                    continue;
                case '-':
                    OperatorToken token = OP_MIN;

                    if(!tokens.isEmpty()){
                        Token lastToken = tokens.get(tokens.size() - 1);

                        if(lastToken instanceof NumberToken){
                            token = OP_SUB;
                        }else if(lastToken instanceof OperatorToken){
                            OperatorToken lastOperatorToken = (OperatorToken)lastToken;
                            if(lastOperatorToken.getOperatorType() == OperatorToken.OperatorType.SUFFIX){
                                token = OP_SUB;
                            }
                        }
                    }

                    tokens.add(token);
                    continue;

            }

            if(Character.isDigit(c) || c == '.'){
                String content = "" + c;

                int j = i + 1;
                while(j < expression.length()){
                    char c2 = expression.charAt(j);

                    if(Character.isDigit(c2) || c2 == '.' || c2 == 'E'){
                        if(c2 == 'E' && j + 1 < expression.length() && expression.charAt(j + 1) == '-'){
                            content = "E-";
                            j += 2;
                        }else{
                            content += c2;
                            j++;
                        }
                    }else{
                        break;
                    }
                }

                i = j - 1;

                try{
                    double value = Double.parseDouble(content);
                    tokens.add(new NumberToken(content, false));
                }catch(Exception e){
                    throw new Exception("Hatalı sayı girildi");
                }
            }else if(Character.isAlphabetic(c)){
                String content = "" + c;

                int j = i + 1;
                while(j < expression.length()){
                    char c2 = expression.charAt(j);

                    if(Character.isDigit(c2) || Character.isAlphabetic(c2)){
                        content += c2;
                        j++;
                    }else{
                        break;
                    }
                }

                i = j - 1;

                if(j < expression.length() && expression.charAt(j) == '('){
                    tokens.add(new FunctionToken(content));
                }else{
                    tokens.add(new NumberToken(content, true));
                }
            }
        }

        return tokens;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if(formulaText.getText().length() > 0){
            outState.putString("formula", formulaText.getText().toString());
        }
            outState.putBoolean("evaluate", resultText.getText().length() > 0);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String savedFormula = savedInstanceState.getString("formula");

        if(savedFormula != null){
            formulaText.setText(savedFormula);
        }

        if(savedInstanceState.getBoolean("evaluate")){
            update();
        }
    }
}