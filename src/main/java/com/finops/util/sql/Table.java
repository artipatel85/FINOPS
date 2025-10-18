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
public class Table {
    private String name;
    private String alias;

    public Table(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    @Override
    public String toString() {
        return name+Query.SPACE+alias;
    }
    
    
}
