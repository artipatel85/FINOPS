/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.finance.report;

import com.finops.report.XLSReport;
import com.finops.report.model.ReportBean;
import com.finops.report.service.SalesRegisterService;
import com.finops.util.DateUtil;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author chiragkhetani
 */
public class SalesRegisterXLSReport extends XLSReport {
    private ReportBean vo;
    private String month;
    private SalesRegisterService service;

    public SalesRegisterXLSReport(ReportBean vo, HttpServletResponse response,
                                  SalesRegisterService service) {
        super(null, null, response, "GSTIN");
        this.service = service;
        try {
            this.month = DateUtil.monthFromDate(vo.getParam1());
        } catch (ParseException ex) {
            Logger.getLogger(SalesRegisterXLSReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.vo = vo;
    }

    @Override
    public void createHeader() {
        List<Object[]> arrList = new ArrayList<>();
        List<Object[]> preArrList = new ArrayList<>();
        Object[] headers = null;

        switch (vo.getIntparam1()) {
            case 99:
                headers = new Object[]{"Branch","Invoice No","Dated","Local/Foreign","Type","Taxable Amount","Non Taxable Amount",
                        "IGST","SGST","CGST","GSTIN","BL NO","PARTY","Sea/Air","Export/Import"};
                break;
            case 100:
                headers = new Object[]{"Branch","Invoice No","Dated","Local/Foreign","Type","Taxable Amount","Non Taxable Amount",
                        "IGST","SGST","CGST","Creditor Inv.No.","Dated","GSTIN NO","BL NO","PARTY","Sea/Air","Export/Import","DESCRIPTION"};
                break;
            case 1:
                headers = new Object[]{"GSTIN of the Recipient", "Name of Recipient",
                        "Invoice Number", "Invoice Date", "Invoice Value*", "Place of Supply*",
                        "Reverse Charge", "Applicable % of Tax Rate", "Invoice Type", "GSTIN E-commerce",
                        "Rate*", "Taxable Value", "Cess Amount", "POS"};
                break;
            case 2:
                headers = new Object[]{"Export Type", "Invoice Number",
                        "Invoice Date*", "Invoice Value*", "Port Code", "Shipping Bill Number",
                        "Shipping Bill Date", "Applicable % of Tax Rate", "Rate", "Taxable Value", "Cess Amount"};
                break;
            case 3:
                headers = new Object[]{"Description*", "Nil Rated Supplies*",
                        "Exempted (other than Nil rated/non-GST supply)*", "Non-GST supplies*"};
                break;
            case 4:
                headers = new Object[]{"Export Type", "Invoice Number",
                        "Invoice Date*", "Invoice Value*", "Port Code", "Shipping Bill Number",
                        "Shipping Bill Date", "Applicable % of Tax Rate", "Rate", "Taxable Value", "Cess Amount"};
                break;
            case 5:
                headers = new Object[]{"GSTIN of the Recipient", "Name of Recipient",
                        "Invoice Number", "Invoice Date", "Invoice Value*", "Place of Supply*",
                        "Reverse Charge", "Applicable % of Tax Rate", "Invoice Type", "GSTIN E-commerce", "Rate*", "Taxable Value",
                        "Cess Amount"};
                break;
            case 6:
                headers = new Object[]{"Credit", "Debit", "Account", "Branch"};
                break;
            case 7:
                headers = new Object[]{"Name of Recipient", "GSTIN of the Recipient"
                        , "Invoice Number", "Invoice Date", "Invoice Value*", "Place of Supply*",
                        "HSN", "Reverse Charge", "Invoice Type", "GSTIN E-commerce", "CGST Rate",
                        "SGST/UTGST Rate", "IGST Rate", "Rate*", "Taxable Value", "CGST Amount",
                        "SGST/UTGST Amount", "IGST Amount", "Cess Amount"};
                break;
            case 8:
                headers = new Object[]{"UR Type", "Original Note/Refund Voucher Number",
                        "Original Note/Refund Voucher date", "Original Invoice/Advance Receipt Number",
                        "Original Invoice/Advance Receipt date", "Revised Note/Refund Voucher Number",
                        "Revised Note/Refund Voucher date", "Document Type", "Supply Type"
                        , "Note/Refund Voucher Value", "Applicable % of Tax Rate",
                        "Rate", "Taxable Value", "Cess Amount", "Pre GST"};
                break;
            case 9:
                headers = new Object[]{"Voucher No", "Date", "IGST", "CGST", "SGST", "Invoice Number",
                        "GSTIN No", "Account Name"};
                break;
            case 10:
                headers = new Object[]{"HSN Code", "Total Value", "Taxable Value", "Rate of Tax", "Central Tax", "State Tax / UT Tax",
                        "Integrated Tax", "Cess"};
                break;
            case 11:
                headers = new Object[]{"HSN Code", "Total Value", "Taxable Value", "Rate of Tax", "Central Tax", "State Tax / UT Tax",
                        "Integrated Tax", "Cess"};
                break;
            case 12:
                headers = new Object[]{"Description*", "Nil Rated Supplies*",
                        "Exempted (other than Nil rated/non-GST supply)*", "Non-GST supplies*", "Invoice Number"};
                break;
            case 13:
                headers = new Object[]{"S.NO", "DESCRIPTION /NATURE OF EXPENSE", "VALUE",
                        "INPUT TAX CREDIT AMOUNT  IGST", "INPUT TAX CREDIT AMOUNT  CGST", "INPUT TAX CREDIT AMOUNT  SGST"};
                break;
        }
        arrList.add(headers);
        this.headerData = arrList;
        super.createHeader();
    }

    @Override
    public void createPreHeader() {
        List<Object[]> preArrList = new ArrayList<>();
        Object[] headers = null;

        switch (vo.getIntparam1()) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                headers = new Object[]{"Summary For EXP(6)"};
                preArrList.add(headers);
                headers = new Object[]{"", "No. of Invoices", "", "Total Invoice Value", "", "No. of Shipping Bill", "", "", "", "", "Total Taxable Value"};
                preArrList.add(headers);
                headers = new Object[]{"", "=SUMPRODUCT((B5:B1400<>\"\")/COUNTIF(B5:B1400,B5:B1400&\"\"))", "", "=SUM(D5:D1500)", "", 0, "'", "", "", "", "=SUM(J5:J1500)"};
                preArrList.add(headers);
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
            case 11:
                break;
            case 12:
                break;
            case 13:
                break;
        }
        this.preHeaderData = preArrList;
        super.createPreHeader();
    }

    @Override
    public void createData() {
        switch (vo.getIntparam1()) {
            case 99:
                invoiceData();
                break;
            case 100:
                expenseData();
                break;
            case 1:
                b2b();
                break;
            case 2:
                b2c();
                break;
            case 3:
                exempted();
                break;
            case 4:
                export();
                break;
            case 5:
                local();
                break;
            case 6:
                journalInput();
                break;
            case 7:
                debitNote();
                break;
            case 8:
                creditNote();
                break;
            case 9:
                journalVouchers();
                break;
            case 10:
                sacInwardSupply();
                break;
            case 11:
                sacOutwardSupply();
                break;
            case 12:
                exemptedWithInvoiceNo();
                break;
            case 13:
                expenseHeaderGST();
                break;
        }
    }

    private void expenseData() {
        List<ReportBean> dataList = service.expense(vo);
        List<Object[]> arrList = new ArrayList<Object[]>();
        for(ReportBean svo : dataList){
            Object[] lines = new Object[]{svo.getParam3(),svo.getParam7(),svo.getParam8(),svo.getParam9(),svo.getParam13(),
                    svo.getBigparam1(),svo.getBigparam2(),svo.getBigparam3(),svo.getBigparam4(),svo.getBigparam5(),
                    svo.getParam10(),svo.getParam11(),svo.getParam14(),svo.getParam15(),svo.getParam12(),svo.getParam16(),
                    svo.getParam17(),svo.getParam18()};
            arrList.add(lines);
        }

        this.data = arrList;
    }

    private void invoiceData() {
        List<ReportBean> dataList = service.invoice(vo);
        List<Object[]> arrList = new ArrayList<Object[]>();

        for(ReportBean svo : dataList){
            Object[] lines = new Object[]{svo.getParam3(),svo.getParam7(),svo.getParam8(),svo.getParam9(),svo.getParam11(),
                    svo.getBigparam1(),svo.getBigparam2(),svo.getBigparam21(),svo.getBigparam22(),svo.getBigparam23(),svo.getParam12(),
                    svo.getParam13(),svo.getParam10(),svo.getParam14(), svo.getParam15()};
            arrList.add(lines);
        }

        this.data = arrList;
    }

    private void b2b() {
        List<ReportBean> dataList = service.gstinReport(vo, "B2B");
        List<Object[]> arrList = new ArrayList<Object[]>();
        this.fileName = "B2B-" + vo.getParam3() + "-" + month + "-INV";
        for (ReportBean svo : dataList) {
            Object[] lines = new Object[]{svo.getParam2(), svo.getParam1(), svo.getParam3(), svo.getParam4(),
                    Double.parseDouble(svo.getParam5()), svo.getParam6(), svo.getParam8(), "", svo.getParam9(),
                    svo.getParam10(), Double.parseDouble(svo.getParam14()),
                    Double.parseDouble(svo.getParam15()), svo.getParam19(), svo.getParam20()};
            arrList.add(lines);
        }
        this.data = arrList;

    }

    private void b2c() {
        List<ReportBean> dataList = service.gstinReport(vo, "B2C");
            List<Object[]> arrList = new ArrayList<Object[]>();
            this.fileName = "B2C-" + vo.getParam3() + "-" + month;
            for (ReportBean svo : dataList) {
                Object[] lines = new Object[]{svo.getParam1(), svo.getParam3(), svo.getParam4(),
                        svo.getBigparam1(), svo.getParam7(), svo.getParam8(), svo.getParam9(),
                        "", Double.parseDouble(svo.getParam10()), new BigDecimal(svo.getParam11()),
                        svo.getParam13()};
                arrList.add(lines);
            }

            this.data = arrList;

    }

    private void b2cOld() {
            List<ReportBean> dataList = service.gstinReport(vo,"B2C");
            List<Object[]> arrList = new ArrayList<Object[]>();
            this.fileName = "B2C-" + vo.getParam3() + "-" + month;
            for (ReportBean svo : dataList) {
                Object[] lines = new Object[]{svo.getParam1(), svo.getParam2(), svo.getParam3(), svo.getParam4(),
                        Double.parseDouble(svo.getParam5()), svo.getParam6(), svo.getParam7(), svo.getParam8(), svo.getParam9(),
                        svo.getParam10(), svo.getParam11(), svo.getParam12(), svo.getParam13(), Double.parseDouble(svo.getParam14()),
                        Double.parseDouble(svo.getParam15()), Double.parseDouble(svo.getParam16()),
                        Double.parseDouble(svo.getParam17()), Double.parseDouble(svo.getParam18()), svo.getParam19()};
                arrList.add(lines);
            }
            this.data = arrList;

    }

    private void exempted() {

            List<ReportBean> dataList = service.gstinReport(vo, "EXEMPTED");
            List<Object[]> arrList = new ArrayList<Object[]>();
            this.fileName = "EXEMPTED-NILL-" + vo.getParam3() + "-" + month;
            for (ReportBean svo : dataList) {
                Object[] lines = new Object[]{svo.getParam1(),
                        Double.parseDouble(svo.getParam3()), Double.parseDouble(svo.getParam4()),
                        Double.parseDouble(svo.getParam5())};
                arrList.add(lines);
            }

            this.data = arrList;

    }

    private void export() {
        List<ReportBean> dataList = service.gstinReport(vo,"EXPORT-SEZ");
            List<Object[]> arrList = new ArrayList<Object[]>();
            this.fileName = "EXPORT-SEZ-" + vo.getParam3() + "-" + month;
            for (ReportBean svo : dataList) {
                Object[] lines = new Object[]{svo.getParam1(), svo.getParam3(), svo.getParam4(),
                        svo.getBigparam1(), svo.getParam7(), svo.getParam8(), svo.getParam9(),
                        "", Double.parseDouble(svo.getParam10()), new BigDecimal(svo.getParam11()),
                        svo.getParam13()};
                arrList.add(lines);
            }
            this.data = arrList;
    }

    private void local() {
            List<ReportBean> dataList = service.gstinReport(vo, "LOCAL-SEZ");
            List<Object[]> arrList = new ArrayList<Object[]>();
            this.fileName = "LOCAL-SEZ-" + vo.getParam3() + "-" + month;
            for (ReportBean svo : dataList) {
                Object[] lines = new Object[]{svo.getParam2(), svo.getParam1(), svo.getParam3(), svo.getParam4(),
                        Double.parseDouble(svo.getParam5()), svo.getParam6(), svo.getParam8(), "", svo.getParam9(),
                        svo.getParam10(), Double.parseDouble(svo.getParam14()),
                        Double.parseDouble(svo.getParam15()), svo.getParam19()};
                arrList.add(lines);
            }

            this.data = arrList;
    }

    private void exemptedWithInvoiceNo() {
            List<ReportBean> dataList = service.gstinReport(vo, "EXEMPTED-INVOICE");
            List<Object[]> arrList = new ArrayList<Object[]>();
            this.fileName = "EXEMPTED-NILL-Invoice-" + vo.getParam3() + "-" + month;
            for (ReportBean svo : dataList) {
                Object[] lines = new Object[]{svo.getParam1(),
                        Double.parseDouble(svo.getParam3()), Double.parseDouble(svo.getParam4()),
                        Double.parseDouble(svo.getParam5()), svo.getParam7()};
                arrList.add(lines);
            }

            this.data = arrList;
    }

    private void journalInput() {

            List<ReportBean> dataList = service.gstinReport(vo, "JOURNAL-INPUT");
            List<Object[]> arrList = new ArrayList<Object[]>();
            this.fileName = "Journal-Input-" + vo.getParam3() + "-" + month;
            for (ReportBean svo : dataList) {
                Object[] lines = new Object[]{Double.parseDouble(svo.getParam1()), Double.parseDouble(svo.getParam2()),
                        svo.getParam3(), svo.getParam4()};
                arrList.add(lines);
            }

            this.data = arrList;

    }

    private void debitNote() {
            List<ReportBean> dataList = service.gstinReport(vo, "DEBIT-NOTE");
            List<Object[]> arrList = new ArrayList<Object[]>();
            this.fileName = "DEBITNOTE-" + vo.getParam3() + "-" + month + "-INV";
            for (ReportBean svo : dataList) {
                Object[] lines = new Object[]{svo.getParam1(), svo.getParam2(), svo.getParam3(), svo.getParam4(),
                        Double.parseDouble(svo.getParam5()), svo.getParam6(), svo.getParam7(), svo.getParam8(), svo.getParam9(),
                        svo.getParam10(), svo.getParam11(), svo.getParam12(), svo.getParam13(), Double.parseDouble(svo.getParam14()),
                        Double.parseDouble(svo.getParam15()), Double.parseDouble(svo.getParam16()),
                        Double.parseDouble(svo.getParam17()), Double.parseDouble(svo.getParam18()), svo.getParam19()};
                arrList.add(lines);
            }
            this.data = arrList;
    }

    private void creditNote() {
            List<ReportBean> dataList = service.gstinReport(vo, "CREDIT-NOTE");
            List<Object[]> arrList = new ArrayList<Object[]>();
            this.fileName = "CREDITNOTE-" + vo.getParam3() + "-" + month + "-INV";
            for (ReportBean svo : dataList) {
                Object[] lines = new Object[]{svo.getParam1(), svo.getParam2(), svo.getParam3(), svo.getParam4(),
                        svo.getParam5(), svo.getParam6(), svo.getParam7(),
                        svo.getParam8(), svo.getParam9(), Double.parseDouble(svo.getParam10()),
                        Double.parseDouble(svo.getParam11()), Double.parseDouble(svo.getParam12()),
                        Double.parseDouble(svo.getParam13()), Double.parseDouble(svo.getParam14()), svo.getParam15()};
                arrList.add(lines);
            }
            this.data = arrList;
    }

    private void journalVouchers() {
            List<ReportBean> dataList = service.gstinReport(vo, "JOURNAL-VOUCHER");
            List<Object[]> arrList = new ArrayList<Object[]>();
            this.fileName = "JOURNAL-VOUCHER-" + vo.getParam3() + "-" + month + "-INV";
            for (ReportBean svo : dataList) {
                Object[] lines = new Object[]{svo.getParam1(), svo.getParam2(), Double.parseDouble(svo.getParam3()),
                        Double.parseDouble(svo.getParam4()), Double.parseDouble(svo.getParam5()), svo.getParam6(),
                        svo.getParam7(), svo.getParam8()};
                arrList.add(lines);
            }
            this.data = arrList;
    }

    private void sacInwardSupply() {
            List<ReportBean> dataList = service.gstinReport(vo, "HSN-INWARD");
            List<Object[]> arrList = new ArrayList<Object[]>();
            this.fileName = "SAC-INWARD-SUPPLY-" + vo.getParam3() + "-" + month + "-INV";
            for (ReportBean svo : dataList) {
                Object[] lines = new Object[]{svo.getParam1(), Double.parseDouble(svo.getParam2()),
                        Double.parseDouble(svo.getParam3()), Double.parseDouble(svo.getParam4()), Double.parseDouble(svo.getParam5()),
                        Double.parseDouble(svo.getParam6()), Double.parseDouble(svo.getParam7()), ""};
                arrList.add(lines);
            }
            this.data = arrList;
    }

    private void sacOutwardSupply() {
            List<ReportBean> dataList = service.gstinReport(vo, "HSN-OUTWARD");
            List<Object[]> arrList = new ArrayList<Object[]>();
            this.fileName = "SAC-OUTWARD-SUPPLY-" + vo.getParam3() + "-" + month + "-INV";
            for (ReportBean svo : dataList) {
                Object[] lines = new Object[]{svo.getParam1(), Double.parseDouble(svo.getParam2()),
                        Double.parseDouble(svo.getParam3()), Double.parseDouble(svo.getParam4()), Double.parseDouble(svo.getParam5()),
                        Double.parseDouble(svo.getParam6()), Double.parseDouble(svo.getParam7()), ""};
                arrList.add(lines);
            }
            this.data = arrList;

    }

    private void expenseHeaderGST() {

            int i = 1;
            List<ReportBean> dataList = service.gstinReport(vo, "EXPENSE-GST");
            List<Object[]> arrList = new ArrayList<Object[]>();
            this.fileName = "ExpensHeaderGST-" + vo.getParam3() + "-" + month + "-INV";
            for (ReportBean svo : dataList) {
                Object[] lines = new Object[]{i++, svo.getParam1(), Double.parseDouble(svo.getParam2()),
                        Double.parseDouble(svo.getParam3()), Double.parseDouble(svo.getParam4()), Double.parseDouble(svo.getParam5())};
                arrList.add(lines);
            }
            this.data = arrList;

    }
}
