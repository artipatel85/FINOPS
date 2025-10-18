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
public class AndBetweenCondition extends Condition{
    private String prefix = " AND ";
    private final String KEYWORD1 = " BETWEEN ";
    private final String KEYWORD2 = " AND ";
    private String key;
    private String value1;
    private String value2;

    public AndBetweenCondition(String key, String value1, String value2) {
        this.key = key;
        this.value1 = value1;
        this.value2 = value2;
    }

    @Override
    public String toString() {
        return prefix+ key + KEYWORD1 + value1 + KEYWORD2 + value2; //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
