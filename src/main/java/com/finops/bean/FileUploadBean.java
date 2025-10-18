package com.finops.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class FileUploadBean extends AbstractBean{

    private String param1;
    private String param2;
    private String param3;
    private String param4;
    private List<FileUploadRowBean> rowList = new ArrayList<>();

    public void setDummy(int num){
        for(int i=0; i<num; i++){
            getRowList().add(new FileUploadRowBean());
        }
    }


}
