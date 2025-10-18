package com.finops.finance.report;

import com.finops.report.XLSReport;
import com.finops.report.model.ReportBean;
import com.finops.report.service.ProfitabilityService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Bhaumik
 */
public class ProfitabilityXLSReport extends XLSReport {
    private ReportBean vo;

    private ProfitabilityService profitabilityService;
    private Map<String, String> salesmanMap;

    public ProfitabilityXLSReport(ReportBean vo, HttpServletResponse response, ProfitabilityService profitabilityService,
                                  Map<String, String> salesmanMap) {
        super(null, null, response,"Profitability-Summary");
        this.vo = vo;
        this.profitabilityService = profitabilityService;
        this.salesmanMap = salesmanMap;
    }   

    @Override
    public void createData() {
        summary();        
    }
    
    @Override
    public void createHeader() {
        List<Object[]> arrList = new ArrayList<>();
        Object[] headers = new Object[]{"Job No","BL No","Income","Expense","Profit/Loss","SEA/AIR",
            "Export/Import","Branch","Salesman","Party"};
        arrList.add(headers);
        this.headerData = arrList;
        super.createHeader();
    }
    
    @Override
    public void createFooter() {
        List<Object[]> arrList = new ArrayList<>();
        Object[] oar = {"","","=SUM(C2:C?)","=SUM(D2:D?)","=SUM(E2:E?)","","","","",""};
        
        arrList.add(oar);
        this.footerData = arrList;
        super.createFooter();
    }
    
    private void summary(){
        vo.setReportRequest(true);
        vo.setLength(500);
        List<ReportBean> dataList = profitabilityService.summary(vo, salesmanMap);
        List<Object[]> arrList = new ArrayList<>();
        
        for(ReportBean svo : dataList){
            Object[] lines = new Object[]{svo.getParam1(),svo.getParam2(),Double.valueOf(svo.getParam3()),
                Double.valueOf(svo.getParam4()),Double.valueOf(svo.getParam3()) - Double.valueOf(svo.getParam4()),
                svo.getParam6(),svo.getParam7(),svo.getParam8(), svo.getParam11(), svo.getParam10()};
            arrList.add(lines);
        }
        
        this.data = arrList;
    }
     
}
