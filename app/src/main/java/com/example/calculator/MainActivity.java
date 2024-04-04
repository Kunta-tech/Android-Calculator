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

import java.util.*;
import java.util.ArrayDeque;
import java.util.Deque;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button[] NumButtons;
    Button btnAdd, btnSub, btnMul, btnDiv, btnDel, btnRe, btnEq;
    static TextView BoardView, AnsView;
    boolean BoardState_init = true;
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
        btnDel = findViewById(R.id.button_DEL);
        btnRe = findViewById(R.id.button_RE);
        btnEq = findViewById(R.id.button_Eq);

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
    }
    public static double evaluate(String expression) {
        String postfix = infixToPostfix(expression);
        return evaluatePostfix(postfix);
    }

    private static String infixToPostfix(String infix) {
        StringBuilder postfix = new StringBuilder();
        Deque<Character> stack = new ArrayDeque<>();
        boolean isCurNum = false;

        for (char c : infix.toCharArray()) {
            if(Character.isLetterOrDigit(c)){
                if(!isCurNum) postfix.append(' ');
                isCurNum = true;
            }else {
                isCurNum = false;
                postfix.append(' ');
            }

            if (Character.isLetterOrDigit(c)) {
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
                try {
                    double result = performOperation(operand1, operand2, token.charAt(0));
                }catch (Exception e){
                    AnsView.setText("Error in Expression");
                    AnsView.setError(e.toString());
                    throw new IllegalArgumentException("Expression Problem");
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
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
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
        } else if (v.getId() == R.id.button_Eq){
            double dd = evaluate(BoardView.getText().toString());
            AnsView.setText(Double.toString(dd));
            //AnsView.setText(infixToPostfix(BoardView.getText().toString()));
        }
        if(BoardView.getText().length() == 0){
            BoardView.setText(getResources().getString(R.string.init_str));
            BoardState_init = true;
        }
    }
}