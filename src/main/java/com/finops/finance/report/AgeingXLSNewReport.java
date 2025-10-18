/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.finance.report;

import com.finops.report.XLSReport;
import com.finops.report.model.ReportBean;
import com.finops.report.service.OutstandingService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author BirenDesai
 */
public class AgeingXLSNewReport extends XLSReport {

    private final ReportBean bean;
    private OutstandingService service;

    public AgeingXLSNewReport(HttpServletResponse response, ReportBean vo, OutstandingService service) {
        super("Ageing - Outstanding", null, response, "Outstanding");
        this.bean = vo;
        this.service = service;
    }

    @Override
    public void createData() {
        List<Object[]> arrList = new ArrayList<>();
        Object[] oar = null;
        if (null != bean.getParam10()) {
            switch (bean.getParam10()) {
                case "PARTYSUMMARY": {
                    List<ReportBean> dataList = service.receivable(bean,1);
                    for (ReportBean vo : dataList) {
                        oar = new Object[]{"", vo.getParam1(), Integer.parseInt(vo.getParam14() == null ? "0" : vo.getParam14()), Double.parseDouble(vo.getParam5()),
                            Double.parseDouble(vo.getParam6()),
                            vo.getParam11()};
                        arrList.add(oar);
                }
                    break;
                }
                case "PARTYDETAIL": {
                    List<ReportBean> dataList = service.getAgeingInvoicewise(bean);
                    for (ReportBean vo : dataList) {
                        oar = new Object[]{vo.getParam1(), vo.getParam19(), vo.getParam2(), Double.parseDouble(vo.getParam8()),
                            Double.parseDouble(vo.getParam9()), Double.parseDouble(vo.getParam10()),
                            Double.parseDouble(vo.getParam11()), Double.parseDouble(vo.getParam12()),
                            Double.parseDouble(vo.getParam13()), Double.parseDouble(vo.getParam14()),
                            vo.getParam17(), Double.parseDouble(vo.getParam3()), vo.getParam20(),
                            vo.getParam21(),vo.getParam22(),vo.getParam23()};
                        arrList.add(oar);
                    }
                    break;
                }
                case "SALESMANSUMMARY": {
                    List<ReportBean> dataList = service.receivable(bean);
                    for (ReportBean vo : dataList) {
                        oar = new Object[]{vo.getParam13(), vo.getParam1(), Integer.parseInt(vo.getParam14() == null ? "0" : vo.getParam14()), Double.parseDouble(vo.getParam5()),
                            Double.parseDouble(vo.getParam6()), Double.parseDouble(vo.getParam7()),
                            Double.parseDouble(vo.getParam8()), Double.parseDouble(vo.getParam9()),
                            Double.parseDouble(vo.getParam10()), Double.parseDouble(vo.getParam11())};
                        arrList.add(oar);
                    }
                    break;
                }
                case "SALESMANDETAIL": {
                    List<ReportBean> dataList = service.getAgeingInvoicewise(bean);
                    for (ReportBean vo : dataList) {
                        oar = new Object[]{vo.getParam1(), vo.getParam19(), vo.getParam2(), Double.parseDouble(vo.getParam8()),
                            Double.parseDouble(vo.getParam9()), Double.parseDouble(vo.getParam10()),
                            Double.parseDouble(vo.getParam11()), Double.parseDouble(vo.getParam12()),
                            Double.parseDouble(vo.getParam13()), Double.parseDouble(vo.getParam14()),
                            vo.getParam17(), Double.parseDouble(vo.getParam3()), vo.getParam20(), 
                            vo.getParam21(), vo.getParam22()};
                        arrList.add(oar);
                    }
                    break;
                }
                case "PARTYSUMMARY2": {
                    List<ReportBean> dataList = service.receivable2(bean);
                    for (ReportBean vo : dataList) {
                        oar = new Object[]{"", vo.getParam1(), Double.parseDouble(vo.getParam5()),
                            Double.parseDouble(vo.getParam6()), Double.parseDouble(vo.getParam11())};
                        arrList.add(oar);
                    }
                    break;
                }
                case "PARTYSUMMARY3": {
                    List<ReportBean> dataList = service.receivable3(bean);
                    for (ReportBean vo : dataList) {
                        oar = new Object[]{"", vo.getParam1(), Double.parseDouble(vo.getParam5()),
                            Double.parseDouble(vo.getParam6()), Double.parseDouble(vo.getParam7()),
                            Double.parseDouble(vo.getParam8()), Double.parseDouble(vo.getParam9()),
                            Double.parseDouble(vo.getParam10()), Double.parseDouble(vo.getParam11())};
                        arrList.add(oar);
                    }
                    break;
                }
                default:
                    break;
            }
        }
        this.data = arrList;
        //AbstractReport report = new AgeingXLSReport(parameters, response, vo)
    }

    @Override
    public void createHeader() {
        List<Object[]> arrList = new ArrayList<>();
        Object[] oar = null;
        if (null != bean.getParam10()) {
            switch (bean.getParam10()) {
                case "PARTYSUMMARY": {
                    oar = new Object[]{"", "PARTY", "CREDIT PERIOD",  "0-180",
                        "181-999", "TOTAL"};
                    break;
                }
                case "PARTYDETAIL": {
                    oar = new Object[]{"Party : ", bean.getParam1()};
                    arrList.add(oar);
                    oar = new Object[]{"Invoice No", "BL No", "Invoice Date", "0-15", "16-30", "31-60", "61-90", "91-180",
                        "181-999", "TOTAL", "Currency", "Exchange Rate", "Age", "Shipping Bill No","Export/Import","Sea/Air"};
                    break;
                }
                case "SALESMANSUMMARY": {
                    oar = new Object[]{"SALESMAN", "PARTY", "CREDIT PERIOD", "0-15", "16-30", "31-60", "61-90", "91-180",
                        "181-999", "TOTAL"};
                    break;
                }
                case "SALESMANDETAIL": {
                    oar = new Object[]{"Salesman :", bean.getParam7()};
                    arrList.add(oar);
                    oar = new Object[]{"Invoice No", "BL No", "Invoice Date", "0-15", "16-30", "31-60", "61-90", "91-180",
                        "181-999", "TOTAL", "Currency", "Exchange Rate", "Age", "Shipping Bill No","Export/Import"};
                    break;
                }
                case "PARTYSUMMARY2": {
                    oar = new Object[]{"", "PARTY", "0-180","181-365", "TOTAL"};
                    break;
                }
                case "PARTYSUMMARY3": {
                    oar = new Object[]{"Invoice No", "BL No", "Invoice Date", "0-60", "61-90", "91-120", "121-180", "181-365",
                        ">365", "TOTAL", "Currency", "Exchange Rate", "Age"};
                    break;
                }
                default:
                    break;
            }
        }
        arrList.add(oar);
        this.headerData = arrList;
        super.createHeader();
    }

    @Override
    public void createFooter() {
        List<Object[]> arrList = new ArrayList<>();
        this.sheetName = "Outstanding";
        Object[] oar = {"","","","=SUM(D2:D?)","=SUM(E2:E?)","=SUM(F2:F?)","=SUM(G2:G?)","=SUM(H2:H?)",
                "=SUM(I2:I?)","=SUM(J2:J?)"};
        
        arrList.add(oar);
        this.footerData = arrList;
        super.createFooter();
    }

}
