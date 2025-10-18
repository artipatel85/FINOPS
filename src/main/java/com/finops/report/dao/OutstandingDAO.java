package com.finops.report.dao;

import com.finops.dao.AbstractDAO;
import com.finops.report.model.ReportBean;
import com.finops.util.DateUtil;
import com.finops.util.FinanceUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class OutstandingDAO extends AbstractDAO {

    public List<ReportBean> receivable(ReportBean bean) {
        String query = null;
        String filter = "";//" AND cc.code_combination_id = 1416";
        String filter2 = "";

        String asOnDate = bean.getParam2();
        if (!StringUtils.hasText(asOnDate)) {
            asOnDate = DateUtil.getSystemDate();
        }

        if ("1".equals(bean.getParam9())) {
            if (StringUtils.hasText(bean.getParam8())) {
                filter = " AND cc.code_combination_id = " + bean.getParam8();
            }
            query = partywiseOutstanding(bean, filter, filter2, asOnDate);
            query = query + " ORDER BY acct_name ";
        } else {
            if (StringUtils.hasText(bean.getParam7())) {
                filter2 = " AND sa.name = '" + bean.getParam7() + "' ";
            }
            query = partywiseOutstanding(bean, filter, filter2, asOnDate);
            query = query + " ORDER BY acct_name ";
        }


        List<ReportBean> ddlList = new ArrayList<>();
        String url = "partyViewDetail.fin";

        return jdbcTemplate.query(query,
                (rs, rowNum) -> {
                        ReportBean dto = new ReportBean();
                        dto.setParam1(rs.getString("acct_name"));
                        dto.setParam2(StringUtils.hasText(rs.getString("sales_by")) ? rs.getString("sales_by") : "");
                        dto.setIntparam1(rs.getInt("code_combination_id"));
                        dto.setDoubleParam2(rs.getDouble("opening_balance"));
                        dto.setDoubleParam3(rs.getDouble("settled_opn_balance"));
                        dto.setParam3(url + "?id="+dto.getIntparam1());
                        dto = buildAgeingVO(dto, rs);

                        double total = rs.getDouble("total");
                        if ("DEBTOR".equals(bean.getParam3())) { //TODO - Creditor/debtor check
                            if (total < 0) {
                                return dto;
                            }
                        } else {
                            if (total > 0) {
                                return dto;
                            }
                        }
                        if (total < 0) {
                            total *= -1;
                        }
                        dto.setParam11(FinanceUtil.formatBigDecimal(total, 2));
                        dto.setParam12(FinanceUtil.formatBigDecimal(rs.getDouble("current_balance"), 2));
                        dto.setParam13(rs.getString("sales_by"));
                        dto.setParam14(rs.getString("credit_period"));
                        return dto;
                }, asOnDate, asOnDate, asOnDate, asOnDate, asOnDate, asOnDate, asOnDate, asOnDate, asOnDate, asOnDate, bean.getCompanyId());
    }

    private ReportBean buildAgeingVO(ReportBean dto, ResultSet rs) throws SQLException {
        double band1 = rs.getDouble("band1total");
        double band2 = rs.getDouble("band2total");
        double band3 = rs.getDouble("band3total");
        double band4 = rs.getDouble("band4total");
        double band5 = rs.getDouble("band5total");
        double band6 = rs.getDouble("band6total");
        double receipts = rs.getDouble("receiptTotal");
        double opening = dto.getDoubleParam2();
        double settledOpeningBal = dto.getDoubleParam3();
        //System.out.println(band1+"--"+band2+"--"+band3+"--"+band4+"--"+band5+"--"+band6+"--"+receipts+"--"+opening);
        if (opening < 0) {
            receipts = receipts - opening + settledOpeningBal;
        } else {
            band6 = band6 + opening - settledOpeningBal;
        }
        dto.setDoubleParam1(receipts);

        if (band6 > 0 && dto.getDoubleParam1() > 0) {
            band6 = updateBands(dto, band6);
        }
        if (band5 > 0 && dto.getDoubleParam1() > 0) {
            band5 = updateBands(dto, band5);
        }
        if (band4 > 0 && dto.getDoubleParam1() > 0) {
            band4 = updateBands(dto, band4);
        }
        if (band3 > 0 && dto.getDoubleParam1() > 0) {
            band3 = updateBands(dto, band3);
        }
        if (band2 > 0 && dto.getDoubleParam1() > 0) {
            band2 = updateBands(dto, band2);
        }
        if (band1 > 0 && dto.getDoubleParam1() > 0) {
            band1 = updateBands(dto, band1);
        }
        dto.setParam10(FinanceUtil.formatBigDecimal(band6, 2));
        dto.setParam9(FinanceUtil.formatBigDecimal(band5, 2));
        dto.setParam8(FinanceUtil.formatBigDecimal(band4, 2));
        dto.setParam7(FinanceUtil.formatBigDecimal(band3, 2));
        dto.setParam6(FinanceUtil.formatBigDecimal(band2, 2));
        dto.setParam5(FinanceUtil.formatBigDecimal(band1, 2));

        return dto;
    }

    private double updateBands(ReportBean dto, double band) {
        double receipts = dto.getDoubleParam1();

        if (band > receipts) {
            band -= receipts;
            receipts = 0;
        } else {
            receipts -= band;
            band = 0;
        }
        dto.setDoubleParam1(receipts);
        return band;
    }


    private String partywiseOutstanding(ReportBean bean, String filter, String filter2, String asOnDate) {
        String dayBalance = DateUtil.getDayBalanceField(asOnDate);

        String query = "SELECT DISTINCT ageing.code_combination_id,cc.acct_name,ageing.band1total,ageing.band2total,ageing.band3total,ageing.band4total,"
                + "         ageing.band5total,ageing.band6total,"
                + "(band1total+band2total+band3total+band4total+band5total+band6total-receiptTotal+opening_balance-settled_opn_balance) as total,"
                + "         ageing.receiptTotal,sa.name sales_by, ageing.opening_balance, ageing.current_balance,settled_opn_balance,prt.credit_period "
                + "     FROM (SELECT cc.code_combination_id,"
                + "     SUM(CASE WHEN datediff(?,je.je_date) < 16 AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 LIKE '" + bean.getParam6() + "%' "
                + "	THEN (je.accounted_dr) ELSE 0 END) band1total,"
                + "     SUM(CASE WHEN datediff(?,je.je_date) > 15 AND datediff(?,je.je_date) < 31 AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 LIKE '" + bean.getParam6() + "%' "
                + "	THEN (je.accounted_dr) ELSE 0 END) band2total,"
                + "     SUM(CASE WHEN datediff(?,je.je_date) > 30 AND datediff(?,je.je_date) < 61 AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 LIKE '" + bean.getParam6() + "%' "
                + "	THEN (je.accounted_dr) ELSE 0 END) band3total,"
                + "     SUM(CASE WHEN datediff(?,je.je_date) > 60 AND datediff(?,je.je_date) < 91 AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 LIKE '" + bean.getParam6() + "%' "
                + "	THEN (je.accounted_dr) ELSE 0 END) band4total,"
                + "     SUM(CASE WHEN datediff(?,je.je_date) > 90 AND datediff(?,je.je_date) < 181 AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 LIKE '" + bean.getParam6() + "%' "
                + "	THEN (je.accounted_dr) ELSE 0 END) band5total,"
                + "     SUM(CASE WHEN datediff(?,je.je_date) > 180 AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 LIKE '" + bean.getParam6() + "%' "
                + "	THEN (je.accounted_dr) ELSE 0 END) band6total,"
                + "	SUM(CASE WHEN je.JE_SOURCE IN ('RECEIPT','JOURNAL','EXPENSE','CREDITNOTE') THEN je.accounted_cr ELSE 0 END) receiptTotal,"
                + "     cc._0_0 opening_balance,COALESCE(sob.settled_opn_balance,0) settled_opn_balance,"
                + "     (cc."+dayBalance+") current_balance "
                + "     FROM gl_day_balances_f cc"
                + "     LEFT OUTER JOIN gl_je_f je ON (cc.code_combination_id = je.code_combination_id "
                + "     AND je.ACCT_YEAR = " + bean.getAcctYear() + " AND je.JE_SOURCE IN ('INVOICE','MISC','RECEIPT','JOURNAL','PAYMENT','EXPENSE','CREDITNOTE','BOS') "
                + "     AND je.je_date <= '"+asOnDate+"' AND je.company_id = ? AND (je.REFERENCE2 = '' or je.REFERENCE2 IS NULL)) "
                + "     LEFT OUTER JOIN settled_opening_balance_d sob ON (sob.code_combination_id = cc.code_combination_id "
                + "     AND sob.acct_year = " + bean.getAcctYear() + " ) "
                + "     WHERE cc.ACCT_YEAR = " + bean.getAcctYear() + " "
                + "     AND (je.REFERENCE2 = '' or je.REFERENCE2 IS NULL) "
                + "     " + filter
                + "     AND cc.code_combination_id IN (SELECT code_combination_id FROM gl_code_combination_d WHERE parent_id IN (22))"
                + "     GROUP BY cc.code_combination_id,je.company_id, cc._0_0,cc."+dayBalance+",sob.settled_opn_balance) ageing"
                + "     INNER JOIN gl_code_combination_d cc ON (cc.code_combination_id = ageing.code_combination_id)"
                + "     LEFT OUTER JOIN prt_contact_details_d prt ON (prt.code_combination_id=cc.code_combination_id)"
                + "     LEFT OUTER JOIN partner_account_d pa ON (pa.PARTNER_ACCT_CODE = cc.CODE_COMBINATION_ID)"
                + "	    LEFT OUTER JOIN salesman_f sa ON (sa.code = pa.SALESMAN_CODE)"
                + "     WHERE (band1total+band2total+band3total+band4total+band5total+band6total-receiptTotal+opening_balance-settled_opn_balance) > 0 ";
        //query.append(" ORDER BY acct_name ");
        if ("LOCAL".equals(bean.getParam4())) {
            query = query + " AND prt.country_code = 'IN' ";
        } else {
            query = query + " AND prt.country_code <> 'IN' ";
        }
        query = query + filter2;
        //System.out.println(query);
        return query;
    }


    public List<ReportBean> getAgeingInvoicewise(ReportBean bean) {
        String query;
        String filter = "";
        String asOnDate = bean.getParam2();
        if (!StringUtils.hasText(asOnDate)) {
            asOnDate = DateUtil.getSystemDate();
        }

        if ("1".equals(bean.getParam9())) {
            if (StringUtils.hasText(bean.getParam8())) {
                filter = " je.code_combination_id = " + bean.getParam8();
            }
        } else {
            if (StringUtils.hasText(bean.getParam7())) {
                filter = " trx.sales_by = '" + bean.getParam7() + "'";
            }
        }
        if ("LOCAL".equalsIgnoreCase(bean.getParam4())) {
            query = ageingInvoicewiseLocalQuery(bean, filter, asOnDate);
        } else {
            query = ageingInvoicewiseForeignQuery(bean, filter, asOnDate);
        }

        return jdbcTemplate.query(query,
                (rs, rowNum) -> {
                    ReportBean dto = new ReportBean();
                    dto.setParam1(rs.getString("je_voucher_no"));
                    dto.setParam2(rs.getString("je_date"));
                    dto.setParam3(rs.getString("exchange_rate"));
                    dto.setParam4("31-60");
                    dto.setParam5("61-90");
                    dto.setParam6("91-180");
                    dto.setParam7("> 180");
                    dto.setParam8(FinanceUtil.formatBigDecimal(rs.getDouble("band1total"), 2));
                    dto.setParam9(FinanceUtil.formatBigDecimal(rs.getDouble("band2total"), 2));
                    dto.setParam10(FinanceUtil.formatBigDecimal(rs.getDouble("band3total"), 2));
                    dto.setParam11(FinanceUtil.formatBigDecimal(rs.getDouble("band4total"), 2));
                    dto.setParam12(FinanceUtil.formatBigDecimal(rs.getDouble("band5total"), 2));
                    dto.setParam13(FinanceUtil.formatBigDecimal(rs.getDouble("band6total"), 2));
                    dto.setParam14(FinanceUtil.formatBigDecimal(rs.getDouble("total"), 2));
                    dto.setParam15(rs.getString("sales_by"));
                    dto.setParam16(rs.getString("acct_name"));
                    dto.setParam17(rs.getString("currency_code"));
                    dto.setParam18(rs.getString("customer_trx_id"));
                    dto.setParam19(rs.getString("awb_bl_no"));
                    dto.setParam20(rs.getString("noofdays"));
                    dto.setParam21(rs.getString("inv_no"));
                    dto.setParam22(rs.getString("exp_imp"));
                    //dto.setBigpar(rs.getBigDecimal("opening_balance"));
                    dto.setParam23(rs.getString("sea_air"));
                    dto.setParam24(rs.getString("irn"));
                    return dto;
                }, asOnDate, asOnDate, asOnDate, asOnDate, asOnDate, asOnDate, asOnDate, asOnDate, asOnDate, asOnDate, asOnDate, bean.getCompanyId());
    }


    private String ageingInvoicewiseLocalQuery(ReportBean bean, String filter, String asOnDate) {

        return "SELECT ageing.customer_trx_id,ageing.JE_VOUCHER_NO,ageing.acct_name,STR_TO_DATE(ageing.je_date,'%Y-%m-%d') je_date,ageing.band1total,ageing.band2total,ageing.band3total,"
                + "ageing.band4total,ageing.band5total,ageing.band6total,ageing.opening_balance,"
                + "(band1total+band2total+band3total+band4total+band5total+band6total) as total,"
                + "ageing.receiptTotal,ageing.sales_by, ageing.opening_balance, ageing.CURRENCY_CODE,"
                + "ageing.exchange_rate,ageing.awb_bl_no,ageing.noofdays,ageing.inv_no,ageing.exp_imp, ageing.sea_air, ageing.irn "
                + "FROM (SELECT trx.customer_trx_id,je.JE_VOUCHER_NO,je.JE_DATE, gccd.acct_name,je.exchange_rate,"
                + "SUM(CASE WHEN datediff(?,je.je_date) < 16  AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 like '" + bean.getParam6() + "%' "
                + "THEN (je.accounted_dr-je.accounted_cr) ELSE 0 END) band1total,"
                + "SUM(CASE WHEN datediff(?,je.je_date) > 15  AND datediff(?,je.je_date) < 31  AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 like '" + bean.getParam6() + "%' THEN (je.accounted_dr-je.accounted_cr) ELSE 0 END) band2total,"
                + "SUM(CASE WHEN datediff(?,je.je_date) > 30  AND datediff(?,je.je_date) < 61  AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 like '" + bean.getParam6() + "%' THEN (je.accounted_dr-je.accounted_cr) ELSE 0 END) band3total,"
                + "SUM(CASE WHEN datediff(?,je.je_date) > 60  AND datediff(?,je.je_date) < 91  AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 like '" + bean.getParam6() + "%' THEN (je.accounted_dr-je.accounted_cr) ELSE 0 END) band4total,"
                + "SUM(CASE WHEN datediff(?,je.je_date) > 90  AND datediff(?,je.je_date) < 181  AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 like '" + bean.getParam6() + "%' THEN (je.accounted_dr-je.accounted_cr) ELSE 0 END) band5total,"
                + "SUM(CASE WHEN datediff(?,je.je_date) > 180  AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 like '" + bean.getParam6() + "%' THEN (je.accounted_dr-je.accounted_cr) ELSE 0 END) band6total,"
                + "SUM(CASE WHEN je.JE_SOURCE IN ('RECEIPT','JOURNAL') THEN je.accounted_cr ELSE 0 END) receiptTotal,"
                + "MAX(trx.sales_by) sales_by, cc._0_0 opening_balance,cur.CURRENCY_CODE,trx.awb_bl_no,"
                + "datediff(?,je.je_date) noofdays,trx.INV_NO,  je.exp_imp, je.sea_air, irn.irn "
                + "FROM gl_je_f je "
                + "INNER JOIN gl_day_balances_f cc ON (cc.code_combination_id = je.code_combination_id AND cc.ACCT_YEAR = " + bean.getAcctYear() + ") "
                + "LEFT OUTER JOIN ar_customer_trx_f trx ON (trx.customer_trx_id = je.je_source_hdr_id) "
                + "LEFT OUTER JOIN currency_d cur ON (cur.currency_id = je.CURRENCY_ID) "
                + "LEFT OUTER JOIN gl_code_combination_d gccd ON (gccd.CODE_COMBINATION_ID=je.CODE_COMBINATION_ID AND gccd.PARENT_ID = 22) "
                + "LEFT OUTER JOIN ar_invoice_irn_f irn ON (irn.customer_trx_id = trx.customer_trx_id) "
                + "WHERE " + filter + " AND je.company_id = ? AND je.ACCT_YEAR = " + bean.getAcctYear() + " "
                + "AND je.je_date <= '"+asOnDate+"' AND je.JE_SOURCE IN ('INVOICE','MISC','RECEIPT','JOURNAL','PAYMENT','BOS') "
                + "AND (je.REFERENCE2 = '' OR je.reference2 IS NULL) "
                + "GROUP BY trx.customer_trx_id,je.JE_VOUCHER_NO,je.company_id, cc._0_0,je.JE_DATE,"
                + "gccd.acct_name,cur.currency_code,je.exchange_rate,trx.awb_bl_no,trx.inv_no,trx.exp_imp, je.exp_imp, je.je_source, je.sea_air, irn.irn) ageing "
                + "WHERE (band1total+band2total+band3total+band4total+band5total+band6total) > 0";
    }

    private String ageingInvoicewiseForeignQuery(ReportBean bean, String filter, String asOnDate) {
        return "SELECT ageing.customer_trx_id,ageing.JE_VOUCHER_NO,ageing.acct_name,STR_TO_DATE(ageing.je_date,'%Y-%m-%d') je_date,ageing.band1total,ageing.band2total,ageing.band3total,"
                + "ageing.band4total,ageing.band5total,ageing.band6total,ageing.opening_balance,"
                + "(band1total+band2total+band3total+band4total+band5total+band6total) as total,"
                + "ageing.receiptTotal,ageing.sales_by, ageing.opening_balance, ageing.CURRENCY_CODE,"
                + "ageing.exchange_rate,ageing.awb_bl_no,ageing.noofdays,ageing.INV_NO,ageing.exp_imp,ageing.sea_air, ageing.irn "
                + "FROM (SELECT trx.customer_trx_id,je.JE_VOUCHER_NO,je.JE_DATE, gccd.acct_name,je.exchange_rate,"
                + "SUM(CASE WHEN datediff(?,je.je_date) < 16  AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 like '" + bean.getParam6() + "%' "
                + "THEN (je.entered_dr-je.entered_cr) ELSE 0 END) band1total,"
                + "SUM(CASE WHEN datediff(?,je.je_date) > 15  AND datediff(?,je.je_date) < 31  AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') "
                + "AND je.attribute2 like '" + bean.getParam6() + "%' THEN (je.entered_dr-je.entered_cr) ELSE 0 END) band2total,"
                + "SUM(CASE WHEN datediff(?,je.je_date) > 30  AND datediff(?,je.je_date) < 61  AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') "
                + "AND je.attribute2 like '" + bean.getParam6() + "%' THEN (je.entered_dr-je.entered_cr) ELSE 0 END) band3total,"
                + "SUM(CASE WHEN datediff(?,je.je_date) > 60  AND datediff(?,je.je_date) < 91  AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') "
                + "AND je.attribute2 like '" + bean.getParam6() + "%' THEN (je.entered_dr-je.entered_cr) ELSE 0 END) band4total,"
                + "SUM(CASE WHEN datediff(?,je.je_date) > 90  AND datediff(?,je.je_date) < 181  AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') "
                + "AND je.attribute2 like '" + bean.getParam6() + "%' THEN (je.entered_dr-je.entered_cr) ELSE 0 END) band5total,"
                + "SUM(CASE WHEN datediff(?,je.je_date) > 180  AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 like '" + bean.getParam6() + "%' "
                + "THEN (je.entered_dr-je.entered_cr) ELSE 0 END) band6total,"
                + "SUM(CASE WHEN je.JE_SOURCE IN ('RECEIPT','JOURNAL') THEN je.accounted_cr ELSE 0 END) receiptTotal,"
                + "MAX(trx.sales_by) sales_by, cc._0_0 opening_balance,cur.CURRENCY_CODE,trx.awb_bl_no, "
                + "datediff(?,je.je_date) noofdays,trx.INV_NO, je.exp_imp, je.sea_air, irn.irn "
                + "FROM gl_je_f je "
                + "INNER JOIN gl_day_balances_f cc ON (cc.code_combination_id = je.code_combination_id AND cc.ACCT_YEAR = " + bean.getAcctYear() + ") "
                + "LEFT OUTER JOIN ar_customer_trx_f trx ON (trx.customer_trx_id = je.je_source_hdr_id) "
                + "LEFT OUTER JOIN currency_d cur ON (cur.currency_id = je.CURRENCY_ID) "
                + "LEFT OUTER JOIN gl_code_combination_d gccd ON (gccd.CODE_COMBINATION_ID=je.CODE_COMBINATION_ID AND gccd.PARENT_ID = 22) "
                + "LEFT OUTER JOIN ar_invoice_irn_f irn ON (irn.customer_trx_id = trx.customer_trx_id) "
                + "WHERE " + filter + " AND je.company_id = ? AND je.ACCT_YEAR = " + bean.getAcctYear() + " "
                + "AND je.je_date <= '"+asOnDate+"' AND je.JE_SOURCE IN ('INVOICE','MISC','RECEIPT','JOURNAL','PAYMENT','BOS') "
                + "AND (je.REFERENCE2 = '' OR je.reference2 IS NULL) "
                + "GROUP BY trx.customer_trx_id,je.JE_VOUCHER_NO,je.company_id, cc._0_0,je.JE_DATE,"
                + "gccd.acct_name,cur.currency_code,je.exchange_rate,trx.awb_bl_no,trx.inv_no,trx.exp_imp, je.exp_imp, je.je_source, je.sea_air, irn.irn) ageing "
                + "WHERE (band1total+band2total+band3total+band4total+band5total+band6total) > 0 "
                + "ORDER BY je_date ";
    }

    public List<ReportBean> receivable2(ReportBean bean) {
        String query = null;
        String filter = "";//" AND cc.code_combination_id = 1416";

        String asOnDate = bean.getParam2();
        if (!StringUtils.hasText(asOnDate)) {
            asOnDate = DateUtil.getSystemDate();
        }

        String filter2 = "";
        query = partywiseOutstanding2(bean, filter, filter2, asOnDate);
        query = query + " ORDER BY acct_name ";

        String url = "partyViewDetail.fin";

        return jdbcTemplate.query(query, new ResultSetExtractor<List<ReportBean>>() {
            @Override
            public List<ReportBean> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<ReportBean> ddlList = new ArrayList<>();
                while (rs.next()) {
                    ReportBean dto = new ReportBean();
                    dto.setParam1(rs.getString("acct_name"));
                    dto.setParam2(defaultNull(rs.getString("sales_by")));
                    dto.setIntparam1(rs.getInt("code_combination_id"));
                    dto.setDoubleParam2(rs.getDouble("opening_balance"));
                    dto.setDoubleParam3(rs.getDouble("settled_opn_balance"));
                    dto.setParam3(url + "?id=" + dto.getIntparam1());
                    dto = buildAgeingVO2(dto, rs);

                    double total = rs.getDouble("total");
                    if ("DEBTOR".equals(bean.getParam3())) { //TODO - Creditor/debtor check
                        if (total < 0) {
                            continue;
                        }
                    } else {
                        if (total > 0) {
                            continue;
                        }
                    }
                    if (total < 0) {
                        total *= -1;
                    }
                    dto.setParam11(FinanceUtil.formatBigDecimal(total, 2));
                    dto.setParam12(FinanceUtil.formatBigDecimal(rs.getDouble("current_balance"), 2));
                    dto.setParam13(rs.getString("sales_by"));
                    ddlList.add(dto);
                }
                return ddlList;
            }
        }, asOnDate, asOnDate, bean.getCompanyId());
    }

    private ReportBean buildAgeingVO2(ReportBean dto, ResultSet rs) throws SQLException {
        double band1 = rs.getDouble("band1total");
        double band2 = rs.getDouble("band2total");

        double receipts = rs.getDouble("receiptTotal");
        double opening = dto.getDoubleParam2();
        double settledOpeningBal = dto.getDoubleParam3();
        //System.out.println(band1+"--"+band2+"--"+band3+"--"+band4+"--"+band5+"--"+band6+"--"+receipts+"--"+opening);
        if (opening < 0) {
            receipts = receipts - opening + settledOpeningBal;
        } else {
            band2 = band2 + opening - settledOpeningBal;
        }
        dto.setDoubleParam1(receipts);

        if (band2 > 0 && dto.getDoubleParam1() > 0) {
            band2 = updateBands(dto, band2);
        }
        if (band1 > 0 && dto.getDoubleParam1() > 0) {
            band1 = updateBands(dto, band1);
        }
        dto.setParam6(FinanceUtil.formatBigDecimal(band2, 2));
        dto.setParam5(FinanceUtil.formatBigDecimal(band1, 2));

        return dto;
    }

    private String partywiseOutstanding2(ReportBean bean, String filter, String filter2, String asOnDate) {
        String dayBalance = DateUtil.getDayBalanceField(asOnDate);

        String query = "SELECT DISTINCT ageing.code_combination_id,cc.acct_name,ageing.band1total,ageing.band2total,"
                + "(band1total+band2total-receiptTotal+opening_balance-settled_opn_balance) as total,"
                + "         ageing.receiptTotal,sa.name sales_by, ageing.opening_balance, ageing.current_balance,settled_opn_balance "
                + "     FROM (SELECT cc.code_combination_id,"
                + "     SUM(CASE WHEN datediff(?,je.je_date) < 181 AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 LIKE '" + bean.getParam6() + "%' "
                + "	THEN (je.accounted_dr) ELSE 0 END) band1total,"
                + "     SUM(CASE WHEN datediff(?,je.je_date) > 180 AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 LIKE '" + bean.getParam6() + "%' "
                + "	THEN (je.accounted_dr) ELSE 0 END) band2total,"
                + "	SUM(CASE WHEN je.JE_SOURCE IN ('RECEIPT','JOURNAL','EXPENSE','CREDITNOTE') THEN je.accounted_cr ELSE 0 END) receiptTotal,"
                + "     cc._0_0 opening_balance,COALESCE(sob.settled_opn_balance,0) settled_opn_balance,"
                + "     (cc."+dayBalance+") current_balance "
                + "     FROM gl_day_balances_f cc"
                + "     LEFT OUTER JOIN gl_je_f je ON (cc.code_combination_id = je.code_combination_id "
                + "     AND je.je_date <= '"+asOnDate+"' AND je.ACCT_YEAR = " + bean.getAcctYear() + " AND je.JE_SOURCE IN ('INVOICE','MISC','RECEIPT','JOURNAL','PAYMENT','EXPENSE','CREDITNOTE','BOS') "
                + "     AND je.company_id = ? AND (je.REFERENCE2 = '' or je.REFERENCE2 IS NULL)) "
                + "     LEFT OUTER JOIN settled_opening_balance_d sob ON (sob.code_combination_id = cc.code_combination_id "
                + "     AND sob.acct_year = " + bean.getAcctYear() + ") "
                + "     WHERE cc.ACCT_YEAR = " + bean.getAcctYear() + " "
                + "     AND (je.REFERENCE2 = '' or je.REFERENCE2 IS NULL) "
                + "     " + filter
                + "     AND cc.code_combination_id IN (SELECT code_combination_id FROM gl_code_combination_d WHERE parent_id IN (22))"
                + "     GROUP BY cc.code_combination_id,je.company_id, cc._0_0,cc."+dayBalance+",sob.settled_opn_balance) ageing"
                + "     INNER JOIN gl_code_combination_d cc ON (cc.code_combination_id = ageing.code_combination_id)"
                + "     LEFT OUTER JOIN prt_contact_details_d prt ON (prt.code_combination_id=cc.code_combination_id)"
                + "     LEFT OUTER JOIN partner_account_d pa ON (pa.PARTNER_ACCT_CODE = cc.CODE_COMBINATION_ID)"
                + "	LEFT OUTER JOIN salesman_f sa ON (sa.code = pa.SALESMAN_CODE)"
                + "     WHERE (band1total+band2total-receiptTotal+opening_balance-settled_opn_balance) <> 0 ";
        //query.append(" ORDER BY acct_name ");
        if ("LOCAL".equals(bean.getParam4())) {
            query = query + " AND prt.country_code = 'IN' ";
        } else {
            query = query + " AND prt.country_code <> 'IN' ";
        }
        query = query + filter2;
        //System.out.println(query);
        return query;
    }

    public List<ReportBean> receivable3(ReportBean bean) {
        String query = null;
        String filter = "";//" AND cc.code_combination_id = 1416";
        String filter2 = "";

        String asOnDate = bean.getParam2();
        if (!StringUtils.hasText(asOnDate)) {
            asOnDate = DateUtil.getSystemDate();
        }

        query = partywiseOutstanding3(bean, filter, filter2, asOnDate);
        query = query + " ORDER BY acct_name ";
        String url = "partyViewDetail.fin";

        return jdbcTemplate.query(query, new ResultSetExtractor<List<ReportBean>>() {
            @Override
            public List<ReportBean> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<ReportBean> ddlList = new ArrayList<>();
                while(rs.next()) {
                    ReportBean dto = new ReportBean();
                    dto.setParam1(rs.getString("acct_name"));
                    dto.setParam2(defaultNull(rs.getString("sales_by")));
                    dto.setIntparam1(rs.getInt("code_combination_id"));
                    //dto.setParam3(url + "&lineAcctId=" + dto.getIntparam1() + "&lineAcctName=" + dto.getParam1() + "&opnBal=0.00");
                    dto.setDoubleParam2(rs.getDouble("opening_balance"));
                    dto.setDoubleParam3(rs.getDouble("settled_opn_balance"));
                    dto.setParam3(url + "?id=" + dto.getIntparam1());
                    dto = buildAgeingVO(dto, rs);

                    double total = rs.getDouble("total");
                    if ("DEBTOR".equals(bean.getParam3())) { //TODO - Creditor/debtor check
                        if (total < 0) {
                            continue;
                        }
                    } else {
                        if (total > 0) {
                            continue;
                        }
                    }
                    if (total < 0) {
                        total *= -1;
                    }
                    dto.setParam11(FinanceUtil.formatBigDecimal(total, 2));
                    dto.setParam12(FinanceUtil.formatBigDecimal(rs.getDouble("current_balance"), 2));
                    dto.setParam13(rs.getString("sales_by"));
                    ddlList.add(dto);
                }
                return ddlList;
            }
        }, asOnDate, asOnDate, asOnDate, asOnDate, asOnDate, asOnDate, asOnDate, asOnDate, asOnDate, asOnDate, bean.getCompanyId());
    }

    private String partywiseOutstanding3(ReportBean bean, String filter, String filter2, String asOnDate) {
        String dayBalance = DateUtil.getDayBalanceField(asOnDate);

        String query = "SELECT DISTINCT ageing.code_combination_id,cc.acct_name,ageing.band1total,ageing.band2total,ageing.band3total,ageing.band4total,"
                + "         ageing.band5total,ageing.band6total,"
                + "(band1total+band2total+band3total+band4total+band5total+band6total-receiptTotal+opening_balance-settled_opn_balance) as total,"
                + "         ageing.receiptTotal,sa.name sales_by, ageing.opening_balance, ageing.current_balance,settled_opn_balance "
                + "     FROM (SELECT cc.code_combination_id,"
                + "     SUM(CASE WHEN datediff(?,je.je_date) < 61 AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 LIKE '" + bean.getParam6() + "%' "
                + "	THEN (je.accounted_dr) ELSE 0 END) band1total,"
                + "     SUM(CASE WHEN datediff(?,je.je_date) > 60 AND datediff(?,je.je_date) < 91 AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 LIKE '" + bean.getParam6() + "%' "
                + "	THEN (je.accounted_dr) ELSE 0 END) band2total,"
                + "     SUM(CASE WHEN datediff(?,je.je_date) > 90 AND datediff(?,je.je_date) < 121 AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 LIKE '" + bean.getParam6() + "%' "
                + "	THEN (je.accounted_dr) ELSE 0 END) band3total,"
                + "     SUM(CASE WHEN datediff(?,je.je_date) > 120 AND datediff(?,je.je_date) < 181 AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 LIKE '" + bean.getParam6() + "%' "
                + "	THEN (je.accounted_dr) ELSE 0 END) band4total,"
                + "     SUM(CASE WHEN datediff(?,je.je_date) > 180 AND datediff(?,je.je_date) < 366 AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 LIKE '" + bean.getParam6() + "%' "
                + "	THEN (je.accounted_dr) ELSE 0 END) band5total,"
                + "     SUM(CASE WHEN datediff(?,je.je_date) > 365 AND je.JE_SOURCE IN ('INVOICE','MISC','PAYMENT','JOURNAL','BOS') AND je.attribute2 LIKE '" + bean.getParam6() + "%' "
                + "	THEN (je.accounted_dr) ELSE 0 END) band6total,"
                + "	SUM(CASE WHEN je.JE_SOURCE IN ('RECEIPT','JOURNAL','EXPENSE','CREDITNOTE') THEN je.accounted_cr ELSE 0 END) receiptTotal,"
                + "     cc._0_0 opening_balance,COALESCE(sob.settled_opn_balance,0) settled_opn_balance,"
                + "     (cc."+dayBalance+") current_balance "
                + "     FROM gl_day_balances_f cc"
                + "     LEFT OUTER JOIN gl_je_f je ON (cc.code_combination_id = je.code_combination_id "
                + "     AND je.je_date <= '"+asOnDate+"' AND je.ACCT_YEAR = " + bean.getAcctYear() + " AND je.JE_SOURCE IN ('INVOICE','MISC','RECEIPT','JOURNAL','PAYMENT','EXPENSE','CREDITNOTE','BOS') "
                + "     AND je.company_id = ? AND (je.REFERENCE2 = '' or je.REFERENCE2 IS NULL)) "
                + "     LEFT OUTER JOIN settled_opening_balance_d sob ON (sob.code_combination_id = cc.code_combination_id "
                + "     AND sob.acct_year = " + bean.getAcctYear() + " ) "
                + "     WHERE cc.ACCT_YEAR = " + bean.getAcctYear() + " "
                + "     AND (je.REFERENCE2 = '' or je.REFERENCE2 IS NULL) "
                + "     " + filter
                + "     AND cc.code_combination_id IN (SELECT code_combination_id FROM gl_code_combination_d WHERE parent_id IN (22))"
                + "     GROUP BY cc.code_combination_id,je.company_id, cc._0_0,cc."+dayBalance+",sob.settled_opn_balance) ageing"
                + "     INNER JOIN gl_code_combination_d cc ON (cc.code_combination_id = ageing.code_combination_id)"
                + "     LEFT OUTER JOIN prt_contact_details_d prt ON (prt.code_combination_id=cc.code_combination_id)"
                + "     LEFT OUTER JOIN partner_account_d pa ON (pa.PARTNER_ACCT_CODE = cc.CODE_COMBINATION_ID)"
                + "	LEFT OUTER JOIN salesman_f sa ON (sa.code = pa.SALESMAN_CODE)"
                + "     WHERE (band1total+band2total+band3total+band4total+band5total+band6total-receiptTotal+opening_balance-settled_opn_balance) <> 0 ";
        //query.append(" ORDER BY acct_name ");
        if ("LOCAL".equals(bean.getParam4())) {
            query = query + " AND prt.country_code = 'IN' ";
        } else {
            query = query + " AND prt.country_code <> 'IN' ";
        }
        query = query + filter2;
        //System.out.println(query);
        return query;
    }
}
