package com.finops.report.dao;

import com.finops.aop.MeasureTime;
import com.finops.dao.AbstractDAO;
import com.finops.report.model.ReportBean;
import com.finops.util.DateUtil;
import com.finops.util.FinanceUtil;
import com.finops.util.sql.Condition;
import com.finops.util.sql.Query;
import com.finops.util.sql.QueryBuilder;
import com.finops.util.sql.QueryType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class TrialDAO extends AbstractDAO {


    @MeasureTime
    public List<ReportBean> findTrialRecordsByPage(ReportBean bean) {
        QueryBuilder subQuery = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" * ")
                .FROM("gl_code_combination_d", "AA")
                .WHERE("acct_flag", "'L'");

        if (!StringUtils.hasText(bean.getSearchFieldValueList().get(1))) {
            subQuery.ORDER_BY("ACCT_NAME");
            subQuery.LIMIT(bean.getStart(), bean.getLength());
        }

        QueryBuilder findByPageQuery = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" cc.ACCT_CODE,cc.acct_name,dbal._0_0 opening,SUM(accounted_dr) debit,SUM(accounted_cr) credit," +
                        "cc.CODE_COMBINATION_ID,cc.parent_id under,dbal._31_3 closing,parent.acct_name parentName ")
                .FROM("(" + subQuery.build().toString() + ")", "cc")
                .LEFT_JOIN("gl_je_f", "je",
                        new Condition("cc.CODE_COMBINATION_ID", "je.CODE_COMBINATION_ID", Query.EQUALS),
                        new Condition("je.acct_year", "?", Query.EQUALS))
                .LEFT_JOIN("gl_day_balances_f", "dbal",
                        new Condition("dbal.CODE_COMBINATION_ID", "cc.CODE_COMBINATION_ID", Query.EQUALS),
                        new Condition("dbal.acct_year", "?", Query.EQUALS),
                        new Condition("dbal.COMPANY_ID", "1001", Query.EQUALS))
                .LEFT_JOIN("gl_code_combination_d", "parent",
                        new Condition("parent.acct_id", "cc.parent_id", Query.EQUALS))
                .WHERE("cc.acct_flag", "'L'")
                .GROUP_BY(" cc.CODE_COMBINATION_ID,_0_0,cc.ACCT_CODE,cc.acct_name,cc.PARENT_ID,dbal._31_3,parent.acct_name ");

        String[] fields = {"acct_code", "cc.acct_name", "parentName", "opening"};
        addFilter(findByPageQuery, bean, fields);
        addLimit("TRIM(cc.ACCT_NAME) ", findByPageQuery, bean);

        return jdbcTemplate.query(
                findByPageQuery.build().toString(),
                (rs, rowNum) -> {
                    ReportBean dto = new ReportBean();
                    dto.setParam1(rs.getString("acct_code"));
                    dto.setParam2(rs.getString("acct_name"));

                    double opening = rs.getDouble("opening");
                    if ("Y".equals(bean.getParam7()) && opening == 0) {
                        return null;
                    }
                    //double closingBal = rs.getDouble("closing");
                    double debit = rs.getDouble("debit");
                    double credit = rs.getDouble("credit");
                    String codeCombinationId = rs.getString("CODE_COMBINATION_ID");
                    dto.setParam3(FinanceUtil.modAndFormat(opening));
                    dto.setParam4(FinanceUtil.amountDrCr(opening));
                    dto.setParam5(FinanceUtil.formatBigDecimal(debit, 2));
                    dto.setParam6(FinanceUtil.formatBigDecimal(credit, 2));

                    double closingBal = FinanceUtil.closingBalance2(opening, debit, credit);
                    dto.setParam7(FinanceUtil.modAndFormat(closingBal));

                    dto.setParam8(rs.getString("parentName"));
                    dto.setParam10(FinanceUtil.amountDrCr(closingBal));
                    String link = "Monthly.fin?id=" + codeCombinationId + "&acctName=&opn=" + opening;
                    String link2 = "bclPDF.fin?id=" + codeCombinationId + "&clng=" + closingBal;
                    dto.setParam9(link);
                    dto.setParam11(link2);
                    dto.setParam12(codeCombinationId);

                    return dto;
                },
                bean.getAcctYear(), bean.getAcctYear());
    }

    public List<ReportBean> monthlySummaryData(ReportBean bean) {
        QueryBuilder findByPageQuery = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" sum(accounted_dr) dr,sum(accounted_cr) cr,year(je_date) year, month(je_date) month,cc.parent_id ")
                .FROM("gl_je_f", "je")
                .INNER_JOIN("gl_code_combination_d", "cc",
                        new Condition("cc.CODE_COMBINATION_ID", "je.CODE_COMBINATION_ID", Query.EQUALS),
                        new Condition("je.acct_year", "?", Query.EQUALS))
                .LEFT_JOIN("gl_day_balances_f", "dbal",
                        new Condition("dbal.CODE_COMBINATION_ID", "cc.CODE_COMBINATION_ID", Query.EQUALS),
                        new Condition("dbal.acct_year", "?", Query.EQUALS),
                        new Condition("dbal.COMPANY_ID", "1001", Query.EQUALS))
                .WHERE("je.CODE_COMBINATION_ID", "?")
                .AND("je.acct_year", "?")
                .GROUP_BY(" year(je_date),month(je_date),parent_id order by year,month ");

        String closing = "0.00";
        String type = "nonBank";

        return jdbcTemplate.query(
                findByPageQuery.build().toString(),
                (rs, rowNum) -> {
                    ReportBean rb = new ReportBean();
                    double dr = rs.getDouble("dr");
                    double cr = rs.getDouble("cr");
                    int month = rs.getInt("month");
                    String href = "Trasactions.fin?month=" + month;
                    //rb.setParam1("<a href='" + href + "'>" + new DateFormatSymbols().getMonths()[month - 1] + "</a>");
                    rb.setDoubleParam1(dr);
                    rb.setDoubleParam2(cr);
                    rb.setIntparam1(month < 4 ? month + 10 : month);
                    rb.setIntparam2(rs.getInt("parent_id"));
                    rb.setIntparam3(month);
                    return rb;
                },
                bean.getAcctYear(), bean.getAcctYear(), bean.getParam1(), bean.getAcctYear());
    }

    public ReportBean setupMonthlyData(ReportBean bean) {
        String inputQuery = "SELECT DBAL._0_0 param9,DBAL._31_3 closing_bal,CC.acct_name,sob.settled_opn_balance param8 " +
                " FROM gl_day_balances_f DBAL " +
                " LEFT OUTER JOIN gl_code_combination_d CC ON (DBAL.code_combination_id = CC.code_combination_id) " +
                " LEFT OUTER JOIN settled_opening_balance_d sob ON (sob.code_combination_id = DBAL.code_combination_id AND sob.acct_year = ?) " +
                " WHERE DBAL.acct_year = ? AND DBAL.code_combination_id = ? ";

        return jdbcTemplate.queryForObject(
                inputQuery,
                new BeanPropertyRowMapper<>(ReportBean.class),
                bean.getAcctYear(), bean.getAcctYear(), bean.getParam1());
    }


    @MeasureTime
    public List<ReportBean> findTransactionsByPage(ReportBean bean) {
        QueryBuilder findByPageQuery = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" je.je_hdr_id param9,je.je_voucher_no param2,CONVERT(je_date,DATE) param1,je.je_source param4,je.chq_no param5, " +
                        " chq_date param6,je.ACCOUNTED_DR param7, je.accounted_cr param8, " +
                        "je.je_line_remarks,je.reference2 param10,cc.acct_name,je.je_source_hdr_id param11, " +
                        "je.code_combination_id param13,je_line_id param14, " +
                        "(CASE WHEN (je_source IN ('RECEIPT','PAYMENT','CONTRA','JOURNAL')) THEN  " +
                        "CONCAT(acct_name,' ::: ',je_line_remarks,' :: TOTAL - ',(ACCOUNTED_DR+ACCOUNTED_CR))  " +
                        "WHEN (je.je_source IN ('INVOICE','MISC','BOS')) THEN  " +
                        "CONCAT('BL NO - ',AWB_BL_NO,' :: INV NO - ',trx.INV_NO,' ::  BASIC - '," +
                        "CAST(CEILING((trx.TOTAL_TAXABLE+trx.TOTAL_NON_TAXABLE)*trx.exchange_rate) AS DECIMAL(10,2))," +
                        "' :: TOTAL - ' ,(ACCOUNTED_DR+ACCOUNTED_CR) ,' :: ',JE_LINE_REMARKS)  " +
                        "WHEN (je_source IN ('EXPENSE','CREDITNOTE'))  " +
                        "THEN CONCAT('BL NO - ',AWB_BL_NO,' :: INV NO - ',trx.INV_SB_NO,' :: TOTAL - ', " +
                        "(ACCOUNTED_DR+ACCOUNTED_CR) ,' :: ',JE_LINE_REMARKS)  " +
                        "ELSE '' END) param3 ")
                .FROM("gl_je_f", "je")
                .LEFT_JOIN("gl_code_combination_d", "cc",
                        new Condition("cc.CODE_COMBINATION_ID", "je.cash_bank", Query.EQUALS),
                        new Condition("je.acct_year", "?", Query.EQUALS))
                .LEFT_JOIN("ar_customer_trx_f", "trx",
                        new Condition("trx.trx_number", "je.je_voucher_no", Query.EQUALS),
                        new Condition("je.acct_year", "?", Query.EQUALS))
                .WHERE("je.code_combination_id", "?")
                .AND_BETWEEN("je_date", "?", "?");
        if ("1".equals(bean.getParam6())) {
            findByPageQuery.ANDNOTNULL("reference2").ANDNOTEQUALS("reference2", "''");
        } else if ("2".equals(bean.getParam6())) {
            findByPageQuery.ANDNULL("(reference2").OR("reference2", "''");
        }

        StringBuilder query = new StringBuilder("SELECT * FROM (" + findByPageQuery.build().toString() + ") alias WHERE 1=1 ");

        String[] fields = {"param1", "param2", "param3", "param4", "param5", "param6",
                "param7", "param8", "param10"};
        addFilter(query, bean, fields);
        query.append(" ORDER BY param1,param4,param10 DESC LIMIT " + bean.getStart() + ",  " + bean.getLength());

        bean.setITotalDisplayRecords(15000);

            return jdbcTemplate.query(
                query.toString(),
                new BeanPropertyRowMapper<>(ReportBean.class),
                bean.getAcctYear(), bean.getAcctYear(), bean.getParam1(),
                bean.getParam4(), bean.getParam5());
    }

    public ReportBean getSum(ReportBean bean) {
        String query = "SELECT SUM(ACCOUNTED_DR) doubleParam1, SUM(ACCOUNTED_CR) doubleParam2 FROM gl_je_f je " +
                " WHERE je.code_combination_id= ? AND je_date BETWEEN ? AND ? AND ACCT_YEAR = ? ";
        System.out.println(query);
        System.out.println(bean.getParam1());
        System.out.println(bean.getParam4());
        System.out.println(bean.getParam5());
        System.out.println(bean.getAcctYear());

        return jdbcTemplate.queryForObject(query,
                new BeanPropertyRowMapper<>(ReportBean.class), bean.getParam1(), bean.getParam4(), bean.getParam5(), bean.getAcctYear());

    }

    public void ledgerMatch(ReportBean bean, String[] idArr) {
        String query = "UPDATE gl_je_f  SET reference2 = ? "
                + "WHERE COMPANY_ID=? "
                + "AND acct_year=? "
                + "AND je_hdr_id = ? AND code_combination_id = ? AND je_line_id = ? ";

        jdbcTemplate.update(query,
                bean.getParam7(), bean.getCompanyId(), bean.getAcctYear(), idArr[0], idArr[1], idArr[2]);
    }

    public List<ReportBean> paymentAdvise(ReportBean bean) {
        String query = "SELECT ar.INV_SB_NO param1,je.je_source param2,SUM(je.ACCOUNTED_CR) bigParam1,SUM(ACCOUNTED_DR) bigParam2,BL_NO param3,JE_VOUCHER_NO param4," +
                "AR.TOTAL_TAX param5, AR.TAX_TYPE param6,cc.acct_name param7,CONVERT(ar.INV_DATE,DATE) param8 FROM gl_je_f je, ar_customer_trx_f ar,gl_code_combination_d cc " +
                "WHERE REFERENCE2 = ? AND JE_SOURCE IN ('EXPENSE') " +
                "AND je.CODE_COMBINATION_ID = ? AND ar.TRX_NUMBER = je.je_voucher_no AND je.CODE_COMBINATION_ID = cc.CODE_COMBINATION_ID " +
                "GROUP BY ar.INV_SB_NO,je.je_source,BL_NO,JE_VOUCHER_NO,TAX_TYPE,TOTAL_TAX,acct_name,ar.INV_DATE " +
                "UNION " +
                "SELECT je.JE_VOUCHER_NO param1,je.je_source param2,SUM(je.ACCOUNTED_DR) bigParam1,SUM(ACCOUNTED_CR) bigParam1,BL_NO param3," +
                "JE_VOUCHER_NO param4,0 param5,'' param6,cc.acct_name param7,CONVERT(je.JE_DATE,DATE) param8 FROM gl_je_f je,gl_code_combination_d cc " +
                "WHERE REFERENCE2 = ? AND JE_SOURCE IN ('PAYMENT') AND HDR_LINE_FLAG = 'L' " +
                "AND je.CODE_COMBINATION_ID = ? AND je.CODE_COMBINATION_ID = cc.CODE_COMBINATION_ID " +
                "GROUP BY je.je_source,BL_NO,JE_VOUCHER_NO,acct_name,je_date ";

        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(ReportBean.class),
                bean.getParam7(), bean.getParam1(), bean.getParam7(), bean.getParam1());
    }

    public List<ReportBean> trialPartywise(ReportBean bean) {
        String join = "LEFT OUTER ";
        if (!"ALL".equals(bean.getParam6())) {
            join = " INNER ";
        }
        List<ReportBean> trialList = new ArrayList<ReportBean>();
        boolean ifProceed = false;
        List<String> valueList = bean.getSearchFieldValueList();
        if (bean.getParam6() == null) {
            for (int i = 0; i < valueList.size(); i++) {
                if (StringUtils.hasText(valueList.get(i))) {
                    ifProceed = true;
                    break;
                }
            }
            if (!ifProceed) {
                return trialList;
            }
        }

        String dateMonth = bean.getParam1();
        String dm = DateUtil.getDayBalanceField(dateMonth);
        String attribute2Part = "";

        if (!"ALL".equals(bean.getParam6())) {
            attribute2Part = " AND je.attribute2 = '" + bean.getParam6() + "' ";
        }

        String condition = "";
        String QUERY2 = "";
        if ("0".equalsIgnoreCase(bean.getParam4())) {
            condition = " AND (cc.parent_id NOT IN (22,29)) ";
            QUERY2 = " UNION ALL SELECT par.ACCT_CODE,par.acct_name,dbal._0_0 opening,"
                    + "SUM(accounted_dr) debit,SUM(accounted_cr) credit,"
                    + "par.CODE_COMBINATION_ID,par.ACCT_NAME under,par.parent_id,dbal." + dm + " closing "
                    + "FROM gl_code_combination_d cc "
                    + "LEFT OUTER JOIN gl_code_combination_d par ON (par.acct_id=cc.PARENT_ID) "
                    + join + " JOIN gl_je_f je ON (cc.CODE_COMBINATION_ID=je.CODE_COMBINATION_ID "
                    + "AND je.acct_year=" + bean.getAcctYear() + " AND je.je_date <='" + dateMonth + "' " + attribute2Part + ") "
                    + join + " JOIN gl_day_balances_f dbal ON "
                    + "(dbal.CODE_COMBINATION_ID = par.CODE_COMBINATION_ID AND dbal.acct_year=" + bean.getAcctYear() + ") "
                    + "WHERE cc.acct_flag='L' AND (cc.parent_id IN (22,29))"
                    + "group by par.CODE_COMBINATION_ID,_0_0,par.ACCT_CODE,par.acct_name,par.PARENT_ID," + dm + ","
                    + "par.acct_name ";
        }
        String[] fields = {"acct_code", "acct_name", "under", "opening"};
        StringBuilder query = new StringBuilder("SELECT trial.*,prt.country_code "
                + "FROM "
                + "(SELECT cc.ACCT_CODE,cc.acct_name,dbal._0_0 opening,"
                + "SUM(accounted_dr) debit,SUM(accounted_cr) credit,"
                + "cc.CODE_COMBINATION_ID,par.ACCT_NAME under,cc.parent_id,dbal." + dm + " closing "
                + "FROM gl_code_combination_d cc "
                + join + " JOIN gl_je_f je ON (cc.CODE_COMBINATION_ID=je.CODE_COMBINATION_ID "
                + "AND je.acct_year=" + bean.getAcctYear() + " AND je.je_date <='" + dateMonth + "' " + attribute2Part + ") "
                + join + " JOIN gl_day_balances_f dbal ON "
                + "(dbal.CODE_COMBINATION_ID = cc.CODE_COMBINATION_ID AND dbal.acct_year=" + bean.getAcctYear() + ") "
                + "LEFT OUTER JOIN gl_code_combination_d par ON (par.acct_id=cc.PARENT_ID) "
                + "WHERE cc.acct_flag='L' " + condition + " "
                + "group by cc.CODE_COMBINATION_ID,_0_0,cc.ACCT_CODE,cc.acct_name,cc.PARENT_ID," + dm + ","
                + "par.acct_name"
                + QUERY2 + ") trial "
                + "LEFT OUTER JOIN prt_contact_details_d prt ON (prt.code_combination_id = trial.CODE_COMBINATION_ID) WHERE 1=1 ");

        if (StringUtils.hasText(bean.getParam4()) && !"0".equalsIgnoreCase(bean.getParam4())) {
            query.append("AND trial.parent_id = ").append(bean.getParam4()).append(" ");
        } else if (StringUtils.hasText(bean.getParam4()) && "0".equalsIgnoreCase(bean.getParam4())) {
            query.append("AND trial.parent_id NOT IN (22,29) ");
        }

        query = addFilter(query, bean, fields);
        query.append(" ORDER BY acct_name,UNDER ");
        if (bean.getLength() > 0) {
            query.append(" LIMIT " + bean.getStart() + "," + bean.getLength());
        }
        System.out.println("/nSingle Account Query :: " + query);


        List<ReportBean> reportBeanList = jdbcTemplate.query(
                query.toString(),
                (rs, rowNum) -> {
                    String country = rs.getString("country_code");
                    if (("IN".equals(country) && "FOREIGN".equals(bean.getParam5()))
                            || (!"IN".equals(country) && "LOCAL".equals(bean.getParam5()))) {
                        return null;
                    }
                    ReportBean dto = new ReportBean();
                    dto.setParam1(rs.getString("acct_code"));
                    dto.setParam2(rs.getString("acct_name"));
                    double opening = rs.getDouble("opening");

                    if ("Y".equals(bean.getParam7()) && opening == 0) {
                        return null;
                    }
                    //double closingBal = rs.getDouble("closing");
                    double debit = rs.getDouble("debit");
                    double credit = rs.getDouble("credit");
                    String codeCombinationId = rs.getString("CODE_COMBINATION_ID");
                    dto.setParam3(FinanceUtil.modAndFormat(opening));
                    dto.setParam4(FinanceUtil.amountDrCr(opening));
                    dto.setParam5(FinanceUtil.formatBigDecimal(debit, 2));
                    dto.setParam6(FinanceUtil.formatBigDecimal(credit, 2));

                    double closingBal = FinanceUtil.closingBalance2(opening, debit, credit);
                    dto.setParam7(FinanceUtil.modAndFormat(closingBal));

                    dto.setParam8(rs.getString("under"));
                    dto.setParam10(FinanceUtil.amountDrCr(closingBal));
                    String link = "Monthly.fin?id=" + codeCombinationId + "&acctName=" + dto.getParam2()
                            + "&opn=" + opening;
                    String link2 = "bclPDF.fin?id=" + codeCombinationId + "&clng=" + closingBal;
                    dto.setParam9(link);
                    dto.setParam11(link2);
                    dto.setParam12(codeCombinationId);
                    if (closingBal == 0 && "Y".equals(bean.getParam2())) {
                        return null;
                    } else {
                        if ("Y".equals(bean.getParam7())) {
                            dto.setParam5("0");
                            dto.setParam6("0");
                            dto.setParam7("0");
                        }

                    }
                    return dto;
                });

        reportBeanList.removeIf(Objects::isNull);
        return reportBeanList;
    }

    public List<ReportBean> trialBranchwise(ReportBean vo) {
        return null;
    }

    public List<ReportBean> tdsRegisterData(ReportBean reportBean, boolean isPayable) {

        String list = "(2372,2376,2377,2378,2379,2585,2705,2706,2707,2720,2721,2722,4279)";

        if (!isPayable) {
            list = "(2371)";
        }

        StringBuilder query = new StringBuilder("SELECT je_source param1,je_voucher_no param2, CONVERT(je_date,date) param3,  " +
                "prt.pan_no param4, prt.gstin_no param5,"
                + "cc1.acct_name param6,cc.acct_name param7, "
                + "(trx.TOTAL_TAXABLE+trx.TOTAL_NON_TAXABLE) param8,(accounted_dr+accounted_cr) param9  FROM gl_je_f je "
                + "INNER JOIN gl_code_combination_d cc ON (je.code_combination_id = cc.code_combination_id) "
                + "INNER JOIN gl_code_combination_d cc1 ON (je.cash_bank = cc1.code_combination_id) "
                + "LEFT OUTER JOIN ar_customer_trx_f trx ON (trx.trx_number = je.je_voucher_no) "
                + "LEFT OUTER JOIN prt_contact_details_d prt ON (prt.code_combination_id = cc1.code_combination_id) "
                + "WHERE je.code_combination_id IN " + list
                + " AND je.je_date BETWEEN ? AND ?  ");

        String[] fields = {"je_voucher_no", "je_date", "je_source", "acct_name",
                "tds_account", "pan_no", "gstin_no", "basic_value", "tds_amount"};

        addFilter(query, reportBean, fields);

        query.append(" ORDER BY param7,je_date,je_voucher_no LIMIT " + reportBean.getStart() + " , " + reportBean.getLength());

        return jdbcTemplate.query(query.toString(), new BeanPropertyRowMapper<>(ReportBean.class),
                reportBean.getParam4(), reportBean.getParam5());
    }

    public void settleOpeningBalance(ReportBean bean) {
        String query = "UPDATE settled_opening_balance_d "
                + "SET settled_opn_balance = ? "
                + "WHERE acct_year=? "
                + "AND code_combination_id = ? ";

        int updateCount = jdbcTemplate.update(query, bean.getParam8(), bean.getAcctYear(), bean.getParam1());
        if (updateCount == 0) {
            String query2 = "INSERT INTO settled_opening_balance_d "
                    + "VALUES (?,?,?,?) ";

            jdbcTemplate.update(query2, bean.getParam1(), bean.getAcctYear(), bean.getParam8(), "0.00");
        }
    }

    public ReportBean bcl(ReportBean bean) {
        String dateMonth = bean.getParam12();
        String dm = DateUtil.getDayBalanceField(dateMonth);
        StringBuilder query = new StringBuilder("SELECT cc.acct_name,SUM(accounted_dr) debit,SUM(accounted_cr) credit,\n"
                + "               cc.CODE_COMBINATION_ID, cc.parent_id,dbal._0_0 opening, dbal." + dm + " closing, prt.ADDRESS1,\n"
                + "			   prt.PAN_NO, prt.TAN_NO, prt.GSTIN_NO\n"
                + "               FROM gl_code_combination_d cc \n"
                + "               LEFT OUTER JOIN gl_je_f je ON (cc.CODE_COMBINATION_ID=je.CODE_COMBINATION_ID \n"
                + "               AND je.acct_year= ? AND je.je_date <= ? ) \n"
                + "               LEFT OUTER JOIN gl_day_balances_f dbal ON \n"
                + "               (dbal.CODE_COMBINATION_ID = cc.CODE_COMBINATION_ID AND dbal.acct_year= ?) \n"
                + "			   LEFT OUTER JOIN partner_account_d prt ON (prt.PARTNER_ACCT_CODE = cc.CODE_COMBINATION_ID \n"
                + "			   AND prt.LOADNG_AGNT = ?)\n"
                + "               WHERE cc.acct_flag='L' AND cc.PARENT_ID IN (22,29) AND cc.code_combination_id = ? \n"
                + "			   group by cc.acct_name,cc.PARENT_ID,dbal." + dm + ",prt.ADDRESS1,cc.CODE_COMBINATION_ID,\n"
                + "			   prt.PAN_NO, prt.TAN_NO, prt.GSTIN_NO,dbal._0_0	  ");

        ReportBean dto = new ReportBean();
        return jdbcTemplate.queryForObject(query.toString(), (rs, rowNum) -> {
            dto.setParam2(rs.getString("acct_name"));
            double opening = rs.getDouble("opening");

            //double closingBal = rs.getDouble("closing");
            double debit = rs.getDouble("debit");
            double credit = rs.getDouble("credit");
            //int codeCombinationId = rs.getInt("CODE_COMBINATION_ID");
            dto.setParam3(FinanceUtil.modAndFormat(opening));
            dto.setParam4(FinanceUtil.amountDrCr(opening));
            dto.setParam5(FinanceUtil.formatBigDecimal(debit, 2));
            dto.setParam6(FinanceUtil.formatBigDecimal(credit, 2));
            double closingBal = FinanceUtil.closingBalance2(opening, debit, credit);
            dto.setParam7(FinanceUtil.modAndFormat(closingBal));
            dto.setParam10(FinanceUtil.amountDrCr(closingBal));
            dto.setParam8(rs.getString("address1"));
            dto.setParam9(rs.getString("pan_no"));
            dto.setParam11(rs.getString("tan_no"));
            dto.setParam12(rs.getString("gstin_no"));
            return dto;
        }, bean.getAcctYear(), dateMonth, bean.getAcctYear(), bean.getLoadingAgent(), Integer.parseInt(bean.getParam11()));
    }
}
