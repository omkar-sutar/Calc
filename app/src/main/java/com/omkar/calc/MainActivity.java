package com.omkar.calc;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        }
    }
    public void onButtonClick(View view){

        TextView textView=findViewById(R.id.textView1);
        Button b=findViewById(view.getId());
        String buttonText=b.getText().toString();

        //If button C,D,N,= are pressed
        if(view.getId()==R.id.buttonC) {
            textView.setText("0");
            adjustTextSize(textView);
            return;
        } else if(view.getId()==R.id.buttonD){
            if(textView.getText().length()==1){
                textView.setText("0");
                adjustTextSize(textView);
                return;
            }
            String textString=textView.getText().toString();
            String subString = textString.substring(0, textString.length()-1);
            textView.setText(subString);
            adjustTextSize(textView);
            return;
        } else if(view.getId()==R.id.buttonN) return;
        else if(view.getId()==R.id.buttonEquals) {
            String newText=evaluateExpression(textView.getText().toString());
            textView.setText(newText);
            adjustTextSize(textView);
            return;
        }
        //Clears initial zero if a number key is pressed
        String textViewText=textView.getText().toString();
        Character lastChar=textViewText.charAt(textViewText.length()-1);
        if(textView.getText().toString().startsWith("0") && Character.isDigit(lastChar)) textView.setText("");
        //Append input to textView, but first check if an operator is appended on digit and not other operator
        //Input is operator and recent text character also operator
        if(!Character.isDigit(buttonText.charAt(0)) && !Character.isDigit(lastChar)) {
            String subString = textViewText.substring(0, textViewText.length()-1);
            textView.setText(subString);
        }
        textView.append(buttonText);
        adjustTextSize(textView);


    }
    public void adjustTextSize(TextView textView){
        //Reduce text size if textView is filled
        int textLength=textView.getText().toString().length();
        if(textLength<7){
            textView.setTextSize(85F);
        }
        if(textLength>=7 && textLength<12){
            textView.setTextSize(45.0F);
        }
        else if(textLength>=12){
            textView.setTextSize(20F);
        }
    }
    public String evaluateExpression(String text){
        if(!(isValidExpression(text))){
            Toast.makeText(getApplicationContext(),"Invalid expression",Toast.LENGTH_SHORT);
            return text;
        }
        String answer=EvaluateExpression.evaluateExpression(text.replaceAll("x","*"));
        return answer;
    }
    public boolean isValidExpression(String text){
        Character[] textArray=new Character[text.length()];
        for(int i=0;i<text.length();i++) textArray[i]=text.charAt(i);
        //Check if expression begins and ends with digit
        if(!(Character.isDigit(textArray[0]) && Character.isDigit(textArray[textArray.length-1]))){
            Toast.makeText(getApplicationContext(),"Invalid expression",Toast.LENGTH_SHORT).show();
            return false;
        }
        //Count number of operators
        int numOperators=getOperatorCount(text);
        //Count number of operands
        int numOperands=getOperands(text).length;
        //return if valid expression
        return numOperands - numOperators == 1;

    }
    public int getOperatorCount(String text){
        String temp=text.replace("+","").replace("-","").replace("x","").replace("/","");
        return text.length()-temp.length();
    }
    public float[] getOperands(String text){
        String temp=text.replace("+"," ").replace("-"," ").replace("x"," ").replace("/"," ");
        String[] numbersStringArray=temp.split(" ");
        float[] numbers=new float[numbersStringArray.length];
        for(int i=0;i<numbers.length;i++){
            numbers[i]=Float.parseFloat(numbersStringArray[i]);
        }
        return numbers;
    }
}

class EvaluateExpression {

    public static String evaluateExpression(String expression){
        Stack<String> operands=new Stack<String>();
        Stack<Character> operators=new Stack<Character>();
        for (int i = 0; i < expression.length(); i++) {
            Character current=expression.charAt(i);
            if(Character.isDigit(current)){
                StringBuilder temp = new StringBuilder();
                while (Character.isDigit(expression.charAt(i)) || expression.charAt(i)=='.'){
                    temp.append(expression.charAt(i));
                    i++;
                    if(i>=expression.length()) break;
                }
                operands.push(temp.toString());
            }
            if(i>=expression.length())break;
            current=expression.charAt(i);
            if(!Character.isDigit(current)){
                updateOperatorsStack(operands,operators,current);
            }
        }

        while (!operators.isEmpty()){
            float num1=Float.parseFloat(operands.pop()), num2=Float.parseFloat(operands.pop());
            float ans;
            Character topOperator=operators.pop();
            if(topOperator=='+') ans=num1+num2;
            else if(topOperator=='-')ans=num2-num1;
            else if(topOperator=='*')ans=num1*num2;
            else ans=num2/num1;
            String answer=String.valueOf(ans);
            operands.push(answer);

        }
        return String.valueOf(operands.pop());
    }
    public static void updateOperatorsStack(Stack<String> operands,Stack<Character> operators,Character newOperator){
        if(operators.isEmpty()) operators.push(newOperator);
        else if(getPrecedence(newOperator)>= getPrecedence(operators.peek())) operators.push(newOperator);
        else{
            float num1=Float.parseFloat(operands.pop());
            float num2=Float.parseFloat(operands.pop());
            float ans;
            Character topOperator=operators.pop();
            if(topOperator=='+') ans=num1+num2;
            else if(topOperator=='-')ans=num2-num1;
            else if(topOperator=='*')ans=num1*num2;
            else ans=num2/num1;
            String answer=String.valueOf(ans);
            operands.push(answer);
            updateOperatorsStack(operands,operators,newOperator);
        }
    }

    public static int getPrecedence(Character operator){
        switch (operator){
            case '+':return 0;
            case '-': return 1;
            case '*':return 2;
            case '/':return 3;
        }
        return 0;
    }
}
