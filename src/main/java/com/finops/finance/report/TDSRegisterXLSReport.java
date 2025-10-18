package com.finops.finance.report;

import com.finops.report.XLSReport;
import com.finops.report.model.ReportBean;
import com.finops.report.service.TrialService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bhaumik
 */
public class TDSRegisterXLSReport extends XLSReport {
    private ReportBean vo;
    private TrialService trialService;

    public TDSRegisterXLSReport(ReportBean vo, HttpServletResponse response,
                                TrialService service) {
        super(null, null, response,"TDS-Register");
        this.vo = vo;
        this.trialService = service;
    }   

    @Override
    public void createData() {
        transactions();        
    }
    
    @Override
    public void createHeader() {
        List<Object[]> arrList = new ArrayList<>();
        Object[] headers = new Object[]{"Type","Voucher No","Date","Party","TDS-Account","PAN",
            "GSTIN","Basic","TDS"};
        arrList.add(headers);
        this.headerData = arrList;
        super.createHeader();
    }
    
    @Override
    public void createFooter() {

    }
    
    private void transactions(){
        int id = vo.getIntparam2();
        boolean isPayable = (id == 1);
        vo.setLength(10000);
        List<ReportBean> dataList = trialService.tdsRegisterData(vo, isPayable);
        List<Object[]> arrList = new ArrayList<>();
        
        for(ReportBean svo : dataList){
            Object[] lines = new Object[]{svo.getParam1(),svo.getParam2(),svo.getParam3(),svo.getParam6(),
                svo.getParam7(),svo.getParam4(),svo.getParam5(),
                svo.getParam8(),Double.valueOf(svo.getParam9())};
            arrList.add(lines);
        }
        
        this.data = arrList;
    }
     
}
