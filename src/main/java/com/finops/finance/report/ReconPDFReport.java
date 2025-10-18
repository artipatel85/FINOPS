/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.finance.report;

import com.finops.finance.service.AccountService;
import com.finops.report.model.ReportBean;
import com.finops.report.service.ReconciliationService;
import com.finops.util.DateUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 *
 * @author bhaumik
 */
public class ReconPDFReport extends PDFReport{
    private final ReportBean bean;
    private ReconciliationService service;
    private AccountService accountService;

    public ReconPDFReport(String reportTitle, Map params, HttpServletResponse response,
                          ReportBean bean, ServletContext context, ReconciliationService service,
                          AccountService accountService) {
        super(reportTitle, params, response, context);
        this.bean = bean;
        this.service = service;
        this.accountService = accountService;
    }

    @Override
    public void createData() {
        bean.setReportRequest(true);
        //bean.setReportQuery(" AND je.cash_bank = "+bean.getParam3());
        String endDate = bean.getParam1();
        if (!StringUtils.hasText(endDate)) {
            endDate = DateUtil.getSystemDate();
            bean.setParam1(endDate);
        }
        String dayMonth = DateUtil.getDayBalanceField(endDate);
        ReportBean curBalBean = accountService.getCurrentBalance(
                Integer.toString(bean.getAcctYear()), bean.getParam3(), endDate);
        this.parameters.put("acctName", bean.getParam2());
        this.parameters.put("curBalance", new BigDecimal(curBalBean.getParam1()));
        
        this.parameters.put("endDate", endDate);
        this.data = service.reconciliationReportView(bean);
    }
    
}
