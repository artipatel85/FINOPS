package com.finops.admin.model;

import com.finops.bean.AbstractBean;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class RoleBean extends AbstractBean {

    private String roleName;
    private String description;
    private String saveUpdate;

    private List<FormBean> priviledgeList = new ArrayList<>();
}
