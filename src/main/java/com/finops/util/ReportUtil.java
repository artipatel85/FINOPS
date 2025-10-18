/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.util;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;


public class ReportUtil {
    public static HSSFCellStyle getNumberCellStyle(HSSFWorkbook workbook){
        HSSFCellStyle numberCellStyle = workbook.createCellStyle();
        numberCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        DataFormat format = workbook.createDataFormat();
        numberCellStyle.setDataFormat(format.getFormat("0.00"));
        return numberCellStyle;
    }
    
    public static HSSFCellStyle getHeaderStyle(HSSFWorkbook workbook){
        HSSFCellStyle cs = workbook.createCellStyle();
        cs.setFont(HSSFont(workbook, IndexedColors.WHITE.getIndex()));
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cs.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
        return cs;
    }
    
    private static HSSFFont HSSFont(HSSFWorkbook workbook, short cc){
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setColor(cc);
        return font;
    }
    
    public static HSSFCellStyle getFooterNumberStyle(HSSFWorkbook workbook){
        HSSFCellStyle cs = getNumberCellStyle(workbook);
        cs.setFont(HSSFont(workbook, IndexedColors.BLACK.getIndex()));
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cs.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        return cs;
    }
    
    public static HSSFCellStyle getFooterStyle(HSSFWorkbook workbook){
        HSSFCellStyle cs = workbook.createCellStyle();
        cs.setFont(HSSFont(workbook, IndexedColors.BLACK.getIndex()));
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cs.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        return cs;
    }
}
