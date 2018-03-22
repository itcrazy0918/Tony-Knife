package com.tly.bigdata.util.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.tly.bigdata.util.CheckUtil;
import com.tly.bigdata.util.DateTimeUtil;
import com.tly.bigdata.util.StringUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.collect.Lists;

public class ExcelUtil {
    private ExcelUtil () {
        
    }
    
    /**
     * 创建一个Excel
     * @param output
     * @param excelSheetList
     */
    public static void writeExcel (OutputStream output, List<ExcelSheet> excelSheetList) {
        CheckUtil.checkNotNull("ExcelUtil.writeExcel.output", output);
        CheckUtil.checkNotNullAndEmpty("ExcelUtil.writeExcel.excelSheetList", excelSheetList);
        
        Workbook workbook = new XSSFWorkbook ();
        for (ExcelSheet excelSheet : excelSheetList) {
            List<List<String>> head = excelSheet.getHead();
            List<List<Object>> data = excelSheet.getData();
            
            Sheet sheet = workbook.createSheet(excelSheet.getSheetName());
            int maxColumnSize = 0;
            
            // excel sheet head
            if (head != null && !head.isEmpty()) {
                // 字体
                Font headCellFont = workbook.createFont();
                headCellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                
                // 颜色
                CellStyle headCellStyle = workbook.createCellStyle();
                headCellStyle.setFont(headCellFont);
                
                headCellStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
                headCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
                
                int rowCount = head.size();
                for (int r = 0; r < rowCount; r++) {
                    List<String> cellDataList = head.get(r);
                    createExcelSheetRow(sheet, r, cellDataList, headCellStyle);
                    
                    maxColumnSize = Math.max(maxColumnSize, cellDataList.size());
                }
            }
            
            // excel sheet data
            if (data != null && !data.isEmpty()) {
                int rowCount = data.size();
                for (int r = 0; r < rowCount; r++) {
                    List<Object> cellDataList = data.get(r);            
                    createExcelSheetRow(sheet, r + head.size(), cellDataList, null);
                    
                    maxColumnSize = Math.max(maxColumnSize, cellDataList.size());
                }
            }
            
            // 设置列宽
            for (int i = 0; i < maxColumnSize; i++) {
                Integer fixedWidth = excelSheet.getColumnWidthMap().get(i);
                if (fixedWidth == null) {
                    sheet.autoSizeColumn(i);    // 自适应
                }
                else {
                    sheet.setColumnWidth(i, fixedWidth);    // 自定义
                }
            }
        }
        
        // write
        try {
            workbook.write(output);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建 excel sheet 的 row
     * @param sheet
     * @param r
     * @param cellDataList
     * @param cellStyle
     */
    private static void createExcelSheetRow(Sheet sheet, int r, List<?> cellDataList, CellStyle cellStyle) {
        Row row = sheet.createRow(r);
        
        int cellCount = cellDataList.size();
        for (int c = 0; c < cellCount; c++) {
            Cell cell = row.createCell(c);
            
            Object cellData = cellDataList.get(c);
            if (cellData instanceof Number) {
                cell.setCellValue( ((Number) cellData).doubleValue() );
            }
            else if (cellData instanceof Calendar) {
                cell.setCellValue( DateTimeUtil.formatCalendar( (Calendar) cellData ) );
            }
            else if (cellData instanceof Date) {
                cell.setCellValue( DateTimeUtil.formatDateTime( (Date) cellData ) );
            }
            else {
                String value = cellData.toString();
                if (StringUtil.isNumber(value)) {
                    cell.setCellValue( Double.parseDouble(value) );    
                }
                else {
                    cell.setCellValue( value );
                }
            }
            
            if (cellStyle != null) {
                cell.setCellStyle(cellStyle);
            }
        }
    }
    
    public static void main(String[] args) throws Exception {        
        ExcelSheet excelSheet_1 = new ExcelSheet("excelSheet_1");
        excelSheet_1.setColumnWidth(0, 20);
        excelSheet_1.setColumnWidth(1, 10);
        excelSheet_1.appendHeadRow("id", "name");
        excelSheet_1.appendHeadRow();
        excelSheet_1.appendHeadRow("id1", "name1");
        excelSheet_1.appendDataRow("101", "a");
        excelSheet_1.appendDataRow("102", "b", Calendar.getInstance());
        excelSheet_1.appendDataRow("103", "cddddddddddddddddddddddddX");
        excelSheet_1.appendDataRow();
        excelSheet_1.appendDataRow("104", new Date(), 123);
        excelSheet_1.appendDataRow("105", new Date(), 456L);
        excelSheet_1.appendDataRow("106", new Date(), 789F);
        excelSheet_1.appendDataRow("106", new Date(), 789789D);
        excelSheet_1.appendDataRow("A1077123456789", "hahhahahahhahahahhahahahhahahahhahaX", "A123456789012345678901234567890");
        
        ExcelSheet excelSheet_2 = new ExcelSheet("excelSheet_2");
        excelSheet_2.appendHeadRow("id", "name", "data");
        excelSheet_2.appendDataRow("104", new Date(), 123);
        excelSheet_2.appendDataRow("105", new Date(), 456);
        excelSheet_2.appendDataRow("106", new Date(), 789);
        
        FileOutputStream fos = new FileOutputStream(new File("/data/my11.xlsx"));
        writeExcel(fos, Lists.newArrayList(excelSheet_1, excelSheet_2));
        
        System.out.println( "End .." );
    }
}
