package com.finops.finance.dao;

import com.finops.dao.AbstractDAO;
import com.finops.finance.bean.BillRow;
import com.finops.finance.bean.InvoiceBean;
import com.finops.report.model.ReportBean;
import com.finops.util.Constants;
import com.finops.util.sql.*;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class
InvoiceDAO extends AbstractDAO {

    public List<InvoiceBean> findAllByPage(InvoiceBean bean) {
        QueryBuilder findByPageQuery = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" ar.CUSTOMER_TRX_ID billId,ar.TRX_NUMBER billNo,STR_TO_DATE(ar.TRX_DATE,'%Y-%m-%d') billDate,CNEE," +
                        "AWB_BL_NO blNo,AWB_BL_DATE blDate,REV_EXP,LOCAL_FOREIGN,TRX_TOTAL,REMARKS,DUE_DATE," +
                        "ar.CREATED_BY,INV_DN_CN_MISC billType,ar.CREATION_DATE,ar.AMENDED_BY,ar.AMENDED_DATE," +
                        "shpr,ar.SEA_AIR,ar.EXP_IMP,ar.INV_SB_NO,ar.inv_no,ar.irn, irn.irn,ar.billto_name ")
                .FROM("ar_customer_trx_f", "ar")
                .LEFT_JOIN("ar_invoice_irn_f", "irn"
                        , new Condition("irn.customer_trx_id", "ar.customer_trx_id", Query.EQUALS))
                .WHERE("loading_agnt", "?");
        if(StringUtils.hasText(bean.getBlNo())){
            findByPageQuery.AND("awb_bl_no", "'"+bean.getBlNo()+"'");
        }
        if (Constants.EXPENSE.equals(bean.getRevExp())) {
            findByPageQuery.AND_IN("INV_DN_CN_MISC", "('EXPENSE','CREDITNOTE','DEBITNOTE','MISCEXP')");
            findByPageQuery.AND("ar.is_proforma", "'N'");
        }
        else if (Constants.PROFORMA.equals(bean.getRevExp()) || bean.isProforma()) {
            findByPageQuery.AND_IN("INV_DN_CN_MISC", "('INVOICE','MISC','SUPPLEMENTRY')");
            findByPageQuery.AND("ar.is_proforma", "'Y'");
        }
        else if (Constants.BILL_OF_SUPPLY.equals(bean.getBillType())) {
            findByPageQuery.AND_IN("INV_DN_CN_MISC", "('BOS')");
            findByPageQuery.AND("ar.is_proforma", "'N'");
        }
        else {
            findByPageQuery.AND_IN("INV_DN_CN_MISC", "('INVOICE','MISC','SUPPLEMENTRY')");
            findByPageQuery.AND("ar.is_proforma", "'N'");
        }

        findByPageQuery.AND_BETWEEN("ar.trx_date", "?", "?");
        String invNoString = "ar.INV_NO";
        if("EXPENSE".equalsIgnoreCase(bean.getRevExp())){
            invNoString = "ar.inv_sb_no";
        }

        String[] fields = {"ar.TRX_NUMBER", "ar.TRX_DATE", "ar.billto_name", "ar.SEA_AIR", "ar.EXP_IMP", "ar.LOCAL_FOREIGN",
                "ar.INV_DN_CN_MISC", invNoString , "ar.GRAND_TOTAL"};

        addFilter(findByPageQuery, bean, fields);
        bean.setLength(1200);
        addLimit("ar.TRX_DATE DESC", findByPageQuery, bean);

        return jdbcTemplate.query(
                findByPageQuery.build().toString(),
                new BeanPropertyRowMapper<>(InvoiceBean.class),
                bean.getLoadingAgent(), bean.getYrStartDate(), bean.getYrEndDate());
    }

    public List<BillRow> findAllById(InvoiceBean bean) {
        QueryBuilder findById = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" arl.code_combination_id,arl.code_desc,arl.taxable_1," +
                        "arl.taxable_2,arl.taxable_3,arl.taxable_4,arl.taxable_5,arl.non_taxable," +
                        "arl.per_on_tot_val taxPercentageArr,arl.per_on_val,arl.discount_per,arl.discount_amt," +
                        "arl.oth_chrgs_per,arl.oth_chrgs_amt,arl.status,cc.acct_name," +
                        "arl.entered_amount amount,arl.remark additionalDesc,arl.template_id,arl.accounted_amount," +
                        "taxm.is_partition,taxm.sub_1_desc,taxm.sub_2_desc,taxm.sub_3_desc," +
                        "taxm.sub_1_per,taxm.sub_2_per,taxm.sub_3_per,taxm.percentage,arl.dr_cr," +
                        "taxm.default_percentage,arl.rev_exp_tax_disc_oc category," +
                        "arl.tax_1_per tax1Per,arl.tax_2_per tax2Per,arl.tax_3_per tax3Per,arl.tax_4_per tax4Per,arl.tax_5_per tax5Per,arl.sac_code," +
                        "ar.CUSTOMER_TRX_ID billId,ar.TRX_AUTO_SEQ_NO,ar.TRX_NUMBER billNo," +
                        "STR_TO_DATE(ar.TRX_DATE,'%Y-%m-%d') billDate,ar.REV_EXP,curr.currency_code," +
                        "ar.SEA_AIR,ar.EXP_IMP,ar.LOCAL_FOREIGN,ar.INV_DN_CN_MISC billType,ar.PARTNER_ACCOUNT_CODE," +
                        "ar.CURRENCY_ID,ar.EXCHANGE_RATE,ar.AWB_BL_NO headerBlNO,ar.party_acct_code," +
                        "STR_TO_DATE(ar.AWB_BL_DATE,'%Y-%m-%d') AWB_BL_DATE ,ar.CARRIER_CODE,ar.CNEE,ar.CNEE_CONTCT_DTLS" +
                        ",ar.POD,ar.POD_NAME,ar.DESCRIPTION,ar.NO_OF_PKGS,ar.ACT_UNIT,ar.ACT_GROSS_WT" +
                        ",ar.ACT_CBM,ar.CONTNR_NO ,ar.CHARGBL_WT,ar.TRX_TOTAL,ar.REMARKS,ar.FILE_NO," +
                        "ar.INV_SB_NO,ar.LOADING_AGNT branch,ar.total_tds," +
                        "STR_TO_DATE(ar.DUE_DATE,'%Y-%m-%d') DUE_DATE,ar.POST_FLAG,ar.CREATED_BY" +
                        ",ar.CREATION_DATE,ar.AMENDED_BY,ar.AMENDED_DATE,ar.TOTAL_TAXABLE" +
                        ",ar.TOTAL_NON_TAXABLE,ar.TOTAL_TAX,ar.SHPR,ar.JOB_NUMBER,ar.POL,ar.sales_by," +
                        "ar.so_number,ar.billto,ar.billto_contct_dtls,ar.shpr_name,ar.cnee_name," +
                        "pa.gstin_no partyGstin, pa.state_code," +
                        "ar.inv_no,ar.tax_type templateType,ar.billto_name,ar.rcm,ar.main_trx_number,STR_TO_DATE(ar.inv_date,'%Y-%m-%d') inv_date," +
                        "ar.place_of_supply, ar.pos_code, ar.signature_file, ar.irn, CASE WHEN(ar.is_proforma = 'Y') THEN true ELSE false END proforma, inv.irn, inv.action, signed_qr_code, arl.so_no, arl.bl_no, " +
                        "(tax_1_val+ tax_2_val+ tax_3_val+ tax_4_val+ tax_5_val) totalGST ")
                .FROM("ar_customer_trx_lines_f", "arl")
                .INNER_JOIN("ar_customer_trx_f", "ar",
                        new Condition("ar.trx_number", "arl.trx_number", Query.EQUALS))
                .LEFT_JOIN("ar_invoice_irn_f", "inv",
                        new Condition("inv.customer_trx_id", "arl.customer_trx_id", Query.EQUALS))
                .LEFT_JOIN("gl_code_combination_d", "cc",
                        new Condition("cc.code_combination_id", "arl.code_combination_id", Query.EQUALS))
                .LEFT_JOIN("gl_tax_master_f", "taxm",
                        new Condition("taxm.description", "arl.code_desc", Query.EQUALS))
                .LEFT_JOIN("currency_d", "curr",
                        new Condition("curr.currency_id", "ar.currency_id", Query.EQUALS))
                .LEFT_JOIN("prt_contact_details_d", "pa",
                        new Condition("pa.code_combination_id", "ar.party_acct_code", Query.EQUALS))

                .WHERE("arl.customer_trx_id", "?")
                .ORDER_BY("arl.line_no ").build();


        return jdbcTemplate.query(
                findById.toString(),
                new BeanPropertyRowMapper<>(BillRow.class),
                bean.getBillId());
    }
    public void delete(InvoiceBean bean) {
        jdbcTemplate.update(
                "DELETE FROM ar_customer_trx_lines_f WHERE customer_trx_id = ? ",
                bean.getBillId()
        );
        jdbcTemplate.update(
                "DELETE FROM ar_customer_trx_f WHERE customer_trx_id = ? ",
                bean.getBillId()
        );
    }



    public void createHeader(InvoiceBean bean) {
        String headerInsert = "INSERT INTO ar_customer_trx_f "
                + "(CUSTOMER_TRX_ID,TRX_AUTO_SEQ_NO,TRX_NUMBER,TRX_DATE,REV_EXP"
                + ",SEA_AIR,EXP_IMP,LOCAL_FOREIGN,INV_DN_CN_MISC,PARTNER_ACCOUNT_CODE"
                + ",CURRENCY_ID,EXCHANGE_RATE,AWB_BL_NO,AWB_BL_DATE,CARRIER_CODE"
                + ",CNEE,CNEE_CONTCT_DTLS,POD,POD_NAME,DESCRIPTION"
                + ",NO_OF_PKGS,ACT_UNIT,ACT_GROSS_WT,ACT_CBM,CONTNR_NO"
                + ",CHARGBL_WT,TRX_TOTAL,REMARKS,FILE_NO,INV_SB_NO"
                + ",LOADING_AGNT,DUE_DATE,POST_FLAG,CREATED_BY,CREATION_DATE"
                + ",TOTAL_TAXABLE,TOTAL_NON_TAXABLE,TOTAL_TAX,BILLTO,JOB_NUMBER,SALES_BY,SO_NUMBER"
                + ",SHPR,BILLTO_CONTCT_DTLS,TOTAL_TDS,INV_NO,TAX_TYPE,BILLTO_NAME,RCM,MAIN_TRX_NUMBER,"
                + "INV_DATE,PARTY_ACCT_CODE,PLACE_OF_SUPPLY, SIGNATURE_FILE, IRN, IS_PROFORMA, POS_CODE) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE(),"
                + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?, ?, ?)";

        jdbcTemplate.update(headerInsert,
                bean.getBillId(), bean.getBillId(), bean.getBillNo(), bean.getBillDate(), bean.getRevExp(),
                bean.getSeaAir(), bean.getExpImp(), bean.getLocalForeign(), bean.getBillType(), bean.getPartyAcctCode(),
                bean.getCurrencyId(), bean.getExchangeRate(), bean.getBlNo(), defaultNull(bean.getAwbBlDate()), bean.getCarrierCode(), bean.getCnee(),
                java.sql.Types.VARCHAR, bean.getPod(), java.sql.Types.VARCHAR, java.sql.Types.VARCHAR, default0(bean.getNoOfPkgs()),
                "UNIT", default0(bean.getActGrossWt()), default0(bean.getActCbm()), bean.getContnrNo(), "0.00", bean.getTrxTotal(),
                bean.getRemarks(),
                java.sql.Types.VARCHAR, bean.getInvSbNo(), bean.getLoadingAgent(), bean.getDueDate(), "P", bean.getPreparedBy(), bean.getTotalTaxable(),
                bean.getTotalNonTaxable(), bean.getTotalTax(), bean.getBillTo(), bean.getJobNumber(), bean.getSalesBy(), bean.getSoNo(),
                bean.getShpr(), bean.getBilltoContctDtls(), 0, bean.getInvNo(), bean.getTemplateType(), bean.getBilltoName(), bean.getRcm(),
                bean.getMainInvoiceNo(), bean.getInvDate(), bean.getPartyAcctCode(), bean.getPlaceOfSupply(), bean.getSignatureFile(),
                bean.getIrn(), bean.isProforma() ? "Y" : "N", bean.getPosCode());
    }


    public void createJEHeader(InvoiceBean bean, List<Object[]> batchArgsList2) {

        Integer jeHdrId = jdbcTemplate.queryForObject("SELECT MAX(je_hdr_id)+1 FROM gl_je_f ",
                Integer.class);
        if (jeHdrId == null || jeHdrId == 0) {
            jeHdrId = 1;
        }
        bean.setJeHdrId(jeHdrId);

        Object[] objects = {jeHdrId, 1, bean.getBillNo(), bean.getBillDate(), "H", null, bean.getAcctYear(), bean.getBillType(), bean.getBillId(), 0,
                bean.getCodeCombinationId(), bean.getCurrencyId(), bean.getExchangeRate(), bean.getAmountDR(), bean.getAmountCR(), bean.getAccountedDR(), bean.getAccountedCR(),
                bean.getRemarks(), bean.getCompanyId(), "A", bean.getUserId(), null, null, bean.getCashBank(),
                "", bean.getBlNo(), 0.00, 0.00, bean.getLoadingAgent(), 1, bean.getSoNo(), bean.getInvNo(), bean.getSeaAir(), bean.getExpImp(), null, bean.getInvDate()};
        batchArgsList2.add(objects);
    }

    public void createJELine(InvoiceBean invoiceBean, int jeLineId,
                            BillRow bean, List<Object[]> batchArgsList2) {
        Object[] objectArray = {invoiceBean.getJeHdrId(), jeLineId, invoiceBean.getBillNo(), invoiceBean.getBillDate(), "L", null, invoiceBean.getAcctYear(), invoiceBean.getBillType(), invoiceBean.getBillId(), jeLineId - 1,
                bean.getCodeCombinationId(), invoiceBean.getCurrencyId(), invoiceBean.getExchangeRate(), bean.getAmountDR(), bean.getAmountCR(), bean.getAccountedDR(), bean.getAccountedCR(),
                invoiceBean.getRemarks(), invoiceBean.getCompanyId(), "A", invoiceBean.getUserId(), null, null, bean.getCashBank(),
                "", invoiceBean.getBlNo(), 0.00, 0.00, invoiceBean.getLoadingAgent(), 1, invoiceBean.getSoNo(), invoiceBean.getInvNo(), invoiceBean.getSeaAir(), invoiceBean.getExpImp(), null, invoiceBean.getInvDate()};

        batchArgsList2.add(objectArray);
    }

    public void createHeads(InvoiceBean bean, List<Object[]> batchArgsList,
                            List<Object[]> batchArgsList2,
                            List<BillRow> billRows, String type) {
        int autoLineNumber = batchArgsList.size() + 1001;
        int revLineNo = 1;
        int templateNo = autoLineNumber % 1000;
        String drCr = "DR";
        for (BillRow row : billRows) {
            if (!StringUtils.hasText(row.getAcctName()) && "R".equals(type)) {
                continue;
            }
            if(!"R".equals(type)){
                drCr = row.getRowDrCr();
            }
            String blNo = bean.getBlNo();
            if("EXPENSE".equalsIgnoreCase(bean.getRevExp())){
                blNo = row.getBlNo();
            }
            Object[] objectArray = {autoLineNumber++, bean.getBillId(), revLineNo++, bean.getRevExp(), bean.getSeaAir(), bean.getExpImp(), bean.getLocalForeign(), bean.getBillType(), type,
                    row.getCodeCombinationId(), row.getCodeDesc(), 0, 0, 0, 0, 0, row.getChkBox6(), row.getTaxPercentageArr(), row.getTaxOnValueArr(), 0.00, 0.00, 0.00, 0.00, "A", bean.getLoadingAgent(),
                    templateNo++, bean.getBillNo(), row.getAmount(), row.getAmount() * bean.getExchangeRate(), row.getAdditionalDesc(), bean.getTemplateId(), drCr, row.getTax1Per(), row.getTax2Per(), row.getTax3Per(),
                    row.getTax4Per(), row.getTax5Per(), row.getSacCode(), BigDecimal.valueOf(row.getAmount() * row.getTax1Per() / 100).setScale(2, RoundingMode.HALF_UP),
                    BigDecimal.valueOf(row.getAmount() * row.getTax2Per() / 100).setScale(2, RoundingMode.HALF_UP), BigDecimal.valueOf(row.getAmount() * row.getTax3Per() / 100).setScale(2, RoundingMode.HALF_UP),
                    BigDecimal.valueOf(row.getAmount() * row.getTax4Per() / 100).setScale(2, RoundingMode.HALF_UP), BigDecimal.valueOf(row.getAmount() * row.getTax5Per() / 100).setScale(2, RoundingMode.HALF_UP),
                    bean.getInvNo(), bean.getInvDate(), row.getSoNo(), blNo, bean.getBillDate(), bean.getLoadingAgent()};

            batchArgsList.add(objectArray);

            if(row.getAmount() != 0 && !bean.isProforma()) {
                createJELine(bean, templateNo, row, batchArgsList2);
            }
        }
    }

    public void createTDSParty(InvoiceBean bean, List<Object[]> batchArgsList ,
                            List<Object[]> batchArgsList2, String type) {
        int autoLineNumber = batchArgsList.size() + 1001;
        int templateNo = autoLineNumber % 1000;
        String drCr = "DR";
        double accountedDR = bean.getPartyTDSValue()* bean.getExchangeRate();

        Object[] objectArray = {autoLineNumber, bean.getBillId(), 1, bean.getRevExp(), bean.getSeaAir(), bean.getExpImp(), bean.getLocalForeign(), bean.getBillType(), type,
                bean.getPartyTDSAcctCode(), bean.getPartyTDSDesc(), 0, 0, 0, 0, 0, 0, 0, 0, 0.00, 0.00, 0.00, 0.00, "A", bean.getLoadingAgent(),
                templateNo++, bean.getBillNo(), bean.getPartyTDSValue(), accountedDR , bean.getPartyTDSDesc(), bean.getTemplateId(), drCr, 0, 0, 0,
                0,0,0, 0.00,0.00,0.00,0.00,0.00,
                bean.getInvNo(), bean.getInvDate(), bean.getSoNo(),
                bean.getBlNo(), bean.getBillDate(), bean.getLoadingAgent()};

        batchArgsList.add(objectArray);

        if(bean.getPartyTDSValue() != 0 && !bean.isProforma()) {

            Object[] jeLineObject = {bean.getJeHdrId(), templateNo, bean.getBillNo(), bean.getBillDate(), "L", null, bean.getAcctYear(), bean.getBillType(), bean.getBillId(), templateNo - 1,
                    bean.getCodeCombinationId(), bean.getCurrencyId(), bean.getExchangeRate(), bean.getPartyTDSValue(), 0, accountedDR, 0,
                    bean.getRemarks(), bean.getCompanyId(), "A", bean.getUserId(), null, null, bean.getCashBank(),
                    "", bean.getBlNo(), 0.00, 0.00, bean.getLoadingAgent(), 1, bean.getSoNo(), bean.getInvNo(), bean.getSeaAir(), bean.getExpImp(), null, bean.getInvDate()};

            batchArgsList2.add(jeLineObject);
        }

    }

    public void insertHeads(InvoiceBean bean, List<Object[]> batchArgsList, String query) {
        jdbcTemplate.batchUpdate(query, batchArgsList);
    }

    public List<ReportBean> pendingBL(ReportBean bean) {

        String filter = "";
        if (StringUtils.hasText(bean.getParam3())) {
            filter = " AND (LOADING_AGNT='" + bean.getParam3() + "' OR DEST_AGNT='" + bean.getParam3() + "') ";
        }
        String query = "SELECT bl_no param1, STR_TO_DATE(bl_issue_date,'%Y-%m-%d') param2,m_bl_number param3 FROM bl_hdr_f "
                + "WHERE bl_no NOT IN (SELECT awb_bl_no FROM ar_customer_trx_f WHERE sea_air='SEA' "
                + "AND rev_exp LIKE ? AND awb_bl_no IS NOT NULL) "
                + "AND bl_issue_date between ? AND ? " + filter
                + "UNION ALL "
                + "SELECT hawb_number param1, STR_TO_DATE(date_of_issue,'%Y-%m-%d') param2,m_hawb_no param3 FROM hawb_hdr_f "
                + "WHERE hawb_number NOT IN (SELECT awb_bl_no FROM ar_customer_trx_f WHERE sea_air='AIR' AND rev_exp LIKE ? AND awb_bl_no IS NOT NULL) "
                + "AND date_of_issue between ? AND ? " + filter;

        String param4 = "%";
        if("REVENUE".equalsIgnoreCase(bean.getParam4()) || "EXPENSE".equalsIgnoreCase(bean.getParam4())){
            param4 = bean.getParam4();
        }

        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(ReportBean.class),
                param4, bean.getParam1(), bean.getParam2(), param4, bean.getParam1(), bean.getParam2());
    }

    public boolean isDuplicateInvNo(InvoiceBean bean) {
        String sql = "SELECT count(inv_sb_no) FROM ar_customer_trx_f WHERE INV_SB_NO=? ";
        if (!"AUTO".equals(bean.getBillNo())) {
            sql = sql + "AND customer_trx_id <> "+ bean.getBillId();
        }

        return jdbcTemplate.queryForObject(sql, Integer.class, bean.getInvSbNo()) > 0;
    }

    public void deleteInvoice(InvoiceBean bean, String record, int jeHdrId) {
        String glJeFDelete = "DELETE FROM gl_je_f WHERE je_hdr_id = ? ";
        String customerTrxLinesDelete = "DELETE FROM ar_customer_trx_lines_f WHERE customer_trx_id = ? ";
        String customerTrxDelete = "DELETE FROM ar_customer_trx_f WHERE customer_trx_id = ? ";

        jdbcTemplate.update(glJeFDelete, jeHdrId);
        jdbcTemplate.update(customerTrxLinesDelete, record);
        jdbcTemplate.update(customerTrxDelete, record);

    }

}