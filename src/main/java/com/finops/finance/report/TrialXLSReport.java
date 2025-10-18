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
public class TrialXLSReport extends XLSReport {
    private ReportBean vo;
    private TrialService service;

    public TrialXLSReport(ReportBean vo, HttpServletResponse response,
                          TrialService service) {
        super(null, null, response,"TrialBalance");
        this.vo = vo;
        this.service = service;
    }   

    @Override
    public void createData() {
        trialPartywise();        
    }
    
    @Override
    public void createHeader() {
        List<Object[]> arrList = new ArrayList<>();
        Object[] headers = new Object[]{"ACNO","Ledger Account","Under","Opening","Cr/Dr","Debit",
            "Credit","Closing","Cr/Dr"};
        arrList.add(headers);
        this.headerData = arrList;
        super.createHeader();
    }
    
    @Override
    public void createFooter() {
        super.createFooter();
    }
    
    private void trialPartywise(){
        List<ReportBean> dataList = null;

        dataList = service.trialPartywise(vo);

        List<Object[]> arrList = new ArrayList<>();
        List<Object[]> arrList2 = new ArrayList<>();
        double opening = 0;
        double closing = 0;
        
        for(ReportBean svo : dataList){
            if(svo == null){
                continue;
            }
            double curOpening = Double.valueOf(svo.getParam3());
            double curClosing = Double.valueOf(svo.getParam7());
            Object[] lines = new Object[]{svo.getParam1(),svo.getParam2(),svo.getParam8(), curOpening,
                svo.getParam4(),Double.valueOf(svo.getParam5()),Double.valueOf(svo.getParam6()),
                curClosing, svo.getParam10()};
            if("Cr".equalsIgnoreCase(svo.getParam4())){
                opening -= curOpening;
            }
            else{
                opening += curOpening;
            }
            
            if("Cr".equalsIgnoreCase(svo.getParam10())){
                closing -= curClosing;
            }
            else{
                closing += curClosing;
            }
            arrList.add(lines);
        }
        Object[] oar = {"","","",opening,"", "=SUM(F2:F?)","=SUM(G2:G?)",closing,""};
        arrList2.add(oar);
        this.data = arrList;
        this.footerData = arrList2;
        
    }
     
}
