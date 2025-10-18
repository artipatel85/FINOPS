package com.finops.finance.bean;

import com.finops.bean.AbstractBean;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PeriodBean extends AbstractBean {

    private String startDate;
    private String endDate;
    private int startYear;
    private int endYear;
    private int startMonth;
    private int endMonth;
}
