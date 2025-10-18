/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.util.sql;

/**
 *
 * @author Bhaumik
 */
public enum JoinType {
    LEFT_OUTER_JOIN("LEFT OUTER JOIN "),
    RIGHT_OUTER_JOIN("RIGHT OUTER JOIN "),
    INNER_JOIN("INNER JOIN ");
    
    private String joinStatement;
    private JoinType(String joinStatement){
        this.joinStatement = joinStatement;
    }

    /**
     * @return the joinStatement
     */
    public String getJoinStatement() {
        return joinStatement;
    }

    /**
     * @param joinStatement the joinStatement to set
     */
    public void setJoinStatement(String joinStatement) {
        this.joinStatement = joinStatement;
    }
}
