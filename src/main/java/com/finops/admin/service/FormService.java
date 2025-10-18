package com.finops.admin.service;

import com.finops.admin.dao.FormDAO;
import com.finops.admin.model.FormBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormService {

    @Autowired
    private FormDAO formDAO;

    public List<FormBean> findAll(){
        return formDAO.findAll();
    }

    public FormBean findById(int id){
        return formDAO.findById(id);
    }
}
