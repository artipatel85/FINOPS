/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.report;

import com.finops.util.ReportUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public abstract class XLSReport extends AbstractReport {

    protected HSSFWorkbook workbook = null;
    protected List<Object[]> data = null;
    protected String sheetName;
    protected int rownum = 0;
    protected HSSFSheet sheet;
    protected String fileName;
    protected List<Object[]> headerData = null;
    protected List<Object[]> preHeaderData = null;
    protected List<Object[]> footerData = null;

    public XLSReport(String reportTitle, Map params, HttpServletResponse response, String fileName) {
        this.reportTitle = reportTitle;
        this.parameters = params;
        this.response = response;
        workbook = new HSSFWorkbook();
        this.fileName = fileName;
        this.sheetName = fileName;
    }

    @Override
    public void setContentType() {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "filename=" + fileName+".xls");
    }

    @Override
    public void createReport() {
        //Create a blank sheet
        sheet = workbook.createSheet(sheetName);
        createPreHeader();
        createHeader();
        HSSFCellStyle numberCellStyle = ReportUtil.getNumberCellStyle(workbook);
        HSSFCellStyle cellStyle = workbook.createCellStyle();

        for (Object[] oar : data) {
            Row row = sheet.createRow(rownum++);
            int cellnum = 0;
            for (Object obj : oar) {
                Cell cell = row.createCell(cellnum++);
                setCellValue(obj, cell, cellStyle, numberCellStyle);
            }
        }

        createFooter();
    }

    public void createHeader() {
        if (headerData != null) {
            for (Object[] oar : headerData) {
                Row row = sheet.createRow(rownum++);
                int cellnum = 0;
                for (Object obj : oar) {
                    Cell cell = row.createCell(cellnum++);
                    setHeaderCellValue(obj, cell, ReportUtil.getHeaderStyle(workbook));
                }
            }
        }
    }

    public void createPreHeader() {
        if (preHeaderData != null) {
            for (Object[] oar : preHeaderData) {
                Row row = sheet.createRow(rownum++);
                int cellnum = 0;
                for (Object obj : oar) {
                    Cell cell = row.createCell(cellnum++);
                    //setHeaderCellValue(obj, cell, ReportUtil.getHeaderStyle(workbook));
                    setFooterCellValue(obj, cell, ReportUtil.getFooterStyle(workbook),
                            ReportUtil.getFooterNumberStyle(workbook));
                }
            }
        }
    }

    public void createFooter() {
        if (footerData != null) {
            for (Object[] oar : footerData) {
                Row row = sheet.createRow(rownum++);
                int cellnum = 0;
                for (Object obj : oar) {
                    Cell cell = row.createCell(cellnum++);
                    setFooterCellValue(obj, cell, ReportUtil.getFooterStyle(workbook),
                            ReportUtil.getFooterNumberStyle(workbook));
                }
            }
        }

    }

    @Override
    public void flushReport() throws IOException {
        ServletOutputStream ouputStream = response.getOutputStream();
        workbook.write(ouputStream);
        ouputStream.flush();
        ouputStream.close();
    }

    private void setCellValue(Object obj, Cell cell, HSSFCellStyle cs1, HSSFCellStyle cs2) {
        if (obj instanceof String) {
            cell.setCellValue((String) obj);
            cell.setCellStyle(cs1);
        } else if (obj instanceof Integer) {
            cell.setCellValue((Integer) obj);
            cs2.setDataFormat((short)0);
            cell.setCellStyle(cs2);
        } else if (obj instanceof Double) {
            cell.setCellValue((Double) obj);
            cell.setCellStyle(cs2);
        } else if (obj instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) obj).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            cell.setCellStyle(cs2);
        }
    }

    private void setHeaderCellValue(Object obj, Cell cell, HSSFCellStyle cs) {
        cell.setCellValue((String) obj);
        cell.setCellStyle(cs);
    }

    private void setFooterCellValue(Object obj, Cell cell, HSSFCellStyle cs1, HSSFCellStyle cs2) {
        if (obj instanceof String) {
            String val = (String) obj;
            if (StringUtils.hasText(val) && val.startsWith("=")) {
                //cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cell.setCellStyle(cs2);
                cell.setCellFormula(createSumFormula(val));
            } else {
                setCellValue(obj, cell, cs1, cs2);
            }
        } else {
            setCellValue(obj, cell, cs1, cs2);
        }
    }

    private String createSumFormula(String val) {
        String start = val.substring(1);
        if(!val.contains("?"))
            return start;
        return start.replace("?", String.valueOf(rownum - 1));
    }
}
