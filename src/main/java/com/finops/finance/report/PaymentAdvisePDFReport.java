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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bhaumik
 */
public class PaymentAdvisePDFReport extends PDFReport{
    private final ReportBean bean;
    private final TrialService service;

    public PaymentAdvisePDFReport(String reportTitle, Map params, HttpServletResponse response,
                                  ReportBean bean, ServletContext context, TrialService service) {
        super(reportTitle, params, response, context);
        this.bean = bean;
        this.service = service;
    }

    @Override
    public void createData() {
        bean.setLength(1000);
        List<ReportBean> dataList = null;
        String party = null;

        dataList = service.paymentAdvise(bean);


        List<ReportBean> dataList2 = new ArrayList<>();

        BigDecimal totalPayment = BigDecimal.ZERO;
        for(ReportBean svo : dataList){
            if("PAYMENT".equals(svo.getParam2())) {
                totalPayment = totalPayment.add(svo.getBigparam1());

            }
            else{
                dataList2.add(svo);
                party = svo.getParam9();
            }

        }

        this.parameters.put("message", "WE HAVE SETTLED THE ABOVE LISTED  INVOICES  AGAINST PAYMENT OF RS."+totalPayment+" in your account No");
        this.parameters.put("party", party);
        this.data = dataList2;

    }
    
}
