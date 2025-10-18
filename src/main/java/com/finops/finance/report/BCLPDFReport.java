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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bhaumik
 */
public class BCLPDFReport extends PDFReport{
    private final ReportBean bean;
    private TrialService trialService;

    public BCLPDFReport(String reportTitle, Map params, TrialService trialService,
                        HttpServletResponse response, ReportBean bean, ServletContext context) {
        super(reportTitle, params, response, context);
        this.bean = bean;
        this.trialService = trialService;
    }

    @Override
    public void createData() {
        ReportBean rbean = trialService.bcl(bean);
        List<Object[]> arrList = new ArrayList<>();
        arrList.add(new Object[]{"Bhaumik"});
        this.data = arrList;
        
        this.parameters.put("pan", bean.getCompanyPan());
        this.parameters.put("tan", bean.getCompanyTan());
        this.parameters.put("gstin", bean.getCompanyGstinNo());
        this.parameters.put("cin",  "");
        this.parameters.put("fromParty", bean.getCompanyName());
        this.parameters.put("fromPartyAddress", bean.getCompanyAddress());
        this.parameters.put("toParty", rbean.getParam2());
        this.parameters.put("toPartyAddress", rbean.getParam8());
        String subject = "Confirmation of Account for the Period From: "+bean.getYrStartDate()+ " To: "+bean.getParam12();
        this.parameters.put("subject", subject);
        this.parameters.put("partyPan", rbean.getParam9());
        this.parameters.put("partyTan", rbean.getParam11());
        this.parameters.put("content1DearSirMadam", "Dear Sir/Madam");
        String content2youraccount = "Your accounts are standing in our Books of Accounts for the period From: "+bean.getYrStartDate()+ " To: "+bean.getParam12()
                +"\n It is desirable to have your confirmation of balance given below";
        this.parameters.put("content2youraccount", content2youraccount);
        String content3ClosingBalance = "Balance as per our Books Rs. "+rbean.getParam7()+ " "+rbean.getParam10();
        this.parameters.put("content3ClosingBalance", content3ClosingBalance);
        String content4 = "Please note if we do not receive your confirmation / reconciliation with in 15 days of date of this letter, the balance as shown will be"
                + "taken as confirmed";
        this.parameters.put("content4", content4);
        String content5 = "With reference to your request for the balance confirmation for the Period From: "+bean.getYrStartDate()+ " To: "+bean.getParam12()+
                "\nWe advice as under"
                + "\n1. The above balance is confirmed"
                + "\n2. The balance according to our books of accounts is Rs. "+rbean.getParam7()+ " "+rbean.getParam10()+" as explained in attached statement."
                + "\n3. Our Pan No. is "+rbean.getParam9()+", Tan No. is "+rbean.getParam11()+", and GSTIN is "+rbean.getParam12();
        this.parameters.put("content5", content5);
    }
    
}
