package com.finops.finance.report;

import com.finops.finance.service.AccountService;
import com.finops.report.XLSReport;
import com.finops.report.model.ReportBean;
import com.finops.report.service.ReconciliationService;
import com.finops.util.DateUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bhaumik
 */
public class ReconXLSReport extends XLSReport {
    private ReportBean vo; 
    private BigDecimal currentBalance;
    private ReconciliationService service;
    private AccountService accountService;

    public ReconXLSReport(ReportBean vo, HttpServletResponse response, ReconciliationService service,
    AccountService accountService) {
        super(null, null, response,"Reconciliation");
        this.vo = vo;
        this.service = service;
        this.accountService = accountService;
    }   

    @Override
    public void createData() {
        vo.setReportRequest(true);
        //vo.setReportQuery(" AND je.cash_bank = "+vo.getParam3());
        List<ReportBean> dataList = service.reconciliationReportView(vo);
        List<Object[]> arrList = new ArrayList<>();
        
        for(ReportBean svo : dataList){
            Object[] lines = new Object[]{svo.getParam2(),svo.getParam3(),svo.getParam1(),svo.getParam4(),
                svo.getParam7(),svo.getParam8(),svo.getParam9(),Double.parseDouble(svo.getParam10()),
                Double.parseDouble(svo.getParam11())};
            arrList.add(lines);
        }
        
        this.data = arrList;
        String endDate = vo.getParam1();
        if (!StringUtils.hasText(endDate)) {
            endDate = DateUtil.getSystemDate();
            vo.setParam1(endDate);
        }
        String dayMonth = DateUtil.getDayBalanceField(endDate);
        ReportBean curBalBean = accountService.getCurrentBalance(
                Integer.toString(vo.getAcctYear()), vo.getParam3(), endDate);
        currentBalance = new BigDecimal(curBalBean.getParam1());
    }
    
    @Override
    public void createHeader() {
        List<Object[]> arrList = new ArrayList<>();
        Object[] headers = new Object[]{"ACCOUNT",vo.getParam2()};
        arrList.add(headers);
        headers = new Object[]{"Voucher Date","Bank Date","Voucher No","Particulars",
            "Type","CHQ. NO","CHQ. DATE","Debit","Credit"};
        arrList.add(headers);
        this.headerData = arrList;
        super.createHeader();
    }
    
    @Override
    public void createFooter() {
        List<Object[]> arrList = new ArrayList<>();
        BigDecimal dr = currentBalance;
        BigDecimal cr = currentBalance;
        if(dr.doubleValue() < 0){
            dr = new BigDecimal(0.00);
            cr = dr.negate();
        }
        else{
            cr = new BigDecimal(0.00);
        }
        Object[]  oar = new Object[]{"","","","Total","","","","=SUM(H3:H?)","=SUM(I3:I?)"};        
        arrList.add(oar);
        oar = new Object[]{"","","","Balance As on Date "+vo.getParam1(),"","","",dr,cr};
        arrList.add(oar);
        
        this.footerData = arrList;
        super.createFooter();
    }
}
