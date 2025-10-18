package com.finops.report.dao;

import com.finops.dao.AbstractDAO;
import com.finops.report.model.ReportBean;
import com.finops.util.ApplicationUtil;
import com.finops.util.FinanceUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class SalesRegisterDAO extends AbstractDAO {

    private static final String B2B = "SELECT pa.description1 param1,pa.gstin_no param2,arl.TRX_NUMBER param3, "
            + "ar.TRX_DATE param4,"
            + "ar.TRX_TOTAL param5,concat(st.state_code,'-',st.STATE_NAME) param6,"
            + "'' param7,'N' param8,'Regular' param9,'' param10,'' param11,'' para12,'' param13,(tax_1_per+tax_2_per+tax_3_per) param14, "
            + "SUM(ACCOUNTED_AMOUNT) param15,SUM(TAX_2_VAL*EXCHANGE_RATE) param16,"
            + "SUM(TAX_3_VAL*EXCHANGE_RATE) param17, SUM(TAX_1_VAL*EXCHANGE_RATE) param18, "
            + "ar.ZERO_RATED param19,ar.PLACE_OF_SUPPLY param20,ar.billTo param21 "
            + "FROM ar_customer_trx_lines_f arl "
            + "LEFT OUTER JOIN ar_customer_trx_f ar ON (ar.TRX_NUMBER=arl.trx_number) "
            + "LEFT OUTER JOIN gl_code_combination_d cc ON (cc.CODE_COMBINATION_ID=arl.CODE_COMBINATION_ID) "
            + "LEFT OUTER JOIN partner_account_d pa ON (pa.PARTNER_CODE=ar.BILLTO and pa.LOADNG_AGNT=ar.LOADING_AGNT) "
            + "LEFT OUTER JOIN STATE_D st ON (st.STATE_CODE=pa.STATE_CODE) "
            + "LEFT OUTER JOIN gl_code_combination_d cc1 ON (cc1.CODE_COMBINATION_ID=ar.PARTNER_ACCOUNT_CODE) "
            + "WHERE arl.INV_DN_CN_MISC IN ('INVOICE','MISC','BOS') AND is_proforma <> 'Y' AND arl.REV_EXP_TAX_DISC_OC='R' "
            + "AND ACCOUNTED_AMOUNT > 0 AND (tax_1_per+tax_2_per+tax_3_per) > 0 "
            + "AND tax_type IN ('IGST','SGST','UTGST') AND arl.LOADING_AGNT=? "
            + "AND ARL.LOCAL_FOREIGN='LOCAL' AND arl.trx_date BETWEEN ? AND ? "
            + "GROUP BY ar.TRX_TOTAL,arl.TRX_NUMBER,ar.trx_date,cc1.acct_name,ar.billto,"
            + "tax_1_per, tax_2_per, tax_3_per,tax_type,ar.zero_rated,ar.PLACE_OF_SUPPLY, " +
            "pa.state_code, state_name, pa.gstin_no, pa.description1 "
            + "ORDER BY ar.billto,arl.trx_number";

    // (st.state_code+'-'+st.STATE_NAME)

    private static final String B2C = "SELECT 'WPAY' param1,'' param2,arl.TRX_NUMBER param3, "
            + "REPLACE(REPLACE(CONVERT(ar.TRX_DATE,DATE), ' ','-'), ',','') param4,"
            + "'' param5,(ar.TRX_TOTAL*ar.exchange_rate) bigparam1,'' param7,'' param8,'' param9,(tax_1_per+tax_2_per+tax_3_per) param10,"
            + "SUM(ACCOUNTED_AMOUNT) param11,0 param12,'' param13 "
            + "FROM ar_customer_trx_lines_f arl "
            + "LEFT OUTER JOIN ar_customer_trx_f ar ON (ar.TRX_NUMBER=arl.trx_number) "
            + "LEFT OUTER JOIN gl_code_combination_d cc ON (cc.CODE_COMBINATION_ID=arl.CODE_COMBINATION_ID) "
            + "LEFT OUTER JOIN partner_account_d pa ON (pa.PARTNER_CODE=ar.BILLTO and pa.LOADNG_AGNT=ar.LOADING_AGNT) "
            + "LEFT OUTER JOIN STATE_D st ON (st.STATE_CODE=pa.STATE_CODE) "
            + "LEFT OUTER JOIN gl_code_combination_d cc1 ON (cc1.CODE_COMBINATION_ID=ar.PARTNER_ACCOUNT_CODE) "
            + "WHERE arl.INV_DN_CN_MISC IN ('INVOICE','MISC','BOS') AND is_proforma <> 'Y' AND arl.REV_EXP_TAX_DISC_OC='R' "
            + "AND ACCOUNTED_AMOUNT > 0 AND tax_type IN ('IGST','SGST','UTGST') AND arl.LOADING_AGNT=? "
            + "AND ARL.LOCAL_FOREIGN='FOREIGN' AND arl.trx_date BETWEEN ? AND ? "
            + "GROUP BY EXCHANGE_RATE,ar.TRX_TOTAL,pa.GSTIN_NO,arl.TRX_NUMBER,ar.trx_date,cc1.acct_name,"
            + "st.state_code,st.STATE_NAME,tax_1_per, tax_2_per, tax_3_per,tax_type "
            + "ORDER BY pa.GSTIN_NO,arl.trx_number";

    private static final String EXEMPTED = "SELECT (CASE WHEN (st.state_code = 'THESTATECODE') "
            + "THEN 'Intra-State supplies to registered persons' ELSE 'Inter-State supplies to registered persons' END) param1,"
            + "'' param2,0 param3,SUM(arl.ACCOUNTED_AMOUNT) param4,0 param5,GSTIN_NO param6 "
            + "FROM ar_customer_trx_lines_f arl "
            + "LEFT OUTER JOIN ar_customer_trx_f ar ON (ar.TRX_NUMBER=arl.trx_number) "
            + "LEFT OUTER JOIN gl_code_combination_d cc ON (cc.CODE_COMBINATION_ID=arl.CODE_COMBINATION_ID) "
            + "LEFT OUTER JOIN partner_account_d pa ON (pa.PARTNER_CODE=ar.BILLTO and pa.LOADNG_AGNT=ar.LOADING_AGNT) "
            + "LEFT OUTER JOIN STATE_D st ON (st.STATE_CODE=pa.STATE_CODE) "
            + "LEFT OUTER JOIN gl_code_combination_d cc1 ON (cc1.CODE_COMBINATION_ID=ar.PARTNER_ACCOUNT_CODE) "
            + "WHERE arl.INV_DN_CN_MISC IN ('INVOICE','MISC','BOS') AND is_proforma <> 'Y' AND arl.REV_EXP_TAX_DISC_OC='R' "
            + "AND ACCOUNTED_AMOUNT > 0 AND ar.LOADING_AGNT=? "
            + "AND (tax_type IN ('EXEMPTED') OR (tax_type IN ('IGST','SGST','CGST') "
            + "AND (tax_1_per+tax_2_per+tax_3_per) = 0)) AND arl.trx_date BETWEEN ? AND ? "
            + "GROUP BY pa.GSTIN_NO,arl.TRX_NUMBER,ar.trx_date,cc1.acct_name,"
            + "st.state_code,st.STATE_NAME,tax_1_per, tax_2_per, tax_3_per,tax_type "
            + "ORDER BY pa.GSTIN_NO,arl.trx_number";

    private static final String EXPORT_SEZ = "SELECT 'WOPAY' param1,'' param2,arl.TRX_NUMBER param3, "
            + "REPLACE(REPLACE(CONVERT(ar.TRX_DATE,DATE), ' ','-'), ',','') param4,"
            + "'' param5,(ar.TRX_TOTAL*ar.exchange_rate) bigparam1,'' param7,'' param8,'' param9,0 param10,"
            + "SUM(ACCOUNTED_AMOUNT) param11,0 param12,'' param13 "
            + "FROM ar_customer_trx_lines_f arl "
            + "LEFT OUTER JOIN ar_customer_trx_f ar ON (ar.TRX_NUMBER=arl.trx_number) "
            + "LEFT OUTER JOIN gl_code_combination_d cc ON (cc.CODE_COMBINATION_ID=arl.CODE_COMBINATION_ID) "
            + "LEFT OUTER JOIN partner_account_d pa ON (pa.PARTNER_CODE=ar.BILLTO and pa.LOADNG_AGNT=ar.LOADING_AGNT) "
            + "LEFT OUTER JOIN STATE_D st ON (st.STATE_CODE=pa.STATE_CODE) "
            + "LEFT OUTER JOIN gl_code_combination_d cc1 ON (cc1.CODE_COMBINATION_ID=ar.PARTNER_ACCOUNT_CODE) "
            + "WHERE arl.INV_DN_CN_MISC IN ('INVOICE','MISC','BOS') AND is_proforma <> 'Y' AND arl.REV_EXP_TAX_DISC_OC='R' "
            + "AND ACCOUNTED_AMOUNT > 0 AND tax_type IN ('SEZ') AND ar.LOADING_AGNT=? "
            + "AND ARL.LOCAL_FOREIGN='FOREIGN' AND arl.trx_date BETWEEN ? AND ? "
            + "GROUP BY EXCHANGE_RATE,ar.TRX_TOTAL,pa.GSTIN_NO,arl.TRX_NUMBER,ar.trx_date,cc1.acct_name,"
            + "st.state_code,st.STATE_NAME,tax_1_per, tax_2_per, tax_3_per,tax_type "
            + "ORDER BY pa.GSTIN_NO,arl.trx_number";

    private static final String LOCAL_SEZ = "SELECT '' param1,pa.GSTIN_NO param2,arl.TRX_NUMBER param3, "
            + "REPLACE(REPLACE(CONVERT(ar.TRX_DATE,DATE), ' ','-'), ',','') param4,"
            + "ar.TRX_TOTAL param5,"
            + "(st.state_code+'-'+st.STATE_NAME) param6,'' param7,'N' param8,'SEZ without Payment' param9,"
            + "'' param10,'' param11,'' param12,'' param13,(tax_1_per+tax_2_per+tax_3_per) param14, SUM(ACCOUNTED_AMOUNT) param15,"
            + "SUM(TAX_2_VAL*EXCHANGE_RATE) param16,SUM(TAX_3_VAL*EXCHANGE_RATE) param17, "
            + "SUM(TAX_1_VAL*EXCHANGE_RATE) param18 "
            + "FROM ar_customer_trx_lines_f arl "
            + "LEFT OUTER JOIN ar_customer_trx_f ar ON (ar.TRX_NUMBER=arl.trx_number) "
            + "LEFT OUTER JOIN gl_code_combination_d cc ON (cc.CODE_COMBINATION_ID=arl.CODE_COMBINATION_ID) "
            + "LEFT OUTER JOIN partner_account_d pa ON (pa.PARTNER_CODE=ar.BILLTO and pa.LOADNG_AGNT=ar.LOADING_AGNT) "
            + "LEFT OUTER JOIN STATE_D st ON (st.STATE_CODE=pa.STATE_CODE) "
            + "LEFT OUTER JOIN gl_code_combination_d cc1 ON (cc1.CODE_COMBINATION_ID=ar.PARTNER_ACCOUNT_CODE) "
            + "WHERE arl.INV_DN_CN_MISC IN ('INVOICE','MISC','BOS') AND is_proforma <> 'Y' AND arl.REV_EXP_TAX_DISC_OC='R' "
            + "AND ACCOUNTED_AMOUNT > 0 AND tax_type IN ('SEZ') AND ar.LOADING_AGNT=? "
            + "AND ARL.LOCAL_FOREIGN='LOCAL' AND arl.trx_date BETWEEN ? AND ? "
            + "GROUP BY ar.TRX_TOTAL,pa.GSTIN_NO,arl.TRX_NUMBER,ar.trx_date,cc1.acct_name,"
            + "st.state_code,st.STATE_NAME,tax_1_per, tax_2_per, tax_3_per,tax_type "
            + "ORDER BY pa.GSTIN_NO,arl.trx_number";

    private static final String DEBIT_NOTE = "SELECT '' param1,pa.GSTIN_NO param2,arl.TRX_NUMBER param3, CONVERT("
            + "ar.TRX_DATE,DATE) param4,ar.TRX_TOTAL param5,(st.state_code+'-'+st.STATE_NAME) param6,"
            + "'' param7,'N' param8,'Regular' param9,'' param10,'' param11,'' param12,'' param13,(tax_1_per+tax_2_per+tax_3_per) param14, "
            + "SUM(ACCOUNTED_AMOUNT) param15,SUM(TAX_2_VAL*EXCHANGE_RATE) param16,"
            + "SUM(TAX_3_VAL*EXCHANGE_RATE) param17, SUM(TAX_1_VAL*EXCHANGE_RATE) param18, ar.ZERO_RATED param19 "
            + "FROM ar_customer_trx_lines_f arl "
            + "LEFT OUTER JOIN ar_customer_trx_f ar ON (ar.TRX_NUMBER=arl.trx_number) "
            + "LEFT OUTER JOIN gl_code_combination_d cc ON (cc.CODE_COMBINATION_ID=arl.CODE_COMBINATION_ID) "
            + "LEFT OUTER JOIN partner_account_d pa ON (pa.PARTNER_CODE=ar.BILLTO and pa.LOADNG_AGNT=ar.LOADING_AGNT) "
            + "LEFT OUTER JOIN STATE_D st ON (st.STATE_CODE=pa.STATE_CODE) "
            + "LEFT OUTER JOIN gl_code_combination_d cc1 ON (cc1.CODE_COMBINATION_ID=ar.PARTNER_ACCOUNT_CODE) "
            + "WHERE arl.INV_DN_CN_MISC IN ('DEBITNOTE') AND arl.REV_EXP_TAX_DISC_OC='R' "
            + "AND ACCOUNTED_AMOUNT > 0 AND (tax_1_per+tax_2_per+tax_3_per) > 0 "
            + "AND tax_type IN ('IGST','SGST','UTGST') AND ar.LOADING_AGNT=? "
            + "AND ARL.LOCAL_FOREIGN='LOCAL' AND ar.trx_date BETWEEN ? AND ? "
            + "GROUP BY ar.TRX_TOTAL,pa.GSTIN_NO,arl.TRX_NUMBER,ar.trx_date,cc1.acct_name,"
            + "st.state_code,st.STATE_NAME,tax_1_per, tax_2_per, tax_3_per,tax_type,ar.zero_rated "
            + "ORDER BY pa.GSTIN_NO,arl.trx_number";

    private static final String CREDIT_NOTE = "SELECT 'B2CL' param1, arl.TRX_NUMBER param2, CONVERT("
            + "ar.TRX_DATE,DATE) param3, '' param4,'' param5,'' param6,'' param7, 'C' param8,(CASE WHEN (st.state_code = 'THESTATECODE') "
            + "THEN 'Intra State' ELSE 'Inter State' END) param9,0.00 param10,0.00 param11,"
            + "(tax_1_per+tax_2_per+tax_3_per) param12,"
            + "SUM(ACCOUNTED_AMOUNT) param13,0.00 param14,'N' param15 "
            + "FROM ar_customer_trx_lines_f arl "
            + "LEFT OUTER JOIN ar_customer_trx_f ar ON (ar.TRX_NUMBER=arl.trx_number) "
            + "LEFT OUTER JOIN gl_code_combination_d cc ON (cc.CODE_COMBINATION_ID=arl.CODE_COMBINATION_ID) "
            + "LEFT OUTER JOIN partner_account_d pa ON (pa.PARTNER_CODE=ar.BILLTO and pa.LOADNG_AGNT=ar.LOADING_AGNT) "
            + "LEFT OUTER JOIN STATE_D st ON (st.STATE_CODE=pa.STATE_CODE) "
            + "LEFT OUTER JOIN gl_code_combination_d cc1 ON (cc1.CODE_COMBINATION_ID=ar.PARTNER_ACCOUNT_CODE) "
            + "WHERE arl.INV_DN_CN_MISC IN ('CREDITNOTE') AND arl.REV_EXP_TAX_DISC_OC='R' "
            + "AND ACCOUNTED_AMOUNT > 0 AND tax_type IN ('IGST','SGST','UTGST') AND ar.LOADING_AGNT=? "
            + "AND ar.trx_date BETWEEN ? AND ? "
            + "GROUP BY EXCHANGE_RATE,ar.TRX_TOTAL,pa.GSTIN_NO,arl.TRX_NUMBER,ar.trx_date,cc1.acct_name,"
            + "st.state_code,st.STATE_NAME,tax_1_per, tax_2_per, tax_3_per,tax_type "
            + "ORDER BY pa.GSTIN_NO,arl.trx_number";

    private static final String JOURNAL_INPUT = "SELECT SUM(je.ACCOUNTED_CR) param1,SUM(je.ACCOUNTED_DR) param2,cc.ACCT_NAME param3,"
            + "je.ATTRIBUTE2 param4 FROM gl_je_f je,gl_code_combination_d cc "
            + "WHERE cc.CODE_COMBINATION_ID=je.CODE_COMBINATION_ID "
            + "AND cc.CODE_COMBINATION_ID IN (4102,4105,4107) AND je.attribute2 = ? "
            + "AND je.JE_SOURCE IN  ('JOURNAL') AND je.je_date BETWEEN ? AND ? "
            + "GROUP BY cc.ACCT_NAME,je.ATTRIBUTE2 ORDER BY je.ATTRIBUTE2";

    private static final String JOURNAL_VOUCHER = "SELECT DISTINCT jour.JE_VOUCHER_NO param1,CONVERT(je.je_date,DATE) param2," +
            "IGST param3,CGST param4,SGST param5,INV_NO param6,prt.GSTIN_NO param7,cc.ACCT_NAME param8 FROM "
            + "(SELECT je.JE_VOUCHER_NO,SUM(CASE WHEN je.CODE_COMBINATION_ID=4102 THEN (je.ACCOUNTED_DR-je.ACCOUNTED_CR) ELSE 0.00 END) AS IGST,"
            + "SUM(CASE WHEN je.CODE_COMBINATION_ID=4105 THEN (je.ACCOUNTED_DR-je.ACCOUNTED_CR) ELSE 0.00 END) AS CGST,"
            + "SUM(CASE WHEN je.CODE_COMBINATION_ID=4107 THEN (je.ACCOUNTED_DR-je.ACCOUNTED_CR) ELSE 0.00 END) AS SGST "
            + "FROM gl_je_f je "
            + "WHERE je.CODE_COMBINATION_ID IN (4102,4105,4107) AND je.JE_SOURCE IN  ('JOURNAL') "
            +"AND je.ATTRIBUTE2=? AND je.je_date BETWEEN ? AND ? "
            +"GROUP BY je.JE_VOUCHER_NO) JOUR "
            + "INNER JOIN gl_je_f je ON (je.JE_VOUCHER_NO = JOUR.JE_VOUCHER_NO AND je.CODE_COMBINATION_ID "
            + "IN (SELECT CODE_COMBINATION_ID FROM gl_code_combination_d WHERE parent_id in (22,29))) "
            + "LEFT OUTER JOIN gl_code_combination_d cc ON (cc.CODE_COMBINATION_ID = je.CODE_COMBINATION_ID) "
            + "LEFT OUTER JOIN prt_contact_details_d prt ON (cc.CODE_COMBINATION_ID = prt.CODE_COMBINATION_ID) "
            + "WHERE (IGST+SGST+CGST) <> 0 "
            +"ORDER BY param2,param1 ";

    private static final String HSN_INWARD = "SELECT SAC_CODE param1, SUM(ACCOUNTED_AMOUNT) param2, SUM(CASE WHEN NON_TAXABLE=0 THEN ACCOUNTED_AMOUNT ELSE 0 END) param3,"
            + "(TAX_1_PER+TAX_2_PER+TAX_3_PER+TAX_4_PER+TAX_5_PER) param4, SUM(TAX_1_VAL) param5, SUM(TAX_2_VAL) param6, SUM(TAX_3_VAL) param7 "
            + "FROM ar_customer_trx_lines_f line, ar_customer_trx_f trx "
            + "WHERE trx.TRX_NUMBER = line.TRX_NUMBER "
            + "AND line.LOADING_AGNT=? AND line.trx_date BETWEEN ? AND ? AND line.rev_exp = 'EXPENSE' "
            + "GROUP BY SAC_CODE,(TAX_1_PER+TAX_2_PER+TAX_3_PER+TAX_4_PER+TAX_5_PER)  "
            + "ORDER BY sac_code ";


    private static final String HSN_OUTWARD = "SELECT SAC_CODE param1, SUM(ACCOUNTED_AMOUNT) param2, SUM(CASE WHEN NON_TAXABLE=0 THEN ACCOUNTED_AMOUNT ELSE 0 END) param3,"
            + "(TAX_1_PER+TAX_2_PER+TAX_3_PER+TAX_4_PER+TAX_5_PER) param4, SUM(TAX_1_VAL*EXCHANGE_RATE) param5, "
            + "SUM(TAX_2_VAL*EXCHANGE_RATE) param6, SUM(TAX_3_VAL*EXCHANGE_RATE) param7 "
            + "FROM ar_customer_trx_lines_f line, ar_customer_trx_f trx "
            + "WHERE trx.TRX_NUMBER = line.TRX_NUMBER AND trx.is_proforma <> 'Y' "
            + "AND line.LOADING_AGNT=? AND line.trx_date BETWEEN ? AND ? AND line.rev_exp = 'REVENUE' "
            + "GROUP BY SAC_CODE,(TAX_1_PER+TAX_2_PER+TAX_3_PER+TAX_4_PER+TAX_5_PER)  "
            + "ORDER BY SAC_CODE";

    private static final String EXEMPTED_INVOICE = "SELECT (CASE WHEN (st.state_code = THESTATECODE) "
            + "THEN 'Intra-State supplies to registered persons' ELSE 'Inter-State supplies to registered persons' END) param1,"
            + "'' param2,0 param3,arl.ACCOUNTED_AMOUNT param4,0 param5,GSTIN_NO param6,ar.TRX_NUMBER param7 "
            + "FROM ar_customer_trx_lines_f arl "
            + "LEFT OUTER JOIN ar_customer_trx_f ar ON (ar.TRX_NUMBER=arl.trx_number) "
            + "LEFT OUTER JOIN gl_code_combination_d cc ON (cc.CODE_COMBINATION_ID=arl.CODE_COMBINATION_ID) "
            + "LEFT OUTER JOIN partner_account_d pa ON (pa.PARTNER_CODE=ar.BILLTO and pa.LOADNG_AGNT=ar.LOADING_AGNT) "
            + "LEFT OUTER JOIN STATE_D st ON (st.STATE_CODE=pa.STATE_CODE) "
            + "LEFT OUTER JOIN gl_code_combination_d cc1 ON (cc1.CODE_COMBINATION_ID=ar.PARTNER_ACCOUNT_CODE) "
            + "WHERE arl.INV_DN_CN_MISC IN ('INVOICE','MISC','BOS') AND ar.is_proforma <> 'Y' AND arl.REV_EXP_TAX_DISC_OC='R' "
            + "AND ACCOUNTED_AMOUNT > 0 AND ar.LOADING_AGNT=? "
            + "AND (tax_type IN ('EXEMPTED') OR (tax_type IN ('IGST','SGST','CGST') "
            + "AND (tax_1_per+tax_2_per+tax_3_per) = 0)) AND arl.trx_date BETWEEN ? AND ? "
            + "ORDER BY pa.GSTIN_NO,arl.trx_number";

    private static String EXPENSE_GST = "SELECT cc.acct_name param1,SUM(ACCOUNTED_AMOUNT) param2, SUM(TAX_1_VAL) param3,SUM(TAX_2_VAL) param4,"
            + "SUM(TAX_3_VAL) param5,SUM(TAX_4_VAL) param6,SUM(TAX_5_VAL) param7 "
            + "FROM ar_customer_trx_lines_f line, ar_customer_trx_f header, gl_code_combination_d cc "
            + "WHERE header.LOADING_AGNT=? AND line.TRX_DATE BETWEEN ? AND ? and header.TRX_NUMBER = line.TRX_NUMBER AND is_proforma <> 'Y' "
            + "AND line.CODE_COMBINATION_ID = cc.CODE_COMBINATION_ID AND cc.PARENT_ID IN (6,8) "
            + "AND header.INV_DN_CN_MISC NOT IN ('CREDITNOTE') "
            + "GROUP BY cc.ACCT_NAME ";

    public List<ReportBean> gstinReport(ReportBean bean, String type)  {
        String query = null;

        switch (type){
            case "B2B" -> query = B2B;
            case "B2C" -> query = B2C;
            case "EXEMPTED" -> query = EXEMPTED;
            case "EXPORT-SEZ" -> query = EXPORT_SEZ;
            case "LOCAL-SEZ" -> query = LOCAL_SEZ;
            case "DEBIT-NOTE" -> query = DEBIT_NOTE;
            case "CREDIT-NOTE" -> query = CREDIT_NOTE;
            case "JOURNAL-INPUT" -> query = JOURNAL_INPUT;
            case "JOURNAL-VOUCHER" -> query = JOURNAL_VOUCHER;
            case "HSN-INWARD" -> query = HSN_INWARD;
            case "HSN-OUTWARD" -> query = HSN_OUTWARD;
            case "EXEMPTED-INVOICE" -> query = EXEMPTED_INVOICE;
            case "EXPENSE-GST" -> query = EXPENSE_GST;

        }

        String stateCode = ApplicationUtil.getBranchStateCode(bean.getParam3());
        if(stateCode != null){
            query = query.replace("THESTATECODE", stateCode);
        }

        return jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(ReportBean.class),
                bean.getParam3(), bean.getParam1(), bean.getParam2());

    }


    public List<ReportBean> expense(ReportBean bean) {
        String query = "SELECT ar.LOADING_AGNT param6,ar.TRX_NUMBER param7,CONVERT(TRX_DATE,DATE) param8,ar.LOCAL_FOREIGN param9,ar.TAX_TYPE param13,"
                + "(ar.TOTAL_TAXABLE*ar.EXCHANGE_RATE) bigparam1,(ar.TOTAL_NON_TAXABLE * ar.EXCHANGE_RATE) bigparam2,"
                + "A.IGST bigparam3,A.SGST bigparam4,A.CGST bigparam5,ar.inv_sb_no param10,CONVERT(inv_date,DATE) param11,"
                + "(CASE WHEN (pa.GSTIN_NO IS NULL OR pa.GSTIN_NO = '') THEN ccc.GSTIN_NO ELSE pa.GSTIN_NO END) param14,"
                + "(CASE WHEN (pa.DESCRIPTION1 IS NULL OR pa.DESCRIPTION1 = '') THEN ar.BILLTO_NAME "
                + "ELSE pa.DESCRIPTION1 END) param12,ar.awb_bl_no param15,a.DESCRIPTION2 param16,ar.sea_air param17,ar.exp_imp param18 "
                + " FROM ("
                + "SELECT arl.TRX_NUMBER,'' DESCRIPTION2,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=4102 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS IGST,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=4105 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS SGST,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=4107 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS CGST "
                + " FROM ar_customer_trx_lines_f arl, gl_code_combination_d cc "
                + " WHERE arl.CODE_COMBINATION_ID = cc.CODE_COMBINATION_ID AND arl.accounted_amount > 0 "
                + " AND INV_DN_CN_MISC IN ('EXPENSE','CREDITNOTE') AND arl.trx_date BETWEEN '" + bean.getParam1() + "' AND '" + bean.getParam2() + "' "
                + " AND arl.TRX_NUMBER IN (SELECT TRX_NUMBER FROM ar_customer_trx_f WHERE LOADING_AGNT='" + bean.getParam3() + "')"
                + "GROUP BY  arl.TRX_NUMBER ) A "
                + "INNER JOIN ar_customer_trx_f ar ON (ar.TRX_NUMBER=A.TRX_NUMBER)"
                + "LEFT OUTER JOIN partner_account_d pa ON (pa.PARTNER_CODE = ar.BILLTO AND pa.LOADNG_AGNT = ar.LOADING_AGNT) "
                + "LEFT OUTER JOIN prt_contact_details_d ccc ON (ccc.CODE_COMBINATION_ID=ar.PARTY_ACCT_CODE) "
                + "WHERE ar.LOADING_AGNT='" + bean.getParam3() + "' "
                + "AND ar.trx_date BETWEEN '" + bean.getParam1() + "' AND '" + bean.getParam2() + "' "
                + "ORDER BY LOADING_AGNT,CUSTOMER_TRX_ID,ar.TRX_NUMBER,TRX_DATE";

        return jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(ReportBean.class));

    }

    public List<ReportBean> invoice(ReportBean bean) {

        String query = "SELECT ar.LOADING_AGNT param6,ar.TRX_NUMBER param7,CONVERT(TRX_DATE,DATE) param8,ar.LOCAL_FOREIGN param9,ar.TAX_TYPE param11,"
                + "(ar.TOTAL_TAXABLE*ar.EXCHANGE_RATE) bigparam1,(ar.TOTAL_NON_TAXABLE*ar.EXCHANGE_RATE) bigparam2,"
                + "A.IGST bigparam21,A.SGST bigparam22,A.CGST bigparam23,A.UTGST bigparam24,A.OCEAN_FREIGHT bigparam11,A.AIR_FREIGHT bigparam3,A.HANDLING_CHARGES bigparam7,"
                + "A.BROKERAGE_FAC bigparam4,A.BL_FEES_OTHRS bigparam5,A.CLEARING_FORWARDING bigparam6,A.INLAND_HAULAGE_THC bigparam8,A.INCOME_TAX_REFUND_FY20052006 bigparam9,"
                + "A.IMPORT_DUTY bigparam10,A.PROFIT_SHARE_FOREIGN bigparam12,A.GSP_EMBESSY_FEE bigparam13,A.TRANSPORATION_RECEIVED bigparam14,A.HANDLING_NON_TAXABLE bigparam15,"
                + "A.TRANSPORATION_NON_TAXABLE bigparam16,A.IHC_RECEIVED bigparam17,A.THC_RECEIVED bigparam18,A.GREEN_TAX_TOLL_TAX bigparam19,A.CUSTOM_DUTY bigparam20,"
                + "pa.GSTIN_NO param12,pa.DESCRIPTION1 param10,ar.awb_bl_no param13,ar.sea_air param14,ar.exp_imp param15"
                + " FROM"
                + "("
                + "SELECT arl.TRX_NUMBER,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=2229 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS AIR_FREIGHT,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=2230 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS BROKERAGE_FAC,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=2231 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS BL_FEES_OTHRS,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=2232 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS CLEARING_FORWARDING,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=2233 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS HANDLING_CHARGES,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=2234 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS INLAND_HAULAGE_THC,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=2235 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS INCOME_TAX_REFUND_FY20052006,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=2236 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS IMPORT_DUTY,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=2237 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS OCEAN_FREIGHT,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=2238 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS PROFIT_SHARE_FOREIGN,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=2239 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS GSP_EMBESSY_FEE,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=2240 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS TRANSPORATION_RECEIVED,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=2438 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS HANDLING_NON_TAXABLE,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=2440 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS TRANSPORATION_NON_TAXABLE,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=4111 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS IHC_RECEIVED,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=4112 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS THC_RECEIVED,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=4160 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS GREEN_TAX_TOLL_TAX ,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=4161 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS CUSTOM_DUTY ,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=2588 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS IGST,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=2589 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS SGST,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=2590 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS CGST,"
                + "SUM(CASE WHEN arl.CODE_COMBINATION_ID=2591 THEN arl.ACCOUNTED_AMOUNT ELSE 0.00 END) AS UTGST"
                + " FROM ar_customer_trx_lines_f arl"
                + " WHERE INV_DN_CN_MISC IN ('INVOICE','MISC','DEBITNOTE','BOS')  "
                + " AND arl.TRX_NUMBER IN (SELECT TRX_NUMBER FROM ar_customer_trx_f WHERE LOADING_AGNT=? AND arl.trx_date BETWEEN ? AND ?) "
                + "GROUP BY  arl.TRX_NUMBER ) A "
                + "INNER JOIN ar_customer_trx_f ar ON (ar.TRX_NUMBER=A.TRX_NUMBER AND is_proforma <> 'Y') "
                + "LEFT OUTER JOIN partner_account_d pa ON (pa.PARTNER_CODE = ar.BILLTO AND pa.LOADNG_AGNT = ar.LOADING_AGNT) "
                + "WHERE ar.LOADING_AGNT=? "
                + "AND ar.trx_date BETWEEN ? AND ? "
                + (StringUtils.hasText(bean.getParam5()) ? " AND sales_by = '" + bean.getParam5() + "' " : " ")
                + "ORDER BY LOADING_AGNT,CUSTOMER_TRX_ID,ar.TRX_NUMBER,TRX_DATE";

        return jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(ReportBean.class),bean.getParam3(),bean.getParam1(), bean.getParam2(),
                bean.getParam3(), bean.getParam1(), bean.getParam2());
    }
}
