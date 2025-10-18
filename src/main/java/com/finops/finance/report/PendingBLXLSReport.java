/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.finance.report;


import com.finops.finance.service.InvoiceService;
import com.finops.report.XLSReport;
import com.finops.report.model.ReportBean;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chiragkhetani
 */
public class PendingBLXLSReport extends XLSReport {

    private ReportBean vo;

    private InvoiceService invoiceService;

    public PendingBLXLSReport(ReportBean vo, HttpServletResponse response, InvoiceService invoiceService) {
        super(null, null, response, "PendingBL");
        this.invoiceService = invoiceService;
        this.vo = vo;
    }

    @Override
    public void createHeader() {
        List<Object[]> arrList = new ArrayList<>();
        Object[] headers = new Object[]{"BL NO", "BL Date", "Master BL"};

        arrList.add(headers);
        this.headerData = arrList;
        super.createHeader();
    }

    @Override
    public void createData() {

        List<ReportBean> dataList = invoiceService.pendingBL(vo);
        List<Object[]> arrList = new ArrayList<Object[]>();

        for (ReportBean svo : dataList) {
            Object[] lines = new Object[]{svo.getParam1(), svo.getParam2(), svo.getParam3()};
            arrList.add(lines);
        }

        this.data = arrList;

    }
}
