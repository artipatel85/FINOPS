package com.finops.finance.report;

import com.finops.finance.service.GSTINService;
import com.finops.report.XLSReport;
import com.finops.report.model.ReportBean;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bhaumik
 */
public class GstinCreditXLSReport extends XLSReport {
    private ReportBean vo;
    private GSTINService gstinService;

    public GstinCreditXLSReport(ReportBean vo, HttpServletResponse response, GSTINService gstinService) {
        super(null, null, response,"GSTIN-CREDIT-MATCH");
        this.gstinService = gstinService;
        this.vo = vo;
    }   

    @Override
    public void createData() {
        summary();        
    }
    
@Override
    public void createHeader() {
        List<Object[]> arrList = new ArrayList<>();
        Object[] headers = new Object[]{"========","========","========","========","SHIKHAR", "","","","","GSTIN","","","","",""};
        arrList.add(headers);
        headers = new Object[]{"Category","Inv No","Inv Date","Gstin","Voucher","Voucher Date","Taxable","Tax",
            "Party","Taxable","Tax",
                "Party","IGST","CGST","SGST"};
        arrList.add(headers);
        this.headerData = arrList;
        super.createHeader();
    }
    
//    @Override
//    public void createFooter() {
//        List<Object[]> arrList = new ArrayList<>();
//        Object[] oar = {"","","=SUM(C2:C?)","=SUM(D2:D?)","=SUM(E2:E?)","","","","",""};
//
//        arrList.add(oar);
//        this.footerData = arrList;
//        super.createFooter();
//    }
    
    private void summary(){
        vo.setReportRequest(true);
        List<ReportBean> dataList = gstinService.retrieveMappedRecords(vo.getParam2(), vo);
        List<Object[]> arrList = new ArrayList<>();
        Object[] lines = null;

        for(ReportBean svo : dataList){
            if("2-GSTIN".equals(svo.getParam7())){
                lines = new Object[]{svo.getParam7(),svo.getParam3(),svo.getParam4(),svo.getParam9(),"","","","","",
                        svo.getParam5(),svo.getParam6(), svo.getParam8(),
                        svo.getDoubleParam1(), svo.getDoubleParam2(), svo.getDoubleParam3()};
            }
            else  if("3-SHIKHAR".equals(svo.getParam7())){
                lines = new Object[]{svo.getParam7(),svo.getParam3(),svo.getParam4(),svo.getParam9(),svo.getParam1(),svo.getParam2(),svo.getParam5(),svo.getParam6(),
                        svo.getParam8(),"","","","",""};
            }
            else {
                lines = new Object[]{svo.getParam7(),svo.getParam3(), svo.getParam4(), svo.getParam9(),svo.getParam1(), svo.getParam2(),
                        svo.getParam5(), svo.getParam6(), svo.getParam8(),
                        svo.getParam5(), svo.getParam6(),svo.getParam8(),svo.getDoubleParam1(),
                        svo.getDoubleParam2(), svo.getDoubleParam3()};

            }
            arrList.add(lines);
        }
        
        this.data = arrList;
    }
}
