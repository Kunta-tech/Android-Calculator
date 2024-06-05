package com.example.calculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.ArrayDeque;
import java.util.Deque;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button[] NumButtons;
    Button btnAdd, btnSub, btnMul, btnDiv, btnDel, btnRe, btnEq,btnDot,btnBrSt,btnBrEd,btnMod;
    static TextView BoardView, AnsView;
    boolean BoardState_init = true;
    static boolean Expression_Error= false;
    int[] btn_num_ids = {R.id.button0,R.id.button1,R.id.button2,R.id.button3,R.id.button4,R.id.button5,R.id.button6,R.id.button7,R.id.button8,R.id.button9};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        NumButtons = new Button[10];
        BoardView = findViewById(R.id.BoardView);
        AnsView = findViewById(R.id.Ans);
        btnAdd = findViewById(R.id.button_add);
        btnSub = findViewById(R.id.button_sub);
        btnMul = findViewById(R.id.button_mul);
        btnDiv = findViewById(R.id.button_div);
        btnMod = findViewById(R.id.button_mod);
        btnDel = findViewById(R.id.button_DEL);
        btnRe = findViewById(R.id.button_RE);
        btnEq = findViewById(R.id.button_Eq);
        btnDot = findViewById(R.id.button_Dot);
        btnBrSt = findViewById(R.id.button_BrSt);
        btnBrEd = findViewById(R.id.button_BrEd);

        for (int i=0;i<10;i++) {
            NumButtons[i] = findViewById(btn_num_ids[i]);
            NumButtons[i].setOnClickListener(this);
        }
        btnDel.setOnClickListener(this);
        btnRe.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnSub.setOnClickListener(this);
        btnMul.setOnClickListener(this);
        btnDiv.setOnClickListener(this);
        btnEq.setOnClickListener(this);
        btnMod.setOnClickListener(this);
        btnDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoardView.append(".");
            }
        });
        btnBrSt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoardView.append("(");
            }
        });
        btnBrEd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoardView.append(")");
            }
        });
    }
    public static double evaluate(String expression) {
        String postfix = infixToPostfix(expression);
        try{
            return evaluatePostfix(postfix);
        }catch (Exception e){
            AnsView.setText("Error in Expression");
            AnsView.setError(e.toString());
            return 0;
        }

    }

    private static String infixToPostfix(String infix) {
        StringBuilder postfix = new StringBuilder();
        Deque<Character> stack = new ArrayDeque<>();
        boolean isCurNum = false;

        for (char c : infix.toCharArray()) {
            if(Character.isLetterOrDigit(c) || c=='.'){
                if(!isCurNum) postfix.append(' ');
                isCurNum = true;
            } else {
                isCurNum = false;
                postfix.append(' ');
            }

            if (Character.isLetterOrDigit(c) || c=='.') {
                postfix.append(c);
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    postfix.append(stack.pop());
                    postfix.append(' ');
                }
                stack.pop(); // Pop '('
            } else {
                // Operator
                while (!stack.isEmpty() && precedence(c) <= precedence(stack.peek())) {
                    postfix.append(stack.pop());
                    postfix.append(' ');
                }
                stack.push(c);
            }
            //if(!isCurNum) postfix.append(' ');
        }
        postfix.append(' ');

        while (!stack.isEmpty()) {
            postfix.append(stack.pop());
            postfix.append(' ');
        }

        return postfix.toString();
    }
    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  // Regex to check if the string is a number
    }

    private static double evaluatePostfix(String postfix) {
        Deque<Double> stack = new ArrayDeque<>();

        // Split the expression into tokens
        String[] tokens = postfix.split("\\s+");

        for (String token : tokens) {
            if(token.length()>1 && token.charAt(token.length()-1) == '.') token += '0';
            if (isNumeric(token)) {
                // If the token is a number, push it onto the stack
                stack.push(Double.parseDouble(token));
            } else if (token.isEmpty()) {
                continue;
            } else {
                // If the token is an operator, pop two operands from the stack,
                // perform the operation, and push the result back onto the stack
                double operand2 = stack.pop();
                double operand1 = stack.pop();
                double result = 0;
                try {
                    result = performOperation(operand1, operand2, token.charAt(0));
                    AnsView.setError(null);
                    Expression_Error = false;
                }catch (Exception e){
                    AnsView.setText("Error in Expression");
                    AnsView.setError(e.toString());
                    Expression_Error = true;
                }

                stack.push(result);
            }
        }

        // The final result is the only element left on the stack
        return stack.pop();
    }

    private static int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '%':
                return 3;
            default:
                return -1;
        }
    }

    private static double performOperation(double operand1, double operand2, char operator) {
        switch (operator) {
            case '+':
                return operand1 + operand2;
            case '-':
                return operand1 - operand2;
            case '*':
                return operand1 * operand2;
            case '/':
                if (operand2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return operand1 / operand2;
            case '%':
                if (operand2 == 0) {
                    return operand1;
                }
                double cnt = operand1;
                while (cnt>operand2)    cnt-=operand2;
                return cnt;
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
    private static String trimDecimal(String s){
        int pos = s.indexOf('.'),ed = s.length();
        if(pos == -1) return s;
        for(int i= s.length()-1;i>pos;i--){
            if(s.charAt(i) == '0') ed--;
            else break;
        }
        if(ed == pos + 1) ed--;
        return s.substring(0,ed);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        for(int i=0;i<10;i++){
            if(v.getId() == btn_num_ids[i]){
                if(BoardState_init) BoardView.setText("");
                BoardState_init = false;
                BoardView.append(Integer.toString(i));
                return;
            }
        }

        if(v.getId() == R.id.button_DEL){
            BoardView.setText("");
        } else if (v.getId() == R.id.button_RE) {
            CharSequence BTxt= BoardView.getText();
            BoardView.setText(BTxt.subSequence(0,BTxt.length()-1));
        } else if (v.getId() == R.id.button_add) {
            BoardView.append("+");
        } else if (v.getId() == R.id.button_sub) {
            BoardView.append("-");
        } else if (v.getId() == R.id.button_mul) {
            BoardView.append("*");
        } else if (v.getId() == R.id.button_div) {
            BoardView.append("/");
        } else if (v.getId() == R.id.button_mod) {
            BoardView.append("%");
        } else if (v.getId() == R.id.button_Eq){
            double dd = evaluate(BoardView.getText().toString());
            BigDecimal bd = new BigDecimal(Double.toString(dd));
            bd = bd.setScale(5, RoundingMode.HALF_UP);
            if(!Expression_Error)
                AnsView.setText(trimDecimal(bd.toString()));
            //AnsView.setText(infixToPostfix(BoardView.getText().toString()));
        }
        if(BoardView.getText().length() == 0){
            BoardView.setText(getResources().getString(R.string.init_str));
            AnsView.setText("N/A");
            AnsView.setError(null);
            BoardState_init = true;
        }
    }
}