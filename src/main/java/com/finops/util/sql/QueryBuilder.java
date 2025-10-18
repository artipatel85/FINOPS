/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.util.sql;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Bhaumik
 */
public class QueryBuilder {

    Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
    private final Query query;
    
    public QueryBuilder(QueryType type){
        this.query = new Query();
        this.query.setQueryType(type);
    }
    
    
    public QueryBuilder FROM(Table table){
        this.query.setTable(table);
        return this;
    }

    public QueryBuilder FROM(String name, String alias){
        Table table = new Table(name, alias);
        return FROM(table);
    }

    public QueryBuilder ORDER_BY(String orderByFields){
        this.query.setOrderBy(" ORDER BY "+orderByFields);
        return this;
    }
    
    public QueryBuilder TABLE(Table table){
        return FROM(table);
    }
    
    public QueryBuilder LEFT_JOIN(Table table, Condition... conditions){
        Join join = new Join(JoinType.LEFT_OUTER_JOIN, table);
        join.setConditions(Arrays.asList(conditions));
        this.query.addToJoinList(join);
        return this;
    }

    public QueryBuilder INNER_JOIN(Table table, Condition... conditions){
        Join join = new Join(JoinType.INNER_JOIN, table);
        join.setConditions(Arrays.asList(conditions));
        this.query.addToJoinList(join);
        return this;
    }

    public QueryBuilder LEFT_JOIN(String name, String alias, Condition... conditions){
        Table table = new Table(name, alias);
        return LEFT_JOIN(table, conditions);
    }

    public QueryBuilder INNER_JOIN(String name, String alias, Condition... conditions){
        Table table = new Table(name, alias);
        return INNER_JOIN(table, conditions);
    }
    
    public QueryBuilder COLUMNS(String... columns){
        this.query.setColunmList(Arrays.asList(columns));
        return this;
    }
    
    public QueryBuilder COLUMNS(String columns){
        this.query.setColumns(columns);
        return this;
    }
    
    public QueryBuilder VALUE(String values){
        this.query.setValues(values);
        return this;
    }
    
    public QueryBuilder addColumn(String column){
        this.query.addToColumnList(column);
        return this;
    }
    
    public QueryBuilder WHERE(String key, String value){
        this.query.addToWhereColumnList(new WhereCondition(key,value, Query.EQUALS));
        return this;
    }
    
    public QueryBuilder AND(String key, String value){
        this.query.addToWhereColumnList(new AndCondition(key,value, Query.EQUALS));
        return this;
    }

    public QueryBuilder ANDNOTEQUALS(String key, String value){
        this.query.addToWhereColumnList(new AndCondition(key,value, Query.NOT_EQUALS));
        return this;
    }

    public QueryBuilder FREEWHERECONDITION(String condition){
        this.query.addToFreeWhereClause(condition);
        return this;
    }

    public QueryBuilder ANDNULL(String key){
        this.query.addToWhereColumnList(new AndCondition(key,null, Query.IS));
        return this;
    }

    public QueryBuilder ANDNOTNULL(String key){
        this.query.addToWhereColumnList(new AndCondition(key,null, Query.IS_NOT));
        return this;
    }

    public QueryBuilder AND_BETWEEN(String key, String value1, String value2){
        this.query.addToWhereColumnList(new AndBetweenCondition(key,value1, value2));
        return this;
    }

    public QueryBuilder LIMIT(long start, int length){
        this.query.setLimit(new Limit(start, length));
        return this;
    }

    public QueryBuilder GROUP_BY(String groupBy){
        this.query.setGroupBy(" GROUP BY "+groupBy);
        return this;
    }
    
    public QueryBuilder OR(String key, String value){
        this.query.addToWhereColumnList(new OrCondition(key,value, Query.EQUALS));
        return this;
    }

    public QueryBuilder OR_WITH_SEPARATOR(String key, String value, String separator){
        this.query.addToWhereColumnList(new OrCondition(key,value, separator));
        return this;
    }

    public QueryBuilder AND_WITH_SEPARATOR(String key, String value, String separator){
        this.query.addToWhereColumnList(new AndCondition(key,value, separator));
        return this;
    }
    
    public QueryBuilder WHERE_LIKE(String key, String value){
        this.query.addToWhereColumnList(new WhereCondition(key,value, Query.LIKE));
        return this;
    }
    
    public QueryBuilder AND_LIKE(String key, String value){
        this.query.addToWhereColumnList(new AndCondition(key,value, Query.LIKE));
        return this;
    }

    public QueryBuilder AND_IN(String key, String value){
        this.query.addToWhereColumnList(new AndCondition(key,value, Query.IN));
        return this;
    }
    
    public QueryBuilder OR_LIKE(String key, String value){
        this.query.addToWhereColumnList(new OrCondition(key,value, Query.LIKE));
        return this;
    }
    
    public String toString(){
        return query.toString();
    }
    
    public QueryBuilder build(){
        this.query.build();
        logger.debug("QueryBuilder :: " +this.toString());
        return this;
    }
    
    public static void main(String[] args){
        Map<String, String> map = new HashMap<>();
        map.put("cc.acct_type", "L");
        QueryBuilder qb = new QueryBuilder(QueryType.SELECT)
                .COLUMNS("cc.code_combination_id,cc.acct_name,cc.parent_id,cc2.acct_name,prt.address1")
                .FROM(new Table("gl_code_combination_d", "cc"))
                .LEFT_JOIN(new Table("prt_contact_details_d","prt"), 
                        new Condition("prt.code_combination_id", "cc.code_combination_id", Query.EQUALS))
                .LEFT_JOIN(new Table("gl_code_combination_d","cc2"), 
                        new Condition("cc2.acct_id", "cc.parent_id", Query.EQUALS))
                .WHERE("cc.acct_flag","'L'")
                .AND("cc.parent_id", "29")
                .build();
        
        System.out.println(qb.toString());
        
        qb = new QueryBuilder(QueryType.INSERT)
                .TABLE(new Table("gl_code_combination_d",""))
                .COLUMNS("code_combination_id,acct_name,parent_id,acct_name")
                .VALUE("?,?,?,?")
                .build();
        
        System.out.println(qb.toString());
    }
}
