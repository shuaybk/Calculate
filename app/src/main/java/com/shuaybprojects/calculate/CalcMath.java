package com.shuaybprojects.calculate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

/**
 * Created by Shuayb on 28-Nov-16.
 */

//Only works for numbers with max 10 digits after decimal point
//Max 15 digit numbers

public class CalcMath {

    private static final int ROUND_TO = 10;

    public static String solve(String exp) {
        String ans = solveBrackets(exp);
        if (ans.charAt(0) == 'n') {
            ans = ans.replaceFirst("n", "-");
        }
        double finalAns = Math.round(Double.parseDouble(ans)*Math.pow(10, ROUND_TO))/((double)Math.pow(10, ROUND_TO));
        System.out.println(finalAns);
        return Double.toString(finalAns);
    }

    private static String solveBrackets(String exp) {
        Stack<String> stack = new Stack();
        int sBrac, eBrac;

        if (exp.equals("")){
            return "ERROR";
        }
        //Parse and solve brackets first
        while ((sBrac = exp.indexOf("(")) > -1) {
            stack.push("(");
            for (int i = sBrac+1; i < exp.length() ; i++) {
                if (exp.charAt(i) == '(') {
                    stack.push("(");
                }
                else if (exp.charAt(i) == ')') {
                    stack.pop();
                    if (stack.isEmpty()) {
                        eBrac = i;

                        if (eBrac != (exp.length()-1)) {
                            exp = exp.substring(0, sBrac) + solveBrackets(exp.substring(sBrac+1, eBrac)) + exp.substring(eBrac+1);
                        } else {
                            exp = exp.substring(0, sBrac) + solveBrackets(exp.substring(sBrac+1, eBrac));
                        }
                        break;
                    }
                }
            }
        }
        return solvePlus(exp);
    }

    private static String solvePlus(String exp) {
        StringTokenizer tokenizer = new StringTokenizer(exp, "+");

        BigDecimal ans = BigDecimal.valueOf(Double.parseDouble(solveMinus(tokenizer.nextToken())));
        while (tokenizer.hasMoreTokens()) {
            ans = ans.add(BigDecimal.valueOf(Double.parseDouble(solveMinus(tokenizer.nextToken()))));
        }

        String temp = ans.toString();
        if (temp.charAt(0) == '-') {
            temp = temp.replaceFirst("-", "n");
        }
        return temp;
    }

    private static String solveMinus(String exp) {
        StringTokenizer tokenizer = new StringTokenizer(exp, "-");

        BigDecimal ans = BigDecimal.valueOf(Double.parseDouble(solveMult(tokenizer.nextToken())));
        while (tokenizer.hasMoreTokens()) {
            ans = ans.subtract(BigDecimal.valueOf(Double.parseDouble(solveMult(tokenizer.nextToken()))));
        }

        return ans.toString();
    }

    private static String solveMult(String exp) {
        StringTokenizer tokenizer = new StringTokenizer(exp, "\u00D7");

        BigDecimal ans = BigDecimal.valueOf(Double.parseDouble(solveDiv(tokenizer.nextToken())));
        while (tokenizer.hasMoreTokens()) {
            ans = ans.multiply(BigDecimal.valueOf(Double.parseDouble(solveDiv(tokenizer.nextToken()))));
        }

        return ans.toString();
    }

    private static String solveDiv(String exp) {
        StringTokenizer tokenizer = new StringTokenizer(exp, "\u00F7");
        BigDecimal ans;
        String firstToken = tokenizer.nextToken();
        boolean hasPercent = false;

        //Remove the percent sign if it exists
        if (firstToken.endsWith("%")) {
            hasPercent = true;
            firstToken = firstToken.substring(0, firstToken.length()-1);
        }
        //Convert to negative and remove 'n' if it's negative
        if (firstToken.charAt(0) == 'n') {
            ans = BigDecimal.valueOf((-1) * Double.parseDouble(firstToken.substring(1)));
        } else {
            ans = BigDecimal.valueOf(Double.parseDouble(firstToken));
        }
        if (hasPercent) {
            ans = ans.divide(BigDecimal.valueOf(100));
        }
        //If there are more tokens (ie. there's actually division to do)
        while (tokenizer.hasMoreTokens()) {
            String currToken = tokenizer.nextToken();
            //Remove the percent sign if it exists
            if (currToken.endsWith("%")) {
                hasPercent = true;
                currToken = currToken.substring(0, currToken.length()-1);
            } else {
                hasPercent = false;
            }
            //Convert to negative and remove 'n' if it's negative
            if (currToken.charAt(0) == 'n') {
                ans = ans.divide(BigDecimal.valueOf((-1) * Double.parseDouble(currToken.substring(1))), MathContext.DECIMAL128);
            } else {
                ans = ans.divide(BigDecimal.valueOf(Double.parseDouble(currToken)), MathContext.DECIMAL128);
            }
            if (hasPercent) {
                ans = ans.multiply(BigDecimal.valueOf(100));
            }
        }
        return ans.toString();
    }
}
