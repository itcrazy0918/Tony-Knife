package com.tly.bigdata.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.tly.bigdata.util.excel.ExcelSheet;
import com.tly.bigdata.util.excel.ExcelUtil;

public class HttpResponseUtil {
    
    /**
     * 下载Excel文件
     * @param response
     * @param filename
     * @param excelSLheetList
     */
    public static void downloadExcel(HttpServletResponse response, String filename, List<ExcelSheet> excelSLheetList) {
        try {
            filename = new String(filename.getBytes("utf-8"), "ISO-8859-1");
        }
        catch (UnsupportedEncodingException e) {
            filename = "Data.xlsx";
        }
        
        response.reset();          
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-disposition","attachment; filename=" + filename);
        
        OutputStream output = null;
        try {
            output = response.getOutputStream();            
            ExcelUtil.writeExcel(output, excelSLheetList);
            output.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            FileUtil.close(output);
        }
    }
    
}
