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

import java.util.List;
import java.util.Map;

/**
 *
 * @author bhaumik
 */
public class TrialPDFReport extends PDFReport{
    private final ReportBean bean;
    private TrialService service;

    public TrialPDFReport(String reportTitle, Map params, HttpServletResponse response,
                          ReportBean bean, ServletContext context, TrialService service) {
        super(reportTitle, params, response, context);
        this.bean = bean;
        this.service = service;
    }

    @Override
    public void createData() {
        List<ReportBean> dataList = null;
        if("ALL".equals(bean.getParam6())){
            dataList = service.trialPartywise(bean);
        }
        else{
            this.reportTitle="/reports/TrialBalanceBranch.jrxml";
            dataList = service.trialBranchwise(bean);
        }
        this.data = dataList;
        this.parameters.put("companyName", bean.getCompanyName());
        this.parameters.put("address", bean.getCompanyAddress());
    }
    
}
