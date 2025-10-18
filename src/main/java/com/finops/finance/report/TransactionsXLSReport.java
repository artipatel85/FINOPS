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
public class TransactionsXLSReport extends XLSReport {
    private ReportBean vo;
    private TrialService service;

    public TransactionsXLSReport(ReportBean vo, HttpServletResponse response,
                                 TrialService service) {
        super(null, null, response,"Transactions");
        this.vo = vo;
        this.service = service;
    }   

    @Override
    public void createData() {
        transactions();        
    }
    
    @Override
    public void createHeader() {
        List<Object[]> arrList = new ArrayList<>();
        Object[] headers = new Object[]{"Date","Voucher No","Particulars","Type","Chq.No.","Chq.Dt.",
            "Debit","Credit","Ref No"};
        arrList.add(headers);
        this.headerData = arrList;
        super.createHeader();
    }
    
    @Override
    public void createFooter() {

    }
    
    private void transactions(){
        vo.setLength(20000);
        List<ReportBean> dataList = service.findTransactionsByPage(vo);
        List<Object[]> arrList = new ArrayList<>();
        
        for(ReportBean svo : dataList){
            Object[] lines = new Object[]{svo.getParam1(),svo.getParam2(),svo.getParam3(),svo.getParam4(),
                svo.getParam5(),svo.getParam6(),Double.valueOf(svo.getParam7()),
                Double.valueOf(svo.getParam8()),svo.getParam10()};
            arrList.add(lines);
        }
        
        this.data = arrList;
    }
     
}
