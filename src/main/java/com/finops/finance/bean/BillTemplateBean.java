package com.finops.finance.bean;

import com.finops.bean.AbstractBean;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter @Getter
public class BillTemplateBean extends FinanceBean {

    private String templateName;
    private String templateType;


    private List<BillTemplateRow> billTemplateRows = new ArrayList<>();
    private List<BillTemplateRow> billTemplateTaxs = new ArrayList<>();

    private int discountAcctCode;
    private String discountAcctName;
    private String discountDesc;
    private double discountPercentage;
    private double discountOnValue;

    private int otherChargesAcctCode;
    private String otherChargesAcctName;
    private String otherChargesDesc;
    private double otherChargesPercentage;
    private double otherChargesOnValue;
    private int templateId;
    private String revexptaxdiscoc;
}
