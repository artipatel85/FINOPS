package com.finops.dao;

import com.finops.cron.CronBean;
import com.finops.report.model.ReportBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class CronJobDAO {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public void updateOpenBalancesToZero(ReportBean cronBean) {

        String query = "UPDATE gl_balances_f  SET CUR_BAL = OPN_BAL,PERIOD_1_NET_DR = 0,PERIOD_1_NET_CR = 0," +
                "PERIOD_2_NET_DR = 0,PERIOD_2_NET_CR = 0 ,PERIOD_3_NET_DR = 0,PERIOD_3_NET_CR = 0,PERIOD_4_NET_DR = 0," +
                "PERIOD_4_NET_CR = 0,PERIOD_5_NET_DR = 0,PERIOD_5_NET_CR = 0,PERIOD_6_NET_DR = 0,PERIOD_6_NET_CR = 0 ," +
                "PERIOD_7_NET_DR = 0,PERIOD_7_NET_CR = 0,PERIOD_8_NET_DR = 0,PERIOD_8_NET_CR = 0,PERIOD_9_NET_DR = 0," +
                "PERIOD_9_NET_CR = 0,PERIOD_10_NET_DR = 0,PERIOD_10_NET_CR = 0,PERIOD_11_NET_DR = 0,PERIOD_11_NET_CR = 0," +
                "PERIOD_12_NET_DR = 0,PERIOD_12_NET_CR = 0,QRTR_01_NET_DR = 0,QRTR_01_NET_CR = 0,QRTR_02_NET_DR = 0," +
                "QRTR_02_NET_CR = 0,QRTR_03_NET_DR = 0,QRTR_03_NET_CR = 0,QRTR_04_NET_DR = 0,QRTR_04_NET_CR = 0,YTD_NET_DR = 0," +
                "YTD_NET_CR = 0,ADD_BEFORE_6_MNTHS = 0,ADD_AFTER_6_MNTHS = 0,DELETIORS = 0,DEP_FOR_12_MNTHS = 0,DEP_FOR_6_MNTHS = 0 " +
                "WHERE acct_year = ?";

        jdbcTemplate.update(query, cronBean.getAcctYear());
    }

    public void resetDiffInOpenBalance(ReportBean cronBean) {
        String query = "SELECT SUM(dt.bal)* (-1) " +
                "FROM " +
                "(SELECT debit.opn_bal bal,debit.code_combination_id,debit.acct_year " +
                "FROM gl_balances_f debit,gl_code_combination_d cc " +
                "WHERE opn_bal >= 0 AND acct_year = @acctYear AND (cc.acct_flag = 'L') AND cc.code_combination_id = debit.code_combination_id " +
                "UNION ALL " +
                "SELECT credit.opn_bal bal,credit.code_combination_id,credit.acct_year " +
                "FROM gl_balances_f credit,gl_code_combination_d cc " +
                "WHERE opn_bal < 0 AND acct_year = ? AND (cc.acct_flag = 'L') " +
                "AND cc.code_combination_id = credit.code_combination_id) dt";

        Double balance = jdbcTemplate.queryForObject(query, Double.class, cronBean.getAcctYear());

        query = "UPDATE gl_balances_f SET opn_bal = @difference, cur_bal = ? WHERE code_combination_id = 1017 AND acct_year = ? ";

        jdbcTemplate.update(query, balance, cronBean.getAcctYear());
    }

    public void updateOpenDayBalancesToZero(ReportBean cronBean) {

        String query = "DELETE FROM gl_day_balances_f WHERE acct_year = ? ";
        jdbcTemplate.update(query, cronBean.getAcctYear());

        query = "INSERT INTO gl_day_balances_f" +
                "(ACCT_YEAR, CODE_COMBINATION_ID,company_id,_0_0," +
                "_1_4, _2_4, _3_4, _4_4, _5_4, _6_4, _7_4, _8_4, _9_4, _10_4," +
                "_11_4, _12_4, _13_4, _14_4, _15_4, _16_4, _17_4, _18_4, _19_4," +
                "_20_4, _21_4, _22_4, _23_4, _24_4, _25_4, _26_4, _27_4, _28_4," +
                "_29_4, _30_4, _1_5, _2_5, _3_5, _4_5, _5_5, _6_5, _7_5," +
                "_8_5, _9_5, _10_5, _11_5, _12_5, _13_5, _14_5, _15_5, _16_5," +
                "_17_5, _18_5, _19_5, _20_5, _21_5, _22_5, _23_5, _24_5, _25_5," +
                "_26_5, _27_5, _28_5, _29_5, _30_5, _31_5, _1_6, _2_6, _3_6," +
                "_4_6, _5_6, _6_6, _7_6, _8_6, _9_6, _10_6, _11_6, _12_6," +
                "_13_6, _14_6, _15_6, _16_6, _17_6, _18_6, _19_6, _20_6, _21_6," +
                "_22_6, _23_6, _24_6, _25_6, _26_6, _27_6, _28_6, _29_6, _30_6," +
                "_1_7, _2_7, _3_7, _4_7, _5_7, _6_7, _7_7, _8_7, _9_7, _10_7," +
                "_11_7, _12_7, _13_7, _14_7, _15_7, _16_7, _17_7, _18_7, _19_7," +
                "_20_7, _21_7, _22_7, _23_7, _24_7, _25_7, _26_7, _27_7, _28_7," +
                "_29_7, _30_7, _31_7, _1_8, _2_8, _3_8, _4_8, _5_8, _6_8," +
                "_7_8, _8_8, _9_8, _10_8, _11_8, _12_8, _13_8, _14_8, _15_8," +
                "_16_8, _17_8, _18_8, _19_8, _20_8, _21_8, _22_8, _23_8, _24_8," +
                "_25_8, _26_8, _27_8, _28_8, _29_8, _30_8, _31_8, _1_9, _2_9," +
                "_3_9, _4_9, _5_9, _6_9, _7_9, _8_9, _9_9, _10_9, _11_9," +
                "_12_9, _13_9, _14_9, _15_9, _16_9, _17_9, _18_9, _19_9, _20_9," +
                "_21_9, _22_9, _23_9, _24_9, _25_9, _26_9, _27_9, _28_9, _29_9," +
                "_30_9, _1_10, _2_10, _3_10, _4_10, _5_10, _6_10, _7_10, _8_10," +
                "_9_10, _10_10, _11_10, _12_10, _13_10, _14_10, _15_10, _16_10," +
                "_17_10, _18_10, _19_10, _20_10, _21_10, _22_10, _23_10, _24_10," +
                "_25_10, _26_10, _27_10, _28_10, _29_10, _30_10, _31_10, _1_11," +
                "_2_11, _3_11, _4_11, _5_11, _6_11, _7_11, _8_11, _9_11," +
                "_10_11, _11_11, _12_11, _13_11, _14_11, _15_11, _16_11, _17_11," +
                "_18_11, _19_11, _20_11, _21_11, _22_11, _23_11, _24_11, _25_11," +
                "_26_11, _27_11, _28_11, _29_11, _30_11, _1_12, _2_12, _3_12," +
                "_4_12, _5_12, _6_12, _7_12, _8_12, _9_12, _10_12, _11_12," +
                "_12_12, _13_12, _14_12, _15_12, _16_12, _17_12, _18_12, _19_12," +
                "_20_12, _21_12, _22_12, _23_12, _24_12, _25_12, _26_12, _27_12," +
                "_28_12, _29_12, _30_12, _31_12, _1_1, _2_1, _3_1, _4_1, _5_1," +
                "_6_1, _7_1, _8_1, _9_1, _10_1, _11_1, _12_1, _13_1, _14_1," +
                "_15_1, _16_1, _17_1, _18_1, _19_1, _20_1, _21_1, _22_1, _23_1," +
                "_24_1, _25_1, _26_1, _27_1, _28_1, _29_1, _30_1, _31_1, _1_2," +
                "_2_2, _3_2, _4_2, _5_2, _6_2, _7_2, _8_2, _9_2, _10_2," +
                "_11_2, _12_2, _13_2, _14_2, _15_2, _16_2, _17_2, _18_2, _19_2," +
                "_20_2, _21_2, _22_2, _23_2, _24_2, _25_2, _26_2, _27_2, _28_2," +
                "_29_2, _1_3, _2_3, _3_3, _4_3, _5_3, _6_3, _7_3, _8_3, _9_3," +
                "_10_3, _11_3, _12_3, _13_3, _14_3, _15_3, _16_3, _17_3, _18_3," +
                "_19_3, _20_3, _21_3, _22_3, _23_3, _24_3, _25_3, _26_3, _27_3," +
                "_28_3, _29_3, _30_3, _31_3)" +
                "SELECT acct_year,code_combination_id,company_id,opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal, opn_bal," +
                "opn_bal, opn_bal, opn_bal, opn_bal, opn_bal " +
                "FROM gl_balances_f WHERE acct_year = ?";

        jdbcTemplate.update(query, cronBean.getAcctYear());
    }

    public void reApproveAllTheVouchers(ReportBean cronBean) {
        String query = "SELECT code_combination_id,SUM(accounted_dr) accounted_dr,SUM(accounted_cr) accounted_cr," +
                "STR_TO_DATE(je_date,'%Y-%m-%d') je_date,acct_year,company_id " +
                "FROM gl_je_f WHERE status='A' AND acct_year = ? " +
                "GROUP BY code_combination_id,je_date,acct_year,company_id " +
                "order by je_date";

        jdbcTemplate.query(query, new ResultSetExtractor<List<ReportBean>>() {
            @Override
            public List<ReportBean> extractData(ResultSet rs) throws SQLException, DataAccessException {
                while (rs.next()) {
                    int codeCombinationId = rs.getInt(1);
                    double accountedDr = rs.getDouble(2);
                    double accountedCr = rs.getDouble(3);
                    String jeDate = rs.getString(4);
                    int companyId = rs.getInt(5);
                    String[] dateComponents = jeDate.split("-");
                    String dayMonth = "_"+Integer.parseInt(dateComponents[2])+"_"+Integer.parseInt(dateComponents[1]);
                    updateGlDayBalance(codeCombinationId, accountedDr, accountedCr, dayMonth, cronBean.getAcctYear(), 1001);

                }
                return null;
            }
        }, cronBean.getAcctYear());
    }

    private void updateGlDayBalance(int codeCombinationId, double accountedDR,
                                    double accountedCR, String dayMonth, int acctYear, int companyId){
            StringBuilder query = new StringBuilder("UPDATE gl_day_balances_f SET _0_0 = _0_0");
            String[] dayM = dayMonth.split("_");
            double currentBalance = accountedDR - accountedCR;
            int day = Integer.parseInt(dayM[1]);
            int month = Integer.parseInt(dayM[2]);
            if(month < 4){
                month = month + 12;
            }
            boolean start = true;
            for(int i=4; i<=15; i++){
                if(start){
                    i = month;
                }
                if(i > 12){
                    month = i - 12;
                }
                else{
                    month = i;
                }
                for(int j=1; j<=31; j++){
                    if(start){
                        j = day;
                        start = false;
                    }
                    if((month == 4 || month == 6 || month == 9 || month == 11 | month == 2) && j > 30){
                        continue;
                    }
                    if((month == 2) && j > 29){
                        continue;
                    }
                    String jMonth = "_"+j+"_"+month;
                    //System.out.println(jMonth);
                    query.append(","+jMonth+"="+jMonth+"+"+currentBalance);

                }
            }
        query.append(" WHERE code_combination_id = ? AND acct_year = ? AND company_id = ? ");
        System.out.println(dayMonth);
        jdbcTemplate.update(query.toString(), codeCombinationId, acctYear, companyId);
    }

    public static void main(String[] args) {
        CronJobDAO cjd = new CronJobDAO();
        //cjd.updateGlDayBalance(0,0,0, "_31_4");
    }
}
