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
public class Limit extends Condition{
    private String prefix = " LIMIT ";
    private long start;
    private int length;

    public Limit(long start, int length) {
        this.start = start;
        this.length = length;
    }

    @Override
    public String toString() {
        return prefix+ start + "," + length; //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
