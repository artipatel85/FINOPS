package com.finops.finance.report;

import com.finops.report.XLSReport;
import com.finops.report.model.ReportBean;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bhaumik
 */
public class PaymentAdviseXLSReport extends XLSReport {
    private ReportBean vo;
    private List<ReportBean> dataList = null;

    public PaymentAdviseXLSReport(ReportBean vo, HttpServletResponse response) {
        super(null, null, response,"TrialBalance");
        this.vo = vo;
    }   

    @Override
    public void createData() {
        trialPartywise();        
    }
    
    @Override
    public void createHeader() {
        List<Object[]> arrList = new ArrayList<>();
        Object[] headers = new Object[]{"INVOICE NO","INVOICE DATE","BL NO","INVOICE AMOUNT","TDS",
            "NET AMOUNT","VOUCHER NO","PARTY NAME"};
        arrList.add(headers);
        this.headerData = arrList;
        super.createHeader();
    }
    

    private void trialPartywise(){

        //dataList = trialService.paymentAdvise(vo);
        List<Object[]> arrList = new ArrayList<>();

        for(ReportBean svo : dataList){
            if(!"PAYMENT".equals(svo.getParam2())) {
                Object[] lines = new Object[]{svo.getParam1(), svo.getParam10(), svo.getParam5(), svo.getBigparam1(), svo.getBigparam2(),
                        svo.getBigparam1().subtract(svo.getBigparam2()), svo.getParam6(), svo.getParam9()};
                arrList.add(lines);
            }

        }
        this.data = arrList;
    }

    @Override
    public void createFooter() {
        List<Object[]> arrList = new ArrayList<>();
        Object[] oar = {"","","","=SUM(D2:D?)","=SUM(E2:E?)","=SUM(F2:F?)","",""};
        arrList.add(oar);
        BigDecimal totalPayment = BigDecimal.ZERO;
        for(ReportBean svo : dataList){
            if("PAYMENT".equals(svo.getParam2())) {
                totalPayment = totalPayment.add(svo.getBigparam1());
            }

        }
        oar =new Object[] {"WE HAVE SETTLED THE ABOVE LISTED  INVOICES  AGAINST PAYMENT OF RS."+totalPayment+" in your account No"};
        arrList.add(oar);
        this.footerData = arrList;
        super.createFooter();
    }

}
