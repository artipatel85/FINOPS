package com.finops.freight.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class JobBean extends BLBean{

    private String jobDate;
    private String currencyCode;
    private int jobAutoSequence;
    private String sobDate;
    private List<SOBean> soBeanList;
    private List<SIBean> siBeanList;
    private String siType;
}
