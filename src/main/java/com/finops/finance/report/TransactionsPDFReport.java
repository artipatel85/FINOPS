/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.finance.report;

import com.finops.report.model.ReportBean;
import com.finops.report.service.TrialService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 *
 * @author bhaumik
 */
public class TransactionsPDFReport extends PDFReport{
    private final ReportBean bean;
    private TrialService trialService;

    public TransactionsPDFReport(String reportTitle, Map params, HttpServletResponse response,
                                 ReportBean bean, ServletContext context, TrialService service) {
        super(reportTitle, params, response, context);
        this.bean = bean;
        this.trialService = service;
    }

    @Override
    public void createData() {
        bean.setLength(20000);
        List<ReportBean> dataList = trialService.findTransactionsByPage(bean);
        ReportBean opening = dataList.remove(0);
        ReportBean total = dataList.remove(dataList.size()-2);
        ReportBean closing = dataList.remove(dataList.size()-1);
        this.data = dataList;
        this.parameters.put("company", bean.getCompanyName());
        this.parameters.put("address", bean.getCompanyAddress());
        this.parameters.put("fromDate", bean.getParam4());
        this.parameters.put("toDate", bean.getParam5());
        this.parameters.put("openDR", opening.getParam7());
        this.parameters.put("openCR", opening.getParam8());
        this.parameters.put("totalDR", total.getParam7());
        this.parameters.put("totalCR", total.getParam8());
        this.parameters.put("closeDR", closing.getParam7());
        this.parameters.put("closeCR", closing.getParam8());
        this.parameters.put("acctName", bean.getParam3());
    }
    
}
