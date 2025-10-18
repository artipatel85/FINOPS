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
public class Condition {
    private String separator;
    private String key;
    private String value;
    
    public Condition(String key, String value, String separator){
        this.key = key;
        this.value = value;
        this.separator = separator;
    }

    public Condition() {
    }

    public String toString(){
        return key+separator+value;
    }
}
