package com.finops.finance.service;

import com.finops.aop.MeasureTime;
import com.finops.finance.bean.InvoiceBean;
import com.finops.finance.bean.VoucherBean;
import com.finops.finance.bean.VoucherRow;
import com.finops.finance.dao.VoucherDAO;
import com.finops.finance.util.VoucherTypeEnum;
import com.finops.report.model.ReportBean;
import com.finops.util.DateUtil;
import com.finops.util.FinanceUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class VoucherService {

    @Autowired
    private VoucherDAO voucherDAO;

    public List<VoucherBean> findVouchersByPage(VoucherBean bean, String param) {
        return voucherDAO.findVouchersByPage(bean, param);
    }

    public VoucherBean retrieveVoucherRecord(VoucherBean bean, boolean hdrOnlyFlag) {
        List<VoucherBean> voucherBeans = voucherDAO.fetchVoucherRecord(bean, hdrOnlyFlag);
        VoucherBean targetBean = new VoucherBean();
        double lineAmountTotal = 0;
        List<VoucherRow> voucherRows = new ArrayList<>();
        for (VoucherBean vb : voucherBeans) {

            if ("H".equalsIgnoreCase(vb.getHdrLineFlag())) {
                BeanUtils.copyProperties(vb, targetBean);
                if ("RECEIPT".equalsIgnoreCase(vb.getVoucherType())) {
                    targetBean.setHdrTotalAmount(vb.getEnteredDr());
                } else {
                    targetBean.setHdrTotalAmount(vb.getEnteredCr());
                }
                targetBean.setHdrAcctName(vb.getAcctName());
            } else {
                VoucherRow row = new VoucherRow();
                row.setRowAcct(vb.getAcctName());
                row.setRowAcctId(vb.getCodeCombinationId());
                row.setRowRem(vb.getRemarks());
                row.setRowChq(vb.getChqNo());
                row.setRowChqDt(vb.getChqDate());
                if ("RECEIPT".equalsIgnoreCase(vb.getVoucherType())) {
                    row.setRowAmt(vb.getEnteredCr());
                } else {
                    row.setRowAmt(vb.getEnteredDr());
                }


                lineAmountTotal += row.getRowAmt();
                voucherRows.add(row);
                targetBean.setVoucherType(vb.getVoucherType());
                targetBean.setCurrencyId(vb.getCurrencyId());
                targetBean.setCurrencyName(vb.getCurrencyName());
                targetBean.setSeaAir(vb.getSeaAir());
                targetBean.setInvNo(vb.getInvNo());
                targetBean.setInvDate(vb.getInvDate());
                targetBean.setVoucherNo(vb.getVoucherNo());
                targetBean.setJeHdrId(vb.getJeHdrId());
                targetBean.setJeDate(vb.getJeDate());
                targetBean.setExchangeRate(vb.getExchangeRate());
            }
        }
        targetBean.setVoucherRows(voucherRows);
        targetBean.setLineTotalAmount(lineAmountTotal);

        return targetBean;
    }

    public VoucherBean retrieveVoucherRecord(VoucherBean bean) {
        List<VoucherBean> voucherBeans = voucherDAO.fetchVoucherRecord(bean, false);
        VoucherBean targetBean = new VoucherBean();
        double lineAmountTotal = 0;
        List<VoucherRow> voucherRows = new ArrayList<>();
        for (VoucherBean vb : voucherBeans) {
            VoucherRow row = new VoucherRow();
            row.setRowAcct(vb.getAcctName());
            row.setRowAcctId(vb.getCodeCombinationId());
            row.setRowRem(vb.getRemarks());
            row.setRowChq(vb.getChqNo());
            row.setRowChqDt(vb.getChqDate());
            row.setDebit(vb.getEnteredDr());
            row.setCredit(vb.getEnteredCr());
            row.setSo(vb.getSoNo());
            row.setBl(vb.getBlNo());
            if (vb.getEnteredDr() > 0) {
                row.setRowDrCr("DR");
            } else {
                row.setRowDrCr("CR");
            }
            voucherRows.add(row);
            targetBean.setVoucherType(vb.getVoucherType());
            targetBean.setCurrencyId(vb.getCurrencyId());
            targetBean.setCurrencyName(vb.getCurrencyName());
            targetBean.setSeaAir(vb.getSeaAir());
            targetBean.setExpImp(vb.getExpImp());
            targetBean.setInvNo(vb.getInvNo());
            targetBean.setInvDate(vb.getInvDate());
            targetBean.setVoucherNo(vb.getVoucherNo());
            targetBean.setJeHdrId(vb.getJeHdrId());
            targetBean.setJeDate(vb.getJeDate());
            targetBean.setExchangeRate(vb.getExchangeRate());
            targetBean.setLineTotalAmount(vb.getLineTotalAmount());
            targetBean.setHdrTotalAmount(vb.getHdrTotalAmount());
            targetBean.setRemarks(vb.getRemarks());
            targetBean.setSeqNo(vb.getSeqNo());
        }
        targetBean.setVoucherRows(voucherRows);

        return targetBean;
    }

    @MeasureTime
    @Transactional
    public void save(VoucherBean bean) {
            if (bean.getJeHdrId() > 0) {
                voucherDAO.delete(bean.getJeHdrId(), bean.getAcctYear(), bean.getVoucherType());
                for (VoucherRow row : bean.getVoucherRows()) {
                    voucherDAO.insert(bean, row);
                }
            } else {
                for (VoucherRow row : bean.getVoucherRows()) {
                    String voucherNo = generateVoucherNo(bean);
                    bean.setVoucherNo(voucherNo);
                    voucherDAO.insert(bean, row);
                }
            }

    }

    @Transactional
    private String generateVoucherNo(VoucherBean bean) {
        String datePart = null;
        try {
            datePart = DateUtil.getMMDD(bean.getJeDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String typePart = bean.getVoucherType().substring(0, 1);
        int jeSeqPart = voucherDAO.generateAutoNumber(
                "SELECT max(je_seq)+1 FROM gl_je_f " +
                        "WHERE je_date = '" + bean.getJeDate() + "' AND je_source = '" + bean.getVoucherType() + "' AND acct_year = " + bean.getAcctYear() + " " +
                        "AND ATTRIBUTE2 = '" + bean.getLoadingAgent() + "' AND HDR_LINE_FLAG='H' ", 1);
        bean.setSeqNo(jeSeqPart);
        return bean.getLoadingAgent() + datePart + typePart + jeSeqPart;
    }

    public boolean isVoucherDateValid(VoucherBean voucherBean) throws ParseException {
        if (!DateUtil.isDateInLimit(voucherBean.getJeDate(), voucherBean.getYrStartDate(),
                voucherBean.getYrEndDate())) {
            voucherBean.setErrMsg("Voucher Date is outside the range!");
            return false;
        }
        if (voucherBean.getJeHdrId() > 0 &&
                (!voucherBean.getVoucherNo().contains
                        (voucherBean.getJeDate().replaceAll("-", "").substring(4)))) {
            voucherBean.setErrMsg("Voucher Date and Voucher Number do not match!");
            return false;
        }
        return true;
    }

    public boolean shouldSaveVoucher(VoucherBean bean) throws SQLException {
//        if (bean.isAdministrator()) {
//            return true;
//        }
        try {
            if (!DateUtil.isDateInLimit(bean.getJeDate(), bean.getCutOffDate(), bean.getYrEndDate())) {
                bean.setErrMsg("Voucher can not be updated. Cutoffdate " + bean.getCutOffDate() + "!");
                return false;
            }
        } catch (ParseException ex) {
            bean.setErrMsg("Voucher can not be updated. Cutoffdate " + bean.getCutOffDate() + "!");
            return false;
        }

        if (bean.getJeHdrId() > 0) {
            VoucherBean rb = voucherDAO.getVoucherData(bean);
            String type = bean.getVoucherType();
            if (rb == null) {
                bean.setErrMsg("Voucher can not be updated. Cutoffdate " + bean.getCutOffDate() + "!");
                return false;
            } else if (!rb.getBranch().equals(bean.getLoadingAgent())) {
                bean.setErrMsg("Voucher can not be updated. Voucher Branch " + rb.getBranch() + "!");
                return false;
            } else if (StringUtils.hasText(rb.getMatchingRefNo())) {
                bean.setErrMsg("Matching is alreday done for this voucher. Matching string is " + rb.getMatchingRefNo() + "!");
                return false;
            }

            if ("PAYMENT".equals(type) || "RECEIPT".equals(type)) {
                if (StringUtils.hasText(rb.getReconcileDate())) {
                    bean.setErrMsg("Voucher can not be updated. Reconcile date " + rb.getReconcileDate() + "!");
                    return false;
                }
            }
        }
        return true;
    }

    public boolean validate(VoucherBean voucherBean) throws ParseException {
        List<VoucherRow> voucherRows = voucherBean.getVoucherRows();
        for (VoucherRow vr : voucherRows) {
            if (!StringUtils.hasText(vr.getRowAcct()) || vr.getRowAcctId() == 0 || vr.getRowAmt() == 0) {
                vr.setErrMsg("Invalid Row.");
                return false;
            }
        }
        return true;
    }

    public boolean isVoucherTotalValid(VoucherBean voucherBean) throws ParseException {
        if (voucherBean.getHdrTotalAmount() != voucherBean.getLineTotalAmount()) {
            voucherBean.setErrMsg("Invalid Voucher Total!");
            return false;
        }
        return true;
    }

    public JdbcTemplate getJDBCTemplate() {
        return voucherDAO.getJDBCTemplate();
    }

    public boolean isDuplicateInvNo(VoucherBean voucherBean) {
        return voucherDAO.isDuplicateInvNo(voucherBean);
    }

    public VoucherBean getVoucherData(VoucherBean bean) {
        return voucherDAO.getVoucherData(bean);
    }

    @Transactional
    public void saveJournalVoucher(VoucherBean bean) {
        if (bean.getJeHdrId() > 0) {
            voucherDAO.delete(bean.getJeHdrId(), bean.getAcctYear(), bean.getVoucherType());
        } else {
            String datePart = null;
            try {
                datePart = DateUtil.getMMDD(bean.getJeDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String typePart = bean.getVoucherType().substring(0, 1);
            int jeSeqPart = voucherDAO.generateAutoNumber(
                    "SELECT max(je_seq)+1 FROM gl_je_f " +
                            "WHERE je_date = '" + bean.getJeDate() + "' AND je_source = '" + bean.getVoucherType() + "' AND acct_year = " + bean.getAcctYear() + " " +
                            "AND ATTRIBUTE2 = '" + bean.getLoadingAgent() + "' AND HDR_LINE_FLAG='H' ", 1);
            bean.setSeqNo(jeSeqPart);
            String voucherNo = bean.getLoadingAgent() + datePart + typePart + jeSeqPart;
            bean.setVoucherNo(voucherNo);
        }

        List<Object[]> batchArgsList = new ArrayList<>();
        voucherDAO.insertJV(bean, batchArgsList);
    }

    public ReportBean getVoucherDataForBill(InvoiceBean bean) {
        return voucherDAO.getVoucherDataForBill(bean);
    }

    public void reimbursementVoucher(VoucherBean bean) {
        List<VoucherBean> voucherBeans = voucherDAO.fetchVoucherRecord(bean, false);
        List<VoucherRow> voucherRows  = new ArrayList<>();
        int i = 1;
        for(VoucherBean vb : voucherBeans){
            if("H".equalsIgnoreCase(vb.getHdrLineFlag())){
                bean.setVoucherNo(vb.getVoucherNo());
                bean.setJeDate(vb.getJeDate());
                bean.setExchangeRate(vb.getExchangeRate());
                bean.setCurrencyId(vb.getCurrencyId());
                bean.setJeHdrId(vb.getJeHdrId());
                bean.setCurrencyName(vb.getCurrencyName());
                bean.setInvNo(vb.getInvNo());
                bean.setSeaAir(vb.getSeaAir());
                bean.setExpImp(vb.getExpImp());
                bean.setHdrTotalAmount(vb.getHdrTotalAmount());
                bean.setLineTotalAmount(vb.getLineTotalAmount());
                bean.setAddress1(vb.getAddress1());
                bean.setHdrAcctName(vb.getAcctName());
                bean.setGstinNo(vb.getGstinNo());
                bean.setHdrTotalAmount(vb.getEnteredDr() + vb.getEnteredCr());
                bean.setRemarks(vb.getRemarks());
            }
            else{
                VoucherRow row = new VoucherRow();
                row.setRowAcct(vb.getAcctName());
                row.setRowAcctId(vb.getCodeCombinationId());
                row.setCredit(vb.getEnteredCr());
                row.setDebit(vb.getEnteredDr());
                row.setSo(vb.getSoNo());
                row.setBl(vb.getBlNo());
                row.setSrNo(i++);
                if (row.getDebit() > 0) {
                    row.setRowDrCr("DR");
                } else {
                    row.setRowDrCr("CR");
                }
                voucherRows.add(row);
            }
        }
        bean.setVoucherRows(voucherRows);
    }

    @Transactional
    public void deleteVoucher(VoucherBean voucherBean) {
        for (String hdrId : voucherBean.getHdrIds()) {
            voucherDAO.delete(Integer.parseInt(hdrId), voucherBean.getAcctYear(), voucherBean.getVoucherType());
        }

    }
}
