package com.finops.report.dao;

import com.finops.aop.MeasureTime;
import com.finops.dao.AbstractDAO;
import com.finops.report.model.ReportBean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReconciliationDAO extends AbstractDAO {

    @MeasureTime
    public List<ReportBean> reconciliationView(ReportBean bean) {
        List<ReportBean> docfaList = new ArrayList<>();

        StringBuilder query = new StringBuilder("SELECT je_voucher_no param1, je_date param2, reconcile_date param3," +
                "je_line_remarks param4, acct_name param5, cashbank param6, je_source param7," +
                "chq_no param8, chq_date param9, accounted_dr param10, accounted_cr param11, id param12, amt param13, dc param14 FROM ("
                + "select CONCAT(je.je_hdr_id ,'|', je.je_line_id) id,"
                + "je.je_voucher_no,STR_TO_DATE(je.je_date,'%Y-%m-%d')je_date,je.je_source,"
                + "je.je_line_remarks,(je.accounted_cr + je.accounted_dr) AMT,"
                + "(CASE WHEN (je.accounted_cr > 0) THEN 'D' ELSE 'C' END) DC,je.chq_no,"
                + "RECONCILE_DATE,cc2.ACCT_NAME cashbank,cc1.ACCT_NAME,STR_TO_DATE(je.chq_date,'%Y-%m-%d') chq_date, "
                + "je.cash_bank bank_code_id,je.accounted_dr,je.accounted_cr "
                + "FROM gl_je_f je "
                + "LEFT OUTER JOIN gl_code_combination_d cc1 ON (je.cash_bank=cc1.code_combination_id) "
                + "LEFT OUTER JOIN gl_code_combination_d cc2 ON (je.code_combination_id=cc2.code_combination_id) "
                + "WHERE je.company_id = " + bean.getCompanyId() + " AND je.acct_year = " + bean.getAcctYear()
                + " AND cc1.parent_id IN (18) AND je_date <= '" + bean.getParam1() + "' AND je_date >= '" + bean.getYrStartDate() + "' ");

        query.append(" UNION ALL "
                + "SELECT RECONID ID,VRNO je_voucher_no,VRDT je_date,'DOCFA' je_source,"
                + "NARR1 je_line_remarks,AMT,DC,'' chq_no, "
                + "recondt reconcile_date,acct_name cashbank,bank_acct_name acct_name,"
                + "'' chq_date,cash_bank bank_code_id, "
                + "(CASE WHEN DC='D' THEN AMT ELSE 0.00 END) accounted_dr, "
                + "(CASE WHEN DC='C' THEN AMT ELSE 0.00 END) accounted_cr "
                + "FROM DOCFA_RECON WHERE VRDT <= '" + bean.getParam1() + "' AND CASH_BANK = "+bean.getParam3());

        query.append(" UNION ALL "
                + "select CONCAT(je.je_hdr_id, '|', je.je_line_id) id,"
                + "je.je_voucher_no,STR_TO_DATE(je.je_date,'%Y-%m-%d')je_date,je.je_source,"
                + "je.je_line_remarks,(je.accounted_cr + je.accounted_dr) AMT,"
                + "(CASE WHEN (je.accounted_cr > 0) THEN 'D' ELSE 'C' END) DC,je.chq_no,"
                + "RECONCILE_DATE,cc2.ACCT_NAME cashbank,cc1.ACCT_NAME,STR_TO_DATE(je.chq_date,'%Y-%m-%d') chq_date, "
                + "je.cash_bank bank_code_id,je.accounted_dr,je.accounted_cr "
                + "FROM gl_je_f je "
                + "LEFT OUTER JOIN gl_code_combination_d cc1 ON (je.cash_bank=cc1.code_combination_id) "
                + "LEFT OUTER JOIN gl_code_combination_d cc2 ON (je.code_combination_id=cc2.code_combination_id) "
                + "WHERE je.company_id = " + bean.getCompanyId() + " AND je.acct_year <> " + bean.getAcctYear()
                + " AND (reconcile_date IS NULL OR reconcile_date >= '" + bean.getYrStartDate() + "' ) "
                + "AND cc1.parent_id IN (18) AND je_date <= '" + bean.getParam1() + "' AND je.cash_bank="+bean.getParam3());

        query.append(") RECON  "
                + "WHERE 1=1 ");
        if (StringUtils.hasText(bean.getParam4())) {
            if ("1".equals(bean.getParam4())) {
                query.append(" AND reconcile_date IS NOT NULL AND reconcile_date <= '" + bean.getParam1() + "' ");
            } else if ("2".equals(bean.getParam4())) {
                query.append(" AND (reconcile_date IS NULL OR reconcile_date > '" + bean.getParam1() + "') ");
            }
        }
        if (StringUtils.hasText(bean.getParam3())) {
            query.append(" AND bank_code_id = ").append(bean.getParam3()).append(" ");
        }
        String[] fields = {"je_voucher_no", "je_date", "reconcile_date", "je_line_remarks",
                "cashbank", "chq_no", "chq_date", "AMT", "acct_name", "je_source"};

        query = addFilter(query, bean, fields);

        query.append(" ORDER BY je_date desc");
        query.append(" LIMIT " + bean.getStart() + "," + bean.getLength());

        return jdbcTemplate.query(query.toString(),
                new BeanPropertyRowMapper<>(ReportBean.class));

    }


    @MeasureTime
    public List<ReportBean> reconciliationReportView(ReportBean bean) {
        List<ReportBean> docfaList = new ArrayList<>();

        StringBuilder query = new StringBuilder("SELECT je_voucher_no param1, je_date param2, reconcile_date param3," +
                "je_line_remarks param4, acct_name param5, cashbank param6, je_source param7," +
                "chq_no param8, chq_date param9, accounted_dr param10, accounted_cr param11, id param12, amt param13, dc param14 FROM ("
                + "select CONCAT(je.je_hdr_id ,'|', je.je_line_id) id,"
                + "je.je_voucher_no,STR_TO_DATE(je.je_date,'%Y-%m-%d')je_date,je.je_source,"
                + "je.je_line_remarks,(je.accounted_cr + je.accounted_dr) AMT,"
                + "(CASE WHEN (je.accounted_cr > 0) THEN 'D' ELSE 'C' END) DC,je.chq_no,"
                + "RECONCILE_DATE,cc2.ACCT_NAME cashbank,cc1.ACCT_NAME,STR_TO_DATE(je.chq_date,'%Y-%m-%d') chq_date, "
                + "je.cash_bank bank_code_id,je.accounted_dr,je.accounted_cr "
                + "FROM gl_je_f je "
                + "LEFT OUTER JOIN gl_code_combination_d cc1 ON (je.cash_bank=cc1.code_combination_id) "
                + "LEFT OUTER JOIN gl_code_combination_d cc2 ON (je.code_combination_id=cc2.code_combination_id) "
                + "WHERE je.company_id = " + bean.getCompanyId() + " AND je.acct_year = " + bean.getAcctYear()
                + " AND cc1.parent_id IN (18) AND je_date <= '" + bean.getParam1() + "' AND je_date >= '" + bean.getYrStartDate() + "' " +
                "AND je.cash_bank = "+bean.getParam3());

        query.append(" UNION ALL "
                + "SELECT RECONID ID,VRNO je_voucher_no,VRDT je_date,'DOCFA' je_source,"
                + "NARR1 je_line_remarks,AMT,DC,'' chq_no, "
                + "recondt reconcile_date,acct_name cashbank,bank_acct_name acct_name,"
                + "'' chq_date,cash_bank bank_code_id, "
                + "(CASE WHEN DC='D' THEN AMT ELSE 0.00 END) accounted_dr, "
                + "(CASE WHEN DC='C' THEN AMT ELSE 0.00 END) accounted_cr "
                + "FROM DOCFA_RECON WHERE VRDT <= '" + bean.getParam1() + "' AND CASH_BANK = "+bean.getParam3());

        query.append(" UNION ALL "
                + "select CONCAT(je.je_hdr_id, '|', je.je_line_id) id,"
                + "je.je_voucher_no,STR_TO_DATE(je.je_date,'%Y-%m-%d')je_date,je.je_source,"
                + "je.je_line_remarks,(je.accounted_cr + je.accounted_dr) AMT,"
                + "(CASE WHEN (je.accounted_cr > 0) THEN 'D' ELSE 'C' END) DC,je.chq_no,"
                + "RECONCILE_DATE,cc2.ACCT_NAME cashbank,cc1.ACCT_NAME,STR_TO_DATE(je.chq_date,'%Y-%m-%d') chq_date, "
                + "je.cash_bank bank_code_id,je.accounted_dr,je.accounted_cr "
                + "FROM gl_je_f je "
                + "LEFT OUTER JOIN gl_code_combination_d cc1 ON (je.cash_bank=cc1.code_combination_id) "
                + "LEFT OUTER JOIN gl_code_combination_d cc2 ON (je.code_combination_id=cc2.code_combination_id) "
                + "WHERE je.company_id = " + bean.getCompanyId() + " AND je.acct_year <> " + bean.getAcctYear()
                + " AND (reconcile_date IS NULL OR reconcile_date >= '" + bean.getYrStartDate() + "' ) "
                + "AND cc1.parent_id IN (18) AND je_date <= '" + bean.getParam1() + "' AND je.cash_bank="+bean.getParam3());

        query.append(") RECON  "
                + "WHERE 1=1 ");
        if (StringUtils.hasText(bean.getParam4())) {
            if ("1".equals(bean.getParam4())) {
                query.append(" AND reconcile_date IS NOT NULL AND reconcile_date <= '" + bean.getParam1() + "' ");
            } else if ("2".equals(bean.getParam4())) {
                query.append(" AND (reconcile_date IS NULL OR reconcile_date > '" + bean.getParam1() + "') ");
            }
        }
        if (StringUtils.hasText(bean.getParam3())) {
            query.append(" AND bank_code_id = ").append(bean.getParam3()).append(" ");
        }

        return jdbcTemplate.query(query.toString(),
                new BeanPropertyRowMapper<>(ReportBean.class));

    }

    public int reconcile(ReportBean bean) {
        int updateCount = 0;
        String query = "UPDATE gl_je_f SET reconcile_date = ? WHERE COMPANY_ID = ? "
                + "AND je_hdr_id = ? AND je_line_id = ? ";
        //String query2 = "UPDATE docfa_recon SET recondt = ? WHERE reconid = ?";

        for (String s : bean.getUuids()) {
            String[] strings = s.split("\\|");
            String reconDate = bean.getParam1();
            if (!StringUtils.hasText(bean.getParam1())) {
                reconDate = null;
            }
            updateCount += jdbcTemplate.update(query,
                    reconDate,bean.getCompanyId(), strings[0], strings[1]);

        }
        return updateCount;
    }
}
