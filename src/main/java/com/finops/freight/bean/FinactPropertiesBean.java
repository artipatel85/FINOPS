package com.finops.freight.bean;

import com.finops.bean.AbstractBean;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FinactPropertiesBean extends AbstractBean {
    private String revenueHeads;
    private String taxHeads;
    private String expenseHeads;
    private String discountHeads;
    private String otherChargesHeads;
    private String bankAcctParents;
    private String profitNLossCCID;
    private String diffInOpnBalCCID;
    private String creditorsParent;
    private String debtorsParent;
    private String reportPath;
    private String cutOffDate;
    private String balanceMsg;
    private String dailyMsg;
}
