/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.finance.report;
import com.finops.report.XLSReport;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author chiragkhetani
 */
public abstract class CommonXLSReport extends XLSReport {

   public CommonXLSReport(HttpServletResponse response, String fileName) {
        super(null, null, response, fileName);
    }

    @Override
    public void createHeader() {
        List<Object[]> arrList = new ArrayList<>();
        Object[] headers = getHeaders();

        arrList.add(headers);
        this.headerData = arrList;
        super.createHeader();
    }

    public abstract Object[] getHeaders();
    
    @Override
    public void createData() {
        this.data = getData();
    }
    
    public abstract List<Object[]> getData();
}
