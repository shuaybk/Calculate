package com.shuaybprojects.calculate;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    public static final String MULT = "\u00D7";
    public static final String DIV = "\u00F7";

    private static final String REGEX_ANYTHANG = "[().+-" + "\\\\" + MULT + "\\\\" + DIV + "\\\\%\\d]*";
    private static final String REGEX_ANY_EXCEPT_NUM = "[().+-" + "\\\\" + MULT + "\\\\" + DIV + "\\\\%]";

    DisplayMetrics metrics = new DisplayMetrics();
    GridView gridButtons;
    EditText calcText;
    String exp;
    Stack<String> bStack;
    Button delButton;
    String currNum;

    public static int width;
    public static int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        exp = "0";
        currNum = "0";
        bStack = new Stack();

        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_main);

        calcText = (EditText) findViewById(R.id.calcText);
        calcText.setMinimumHeight((int)(height*0.4));
        //To stop the soft keyboard from coming up
        calcText.setInputType(InputType.TYPE_NULL);
        calcText.setTextIsSelectable(true);

        calcText.setText(exp);

        gridButtons = (GridView) findViewById(R.id.buttonGrid);
        gridButtons.setAdapter(new GridAdapter(this));
        gridButtons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onButtonClick(i);
            }
        });

        delButton = (Button)findViewById(R.id.delButton);
        delButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickDelButton();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public void onButtonClick(int i) {
        switch(i) {
            case 0:
                //clear
                exp = "0";
                currNum = "0";
                bStack.clear();
                calcText.setText(exp);
                break;
            case 1:
                //brackets
                append("bracket");
                break;
            case 2:
                //percent
                append("%");
                break;
            case 3:
                //divide
                append(DIV);
                break;
            case 4:
                //7
                append("7");
                break;
            case 5:
                //8
                append("8");
                break;
            case 6:
                //9
                append("9");
                break;
            case 7:
                //multiply
                append(MULT);
                break;
            case 8:
                //4
                append("4");
                break;
            case 9:
                //5
                append("5");
                break;
            case 10:
                //6
                append("6");
                break;
            case 11:
                //subtract
                append("-");
                break;
            case 12:
                //1
                append("1");
                break;
            case 13:
                //2
                append("2");
                break;
            case 14:
                //3
                append("3");
                break;
            case 15:
                //add
                append("+");
                break;
            case 16:
                //decimal
                append(".");
                break;
            case 17:
                //zero
                append("0");
                break;
            case 18:
                //plus/minus
                append("n");
                break;
            case 19:
                //equals
                evaluate();
                bStack.clear();
                break;
        }
    }

    void onClickDelButton() {
        if (exp.length() == 1) {
            exp = "0";
            currNum = "0";
        } else {
            exp = exp.substring(0, exp.length()-1);
            if (currNum.length() > 0) {
                currNum = currNum.substring(0, currNum.length() - 1);
            }
        }
        calcText.setText(exp.replaceAll("n", "-"));
    }

    //Append and validate
    void append(String val) {
        if (exp.length() < 30) {
            if (isOperator(val)) {
                if (!exp.endsWith("+") && !exp.endsWith("-") && !exp.endsWith(MULT) && !exp.endsWith(DIV)) {
                    exp = exp + val;
                    currNum = "";
                }
            }
            if (val.matches("[0-9]")){
                if (exp.equals("0")) {
                    exp = val;
                    currNum = val;
                } else {
                    if (exp.matches(REGEX_ANYTHANG + "[.\\d]{15}")) {
                        // Toast 15 digits reached
                        Toast toast = Toast.makeText(this, "Maximum 15 digits reached", Toast.LENGTH_SHORT);
                        toast.show();
                    } else if (exp.matches(REGEX_ANYTHANG + "[.][0-9]{10}")) {
                        Toast toast = Toast.makeText(this, "Maximum 10 decimal points reached", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        exp = exp + val;
                        currNum = currNum + val;
                    }
                }
            }
            if (val.equals("bracket")) {
                if (exp.equals("0")) {
                    exp = "(";
                    currNum = "";
                    bStack.push("(");
                } else {
                    if (isOperator(lastChar(exp)) || exp.endsWith("(")) {
                        exp = exp + "(";
                        currNum = "";
                        bStack.push("(");
                    }
                    if (lastChar(exp).matches("[0-9]") || exp.endsWith(".") || exp.endsWith(")")) {
                        if (!bStack.isEmpty()) {
                            exp = exp + ")";
                            currNum = "";
                            bStack.pop();
                        } else {
                            exp = exp + MULT + "(";
                            currNum = "";
                            bStack.push("(");
                        }
                    }
                }
            }
            if (val.equals(".")) {
                if (exp.matches(REGEX_ANYTHANG + "[\\d]*[.][\\d]*")) {
                    Toast toast = Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (exp.equals("0")) {
                    exp = "0.";
                    currNum = "0.";
                } else if (exp.matches(REGEX_ANYTHANG + REGEX_ANY_EXCEPT_NUM + "[\\d]{1,14}")) {
                    exp = exp + ".";
                    currNum = currNum + ".";
                } else if (exp.matches("[\\d]{1,14}")) {
                    exp = exp + ".";
                    currNum = currNum + ".";
                }
            }
            if (val.equals("%")) {
                if (exp.matches(REGEX_ANYTHANG + "[)\\d]+")) {
                    exp = exp + "%";
                    if (!exp.endsWith(")")) {
                        currNum = currNum + "%";
                    }
                } else {
                    Toast toast = Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            if (val.equals("n")) {
                if (exp.equals("0")) {
                    exp = "n0";
                    currNum = "n0";
                } else if (exp.endsWith("+") || exp.endsWith("-") || exp.endsWith(MULT) || exp.endsWith(DIV) || exp.endsWith("(")){
                    exp = exp + "n";
                    currNum = "n";
                } else if (currNum.equals("n")) {
                    exp = exp.substring(0, exp.length()-1);
                    currNum = "";
                } else if (currNum.length() > 0) {
                    if (currNum.charAt(0) == 'n') {
                        exp = exp.substring(0, exp.length() - currNum.length());
                        currNum = currNum.substring(1);
                        exp = exp + currNum;
                    } else {
                        exp = exp.substring(0, exp.length() - currNum.length());
                        currNum = "n" + currNum;
                        exp = exp + currNum;
                    }
                }
            }
        }
        calcText.setText(exp.replaceAll("n", "-"));
    }

    //Returns the last character in val as a String
    String lastChar (String val) {
        return "" + val.charAt(val.length()-1);
    }
    //Returns true if val is an operator
    boolean isOperator(String val) {
        if (val.equals("+") || val.equals("-") || val.equals(MULT) || val.equals(DIV)) {
            return true;
        }
        return false;
    }

    void evaluate() {
        //First close all the brackets if there are any still open
        while (!bStack.isEmpty()) {
            exp = exp + ")";
            bStack.pop();
        }
        try {
            exp = BigDecimal.valueOf(Double.parseDouble(CalcMath.solve(exp))).toPlainString();
            calcText.setText(exp);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            if (e.getMessage().endsWith("zero")) {
                calcText.setText("ERROR: Divide by zero");
            } else {
                calcText.setText("SYNTAX ERROR");
            }
            exp = "0";
            currNum = "0";
        }
    }
}

class GridAdapter extends BaseAdapter {

    String[] buttons;
    Context context;

    GridAdapter(Context context) {
        this.context = context;
        Resources res = context.getResources();

        buttons = res.getStringArray(R.array.buttons);
    }

    @Override
    public int getCount() {
        return buttons.length;
    }

    @Override
    public Object getItem(int i) {
        return buttons[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            int heightOfButton = (int)(MainActivity.height*0.6/5-2);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View buttonView = inflater.inflate(R.layout.button_view, viewGroup, false);
            buttonView.setMinimumHeight(heightOfButton);
            holder = new ViewHolder(buttonView);
            holder.buttonText.setHeight(heightOfButton);
            buttonView.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        setStyles(i, holder.buttonText);
        holder.buttonText.setText(buttons[i]);

        return holder.buttonView;
    }

    class ViewHolder {
        View buttonView;
        TextView buttonText;

        ViewHolder(View v) {
            buttonView = v;
            buttonText = (TextView) v.findViewById(R.id.buttonText);
        }
    }

    void setStyles(int i, TextView view) {
        Resources res = context.getResources();
        switch(i) {
            case 0:     //clear button
                view.setBackgroundColor(res.getColor(R.color.azure));
                view.setTextSize(45);
                break;
            case 1:     //bracket button
                view.setBackgroundColor(res.getColor(R.color.azure));
                view.setTextSize(30);
                break;
            case 2:     //percent button
                view.setBackgroundColor(res.getColor(R.color.azure));
                view.setTextSize(40);
                break;
            case 3:     //divide button
                view.setBackgroundColor(res.getColor(R.color.azure));
                view.setTextSize(45);
                break;
            case 7:     //multiply button
                view.setBackgroundColor(res.getColor(R.color.azure));
                view.setTextSize(45);
                break;
            case 11:    //minus button
                view.setBackgroundColor(res.getColor(R.color.azure));
                view.setTextSize(60);
                break;
            case 15:    //plus button
                view.setBackgroundColor(res.getColor(R.color.azure));
                view.setTextSize(45);
                break;
            case 19:    //equals button
                view.setBackgroundColor(res.getColor(R.color.blue));
                view.setTextColor(res.getColor(R.color.white));
                view.setTextSize(55);
                break;
        }
    }
}