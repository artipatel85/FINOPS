/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.util.sql;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bhaumik
 */
public class Join {

    /**
     * @return the conditions
     */
    public List<Condition> getConditions() {
        return conditions;
    }

    /**
     * @param conditions the conditions to set
     */
    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }
    private JoinType joinType;
    private Table table;
    private List<Condition> conditions = new ArrayList<>();
    private static final String ON = "ON";

    public Join(JoinType joinType, Table table) {
        this.joinType = joinType;
        this.table = table;
    }
    
    public void addToCondition(Condition condition){
        this.getConditions().add(condition);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(joinType.getJoinStatement())
                .append(Query.SPACE)
                .append(table)
                .append(Query.SPACE)
                .append(ON)
                .append(Query.SPACE)
                .append(Query.BRACKET_START);

        int i = 0;
        for(Condition condition : this.getConditions()){
            if(i++>0){
                sb.append(" AND ");
            }
            sb.append(condition);
        }
        
        sb.append(Query.BRACKET_END);
        return sb.toString();
    }
    
    
}
