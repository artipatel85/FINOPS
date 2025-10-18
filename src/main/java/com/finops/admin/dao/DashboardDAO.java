package com.finops.admin.dao;

import com.finops.dao.AbstractDAO;
import com.finops.finance.bean.InvoiceBean;
import com.finops.util.sql.Condition;
import com.finops.util.sql.Query;
import com.finops.util.sql.QueryBuilder;
import com.finops.util.sql.QueryType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DashboardDAO extends AbstractDAO {

    public List<Integer> getMonthlyInvoiceData(InvoiceBean invoiceBean, String startDate, String endDate) {
        startDate = invoiceBean.getYrStartDate();
        endDate = invoiceBean.getYrEndDate();
        int acctYear = invoiceBean.getAcctYear();
        String billType = invoiceBean.getBillType();
        QueryBuilder findByPageQuery = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" count(*) count, month(je_date) month ")
                .FROM("gl_je_f", "je")
                .WHERE("je.je_source", "'"+billType+"'")
                .AND_BETWEEN("je.je_date", "?", "?")
                .AND("HDR_LINE_FLAG","'H'")
                .GROUP_BY(" month(je_date)");

        List<Integer> total = new ArrayList<>();

        return jdbcTemplate.query(
                findByPageQuery.build().toString(),
                (rs, rowNum) -> {
                    int count = rs.getInt("count");
                    total.add(count);
                    return count;
                },
                startDate, endDate);
    }
}
