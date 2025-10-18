/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.util.sql;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bhaumik
 */
@lombok.Getter
@lombok.Setter
public class Query {

    private String queryStatement;



    private List<String> colunmList = new ArrayList<>();
    private QueryType queryType;
    private String columns;
    private String values;
    private List<String> freeWhereClause = new ArrayList<>();
    private List<Condition> where = new ArrayList<>();
    private Table table;
    private List<Join> joins = new ArrayList();
    public static final String SPACE = " ";
    public static final String EQUALS = "=";
    public static final String NOT_EQUALS = "<>";
    public static final String LIKE = " LIKE ";
    public static final String BRACKET_START = "(";
    public static final String BRACKET_END = ")";
    public static final String AMPERSAND = "\n";
    public static final String IS = " IS ";
    public static final String IS_NOT = " IS NOT ";
    private Limit limit;
    private String orderBy = "";
    private String groupBy = "";
    public static final String IN = " IN ";

    public void addToColumnList(String column) {
        this.colunmList.add(column);
    }

    /**
     * @return the queryType
     */
    public QueryType getQueryType() {
        return queryType;
    }

    public void addToFreeWhereClause(String column) {
        this.getFreeWhereClause().add(column);
    }

    public void addToJoinList(Join join) {
        this.joins.add(join);
    }

    public void addToWhereColumnList(Condition column) {
        this.where.add(column);
    }

    /**
     * @param queryType the queryType to set
 */
    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    /**
     * @param table the table to set
     */
    public void setTable(Table table) {
        this.table = table;
    }

    private String buildColumnList() {
        if (this.colunmList.size() > 0) {
            return String.join(",", this.colunmList);
        }
        return columns;
    }

    public void build() {
        String queryTypeStr = this.queryType.toString();
        switch (queryTypeStr) {
            case "SELECT":
                buildSelect();
                break;
            case "INSERT":
                buildInsert();
                break;
            default:
                break;
        }
    }

    public void buildSelect() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.queryType.toString())
                .append(columns)
                .append(" FROM ")
                .append(table)
                .append(buildJoinClause())
                .append(buildWhereClause())
                .append(buildFreeWhereClause())
                .append(buidGroupByClause())
                .append(buidOrderByClause())
                .append(limit != null ? limit.toString() : "");
        this.queryStatement = sb.toString();
    }

    public void buildInsert() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.queryType.getTemplate())
                .append(table)
                .append(Query.BRACKET_START)
                .append(columns)
                .append(Query.BRACKET_END)
                .append(" VALUES ")
                .append(Query.BRACKET_START)
                .append(this.values)
                .append(Query.BRACKET_END);
        this.queryStatement = sb.toString();
    }

    public String toString() {
        return queryStatement;
    }

    private String buildWhereClause() {
        StringBuilder builder = new StringBuilder();
        for (Condition condition : this.where) {
            builder.append(Query.SPACE).append(condition);
        }
        return builder.toString();
    }

    private String buildFreeWhereClause() {
        StringBuilder builder = new StringBuilder();
        for (String condition : this.freeWhereClause) {
            builder.append(Query.SPACE).append(condition);
        }
        return builder.toString();
    }

    private String buildJoinClause() {
        StringBuilder builder = new StringBuilder();
        for (Join join : this.joins) {
            builder.append(Query.AMPERSAND).append(join);
        }
        return builder.toString();
    }

    private String buidOrderByClause() {
        return orderBy;
    }

    private String buidGroupByClause() {
        return groupBy;
    }

    /**
     * @return the columns
     */
    public String getColumns() {
        return columns;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(String columns) {
        this.columns = columns;
    }

    /**
     * @return the values
     */
    public String getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(String values) {
        this.values = values;
    }
}
