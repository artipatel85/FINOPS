package com.finops.admin.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserBean extends AdminBean{
    private String bankName;
    private String bankAddress;
    private String bankAcNo;
    private String swiftCode;
}
