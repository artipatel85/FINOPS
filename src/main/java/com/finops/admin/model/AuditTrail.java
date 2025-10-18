/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.admin.model;

import java.util.UUID;

/**
 *
 * @author Bhaumik
 */
public class AuditTrail {
    private String userId;
    private String date;
    private String action;
    private String type;
    private String subType;
    private String id;
    private int acctYear;
    private String branch;
    private String data;
    
    public AuditTrail(String userId, int acctYear, String action, String type, String branch, String data){
        this.userId = userId;
        this.acctYear = acctYear;
        this.action = action;
        this.type = type;
        this.branch = branch;
        this.data = data;
    }
    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the subType
     */
    public String getSubType() {
        return subType;
    }

    /**
     * @param subType the subType to set
     */
    public void setSubType(String subType) {
        this.subType = subType;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the acctYear
     */
    public int getAcctYear() {
        return acctYear;
    }

    /**
     * @param acctYear the acctYear to set
     */
    public void setAcctYear(int acctYear) {
        this.acctYear = acctYear;
    }

    /**
     * @return the branch
     */
    public String getBranch() {
        return branch;
    }

    /**
     * @param branch the branch to set
     */
    public void setBranch(String branch) {
        this.branch = branch;
    }

    /**
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(String data) {
        this.data = data;
    }
    
    
}
