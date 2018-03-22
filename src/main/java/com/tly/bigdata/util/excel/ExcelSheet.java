package com.tly.bigdata.util.excel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ExcelSheet {
    private String sheetName;
    private List<List<String>> head;
    private List<List<Object>> data;
    private Map<Integer, Integer> columnWidthMap;
    
    public ExcelSheet (String sheetName) {
        this.sheetName = sheetName;
        this.head = Lists.newArrayList();
        this.data = Lists.newArrayList();
        this.columnWidthMap = Maps.newTreeMap();
    }
    
    /**
     * 设置列宽
     * @param columnIndex
     * @param letterCount   字符数量(英文算1个，中文算两个)
     * @return
     */
    public ExcelSheet setColumnWidth (int columnIndex, int letterCount) {
        this.columnWidthMap.put(columnIndex, letterCount * 256);
        return this;
    }

    public ExcelSheet appendHeadRow (String... cells) {
        List<String> row = Arrays.asList(cells);
        return appendHeadRow(row);
    }
    
    public ExcelSheet appendHeadRow (List<String> row) {
        if (row == null) {
            row = Lists.newArrayList();
        }
        
        this.head.add(row);
        return this;
    }
    
    public ExcelSheet appendDataRow (String... cells) {
        List<Object> row = new ArrayList<>(cells.length);
        for (String cell : cells) {
            row.add(cell);
        }
        return appendDataRow(row);
    }
    
    public ExcelSheet appendDataRow (Object... cells) {
        List<Object> row = Arrays.asList(cells);
        return appendDataRow(row);
    }
    
    public ExcelSheet appendDataRow (List<Object> row) {
        if (row == null) {
            row = Lists.newArrayList();
        }
        
        this.data.add(row);
        return this;
    }
    
    public String getSheetName() {
        return sheetName;
    }

    public List<List<String>> getHead() {
        return head;
    }

    public List<List<Object>> getData() {
        return data;
    }

    public Map<Integer, Integer> getColumnWidthMap() {
        return columnWidthMap;
    }
}
