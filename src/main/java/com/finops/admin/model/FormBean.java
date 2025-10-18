package com.finops.admin.model;

import com.finops.bean.AbstractBean;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FormBean extends AbstractBean {

    private int formId;
    private String name;
    private String select;
    private String create;
    private String update;
    private String delete;
    private String print;
    private String upload;
    private String download;
    private String approve;
    private String userName;
    private int nodeId;
    private int parentId;
    private String url;


    public String getPattern(){
        return select+"|"+create+"|"+update+"|"+delete+"|"+print+"|"+upload+"|"+download+"|"+approve;
    }
}
