package com.finops.report.service;

import com.finops.report.dao.TrialDAO;
import com.finops.report.model.ReportBean;
import com.finops.util.Constants;
import com.finops.util.DateUtil;
import com.finops.util.FinanceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrialService {

    @Autowired
    private TrialDAO trialDAO;

    public List<ReportBean> findTrialRecordsByPage(ReportBean bean) {
        return trialDAO.findTrialRecordsByPage(bean);
    }

    public List<ReportBean> monthlySummaryData(ReportBean bean) {
        List<ReportBean> reportBeanList = new ArrayList<>();

        String href = "Trasactions.fin?month=0&openingBalance=0";
        ReportBean rb = new ReportBean();
        rb.setIntparam1(0);
        rb.setParam1("<b><a href='" + href + "'> Full Report </a></b>");
        rb.setParam4("<b>" + FinanceUtil.formatNegativeAmount(bean.getParam2()) + "</b>");
        reportBeanList.add(rb);
        Double openingBalance = Double.parseDouble(bean.getParam2());

        List<ReportBean> reportBeanList2 = trialDAO.monthlySummaryData(bean);
        String closing = "0.00";
        double drTotal = 0.00;
        double crTotal = 0.00;
        String type = "nonBank";
        String prevClosing = FinanceUtil.formatBigDecimal(openingBalance,2);

        for (ReportBean rb1 : reportBeanList2) {

            int parentId = rb1.getIntparam2();
            double dr = rb1.getDoubleParam1();
            double cr = rb1.getDoubleParam2();
            closing = FinanceUtil.closingBalance(openingBalance, dr, cr);
            openingBalance = Double.parseDouble(closing);
            int month = rb1.getIntparam3();
            href = "Trasactions.fin?month=" + month + "&openingBalance="+prevClosing;

            rb1.setParam1("<a href='" + href + "'>" + new DateFormatSymbols().getMonths()[month - 1] + "</a>");
            rb1.setParam2(FinanceUtil.formatBigDecimal(dr, 2));
            rb1.setParam3(FinanceUtil.formatBigDecimal(cr, 2));
            rb1.setParam4(FinanceUtil.formatNegativeAmount(closing));
            if (parentId == 18 || parentId == 20 || parentId == 28 || parentId == 30) {
                type = "cashBank";
            } else {
                type = "nonBank";
            }
            drTotal += dr;
            crTotal += cr;
            prevClosing = closing;
        }

        reportBeanList.addAll(reportBeanList2);
        if (reportBeanList2.size() == 0) {
            closing = Double.toString(openingBalance);
        }

        rb = new ReportBean();
        rb.setParam1("<b>Net Balance</b>");
        rb.setParam2("<b><u>" + FinanceUtil.formatBigDecimal(drTotal, 2) + "</u></b>");
        rb.setParam3("<b><u>" + FinanceUtil.formatBigDecimal(crTotal, 2) + "</u></b>");
        rb.setParam4("<b><u>" + FinanceUtil.formatNegativeAmount(closing) + "</u></b>");
        rb.setIntparam1(100);
        reportBeanList.add(rb);
        return reportBeanList;
    }

    public ReportBean setupMonthlyData(ReportBean bean) {
        int month = bean.getIntparam1();
        if (bean.getParam4() == null || bean.getParam4().isEmpty()) {
            int acctYear = bean.getAcctYear();

            int year = acctYear / 10000;
            if (month == 0) {
                bean.setParam4(bean.getYrStartDate());
                bean.setParam5(bean.getYrEndDate());
            } else {
                if (month < 4) {
                    year++;
                }

                bean.setParam4(DateUtil.firstDateOfMonth(year, month-1));
                bean.setParam5(DateUtil.lastDateOfMonth(year, month - 1));
            }
        }
        if (bean.getParam1() != null && !bean.getParam1().isEmpty()) {
            ReportBean inputBean = trialDAO.setupMonthlyData(bean);
            if(month != 0) {
                bean.setParam9(bean.getParam10());
            }
            else{
                bean.setParam9(inputBean.getParam9());
            }
            bean.setParam8(inputBean.getParam8());
        }
        return bean;
    }

    public List<ReportBean> findTransactionsByPage(ReportBean bean) {
        List<ReportBean> finalBeanList = new ArrayList<>();
        double opening = Double.valueOf(bean.getParam9());
        ReportBean rb = new ReportBean();
        rb.setParam3("OPENING BALANCE");
        if (opening > 0) {
            rb.setParam7(bean.getParam9());
            rb.setParam8("0.00");
        } else {
            rb.setParam8(FinanceUtil.formatNegativeAmount2(bean.getParam9()));
            rb.setParam7("0.00");
        }
        finalBeanList.add(rb);

        finalBeanList.addAll(trialDAO.findTransactionsByPage(bean));
        rb = trialDAO.getSum(bean);
        System.out.println(rb.getDoubleParam1());
        double dr = rb.getDoubleParam1();
        double cr = rb.getDoubleParam2();
        double closing = FinanceUtil.closingBalance2(opening, dr, cr);
        System.out.println("OUT");
        // Debit-Credit total
        if (opening > 0) {
            dr += opening;
        } else {
            cr += opening * -1;
        }
        rb = new ReportBean();
        rb.setParam3("TOTAL");
        rb.setParam7(FinanceUtil.formatBigDecimal(dr, 2));
        rb.setParam8(FinanceUtil.formatBigDecimal(cr, 2));
        finalBeanList.add(rb);

        rb = new ReportBean();
        rb.setParam3("CLOSING BALANCE");
        if (closing > 0) {
            rb.setParam7(FinanceUtil.formatBigDecimal(closing, 2));
            rb.setParam8("0.00");
        } else {
            rb.setParam8(FinanceUtil.formatBigDecimal((closing * -1), 2));
            rb.setParam7("0.00");
        }
        rb.setParam12(createUrl(rb));
        finalBeanList.add(rb);

        return  finalBeanList;
    }

    private String createUrl(ReportBean dto) {
        String url = "#";
        String hdrId = dto.getParam9();
        String source = dto.getParam4();
        if ("PAYMENT".equals(source)) {
            url = "retrieveVoucherRow.fin?hdrId=" + hdrId;
        } else if ("RECEIPT".equals(source)) {
            url = "retrieveVoucherRow.fin?hdrId=" + hdrId;
        } else if ("CONTRA".equals(source)) {
            url = "retrieveVoucherRow.fin?hdrId=" + hdrId;
        } else if ("CREDIT".equals(source)) {
            url = "paymentVoucher.do?invoke=retrievePayment&param=CREDIT&jeHdrId=" + hdrId;
        } else if ("DEBIT".equals(source)) {
            url = "paymentVoucher.do?invoke=retrievePayment&param=DEBIT&jeHdrId=" + hdrId;
        } else if ("JOURNAL".equals(source)) {
            url = "retrievejournal.fin?hdrId=" + hdrId;
        } else if ("INVOICE".equals(source) || "EXPENSE".equals(source) || "CREDITNOTE".equals(source)
                || "MISC".equals(source) || "DEBITNOTE".equals(source) || Constants.BILL_OF_SUPPLY.equals(source)) {
            //url = "billing.do?invoke=retrieve&trxid=" + dto.getParam11();
            String mid = "EXPENSE".equals(source) ? source : "REVENUE";

            url = "retrieveBill.fin?param=" + mid + "&billId=" + dto.getParam11();
        }
        return url;
    }

    @Transactional
    public void ledgerMatch(ReportBean bean) {
        for (String id : bean.getUuids()) {
            String[] idArr = id.split("\\|");
            trialDAO.ledgerMatch(bean, idArr);
        }
    }

    public List<ReportBean> paymentAdvise(ReportBean bean) {
        return trialDAO.paymentAdvise(bean);
    }

    public List<ReportBean> trialPartywise(ReportBean vo) {
        return trialDAO.trialPartywise(vo);
    }

    public List<ReportBean> trialBranchwise(ReportBean vo) {
        return trialDAO.trialBranchwise(vo);
    }

    public List<ReportBean> tdsRegisterData(ReportBean reportBean, boolean isPayable) {
        return trialDAO.tdsRegisterData(reportBean, isPayable);
    }

    public void settleOpeningBalance(ReportBean reportBean) {
        trialDAO.settleOpeningBalance(reportBean);
    }

    public ReportBean bcl(ReportBean bean) {
        return trialDAO.bcl(bean);
    }
}
