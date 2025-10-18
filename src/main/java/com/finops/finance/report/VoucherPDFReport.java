package com.finops.finance.report;

import com.finops.finance.bean.VoucherBean;
import com.finops.finance.service.VoucherService;
import com.finops.util.ApplicationUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Map;


public class VoucherPDFReport extends PDFJDBCConnectionReport {

    private final VoucherBean voucherBean;
    private VoucherService service;

    public VoucherPDFReport(String reportPath, VoucherBean bean, Map params,
                            HttpServletResponse response, ServletContext context,
                            HttpSession session, VoucherService service) {
        super(reportPath, params, response, context);
        this.voucherBean = bean;
        this.service = service;
        this.jdbcTemplate = service.getJDBCTemplate();
    }

    public void createData() {
        VoucherBean vb = service.retrieveVoucherRecord(voucherBean, true);

        this.parameters.put("je_voucher_no", vb.getVoucherNo());
        this.parameters.put("je_date", vb.getJeDate());
        this.parameters.put("acct_year", vb.getAcctYear());
        this.parameters.put("company_name", voucherBean.getCompanyName());
        this.parameters.put("entered_dr", vb.getEnteredDr()+"");
        this.parameters.put("entered_cr", vb.getEnteredCr()+"");
        this.parameters.put("remarks", vb.getRemarks());
        this.parameters.put("ADDRESS", voucherBean.getCompanyAddress());
        this.parameters.put("CASH_BANK", vb.getAcctName());

        double phrase = vb.getEnteredDr();
        if("PAYMENT".equalsIgnoreCase(voucherBean.getVoucherType())){
            phrase = vb.getEnteredCr();
        }
        String s = "";
        int dollars = (int) Math.floor(phrase);
        double midval = Math.round((phrase - dollars) * 100.0) / 100.0;
        int cent = (int) Math.floor((midval * 100.00f));
        if (cent > 0) {
            s = vb.getCurrencyName() + " " + ApplicationUtil.convertNumToWord(dollars) + " and " + " " + ApplicationUtil.convertNumToWord(cent) + " " + vb.getCurrencyNumeral();
        } else {
            s = vb.getCurrencyName() + " " + ApplicationUtil.convertNumToWord(dollars);
        }

        this.parameters.put("CHANGEWORD", s.toUpperCase());
    }

}
