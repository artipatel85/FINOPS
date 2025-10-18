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
public class AndCondition extends Condition{
    private String prefix = "AND";
    
    public AndCondition(String key, String value, String separator) {
        super(key, value, separator);
    }

    @Override
    public String toString() {
        return prefix+Query.SPACE+super.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
