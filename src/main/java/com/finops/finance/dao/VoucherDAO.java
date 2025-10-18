package com.finops.finance.dao;

import com.finops.aop.MeasureTime;
import com.finops.dao.AbstractDAO;
import com.finops.finance.bean.InvoiceBean;
import com.finops.finance.bean.VoucherBean;
import com.finops.finance.bean.VoucherRow;
import com.finops.report.model.ReportBean;
import com.finops.util.Constants;
import com.finops.util.sql.Condition;
import com.finops.util.sql.Query;
import com.finops.util.sql.QueryBuilder;
import com.finops.util.sql.QueryType;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class VoucherDAO extends AbstractDAO {

    @MeasureTime
    public List<VoucherBean> findVouchersByPage(VoucherBean bean, String param) {

        QueryBuilder findVoucherByPageQuery = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" je.je_voucher_no voucherNo,je.je_hdr_id,je.cash_bank,STR_TO_DATE(je_date,'%Y-%m-%d') je_date," +
                         "je.je_source,(je.accounted_dr+je.accounted_cr) hdrTotalAmount ,je.created_by,je.creation_date,je.amended_by" +
                         ",je.amended_date,cc1.acct_name hdrAcctName,cc2.acct_name lineAcctName,je.status,je.chq_no,je.chq_date,je.so_number, " +
                        "je.bl_no, je.sea_air, je.exp_imp, inv_no ")
                .FROM("gl_je_f", "je")
                .LEFT_JOIN("GL_CODE_COMBINATION_D", "CC1",
                        new Condition("JE.CODE_COMBINATION_ID", "CC1.CODE_COMBINATION_ID", Query.EQUALS))
                .LEFT_JOIN("GL_CODE_COMBINATION_D", "CC2",
                        new Condition("JE.CASH_BANK", "CC2.CODE_COMBINATION_ID", Query.EQUALS))
                .WHERE("je.je_source", "?")
                .AND("je.hdr_line_flag", "'H'")
                .AND("je.acct_year", "?")
                .AND("je.attribute2", "?");
        String[] fields = {"je_voucher_no", "je_date", "cc1.acct_name", "cc2.acct_name", "accounted_dr", "inv_no"};
        addFilter(findVoucherByPageQuery, bean, fields, Set.of("cc1.acct_name", "cc2.acct_name","je_voucher_no","inv_no"));
        addLimit("  je_hdr_id DESC ", findVoucherByPageQuery, bean);

        return jdbcTemplate.query(findVoucherByPageQuery.build().toString(),
                new BeanPropertyRowMapper<>(VoucherBean.class),
                param, bean.getAcctYear(), bean.getLoadingAgent());
    }


    public List<VoucherBean> fetchVoucherRecord(VoucherBean bean, boolean hdrOnlyFlag) {
        String query = "SELECT je.je_hdr_id, STR_TO_DATE(je_date,'%Y-%m-%d') je_date,"
                + "je.je_line_remarks remarks,je.hdr_line_flag,je.je_voucher_no voucherNo,"
                + "je.currency_id,je.entered_dr,entered_cr,je.exchange_rate,cur.currency_code currencyName,"
                + "je.chq_no, STR_TO_DATE(chq_date,'%Y-%m-%d') chq_date,cc.acct_name,"
                + "je.code_combination_id,je.je_source voucherType,je.acct_year,je.cash_bank lineCodeCombinationId,  " +
                "cc2.acct_name lineAcctName, prt.address1, cur.currency_numeral,je.attribute6 hdrTotalAmount, " +
                "je.attribute7 lineTotalAmount,je.so_number soNo, je.bl_no,je.sea_air, je.exp_imp, je.inv_no, " +
                "je.inv_date,IFNULL(je_seq,0) seqNo,prt.gstin_no " +
                "FROM gl_je_f je "
                + "LEFT OUTER JOIN currency_d cur ON (cur.currency_id = je.currency_id) "
                + "LEFT OUTER JOIN gl_code_combination_d cc ON (cc.code_combination_id = je.code_combination_id) "
                + "LEFT OUTER JOIN gl_code_combination_d cc2 ON (cc2.code_combination_id = je.cash_bank) " +
                " LEFT OUTER JOIN PRT_CONTACT_DETAILS_D prt ON (cc.code_combination_id = prt.code_combination_id) "
                + "WHERE je_hdr_id = ? AND acct_year = ? ";

        if(hdrOnlyFlag == true){
            query = query + " AND hdr_line_flag = 'H' ";
        }
        query = query + " ORDER BY hdr_line_flag ";

        return jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(VoucherBean.class),
                bean.getJeHdrId(), bean.getAcctYear());
    }


    public void insert(VoucherBean bean, VoucherRow row){
        List<Object[]> batchArgsList = new ArrayList<>();
        Integer jeHdrId = generateAutoNumber("SELECT MAX(je_hdr_id)+1 FROM gl_je_f ", 1);
        bean.setJeHdrId(jeHdrId);
        String requestType = bean.getVoucherType();
        double accountedAmount = bean.getExchangeRate() * row.getRowAmt();
        boolean ifDr = false;

        if ("RECEIPT".equals(requestType) || "DEBIT".equals(requestType)) {
            ifDr = true;
        }

        Object[] objects = {jeHdrId, 1, bean.getVoucherNo(), bean.getJeDate(), "H", null, bean.getAcctYear(), bean.getVoucherType(), 0,0,
                bean.getCodeCombinationId(), bean.getCurrencyId(), bean.getExchangeRate(), ifDr ? row.getRowAmt() : 0.00,
                ifDr ? 0.00 : row.getRowAmt(), ifDr ? accountedAmount : 0.00, ifDr ? 0.00 : accountedAmount,
                row.getRowRem(), bean.getCompanyId(), "A", bean.getUserId(), defaultNull(row.getRowChq()), defaultNull(row.getRowChqDt()), row.getRowAcctId(),
                "", bean.getBlNo(), 0.00, 0.00, bean.getLoadingAgent(), bean.getSeqNo(), bean.getSoNo(), bean.getInvNo(), bean.getSeaAir(), null, null, bean.getInvDate()};
        batchArgsList.add(objects);

        createLine(bean, 2, row, batchArgsList, ifDr);
        jdbcTemplate.batchUpdate(Constants.JE_INSERT, batchArgsList);

    }

    public void createLine(VoucherBean bean, int jeLineId,
                             VoucherRow row, List<Object[]> batchArgsList,
                             boolean ifDr) {
        double accountedAmount = bean.getExchangeRate() * row.getRowAmt();

        Object[] objectArray = {bean.getJeHdrId(), jeLineId, bean.getVoucherNo(), bean.getJeDate(), "L", null, bean.getAcctYear(),
                bean.getVoucherType(), 0, 0,
                row.getRowAcctId(), bean.getCurrencyId(), bean.getExchangeRate(), ifDr ? 0.00 : row.getRowAmt(),
                ifDr ? row.getRowAmt() : 0.00, ifDr ? 0.00 : accountedAmount, ifDr ? accountedAmount : 0.00,
                row.getRowRem(), bean.getCompanyId(), "A", bean.getUserId(), defaultNull(row.getRowChq()), defaultNull(row.getRowChqDt()), bean.getCodeCombinationId(),
                "", bean.getBlNo(), 0.00, 0.00, bean.getLoadingAgent(), bean.getSeqNo(), bean.getSoNo(), bean.getInvNo(), bean.getSeaAir(),
                null, null, bean.getInvDate()};

        batchArgsList.add(objectArray);
    }

    public void delete(int hdrId, int acctYear, String jeSource) {
        jdbcTemplate.update(
                "DELETE FROM gl_je_f WHERE je_hdr_id = ? AND acct_year = ? AND je_source = ?",
                hdrId,
                acctYear, jeSource
        );
    }

    public void deleteBySourceHdrId(int hdrId, int acctYear) {
        jdbcTemplate.update(
                "DELETE FROM gl_je_f WHERE je_source_hdr_id = ? AND acct_year = ?",
                hdrId,
                acctYear
        );
    }


    public VoucherBean getVoucherData(VoucherBean bean) {
        String query = "SELECT je_date, reconcile_date, attribute2 branch,  REFERENCE2 matchingRefNo " +
                "FROM gl_je_f WHERE je_hdr_id = ? AND hdr_line_flag=? "
                + "AND (je_date > ? OR reconcile_date is not null) AND je_source='"+bean.getVoucherType()+"' ";

        String hdrLineFlag = "L";
        if("JOURNAL".equalsIgnoreCase(bean.getVoucherType())){
            hdrLineFlag = "H";
        }

        return jdbcTemplate.queryForObject(query,
                new BeanPropertyRowMapper<>(VoucherBean.class),
                bean.getJeHdrId(), hdrLineFlag, bean.getCutOffDate());
    }

    public JdbcTemplate getJDBCTemplate() {
        return this.jdbcTemplate;
    }

    public boolean isDuplicateInvNo(VoucherBean bean) {
        String sql = "SELECT inv_no FROM gl_je_f WHERE INV_NO='" + bean.getInvNo() + "' ";
        if (!"AUTO".equals(bean.getVoucherNo())) {
            sql = sql + "AND  je_voucher_no <> '" + bean.getVoucherNo() + "' ";
        }

        return !jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(String.class)).isEmpty();
    }

    public void insertJV(VoucherBean bean, List<Object[]> batchArgsList){
        Integer jeHdrId = jdbcTemplate.queryForObject("SELECT MAX(je_hdr_id)+1 FROM gl_je_f ",
                Integer.class);
        if (jeHdrId == null || jeHdrId == 0) {
            jeHdrId = 1;
        }
        bean.setJeHdrId(jeHdrId);
        String requestType = bean.getVoucherType();
        int codeCombinationId = bean.getVoucherRows().get(0).getRowAcctId();
        int cashBankId = bean.getVoucherRows().get(1).getRowAcctId();
        boolean ifDr = bean.getVoucherRows().get(0).getRowDrCr().equals("DR");
        double drAmount = ifDr ? bean.getVoucherRows().get(0).getDebit() : 0.00;
        double crAmount = ifDr ? 0.00 : bean.getVoucherRows().get(0).getCredit();
        double accountedAmountDR = bean.getExchangeRate() * drAmount;
        double accountedAmountCR = bean.getExchangeRate() * crAmount;

        Object[] objects = {jeHdrId, 1, bean.getVoucherNo(), bean.getJeDate(), "H", null, bean.getAcctYear(), bean.getVoucherType(), 0,0,
                codeCombinationId, bean.getCurrencyId(), bean.getExchangeRate(), drAmount,
                crAmount, accountedAmountDR, accountedAmountCR,
                bean.getRemarks(), bean.getCompanyId(), "A", bean.getUserId(), null, null, cashBankId,
                "", bean.getVoucherRows().get(0).getBl(), bean.getHdrTotalAmount(), bean.getLineTotalAmount(),
                bean.getLoadingAgent(), bean.getSeqNo(), bean.getVoucherRows().get(0).getSo(), bean.getInvNo(),
                bean.getSeaAir(), bean.getExpImp(), null, StringUtils.hasText(bean.getInvDate()) ? bean.getInvDate() : null};
        batchArgsList.add(objects);

        int jeLineId = 1;
        for(VoucherRow row : bean.getVoucherRows()){
            if(jeLineId == 1){
                jeLineId++;
                continue;
            }
            createLineJV(bean, jeLineId++, row, batchArgsList,codeCombinationId);
        }

        jdbcTemplate.batchUpdate(Constants.JE_INSERT, batchArgsList);

    }

    public void createLineJV(VoucherBean bean, int jeLineId,
                           VoucherRow row, List<Object[]> batchArgsList,
                             int codeCombinationId) {
        double drAmount = row.getDebit();
        double crAmount = row.getCredit();
        double accountedAmountDR = bean.getExchangeRate() * drAmount;
        double accountedAmountCR = bean.getExchangeRate() * crAmount;

        Object[] objectArray = {bean.getJeHdrId(), jeLineId, bean.getVoucherNo(), bean.getJeDate(), "L", null, bean.getAcctYear(),
                bean.getVoucherType(), 0, 0,
                row.getRowAcctId(), bean.getCurrencyId(), bean.getExchangeRate(), drAmount,
                crAmount, accountedAmountDR, accountedAmountCR,
                bean.getRemarks(), bean.getCompanyId(), "A", bean.getUserId(), null, null, codeCombinationId,
                "", row.getBl(), bean.getHdrTotalAmount(), bean.getLineTotalAmount(), bean.getLoadingAgent(), bean.getSeqNo(),
                row.getSo(), bean.getInvNo(), bean.getSeaAir(),
                bean.getExpImp(), null, StringUtils.hasText(bean.getInvDate()) ? bean.getInvDate() : null};

        batchArgsList.add(objectArray);
    }

    public ReportBean getVoucherDataForBill(InvoiceBean bean) {
        String query = "SELECT * FROM gl_je_f WHERE JE_VOUCHER_NO = ? AND hdr_line_flag='H' "
                + "AND acct_year = ? "
                + "AND (je_date > ? OR reconcile_date is not null) ";

        ReportBean rb = null;
        try {
            rb = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(ReportBean.class), bean.getBillNo(), bean.getAcctYear(), bean.getCutOffDate());
        }
        catch (EmptyResultDataAccessException erde){
            System.out.println(erde.getMessage());
        }
        return rb;
    }

    public VoucherBean findByJeSourceHdrId(InvoiceBean invoiceBean, String trxId){
        String query = "SELECT je_hdr_id,je_voucher_no,je_source,ATTRIBUTE2 FROM gl_je_f " +
                "WHERE hdr_line_flag = 'H' AND acct_year = ? " +
                "AND je_source IN ('INVOICE','EXPENSE','MISC','CREDITNOTE','DEBITNOTE','BOS') AND je_source_hdr_id = ?";

        List<VoucherBean> voucherBeans = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(VoucherBean.class), invoiceBean.getAcctYear(), trxId);
        if(voucherBeans.size() == 1){
            return voucherBeans.get(0);
        }
        else{
            return null;
        }
    }

}
