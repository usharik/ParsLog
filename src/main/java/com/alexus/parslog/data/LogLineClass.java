package com.alexus.parslog.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by SBT-Usharovskiy-AL on 23.06.2016.
 */
public class LogLineClass {
    public static final int fieldCount = 20;
    private String fullLine;
    private int number;
    private List<String> fieldList;

    private String f1;
    private String f2;
    private String f3;
    private String f4;
    private String f5;
    private String f6;
    private String f7;
    private String f8;
    private String f9;
    private String f10;
    private String f11;
    private String f12;
    private String f13;
    private String f14;
    private String f15;
    private String f16;
    private String f17;
    private String f18;
    private String f19;
    private String f20;

    public LogLineClass(String line, int number) {
        this.fullLine = line;
        this.number = number;
        this.fieldList = new ArrayList<>(fieldCount);
    }

    private void setFieldValue(int fNum, String value) {
        try {
            Method m = this.getClass().getDeclaredMethod("setF" + fNum, String.class);
            m.invoke(this, value);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void parsByDelimiter(Pattern delimiter) {
        Scanner sc = new Scanner(fullLine);
        sc.useDelimiter(delimiter);

        int i = 1;
        while (sc.hasNext() && i <= fieldCount) {
            String s = sc.next();
            setFieldValue(i++, s);
            fieldList.add(s);
        }
        nullFields(i + 1);
    }

    public void parsByFixedWidth(List<Integer> wList) {
        int i = 0;
        int pos = 0;
        while (i < wList.size() && i <= fieldCount) {
            if (wList.get(wList.size() - 1) > fullLine.length()) {
                setFieldValue(1, fullLine);
                fieldList.add(fullLine);
                return;
            }
            String s = fullLine.substring(pos, wList.get(i)).trim();
            pos = wList.get(i);
            setFieldValue(1 + i++, s);
            fieldList.add(s);
        }
        if (pos < fullLine.length()) {
            String s = fullLine.substring(pos, fullLine.length()).trim();
            setFieldValue(1 + i++, s);
            fieldList.add(s);
        }
        nullFields(i + 1);
    }

    private void nullFields(int first) {
        for (int i = first; i < fieldCount; i++) {
            setFieldValue(i, null);
        }
    }

    public String getFullLine() {
        return fullLine;
    }

    public int getNumber() {
        return number;
    }

    public List<String> getFieldList() {
        return fieldList;
    }

    public String getF1() {
        return f1;
    }

    public void setF1(String f1) {
        this.f1 = f1;
    }

    public String getF2() {
        return f2;
    }

    public void setF2(String f2) {
        this.f2 = f2;
    }

    public String getF3() {
        return f3;
    }

    public void setF3(String f3) {
        this.f3 = f3;
    }

    public String getF4() {
        return f4;
    }

    public void setF4(String f4) {
        this.f4 = f4;
    }

    public String getF5() {
        return f5;
    }

    public void setF5(String f5) {
        this.f5 = f5;
    }

    public String getF6() {
        return f6;
    }

    public void setF6(String f6) {
        this.f6 = f6;
    }

    public String getF7() {
        return f7;
    }

    public void setF7(String f7) {
        this.f7 = f7;
    }

    public String getF8() {
        return f8;
    }

    public void setF8(String f8) {
        this.f8 = f8;
    }

    public String getF9() {
        return f9;
    }

    public void setF9(String f9) {
        this.f9 = f9;
    }

    public String getF10() {
        return f10;
    }

    public void setF10(String f10) {
        this.f10 = f10;
    }

    public String getF11() {
        return f11;
    }

    public void setF11(String f11) {
        this.f11 = f11;
    }

    public String getF12() {
        return f12;
    }

    public void setF12(String f12) {
        this.f12 = f12;
    }

    public String getF13() {
        return f13;
    }

    public void setF13(String f13) {
        this.f13 = f13;
    }

    public String getF14() {
        return f14;
    }

    public void setF14(String f14) {
        this.f14 = f14;
    }

    public String getF15() {
        return f15;
    }

    public void setF15(String f15) {
        this.f15 = f15;
    }

    public String getF16() {
        return f16;
    }

    public void setF16(String f16) {
        this.f16 = f16;
    }

    public String getF17() {
        return f17;
    }

    public void setF17(String f17) {
        this.f17 = f17;
    }

    public String getF18() {
        return f18;
    }

    public void setF18(String f18) {
        this.f18 = f18;
    }

    public String getF19() {
        return f19;
    }

    public void setF19(String f19) {
        this.f19 = f19;
    }

    public String getF20() {
        return f20;
    }

    public void setF20(String f20) {
        this.f20 = f20;
    }
}
