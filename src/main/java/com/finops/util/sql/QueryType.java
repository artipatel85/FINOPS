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
public enum QueryType {
    SELECT("SELECT "),
    SELECT_DISTINCT("SELECT DISTINCT "),
    INSERT("INSERT INTO "),
    UPDATE("UPDATE "),
    DELETE("DELETE FROM ");

    private String template;

    private QueryType(String tpl) {
        this.template = tpl;
    }

    /**
     * @return the template
     */
    public String getTemplate() {
        return template;
    }

    /**
     * @param template the template to set
     */
    public void setTemplate(String template) {
        this.template = template;
    }
}
