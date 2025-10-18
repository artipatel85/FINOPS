package com.finops.report.dao;

import com.finops.aop.MeasureTime;
import com.finops.dao.AbstractDAO;
import com.finops.partner.model.PartnerBean;
import com.finops.partner.service.PartnerService;
import com.finops.report.model.ReportBean;
import com.finops.util.ApplicationUtil;
import com.finops.util.Constants;
import com.finops.util.DateUtil;
import com.finops.util.FinanceUtil;
import com.finops.util.sql.Condition;
import com.finops.util.sql.Query;
import com.finops.util.sql.QueryBuilder;
import com.finops.util.sql.QueryType;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class ProfitabilityDAO extends AbstractDAO {

    //select bl.bl_no, BL_ISSUE_DATE, SUM(CASE WHEN arl.REV_EXP = 'REVENUE' THEN arl.accounted_amount ELSE 0 END) INCOME,
    //SUM(CASE WHEN arl.REV_EXP = 'EXPENSE' THEN arl.accounted_amount * -1 ELSE 0 END) EXPENSE,ar.SEA_AIR, ar.EXP_IMP,bl.JOB_NUMBER
    //FROM bl_hdr_f bl
    //INNER JOIN ar_customer_trx_f ar ON (ar.AWB_BL_NO = bl.bl_no and bl_issue_date between '2022-04-01' and '2022-05-01')
    //INNER JOIN ar_customer_trx_lines_f arl ON (ar.trx_number = arl.trx_number)
    //where bl_issue_date between '2022-04-01' and '2022-05-01'
    //GROUP BY bl.bl_no, BL_ISSUE_DATE,ar.SEA_AIR, ar.EXP_IMP,bl.JOB_NUMBER
    //ORDER BY bl.BL_NO

    @MeasureTime
    public List<ReportBean> summarySea(ReportBean bean){
        QueryBuilder queryBuilder = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" bl.bl_no param2, BL_ISSUE_DATE, SUM(CASE WHEN arl.REV_EXP = 'REVENUE' THEN arl.accounted_amount ELSE 0 END) param3," +
                        "SUM(CASE WHEN arl.REV_EXP = 'EXPENSE' THEN arl.accounted_amount ELSE 0 END) param4," +
                        "ar.SEA_AIR param6, ar.EXP_IMP param7,bl.JOB_NUMBER param1")
                .FROM("bl_hdr_f","bl")
                .INNER_JOIN("ar_customer_trx_f", "ar",
                        new Condition("ar.AWB_BL_NO", "bl.bl_no", Query.EQUALS),
                        new Condition("", "bl_issue_date between ? and ? ",""))
                .INNER_JOIN("ar_customer_trx_lines_f", "arl",
                        new Condition("ar.trx_number", "arl.trx_number", Query.EQUALS),
                        new Condition("ARL.BL_NO","bl.bl_no", Query.EQUALS))
                .WHERE("1", "1")
                .AND_BETWEEN("bl_issue_date", "?","?")
                .GROUP_BY("bl.bl_no, BL_ISSUE_DATE,ar.SEA_AIR, ar.EXP_IMP,bl.JOB_NUMBER");

        String[] fields = {"bl.job_no", "bl.bl_no", "revenue", "expense", "sea_air", "exp_imp", "branch", "name", "party"};
        addFilter(queryBuilder, bean, fields);
        addFilter(queryBuilder, "BL.BL_NO", bean.getParam6()+"", true);
        addFilter(queryBuilder, "BL.job_number", bean.getParam7()+"", true);
        addLimit("bl.BL_NO", queryBuilder, bean);

        return jdbcTemplate.query(queryBuilder.build().toString(),
                new BeanPropertyRowMapper<>(ReportBean.class),
                bean.getParam4(), bean.getParam5(), bean.getParam4(), bean.getParam5());
    }

    @MeasureTime
    public List<ReportBean> summaryAir(ReportBean bean){
        QueryBuilder queryBuilder = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" bl.hawb_number param2, SUM(CASE WHEN arl.REV_EXP = 'REVENUE' THEN arl.accounted_amount ELSE 0 END) param3," +
                        "SUM(CASE WHEN arl.REV_EXP = 'EXPENSE' THEN arl.accounted_amount * -1 ELSE 0 END) param4," +
                        "ar.SEA_AIR param6, ar.EXP_IMP param7,bl.JOB_NUMBER param1")
                .FROM("hawb_hdr_f","bl")
                .INNER_JOIN("ar_customer_trx_f", "ar",
                        new Condition("ar.AWB_BL_NO", "bl.hawb_number", Query.EQUALS))
                .INNER_JOIN("ar_customer_trx_lines_f", "arl",
                        new Condition("ar.trx_number", "arl.trx_number", Query.EQUALS),
                        new Condition("arl.BL_NO", "bl.hawb_number", Query.EQUALS))
                .WHERE("1", "1")
                //.AND_BETWEEN("bl_issue_date", "?","?")
                .GROUP_BY("bl.hawb_number,ar.SEA_AIR, ar.EXP_IMP,bl.JOB_NUMBER");

        String[] fields = {"bl.job_no", "bl.hawb_number", "revenue", "expense", "sea_air", "exp_imp", "branch", "name", "party"};
        addFilter(queryBuilder, bean, fields);
        addFilter(queryBuilder, "BL.hawb_number", bean.getParam6()+"", true);
        addFilter(queryBuilder, "BL.job_number", bean.getParam7()+"", true);
        addLimit("bl.hawb_number", queryBuilder, bean);

        return jdbcTemplate.query(queryBuilder.build().toString(),
                new BeanPropertyRowMapper<>(ReportBean.class));
    }

    @MeasureTime
    public List<ReportBean> detail(ReportBean bean) {
        List<ReportBean> rbList = new ArrayList<>();
        String value = bean.getParam1();
        String condition1 = "trxline.bl_no = ? ";
        String condition2 = "je.BL_NO = ? ";
        if(!StringUtils.hasText(bean.getParam1())){
            condition1 = "trxline.job_no = ? ";
            condition2 = "je.job_number = ? ";
            value = bean.getParam2();
        }

        String query = "SELECT STR_TO_DATE(trxline.trx_date,'%Y-%m-%d') trx_date,"
                + "trxline.trx_number,SUM(trxline.accounted_amount)accounted_amount,cc.acct_name, "
                + "trx.INV_DN_CN_MISC source,trxline.sea_air,trxline.exp_imp,trxline.job_no,trxline.bl_no bl_no, "
                + "CASE WHEN trx.INV_DN_CN_MISC IN ('INVOICE','MISC','BOS') THEN 'CREDIT' ELSE 'DEBIT' END as TYPE,trxline.customer_trx_id as hdr_id "
                + "FROM ar_customer_trx_lines_f trxline "
                + "LEFT OUTER JOIN ar_customer_trx_f trx ON (trx.CUSTOMER_TRX_ID = trxline.CUSTOMER_TRX_ID) "
                + "INNER JOIN gl_code_combination_d cc ON (cc.CODE_COMBINATION_ID = trxline.code_combination_id AND cc.parent_id IN (6,7,8)) "
                + "WHERE "+condition1+" AND trxline.ACCOUNTED_AMOUNT > 0 "
                + "AND trxline.REV_EXP_TAX_DISC_OC NOT IN ('X') "
                + "AND trxline.code_combination_id NOT IN (2373,2377,2378,2370,2373,2382,2382,2380,2380,2588,2589,2590,2591,4102,4105,4107) "
                + "GROUP BY trxline.trx_date,trxline.trx_number,cc.acct_name,trxline.customer_trx_id,"
                + "trx.INV_DN_CN_MISC,trxline.sea_air,trxline.exp_imp,BL_NO, job_no "
                + "UNION ALL "
                + "SELECT STR_TO_DATE(je.JE_DATE,'%Y-%m-%d') trx_date,je.JE_VOUCHER_NO,"
                + "SUM(je.ACCOUNTED_DR+je.ACCOUNTED_CR) accounted_amount, cc.acct_name,"
                + "je_source source,sea_air,exp_imp,je.job_number,je.bl_no, "
                + "CASE WHEN SUM(je.accounted_cr) > 0 THEN 'CREDIT' ELSE 'DEBIT' END as TYPE,"
                + "je_hdr_id as hdr_id "
                + "FROM gl_je_f je "
                + "INNER JOIN gl_code_combination_d cc ON (cc.code_combination_id = je.code_combination_id AND cc.parent_id IN (6,7, 8)) "
                + "WHERE "+condition2+" AND je.JE_SOURCE='JOURNAL' "
                + "AND je.code_combination_id NOT IN (2373,2377,2378,2370,2373,2382,2382,2380,2380,2588,2589,2590,2591,4102,4105,4107) "
                + "GROUP BY je_date,JE_VOUCHER_NO,cc.acct_name,je_hdr_id,je_source,sea_air,exp_imp,BL_NO, job_number "
                + "ORDER BY type ";

        return jdbcTemplate.query(query,
                new ResultSetExtractor<List<ReportBean>>() {
                    @Override
                    public List<ReportBean> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        double invoiceTotal = 0;
                        double expenseTotal = 0;
                        boolean invoiceFlag = true;
                        while (rs.next()) {
                            ReportBean rb = new ReportBean();
                            double amount = rs.getDouble("accounted_amount");
                            String source = rs.getString("source");
                            String hdrId = rs.getString("hdr_id");
                            String link = createUrl(hdrId, source);
                            String type = rs.getString("type");
                            rb.setParam1(rs.getString("trx_date"));
                            rb.setParam2(rs.getString("trx_number"));
                            rb.setParam3(rs.getString("acct_name"));
                            rb.setParam4(type);
                            rb.setParam5(FinanceUtil.formatBigDecimal(amount, 2));
                            rb.setParam6(link);
                            rb.setParam7(rs.getString("sea_air"));
                            rb.setParam8(rs.getString("exp_imp"));
                            rb.setParam9(rs.getString("bl_no"));
                            rb.setParam10(rs.getString("job_no"));
                            if (type.equals("CREDIT")) {
                                invoiceTotal += amount;
                            } else {
                                if (invoiceFlag) {
                                    //Invoice Total
                                    ReportBean invTotal = new ReportBean();
                                    if (bean.isReportRequest()) {
                                        invTotal.setParam3("TOTAL INCOME");
                                        invTotal.setParam5(FinanceUtil.formatBigDecimal(invoiceTotal, 2));
                                    } else {
                                        invTotal.setParam3(ApplicationUtil.dataTableLabel("TOTAL INCOME"));
                                        invTotal.setParam5(ApplicationUtil.dataTableLabel(FinanceUtil.formatBigDecimal(invoiceTotal, 2)));
                                    }

                                    rbList.add(invTotal);
                                    invoiceFlag = false;
                                }
                                expenseTotal += amount;
                            }
                            rbList.add(rb);
                        }
                        //Expense Total
                        ReportBean expTotal = new ReportBean();
                        if (bean.isReportRequest()) {
                            expTotal.setParam3("TOTAL EXPENSE");
                            expTotal.setParam5(FinanceUtil.formatBigDecimal(expenseTotal, 2));
                        } else {
                            expTotal.setParam3(ApplicationUtil.dataTableLabel("TOTAL EXPENSE"));
                            expTotal.setParam5(ApplicationUtil.dataTableLabel(FinanceUtil.formatBigDecimal(expenseTotal, 2)));
                        }
                        rbList.add(expTotal);
                        //Profit/Loss
                        ReportBean pnl = new ReportBean();
                        if (bean.isReportRequest()) {
                            pnl.setParam3("PROFIT/LOSS");
                            pnl.setParam5(FinanceUtil.formatBigDecimal(invoiceTotal - expenseTotal, 2));
                        } else {
                            pnl.setParam3(ApplicationUtil.dataTableLabel("PROFIT/LOSS"));
                            pnl.setParam5(ApplicationUtil.dataTableLabel(FinanceUtil.formatBigDecimal(invoiceTotal - expenseTotal, 2)));
                        }
                        rbList.add(pnl);
                        return rbList;
                    }
                }, value, value);
    }

    private String createUrl(String hdrId, String source) {
        String url = "#";
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
            url = "retrieveVoucherRow.fin?voucherType=JOURNAL&hdrId=" + hdrId;
        } else if ("INVOICE".equals(source) || "EXPENSE".equals(source) || "CREDITNOTE".equals(source)
                || "MISC".equals(source) || "DEBITNOTE".equals(source) || Constants.BILL_OF_SUPPLY.equals(source)) {
            //url = "billing.do?invoke=retrieve&trxid=" + hdrId;
            String mid = "EXPENSE".equals(source) ? source : "REVENUE";

            url = "retrieveBill.fin?param=" + mid + "&billId=" + hdrId;
        }
        return url;
    }


    @MeasureTime
    public List<ReportBean> summary(ReportBean bean, PartnerService partnerService, Map<String, String> salesmanMap){
        int prevAcctYear = DateUtil.getPrevAcctYear(bean.getAcctYear());
        String startDate = DateUtil.addDaysToGivenDate(bean.getParam4(), -90);
        String endDate = DateUtil.addDaysToGivenDate(bean.getParam4(), 90);

        String jobQuery = " ";
        String jobQueryJournal = " ";

        if(StringUtils.hasText(bean.getParam7())){
            jobQuery = " AND bl.job_number LIKE '"+bean.getParam7()+"%' ";
            jobQueryJournal = " AND JOB_NUMBER LIKE '"+bean.getParam7()+"%' ";
        }


        String seaQuery = "SELECT bl.bl_no param2, SUM(CASE WHEN arl.REV_EXP = 'REVENUE' THEN arl.accounted_amount ELSE 0 END) param3," +
        "SUM(CASE WHEN arl.REV_EXP = 'EXPENSE' THEN arl.accounted_amount ELSE 0 END) param4," +
                "arl.SEA_AIR param6, arl.EXP_IMP param7,bl.JOB_NUMBER param1,arl.LOADING_AGNT param8 FROM bl_hdr_f bl " +
                //"INNER JOIN  ar_customer_trx_f ar ON (ar.AWB_BL_NO=bl.bl_no) " +
                "INNER JOIN  ar_customer_trx_lines_f arl ON ( ARL.BL_NO=bl.bl_no) " +
                "INNER JOIN gl_code_combination_d cc ON (cc.code_combination_id = arl.code_combination_id AND cc.parent_id IN (6,7,8)) " +
                "WHERE 1=1  AND bl.creation_date BETWEEN ? AND ? AND BL.BL_NO LIKE ? AND arl.TRX_DATE BETWEEN ? AND DATE_ADD(?,INTERVAL 270 DAY)  "+jobQuery +
                "GROUP BY bl.bl_no,arl.SEA_AIR, arl.EXP_IMP,bl.JOB_NUMBER,arl.LOADING_AGNT ";

        String airQuery = "SELECT bl.hawb_number param2, SUM(CASE WHEN arl.REV_EXP = 'REVENUE' THEN arl.accounted_amount ELSE 0 END) param3, " +
        "SUM(CASE WHEN arl.REV_EXP = 'EXPENSE' THEN arl.accounted_amount ELSE 0 END) param4, " +
                "arl.SEA_AIR param6, arl.EXP_IMP param7,bl.JOB_NUMBER param1, arl.LOADING_AGNT param8 " +
                "FROM hawb_hdr_f bl " +
                //"INNER JOIN  ar_customer_trx_f ar ON (ar.AWB_BL_NO=bl.hawb_number) " +
                "INNER JOIN  ar_customer_trx_lines_f arl ON (ARL.BL_NO=bl.hawb_number) " +
                "INNER JOIN gl_code_combination_d cc ON (cc.code_combination_id = arl.code_combination_id AND cc.parent_id IN (6,7,8)) " +
                "WHERE 1=1  AND bl.creation_date BETWEEN ? AND ? AND BL.hawb_number LIKE ? AND arl.TRX_DATE BETWEEN ? AND DATE_ADD(?,INTERVAL 270 DAY)  "+jobQuery +
                "GROUP BY bl.hawb_number,arl.SEA_AIR, arl.EXP_IMP,bl.JOB_NUMBER,arl.LOADING_AGNT ";

        String journalSeaQuery = "AND EXISTS (SELECT BL_NO FROM BL_HDR_F " +
                "WHERE creation_date BETWEEN ? AND ? AND BL_NO = JE.BL_NO) ";

        String journalAirQuery = "AND EXISTS (SELECT HAWB_NUMBER FROM HAWB_HDR_F " +
                "WHERE CREATION_DATE BETWEEN ? AND ? AND HAWB_NUMBER = JE.BL_NO) ";

        String innerJoinSeaQuery = "INNER JOIN BL_HDR_F BL ON (BL.BL_NO = PROF.PARAM2) ";
        String outerSeaQuery = "SELECT param2, SUM(param3) param3, SUM(param4) param4, param6, param7, param1,param8, " +
                "(sum(param3) - sum(param4)) param9,BL.SHPR, BL.CNEE ";

        String innerJoinAirQuery = "INNER JOIN HAWB_HDR_F BL ON (BL.HAWB_NUMBER = PROF.PARAM2) ";


        String mainQuery = seaQuery;
        String journalSubQuery = journalSeaQuery;
        String innerJoinQuery = innerJoinSeaQuery;

        if("AIR".equals(bean.getParam8())){
            mainQuery = airQuery;
            journalSubQuery = journalAirQuery;
            innerJoinQuery = innerJoinAirQuery;
        }

        StringBuilder query = new StringBuilder(outerSeaQuery +
                "FROM (  " +mainQuery+
                "UNION ALL " +
                "SELECT je.bl_no param2, sum(accounted_cr) param3, sum(accounted_dr) param4, je.SEA_AIR param6, je.EXP_IMP param7," +
                "IFNULL(je.JOB_NUMBER,'') param1,je.attribute2 param8 " +
                "FROM gl_je_f je " +
                "INNER JOIN gl_code_combination_d cc ON (cc.code_combination_id = je.code_combination_id AND cc.parent_id IN (6,7,8)) " +
                "WHERE BL_NO LIKE ? "+jobQueryJournal+" and JE_SOURCE = 'JOURNAL' AND JE.ACCT_YEAR >= ? " +
                journalSubQuery +
                "GROUP BY je.SEA_AIR, je.EXP_IMP,je.JOB_NUMBER,je.bl_no,je.attribute2 " +
                ") prof " + innerJoinQuery +
                "GROUP BY param2, param6, param7, param1,param8,SHPR,CNEE ");

        String[] fields = {"bl.job_no", "bl.bl_no", "revenue", "expense", "sea_air", "exp_imp", "branch", "name", "party"};
//        addFilter(queryBuilder, bean, fields);
//        addFilter(queryBuilder, "BL.BL_NO", bean.getParam6()+"", true);
//        addFilter(queryBuilder, "BL.job_number", bean.getParam7()+"", true);
        if(!bean.isReportRequest()) {
            query.append(" LIMIT 0, 100 ");
        }

//        return jdbcTemplate.query(query.toString(),
//                new BeanPropertyRowMapper<>(ReportBean.class),
//                bean.getParam4(), bean.getParam5(), bean.getParam6()+"%",
//                bean.getParam6()+"%",prevAcctYear, bean.getParam4(), bean.getParam5());

        System.out.println(query.toString());

        List<ReportBean> reportBeanList = jdbcTemplate.query(query.toString(),
                    (rs, rowNum )-> {
                    ReportBean rb = new ReportBean();
                    rb.setParam1(ApplicationUtil.checkForNull(rs.getString("param1")));
                    rb. setParam2(rs.getString("param2"));
                    rb.setParam3(rs.getString("param3"));
                    rb.setParam4(rs.getString("param4"));
                    //rb.setParam5(rs.getString("param5"));
                    rb.setParam6(rs.getString("param6"));
                    rb.setParam7(rs.getString("param7"));
                    rb.setParam8(rs.getString("param8"));
                    rb.setParam9(rs.getString("param9"));
                    String shpr = rs.getString("shpr");
                    String cnee = rs.getString("cnee");

                    String expImp = rb.getParam7();
                    Map<String, PartnerBean> partnerBeanMap = partnerService.fetchAllPartners(rb.getParam8());
                    if("EXPORT".equalsIgnoreCase(expImp)){
                        PartnerBean pb = partnerBeanMap.get(shpr);
                        if(pb != null) {
                            rb.setParam10(pb.getDescription1());
                            String salesManCode = pb.getSalesManCode();
                            if(StringUtils.hasText(bean.getParam12()) && !bean.getParam12().equalsIgnoreCase(salesManCode)){
                                return null;
                            }
                            String salesMan = salesmanMap.get(salesManCode);
                            rb.setParam11(salesMan);
                        }

                    }
                    else{
                        PartnerBean pb = partnerBeanMap.get(cnee);
                        if(pb != null) {
                            rb.setParam10(pb.getDescription1());
                            String salesManCode = pb.getSalesManCode();
                            if(StringUtils.hasText(bean.getParam12()) && !bean.getParam12().equalsIgnoreCase(salesManCode)){
                                return null;
                            }
                            String salesMan = salesmanMap.get(salesManCode);
                            rb.setParam11(salesMan);
                        }
                    }

                    return rb;
                 },
                bean.getParam4(), bean.getParam5(), bean.getParam6()+"%",bean.getParam4(), bean.getParam5(),
                bean.getParam6()+"%",prevAcctYear, bean.getParam4(), bean.getParam5());

        reportBeanList.removeIf(Objects::isNull);
        return reportBeanList;
    }
}
