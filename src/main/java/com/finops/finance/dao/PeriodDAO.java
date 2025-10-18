package com.finops.finance.dao;

import com.finops.dao.AbstractDAO;
import com.finops.finance.bean.PeriodBean;
import com.finops.util.DateUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.List;

@Component
public class PeriodDAO extends AbstractDAO {


    public PeriodBean getPeriod(int acctYear, int companyId) {
        String sql = "select STR_TO_DATE(min(period_start_date),'%Y-%m-%d') START_DATE,"
                + "STR_TO_DATE(max(period_end_date),'%Y-%m-%d') END_DATE,ACCT_YEAR,period_status from gl_periods_d "
                + "WHERE acct_year = ? AND company_id = ? "
                + "GROUP BY ACCT_YEAR, PERIOD_STATUS";

        return jdbcTemplate.queryForObject(
                sql,
                new BeanPropertyRowMapper<>(PeriodBean.class),
                acctYear, companyId);
    }

    public List<PeriodBean> findAll(PeriodBean bean) {
        String sqlquery = "SELECT distinct gl.acct_year,"
                + "(select period_start_date from gl_periods_d "
                + "where acct_year=gl.acct_year and period_serial_no = 1) startdate,"
                + "(select period_end_date from gl_periods_d "
                + "where acct_year=gl.acct_year and period_serial_no = 12) enddate,"
                + "gl.period_status periodStatus "
                + "FROM gl_periods_d gl "
                + "WHERE gl.company_id = ? ";

        if (!bean.isAdministrator()) {
            sqlquery += " AND period_status = 'O' ";
        }

        return jdbcTemplate.query(sqlquery,
                new BeanPropertyRowMapper<>(PeriodBean.class),
                bean.getCompanyId());
    }

    public void close(PeriodBean periodBean) {
        for (int id : periodBean.getIds()) {
            jdbcTemplate.update("UPDATE gl_periods_d set period_status = ? where acct_year = ? ", "C", id);
        }
    }

    public void open(PeriodBean periodBean) {
        for (int id : periodBean.getIds()) {
            jdbcTemplate.update("UPDATE gl_periods_d set period_status = ? where acct_year = ? ", "O", id);
        }
    }

    public void save(PeriodBean periodBean) {
        String sql = "INSERT INTO gl_periods_d (company_id, acct_year, period_serial_no, period_start_date, period_end_date, period_status," +
                "period_name, financial_year_start, financial_year_end, period_id, status, created_by, creation_date, financial_month_start," +
                "financial_month_end, amended_by, amended_date) "
                + "VALUES (?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, SYSDATE(), ?, ?,?, SYSDATE())";


        int periodId = generateAutoNumber("select MAX(PERIOD_ID)+1 period_id FROM gl_periods_d", 1001);

        int startYear = periodBean.getStartYear();
        int endYear = periodBean.getEndYear();
        int startMonth = periodBean.getStartMonth();
        int endMonth = periodBean.getEndMonth();

        int periodSerialNo = 1;

        for (int i = startMonth; i <= startMonth + 11; i++) {
            int year = startYear;
            int month = i;
            if (i > 12) {
                year = endYear;
                month = i - 12;
            }
            String startDate = DateUtil.firstDateOfMonth(year, month-1);
            String endDate = DateUtil.lastDateOfMonth(year, month-1);

            jdbcTemplate.update(sql, periodBean.getCompanyId(), startYear+"" + endYear, periodSerialNo, startDate, endDate, "O",
                    month, startYear, endYear, periodId, "A", periodBean.getUserId(), startMonth, endMonth, periodBean.getUserId());
            periodSerialNo++;
            periodId++;
        }

    }
}
