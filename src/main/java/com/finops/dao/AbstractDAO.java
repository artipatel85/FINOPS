package com.finops.dao;

import com.finops.bean.AbstractBean;
import com.finops.finance.bean.InvoiceBean;
import com.finops.util.ApplicationUtil;
import com.finops.util.sql.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AbstractDAO {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected void addFilter(QueryBuilder findByPageQuery, AbstractBean bean,
                             String[] fields){
        if(bean.getSearchFieldValueList() != null) {
            int i = 0;
            for (String value : bean.getSearchFieldValueList()) {
                if (value != null && !value.isEmpty()) {
                    findByPageQuery.AND_LIKE(fields[i], "'" + value + "%'");
                }
                i++;
            }
        }
    }

    protected void addFilter(QueryBuilder findByPageQuery, String field,
                             String value, boolean like){
        if (value!=null && !value.isEmpty() && !"null".equalsIgnoreCase(value) && !"0".equalsIgnoreCase(value)) {
            if(like) {
                findByPageQuery.AND_LIKE(field, "'" + value + "%'");
            }
            else {
                findByPageQuery.AND(field, "'" + value + "'");
            }
        }
    }

    protected void addFilter(StringBuilder findByPageQuery, String field,
                             String value, boolean like){
        if (value!=null && !value.isEmpty() && !"null".equalsIgnoreCase(value) && !"0".equalsIgnoreCase(value)) {
            if(like) {
                findByPageQuery.append(" AND "+field+ " LIKE '" + value + "%'");
            }
            else {
                findByPageQuery.append(" AND "+field+ " = '" + value+ "'");
            }
        }
    }

    protected void addFilter(QueryBuilder findByPageQuery, AbstractBean bean,
                             String[] fields, Set<String> likeSet){
        int i = 0;
        for (String value : bean.getSearchFieldValueList()) {
            if (value!=null && !value.isEmpty()) {
                if(likeSet.contains(fields[i])){
                    findByPageQuery.AND_LIKE(fields[i], "'" + value + "%'");
                }
                else {
                    findByPageQuery.AND(fields[i], "'" + value + "'");
                }
            }
            i++;
        }
    }

    protected void addLimit(String orderBy, QueryBuilder findByPageQuery, AbstractBean bean){
        findByPageQuery.ORDER_BY(orderBy)
                .LIMIT(bean.getStart(), bean.getLength());
    }

    public int generateAutoNumber(String autoNumberQuery, int initialNo) {
        Integer autoNumber = jdbcTemplate.queryForObject(autoNumberQuery,
                Integer.class);
        return (autoNumber == null || autoNumber == 0) ? initialNo : autoNumber;
    }

    protected StringBuilder addFilter(StringBuilder query, AbstractBean bean, String[] fields) {
        if (bean.getLength() == 0) {
            return query;
        }
        return query.append(createWhereCondition(bean, fields));
    }

    private String createWhereCondition(AbstractBean bean, String[] fields) {
        StringBuilder whereBuilder = new StringBuilder();
        List<String> valueList = bean.getSearchFieldValueList();
        if(valueList != null) {
            for (int i = 0; i < valueList.size(); i++) {
                if (StringUtils.hasText(valueList.get(i))) {
                    whereBuilder.append(" AND ").append(fields[i]).append(" LIKE '").append(valueList.get(i) + "%'");
                }
            }
        }
        return whereBuilder.toString();
    }

    protected String defaultNull(String s){
        return org.apache.commons.lang3.StringUtils.defaultIfEmpty(s, null);
    }

    protected String default0(String s){
        return org.apache.commons.lang3.StringUtils.defaultIfEmpty(s, "0");
    }

    protected void setRecordCount(String selectQuery, AbstractBean bean, boolean hasLimit, String... params){
        if(hasLimit){
            selectQuery = selectQuery.substring(0, selectQuery.indexOf("LIMIT"));
        }
        int index = selectQuery.indexOf(" FROM ");
        String countQuery = "SELECT COUNT(*) " + selectQuery.substring(index);
        int recCount = jdbcTemplate.queryForObject(countQuery, Integer.class, params);

        bean.setITotalRecords(recCount);
        bean.setITotalDisplayRecords(recCount);
    }

    protected String defaultN(String s){
        return (s==null)?"N":s;
    }
}
