package com.alexus.parslog.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by alex on 24.06.2016.
 */
public class LogLineList {
    private List<LogLineClass> parsedLog;
    private HashMap<Integer, Integer> line2pos;
    private HashMap<Integer, Integer> pos2line;
    private int columnCount;
    private int pos;

    public LogLineList() {
        this.parsedLog = new ArrayList<>();
        this.line2pos = new HashMap<>();
        this.pos2line = new HashMap<>();
        this.columnCount = 0;
        this.pos = 0;
    }

    public LogLineClass addLine(String line) {
        int number = parsedLog.size() + 1;
        LogLineClass llc = new LogLineClass(line, number);
        parsedLog.add(llc);
        line2pos.put(number, pos);
        pos += line.length() + 1;
        pos2line.put(pos, number);
        return llc;
    }

    public void parsByDelimiter(Pattern delimiter) {
        columnCount = 0;
        for (LogLineClass llc : parsedLog) {
            llc.parsByDelimiter(delimiter);
            if (columnCount < llc.getFieldList().size()) columnCount = llc.getFieldList().size();
        }
    }

    public void parsByFixedWidth(List<Integer> widthList) {
        columnCount = 0;
        for (LogLineClass llc : parsedLog) {
            llc.parsByFixedWidth(widthList);
            if (columnCount < llc.getFieldList().size()) columnCount = llc.getFieldList().size();
        }
    }

    public int getColumnCount() {
        return columnCount;
    }

    public Integer lineByPos(int pos) {
        return line2pos.get(pos);
    }

    public Integer posByLine(int line) {
        return pos2line.get(line);
    }

    public List<LogLineClass> getParsedLog() {
        return parsedLog;
    }
}
